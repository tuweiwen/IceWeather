package com.example.weatherapitest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClient.updatePrivacyAgree
import com.amap.api.location.AMapLocationClient.updatePrivacyShow
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.setLocationProtocol
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.example.weatherapitest.ui.theme.WeatherApiTestTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val permissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "onCreate: success!")
            } else {
                Log.d(TAG, "onCreate: failed")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 高德隐私合规
        updatePrivacyShow(this, true, true)
        updatePrivacyAgree(this, true)

        // 高德定位Client参数
        val options: AMapLocationClientOption = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Device_Sensors
            isOnceLocation = true
            setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTPS)
        }

        // 高德定位Client
        val locationClient: AMapLocationClient = AMapLocationClient(applicationContext)
        locationClient.setLocationOption(options)

        // 高德地理搜索
        val geoSearch = GeocodeSearch(this)
        // 搜索监听回调
        geoSearch.setOnGeocodeSearchListener(object : OnGeocodeSearchListener {
            override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
                if (p1 == 1000) {
                    Log.d(TAG, "RegeoResult: ${p0!!.regeocodeAddress.formatAddress}")
                }
            }

            override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
                Log.d(TAG, "onGeocodeSearched: $p0")
            }
        })

        /*
        // 搜索查询参数
//        val searchParam = RegeocodeQuery(LatLonPoint(121.4771, 31.2390), 10F, GeocodeSearch.AMAP)
        // 进行异步搜索
//        geoSearch.getFromLocationAsyn(searchParam)*/

        // 定位Callback
        locationClient.setLocationListener {
            Log.d(TAG, "setLocationListener")
            Log.d(TAG, "onCreate: $it")
            val query =
                RegeocodeQuery(LatLonPoint(it.longitude, it.latitude), 100F, GeocodeSearch.AMAP)
            geoSearch.getFromLocationAsyn(query)
            // 获取定位后，停止Client
            locationClient.stopLocation()
        }

        // 开始获取定位
        locationClient.startLocation()

        // 权限检查
        /*if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            // permission granted -> do get position feature here
            Log.d(TAG, "onCreate: get location permission!")
        } else {
            // permission denied -> use this to request location permission,
            //                      and we will expaine this later in artical
            permissionRequestLauncher.launch("android.permission.ACCESS_FINE_LOCATION")
            Log.d(TAG, "onCreate: don't get location permission!")
        }*/
        setContent {
            WeatherApiTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherApiTestTheme {
        Greeting("Android")
    }
}