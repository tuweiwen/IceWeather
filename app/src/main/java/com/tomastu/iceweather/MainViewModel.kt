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
import java.util.Locale

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    private var lat: Double? = null
    private var lon: Double? = null
    private val _isLoading = MutableStateFlow(false)
    private var currentLanguage = Locale.getDefault().language
    private var currentCountry = Locale.getDefault().country
    private var _road = MutableStateFlow(WeatherApplication.context.getString(R.string.unknow_position))

    val isLoading = _isLoading.asStateFlow()

    val road: StateFlow<String>
        get() = _road
    val weatherData: StateFlow<WeatherData?>
        get() = _weatherData

    init {
        // todo : 高德隐私合规，后续需放在首次开屏
        AMapLocationClient.updatePrivacyShow(WeatherApplication.context, true, true)
        AMapLocationClient.updatePrivacyAgree(WeatherApplication.context, true)
    }

    fun updateLanguageAndCountry(language: String, country: String) {
        currentLanguage = language
        currentCountry = country
    }


    private fun requestNewWeatherData() {
        viewModelScope.launch {
            if (lat != null && lon != null) {
                Log.d(TAG, "lon -> $lon, lat -> $lat")
                Log.d(TAG, "currentLanguage -> $currentLanguage, currentCountry -> $currentCountry")
                when (currentLanguage) {
                    "zh", "en" -> {}
                    else -> {
                        currentLanguage = "en"
                    }
                }
                when (currentCountry) {
                    "TW", "CN", "US", "GB" -> {}
                    else -> {
                        // 中文语言地区判断，新加坡使用和大陆一致的简体，香港和澳门使用和台湾一致的繁体
                        if (currentLanguage.equals("zh")) {
                            when (currentCountry) {
                                "SG" -> {
                                    currentCountry = "CN"
                                }

                                "HK", "MO" -> {
                                    currentCountry = "TW"
                                }
                            }
                        } else {
                            currentCountry = "US"
                        }
                    }
                }
                val countryCode = StringBuilder()
                    .append(currentLanguage)
                    .append("_")
                    .append(currentCountry)
                    .toString()
                _weatherData.value =
                    Network.getCaiyunService().getWeather(lon!!, lat!!, countryCode).await()
            } else {
                Toast.makeText(
                    WeatherApplication.context,
                    "",
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
                    1000 -> {
                        _road.value = result!!.regeocodeAddress.roads[0].name
                    }
                    else -> Log.d(TAG, "deGeo failed! code is $code")
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
        isNeedAddress = true
        isMockEnable = true
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTPS)
    }

    // 高德定位Client
    private val locationClient: AMapLocationClient =
        AMapLocationClient(WeatherApplication.context).apply {
            setLocationOption(options)
            // 设置定位Callback（获取到定位后的行为）
            setLocationListener {
                Log.d(TAG, "setLocationListener callback result -> $it")
                lat = it.latitude
                lon = it.longitude
                requestNewWeatherData()
                // 搜索查询参数
                val query = RegeocodeQuery(LatLonPoint(lat!!, lon!!), 100F, GeocodeSearch.AMAP)
                query.extensions = "all"
                // 进行异步搜索
                geoSearch.getFromLocationAsyn(query)
                // 获取定位后，停止Client
                this.stopLocation()
                _isLoading.value = false
            }
        }

    fun getPositionAndGetWeather() {
        _isLoading.value = true
        locationClient.startLocation()

        // 后续更改 _isLoading 状态的位置，在定位的callback中
    }
}
