package com.tomastu.iceweather

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.tomastu.iceweather.ui.theme.WeatherApiTestTheme
import com.tomastu.iceweather.jsonUtils.JsonName2Resource

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var adLoader: AdLoader
    private val permissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "onCreate: permission success!")
            } else {
                Log.d(TAG, "onCreate: permission failed!")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化ViewModel
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // 初始化AdMob
        MobileAds.initialize(this) { status ->
            Log.d(TAG, "AdMob status -> $status")
        }

        // 配置AdMob行为
        // val myAdUnitId = "ca-app-pub-9583057859081785/4173334363"
        val testAdUnitId = "ca-app-pub-3940256099942544/2247696110"     // 广告测试ID
        adLoader =
            AdLoader.Builder(this, testAdUnitId)
                .forNativeAd { nativeAd ->
                    // 考虑在 ViewModel 中加载广告数据
                    // show native ad here
                    Log.d(
                        TAG, """
                        | nativeAd.body -> ${nativeAd.body}
                        | nativeAd.extras -> ${nativeAd.extras}
                        | nativeAd.icon -> ${nativeAd.icon}
                        | nativeAd.advertiser -> ${nativeAd.advertiser}
                        | nativeAd.headline -> ${nativeAd.headline}
                        | nativeAd.images -> ${nativeAd.images} 
                        | nativeAd.price -> ${nativeAd.price}
                        | nativeAd.store -> ${nativeAd.store}
                    """.trimIndent()
                    )
                    if (adLoader.isLoading) {
                        // The AdLoader is still loading ads.
                        // Expect more adLoaded or onAdFailedToLoad callbacks.
                        Log.d(TAG, "forNativeAd: adLoader.isLoading...")
                    } else {
                        // The AdLoader has finished loading ads.
                        Log.d(TAG, "forNativeAd: not adLoader.isLoading...")
                    }
                    // If this callback occurs after the activity is destroyed, you
                    // must call destroy and return or you may get a memory leak.
                    // Note `isDestroyed` is a method on Activity.
                    if (isDestroyed) {
                        Log.d(TAG, "forNativeAd: isDestroyed...")
                        nativeAd.destroy()
                        return@forNativeAd
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        Toast.makeText(this@MainActivity, "ads load failed!", Toast.LENGTH_SHORT)
                            .show()
                        Log.e(
                            TAG, """
                           | p0.responseInfo -> ${p0.responseInfo}
                           | p0.code -> ${p0.code}
                           | p0.cause -> ${p0.cause}
                           | p0.domain -> ${p0.domain}
                           | p0.message -> ${p0.message}
                          """.trimIndent()
                        )
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build()
                )
                .build()

        // 定位权限检查
        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            // permission granted -> do get position feature here
            Log.d(TAG, "onCreate: get location permission!")
        } else {
            // permission denied -> use this to request location permission,
            //                      and we will explain this later in article
            if (checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                permissionRequestLauncher.launch("android.permission.ACCESS_FINE_LOCATION")
                Log.d(TAG, "onCreate: don't get location permission!")
            }
        }

        Log.e(TAG, "SHA1: ${test.sHA1(this)}")

        // 首次请求定位信息
        viewModel.getPositionThenGetWeather()
        // 加载广告
        adLoader.loadAd(AdRequest.Builder().build())

        setContent {
            WeatherApiTestTheme {
                // A surface container using the 'background' color from the theme
                WeatherPage(modifier = Modifier.fillMaxWidth(), viewModel = viewModel)
            }
        }

//        viewModel.requestNewWeatherData()

    }
}

@Composable
fun CurrentWeather(
    temperature: Double,
    weather: String,
    weatherIconId: Int,
    keypoint: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 30.dp, bottom = 30.dp)
    ) {
        Icon(painter = painterResource(id = weatherIconId), contentDescription = weather)

        Row(modifier = Modifier.padding(top = 40.dp, bottom = 20.dp)) {
            Text("${temperature.toInt()}℃", fontSize = 25.sp)
            Text("|", modifier = Modifier.padding(start = 30.dp, end = 30.dp), fontSize = 25.sp)
            Text(weather, fontSize = 25.sp)
        }

        Text(keypoint, fontSize = 15.sp)
    }
}

