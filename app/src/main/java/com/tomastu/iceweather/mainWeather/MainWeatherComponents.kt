package com.tomastu.iceweather.mainWeather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomastu.iceweather.WeatherData
import com.tomastu.iceweather.jsonUtils.JsonName2Resource

@Composable
fun CurrentWeather(
    modifier: Modifier = Modifier,
    temperature: Double,
    weather: String,
    weatherIconId: Int,
    keypoint: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 40.dp, bottom = 40.dp)
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
fun HourForecastList(
    modifier: Modifier = Modifier,
    hourlyWeather: WeatherData.Result.Hourly?
) {
    LazyRow(
        modifier = modifier,
    ) {
        val temperatureList = mutableListOf<Double>()
        if (hourlyWeather != null) {
            temperatureList.run {
                repeat(12) { index ->
                    add(hourlyWeather.temperature[index].value)
                }
                sort()
            }
        } else {
            repeat(12) { temperatureList.add(34.0) }
        }
        repeat(12) { index ->
            item {
                HourlyForecastItemWithLine(
                    modifier = Modifier
                        .width(140.dp),
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
                    ),
                    highestTemp = temperatureList.max(),
                    lowestTemp = temperatureList.min(),
                    previousTemp = if (index == 0) null else hourlyWeather?.temperature?.get(index - 1)?.value
                        ?: 34.toDouble(),
                    nextTemp = if (index == 11) null else hourlyWeather?.temperature?.get(index + 1)?.value
                        ?: 34.toDouble(),
                )
            }
        }
    }
}

@Composable
fun HourForecastItem(
    modifier: Modifier = Modifier,
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
fun DailyForecastList(
    modifier: Modifier = Modifier,
    dailyWeather: WeatherData.Result.Daily?
) {
    Column(modifier = modifier.padding(top = 40.dp)) {
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
    modifier: Modifier = Modifier,
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
                .aspectRatio(1f)
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
fun HourlyForecastItemWithLine(
    modifier: Modifier = Modifier,
    time: String,
    temperature: Double,
    weather: String,
    weatherIconId: Int,
    highestTemp: Double,
    lowestTemp: Double,
    previousTemp: Double?,
    nextTemp: Double?,
) {
    var width by remember { mutableStateOf(0.dp) }
    var height by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current.density

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Canvas(
            modifier = Modifier
                .height(100.dp)
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    width = (coordinates.size.width / density).dp
                    height = (coordinates.size.height / density).dp
                }
        ) {
            val path = Path()
            // 当前天气占高度的百分比
            val currentPercentage = (temperature - lowestTemp) / (highestTemp - lowestTemp)
            if (previousTemp != null) {
                // 左边缘起始点对应的数值
                val leftPointTemp = (previousTemp + temperature) / 2
                // 左边缘起始点占高度的百分比
                val leftPercentage = (leftPointTemp - lowestTemp) / (highestTemp - lowestTemp)
                with(path) {
                    // 移动到左边缘的点
                    moveTo(0F, (height.toPx() * (1 - leftPercentage)).toFloat())
                    // 画线到中间的点
                    lineTo(width.toPx() / 2, (height.toPx() * (1 - currentPercentage)).toFloat())
                }
            }

            if (nextTemp != null) {
                // 右边缘起始点对应的数值
                val rightPointTemp = (nextTemp + temperature) / 2
                // 右边缘起始点占高度的百分比
                val rightPercentage = (rightPointTemp - lowestTemp) / (highestTemp - lowestTemp)
                with(path) {
                    // 移动到中间的点
                    moveTo(width.toPx() / 2, (height.toPx() * (1 - currentPercentage)).toFloat())
                    // 画线到右边缘的点
                    lineTo(width.toPx(), (height.toPx() * (1 - rightPercentage)).toFloat())
                }
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 7f)
            )

            drawCircle(
                color = Color.Black,
                radius = 10f,
                center = Offset(
                    width.toPx() / 2,
                    (height.toPx() * (1 - currentPercentage)).toFloat()
                )
            )
        }

        Text(text = "${temperature.toInt()}℃", fontSize = 15.sp)
    }
}