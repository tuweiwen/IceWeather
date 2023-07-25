package com.example.weatherapitest

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.amap.api.location.AMapLocationClient.updatePrivacyAgree
import com.amap.api.location.AMapLocationClient.updatePrivacyShow
import com.example.weatherapitest.ui.theme.WeatherApiTestTheme

private const val TAG = "MainActivity"

@SuppressLint("StaticFieldLeak")

class MainActivity : ComponentActivity() {
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

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java].apply {
            getPosition()
//            requestNewWeatherData()
        }

        // 权限检查
        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            // permission granted -> do get position feature here
            Log.d(TAG, "onCreate: get location permission!")
        } else {
            // permission denied -> use this to request location permission,
            //                      and we will expaine this later in artical
            permissionRequestLauncher.launch("android.permission.ACCESS_FINE_LOCATION")
            Log.d(TAG, "onCreate: don't get location permission!")
        }

        setContent {
            WeatherApiTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherPage(modifier = Modifier.fillMaxWidth(), viewModel = viewModel)
                }
            }
        }


        viewModel.requestNewWeatherData()
    }
}

@Composable
fun CurrentWeather(temperature: Double, weather: String, keypoint: String) {
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

// todo("漏了skycon没写")
@Composable
fun HourForecastList(hourlyWeather: WeatherData.Result.Hourly?) {
    LazyRow {
        item {
            HourForecastItem(
                Modifier.padding(start = 20.dp, end = 20.dp),
                temperature = hourlyWeather?.temperature?.get(0)?.value ?: 34.toDouble(),
                time = hourlyWeather?.temperature?.get(0)?.datetime?.removeRange(0..10)
                    ?.removeRange(5..10) ?: "12:00"
            )
        }
        repeat(11) { index ->
            item {
                HourForecastItem(
                    Modifier.padding(end = 20.dp),
                    temperature = hourlyWeather?.temperature?.get(index + 1)?.value
                        ?: 34.toDouble(),
                    time = hourlyWeather?.temperature?.get(index + 1)?.datetime?.removeRange(0..10)
                        ?.removeRange(5..10) ?: "12:00"
                )
            }
        }
    }
}

@Composable
fun HourForecastItem(
    modifier: Modifier,
    time: String = "12:00",
    temperature: Double = 34.toDouble()
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = time, fontSize = 15.sp, modifier = Modifier.padding(bottom = 5.dp))
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.test_photo),
            contentDescription = null,
            modifier = Modifier.width(50.dp)
        )
        Text("${temperature.toInt()}℃", fontSize = 15.sp, modifier = Modifier.padding(top = 5.dp))
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
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(modifier: Modifier, viewModel: MainViewModel) {
    val weatherResult = viewModel.weatherData.collectAsState().value?.result
    val realtime = weatherResult?.realtime
    val hourly = weatherResult?.hourly
    val daily = weatherResult?.daily
    val keypoint = weatherResult?.forecastKeypoint
    // val context = LocalContext.current
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Button(onClick = {
                viewModel.locationClient.startLocation()
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