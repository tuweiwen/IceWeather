package com.tomastu.iceweather

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.tomastu.iceweather.ui.theme.WeatherApiTestTheme

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

        // 权限检查
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

        // 加载广告
        val myAdUnitId = "ca-app-pub-9583057859081785/4173334363"
        val testAdUnitId = "ca-app-pub-3940256099942544/2247696110"
        adLoader =
            AdLoader.Builder(this, testAdUnitId)
                .forNativeAd { nativeAd ->
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
                        Toast.makeText(this@MainActivity, "add load failed!", Toast.LENGTH_SHORT)
                            .show()
                        Log.e(TAG, """
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


        setContent {
            WeatherApiTestTheme {
                // A surface container using the 'background' color from the theme
                WeatherPage(modifier = Modifier.fillMaxWidth(), viewModel = viewModel)
            }
        }

        viewModel.requestNewWeatherData()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}

@Composable
fun CurrentWeather(
    temperature: Double,
    weather: String,
    keypoint: String
) {
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
            Text("${temperature.toInt()}℃", fontSize = 25.sp)
            Text("|", modifier = Modifier.padding(start = 30.dp, end = 30.dp), fontSize = 25.sp)
            Text(weather, fontSize = 25.sp)
        }
        Text(keypoint, fontSize = 15.sp)
    }
}

@Composable
fun HourForecastList(hourlyWeather: WeatherData.Result.Hourly?) {
    LazyRow {
        item {
            HourForecastItem(
                Modifier.padding(start = 20.dp, end = 20.dp),
                temperature = hourlyWeather?.temperature?.get(0)?.value ?: 34.toDouble(),
                time = hourlyWeather?.temperature?.get(0)?.datetime?.removeRange(0..10)
                    ?.removeRange(5..10) ?: "12:00",
                skycon = hourlyWeather?.skycon?.get(0)?.value ?: "不知道"
            )
        }
        repeat(11) { index ->
            item {
                HourForecastItem(
                    Modifier.padding(end = 20.dp),
                    temperature = hourlyWeather?.temperature?.get(index + 1)?.value
                        ?: 34.toDouble(),
                    time = hourlyWeather?.temperature?.get(index + 1)?.datetime?.removeRange(0..10)
                        ?.removeRange(5..10) ?: "12:00",
                    skycon = hourlyWeather?.skycon?.get(index + 1)?.value ?: "不知道"
                )
            }
        }
    }
}

@Composable
fun HourForecastItem(
    modifier: Modifier,
    time: String = "12:00",
    temperature: Double = 34.toDouble(),
    skycon: String
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = time,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.test_photo),
            contentDescription = null,
            modifier = Modifier.width(50.dp)
        )
        Text(
            text = skycon,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
        )
        Text(text = "${temperature.toInt()}℃", fontSize = 15.sp)
    }
}

@Composable
fun DailyForecastList(dailyWeather: WeatherData.Result.Daily?) {
    Column {
        DailyForecastItem(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.CenterHorizontally),
            tempMax = dailyWeather?.temperature?.get(0)?.max ?: 12.toDouble(),
            tempMin = dailyWeather?.temperature?.get(0)?.min ?: 23.toDouble(),
            skycon = dailyWeather?.skycon?.get(0)?.value ?: "不知道",
            date = dailyWeather?.temperature?.get(0)?.date?.removeRange(0..4)?.removeRange(5..16)
                ?: "7-20"
        )
        repeat(4) { index ->
            DailyForecastItem(
                modifier = Modifier.padding(bottom = 20.dp),
                tempMax = dailyWeather?.temperature?.get(index + 1)?.max ?: 12.toDouble(),
                tempMin = dailyWeather?.temperature?.get(index + 1)?.min ?: 23.toDouble(),
                skycon = dailyWeather?.skycon?.get(index + 1)?.value ?: "不知道",
                date = dailyWeather?.temperature?.get(index + 1)?.date?.removeRange(0..4)
                    ?.removeRange(5..16) ?: "7-20"
            )
        }
    }
}

@Composable
fun DailyForecastItem(
    modifier: Modifier,
    tempMax: Double,
    tempMin: Double,
    skycon: String,
    date: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(date, fontSize = 15.sp)
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.test_photo),
            contentDescription = null,
            modifier = Modifier
                .height(50.dp)
                .padding(start = 10.dp, end = 10.dp)
        )
        Text(
            "${tempMin.toInt()}℃ ~ ${tempMax.toInt()}℃",
            fontSize = 15.sp,
            modifier = Modifier.padding(end = 5.dp)
        )
        Text(skycon, fontSize = 15.sp)
    }
}

@Composable
fun WeatherPage(
    modifier: Modifier,
    viewModel: MainViewModel
) {
    val weatherData by viewModel.weatherData.collectAsState()
    val weatherResult = weatherData?.result
    val realtime = weatherResult?.realtime
    val hourly = weatherResult?.hourly
    val daily = weatherResult?.daily
    val keypoint = weatherResult?.forecastKeypoint
    // val context = LocalContext.current
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Button(onClick = {
                    viewModel.getPosition()
                    Log.d(TAG, "button: startLocation invoke!")
                }) {
                    Text(text = "get location")
                }
            } /* get location button */
            item {
                Button(onClick = {
                    viewModel.requestNewWeatherData()
                }) {
                    Text(text = "send request")
                }
            } /* send request button */
            item {
                CurrentWeather(
                    realtime?.temperature ?: 32.toDouble(),
                    realtime?.skycon ?: "不知道",
                    keypoint ?: "啥都不知道呢"
                )
            }
            item { HourForecastList(hourly) }
            item { DailyForecastList(daily) }
        }
    }
}