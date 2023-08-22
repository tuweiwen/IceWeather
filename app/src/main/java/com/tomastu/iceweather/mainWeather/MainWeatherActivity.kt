package com.tomastu.iceweather.mainWeather

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.tomastu.iceweather.R
import com.tomastu.iceweather.jsonUtils.JsonName2Resource
import com.tomastu.iceweather.ui.theme.WeatherApiTestTheme
import com.tomastu.iceweather.SHA1

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var adLoader: AdLoader
    private lateinit var viewModel: MainWeatherViewModel
    private val permissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "onCreate: permission success!")
                if (::viewModel.isInitialized) {
                    viewModel.getPositionAndGetWeather()
                }
            } else {
                Log.d(TAG, "onCreate: permission failed!")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP0 : 高德 SDK 需要SHA1值，可以通过此方法动态获取
        Log.d(TAG, "SHA1: ${SHA1.getSHA1(this)}")

        // STEP1 : ViewModel 相关行为
        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[MainWeatherViewModel::class.java]


        // STEP2 : AdMob 相关行为
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
                        /*Toast.makeText(this@MainActivity, "ads load failed!", Toast.LENGTH_SHORT)
                            .show()*/
                        Log.d(
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

        // 加载广告
        adLoader.loadAd(AdRequest.Builder().build())


        // STEP3 : 定位权限相关行为
        // 定位权限检查
        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            // permission granted -> do get position feature here
            Log.d(TAG, "onCreate: get location permission!")

            // 首次请求定位信息
            viewModel.getPositionAndGetWeather()
        } else {
            // permission denied -> use this to request location permission,
            //                      and we will explain this later in article
            if (checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                permissionRequestLauncher.launch("android.permission.ACCESS_FINE_LOCATION")
                Log.d(TAG, "onCreate: don't get location permission!")
            }
        }


        // STEP4 : 加载视图内容
        setContent {
            WeatherApiTestTheme {
                // A surface container using the 'background' color from the theme
                WeatherPage(modifier = Modifier.fillMaxWidth(), viewModel = viewModel)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // 针对 系统语言设置 的变更
        val locales = newConfig.locales[0] // 这里返回的是整个系统中配置过的所有语言，这里只使用第一个
        if (::viewModel.isInitialized) {
            viewModel.updateLanguageAndCountry(locales.language, locales.country)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(
    modifier: Modifier = Modifier,
    viewModel: MainWeatherViewModel,
) {
    val road by viewModel.road.collectAsState()
    val weatherData by viewModel.weatherData.collectAsState()
    val weatherResult = weatherData?.result
    val realtime = weatherResult?.realtime
    val hourly = weatherResult?.hourly
    val daily = weatherResult?.daily
    val keypoint = weatherResult?.forecastKeypoint

    val isLoading by viewModel.isLoading.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Scaffold(topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
                title = {
                    // todo(高德返回的中文地名，在系统为英语的时候需要考虑换成英文)
                    Text(
                        modifier = Modifier,
                        text = road,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }) {
            SwipeRefresh(
                modifier = Modifier.padding(it),
                state = swipeRefreshState,
                onRefresh = { viewModel.getPositionAndGetWeather() }) {
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    item {
                        CurrentWeather(
                            temperature = realtime?.temperature ?: 32.toDouble(),
                            weather = JsonName2Resource.transform2String(
                                realtime?.skycon ?: "weather_unknow"
                            ),
                            weatherIconId = JsonName2Resource.transform2DrawableId(
                                realtime?.skycon ?: "CLEAR_DAY"
                            ),
                            keypoint = keypoint ?: stringResource(id = R.string.keypoint_unknow)
                        )
                    }
                    item { HourForecastList(hourlyWeather = hourly) }
                    item { DailyForecastList(dailyWeather = daily) }
                }
            }
        }
    }
}