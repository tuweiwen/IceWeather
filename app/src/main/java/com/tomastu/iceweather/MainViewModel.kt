package com.tomastu.iceweather

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    private var lat: Double? = null
    private var lon: Double? = null
    private val _isLoading = MutableStateFlow(false)

    val isLoading = _isLoading.asStateFlow()

    init {
        // todo : 高德隐私合规，后续需放在首次开屏
        AMapLocationClient.updatePrivacyShow(WeatherApplication.context, true, true)
        AMapLocationClient.updatePrivacyAgree(WeatherApplication.context, true)
    }

    val weatherData: StateFlow<WeatherData?>
        get() = _weatherData

    private fun requestNewWeatherData() {
        viewModelScope.launch {
            if (lat != null && lon != null) {
                Log.d(TAG, "lon -> $lon, lat -> $lat")
                _weatherData.value = Network.getCaiyunService().getWeather(lon!!, lat!!).await()
            } else {
                Toast.makeText(
                    WeatherApplication.context,
                    "position is missing! get position first!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // 高德地理搜索
    private val geoSearch = GeocodeSearch(WeatherApplication.context).apply {
        // 搜索监听回调
        setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {
            override fun onRegeocodeSearched(
                result: com.amap.api.services.geocoder.RegeocodeResult?,
                code: Int
            ) {
                when (code) {
                    1000 -> Log.d(
                        TAG,
                        "setOnGeocodeSearchListener: result is ${result!!.regeocodeAddress.formatAddress}"
                    )

                    else -> Log.d(
                        TAG,
                        "setOnGeocodeSearchListener: failed! code is $code"
                    )
                }
            }

            override fun onGeocodeSearched(
                result: com.amap.api.services.geocoder.GeocodeResult?,
                code: Int
            ) {
                Log.d(TAG, "onGeocodeSearched: $result")
            }
        })
    }

    // 高德定位Client参数
    private val options: AMapLocationClientOption = AMapLocationClientOption().apply {
        locationMode = AMapLocationClientOption.AMapLocationMode.Device_Sensors
        isOnceLocation = true
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTPS)
    }

    // 高德定位Client
    private val locationClient: AMapLocationClient = AMapLocationClient(WeatherApplication.context).apply {
        setLocationOption(options)
        // 设置定位Callback（获取到定位后的行为）
        setLocationListener {
            Log.d(TAG, "setLocationListener: $it")
            lat = it.latitude
            lon = it.longitude
            requestNewWeatherData()
            // fixme : 以下的定位无法返回值
            // 搜索查询参数
            val query = RegeocodeQuery(LatLonPoint(lat!!, lon!!), 100F, GeocodeSearch.AMAP)
            query.extensions = "all"
            // 进行异步搜索
            Log.d(TAG, "setLocationListener: getFromLocationAsync invoked!")
            geoSearch.getFromLocationAsyn(query)
            // 获取定位后，停止Client
            Log.d(TAG, "setLocationListener: stopLocation invoked!")
            this.stopLocation()
            _isLoading.value = false
        }
    }

    // todo : 需要考虑英语条件下请求不同的语言（keypoint）
    fun getPositionThenGetWeather()  {
        _isLoading.value = true
        locationClient.startLocation()

        // 后续更改 _isLoading 状态的位置，在定位的callback中
    }
}
