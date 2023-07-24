package com.example.weatherapitest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun CurrentWeather() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 30.dp, bottom = 30.dp)
    ) {
        Image(
            bitmap = ImageBitmap.imageResource(R.drawable.test_photo),
            contentDescription = null,
            modifier = Modifier.width(100.dp)
        )
        Row(modifier = Modifier.padding(top = 40.dp, bottom = 20.dp)) {
            Text("32℃", fontSize = 35.sp)
            Text("|", modifier = Modifier.padding(start = 30.dp, end = 30.dp), fontSize = 35.sp)
            Text("多云", fontSize = 35.sp)
        }
        Text("最近的降雨带在南边114公里外呢", fontSize = 20.sp)
    }
}

@Composable
fun HourForecastList() {
    LazyRow {
        item {
            HourForecastItem(
                Modifier
                    .padding(start = 20.dp, end = 20.dp),
            )
        }
        repeat(11) {
            item {
                HourForecastItem(
                    Modifier
                        .padding(end = 20.dp),
                )
            }
        }
    }
}

@Composable
fun HourForecastItem(modifier: Modifier, time: String = "12:00", temperature: String = "34") {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = time, fontSize = 20.sp, modifier = Modifier.padding(bottom = 5.dp))
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.test_photo),
            contentDescription = null,
            modifier = Modifier.width(50.dp)
        )
        Text("$temperature℃", fontSize = 20.sp, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
fun DailyForecastList() {
    Column {
        DailyForecastItem(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.CenterHorizontally)
        )
        repeat(4) {
            DailyForecastItem(modifier = Modifier.padding(bottom = 20.dp))
        }
    }
}

@Composable
fun DailyForecastItem(modifier: Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text("7-24", fontSize = 20.sp)
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.test_photo),
            contentDescription = null,
            modifier = Modifier
                .height(50.dp)
                .padding(start = 10.dp, end = 10.dp)
        )
        Text("27℃ ~ 30℃", fontSize = 20.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherPreview() {
    WeatherApiTestTheme {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            item { CurrentWeather() }
            item { HourForecastList() }
            item { DailyForecastList() }
        }
    }
}