@Composable
fun HourForecastList(hourlyWeather: WeatherData.Result.Hourly?) {
    LazyRow(contentPadding = PaddingValues(start = 20.dp)) {
        repeat(12) { index ->
            item {
                HourForecastItem(
                    modifier = Modifier.padding(end = 20.dp).width(118.dp),
                    temperature = hourlyWeather?.temperature?.get(index)?.value
                        ?: 34.toDouble(),
                    time = hourlyWeather?.temperature?.get(index)?.datetime?.removeRange(0..10)
                        ?.removeRange(5..10) ?: "12:00",
                    weather = JsonName2Resource.transform2String(
                        hourlyWeather?.skycon?.get(index)?.value ?: "weather_unknow"
                    ),
                    weatherIconId = JsonName2Resource.transform2DrawableId(
                        hourlyWeather?.skycon?.get(
                            index
                        )?.value ?: "CLEAR_DAY"
                    )
                )
            }
        }
    }
}

@Composable
fun HourForecastItem(
    modifier: Modifier,
    time: String,
    temperature: Double = 34.toDouble(),
    weather: String,
    weatherIconId: Int,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = time,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 5.dp)
        )

        Icon(
            painter = painterResource(id = weatherIconId),
            contentDescription = weather,
            modifier = Modifier.width(35.dp)
        )

        Text(
            text = weather,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
        )

        Text(text = "${temperature.toInt()}℃", fontSize = 15.sp)
    }
}

@Composable
fun DailyForecastList(dailyWeather: WeatherData.Result.Daily?) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        repeat(5) { index ->
            DailyForecastItem(
                modifier = Modifier.padding(bottom = 20.dp),
                tempMax = dailyWeather?.temperature?.get(index)?.max ?: 12.toDouble(),
                tempMin = dailyWeather?.temperature?.get(index)?.min ?: 23.toDouble(),
                weather = JsonName2Resource.transform2String(
                    dailyWeather?.skycon?.get(index)?.value ?: "weather_unknow"
                ),
                weatherIconId = JsonName2Resource.transform2DrawableId(
                    dailyWeather?.skycon?.get(
                        index
                    )?.value ?: "CLEAR_DAY"
                ),
                date = dailyWeather?.temperature?.get(index)?.date?.removeRange(0..4)
                    ?.removeRange(5..16) ?: "6-18"
            )
        }
    }
}

@Composable
fun DailyForecastItem(
    modifier: Modifier,
    tempMax: Double,
    tempMin: Double,
    weather: String,
    weatherIconId: Int,
    date: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(date, fontSize = 15.sp)

        Icon(
            painter = painterResource(id = weatherIconId),
            contentDescription = weather,
            modifier = Modifier
                .height(50.dp)
                .padding(start = 10.dp, end = 10.dp)
        )

        Text(
            "${tempMin.toInt()}℃ ~ ${tempMax.toInt()}℃",
            fontSize = 15.sp,
            modifier = Modifier.padding(end = 5.dp)
        )

        Text(weather, fontSize = 15.sp)
    }
}

@Composable
fun WeatherPage(
    modifier: Modifier,
    viewModel: MainViewModel,
) {
    val weatherData by viewModel.weatherData.collectAsState()
    val weatherResult = weatherData?.result
    val realtime = weatherResult?.realtime
    val hourly = weatherResult?.hourly
    val daily = weatherResult?.daily
    val keypoint = weatherResult?.forecastKeypoint

    val isLoading by viewModel.isLoading.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    // val context = LocalContext.current
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        SwipeRefresh(state = swipeRefreshState, onRefresh = { viewModel.getPositionThenGetWeather() }) {
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    CurrentWeather(
                        temperature = realtime?.temperature ?: 32.toDouble(),
                        weather = JsonName2Resource.transform2String(realtime?.skycon ?: "weather_unknow"),
                        weatherIconId = JsonName2Resource.transform2DrawableId(
                            realtime?.skycon ?: "CLEAR_DAY"
                        ),
                        keypoint = keypoint ?: stringResource(id = R.string.keypoint_unknow)
                    )
                }
                item { HourForecastList(hourly) }
                item { DailyForecastList(daily) }
            }
        }
    }
}