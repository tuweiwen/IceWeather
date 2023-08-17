package com.tomastu.iceweather.jsonUtils

import com.tomastu.iceweather.R
import com.tomastu.iceweather.WeatherApplication

object JsonName2Resource {
    fun transform2String(jsonName: String) = when (jsonName) {
        "CLEAR_DAY", "CLEAR_NIGHT" -> WeatherApplication.context.resources.getString(R.string.CLEAR_DAY)
        "PARTLY_CLOUDY_DAY", "PARTLY_CLOUDY_NIGHT" -> WeatherApplication.context.resources.getString(
            R.string.PARTLY_CLOUDY_DAY
        )

        "CLOUDY" -> WeatherApplication.context.resources.getString(R.string.CLOUDY)
        "LIGHT_HAZE" -> WeatherApplication.context.resources.getString(R.string.LIGHT_HAZE)
        "MODERATE_HAZE" -> WeatherApplication.context.resources.getString(R.string.MODERATE_HAZE)
        "HEAVY_HAZE" -> WeatherApplication.context.resources.getString(R.string.HEAVY_HAZE)
        "LIGHT_RAIN" -> WeatherApplication.context.resources.getString(R.string.LIGHT_RAIN)
        "MODERATE_RAIN" -> WeatherApplication.context.resources.getString(R.string.MODERATE_RAIN)
        "HEAVY_RAIN" -> WeatherApplication.context.resources.getString(R.string.HEAVY_RAIN)
        "STORM_RAIN" -> WeatherApplication.context.resources.getString(R.string.STORM_RAIN)
        "FOG" -> WeatherApplication.context.resources.getString(R.string.FOG)
        "LIGHT_SNOW" -> WeatherApplication.context.resources.getString(R.string.LIGHT_SNOW)
        "MODERATE_SNOW" -> WeatherApplication.context.resources.getString(R.string.MODERATE_SNOW)
        "HEAVY_SNOW" -> WeatherApplication.context.resources.getString(R.string.HEAVY_SNOW)
        "STORM_SNOW" -> WeatherApplication.context.resources.getString(R.string.STORM_SNOW)
        "DUST" -> WeatherApplication.context.resources.getString(R.string.DUST)
        "SAND" -> WeatherApplication.context.resources.getString(R.string.SAND)
        "WIND" -> WeatherApplication.context.resources.getString(R.string.WIND)
        "weather_unknow" -> WeatherApplication.context.resources.getString(R.string.weather_unknow)

        else -> jsonName
    }

    fun transform2DrawableId(jsonName: String) = when (jsonName) {
        "CLEAR_DAY" -> R.drawable.clear_day
        "CLEAR_NIGHT" -> R.drawable.clear_night
        "CLOUDY" -> R.drawable.cloud
        "PARTLY_CLOUDY_DAY" -> R.drawable.party_cloudy_day
        "PARTLY_CLOUDY_NIGHT" -> R.drawable.party_cloudy_night
        "LIGHT_HAZE", "MODERATE_HAZE", "HEAVY_HAZE", "DUST", "SAND" -> R.drawable.mist
        "LIGHT_RAIN", "MODERATE_RAIN", "HEAVY_RAIN", "STORM_RAIN" -> R.drawable.rain
        "FOG" -> R.drawable.fog
        "LIGHT_SNOW", "MODERATE_SNOW", "HEAVY_SNOW", "STORM_SNOW" -> R.drawable.snow
        "WIND" -> R.drawable.storm

        else -> throw IllegalArgumentException("")
    }
}
