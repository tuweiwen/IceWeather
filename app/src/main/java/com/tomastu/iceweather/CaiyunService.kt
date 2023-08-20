package com.tomastu.iceweather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CaiyunService {
    @GET("{lon},{lat}/weather.json")
    fun getWeather(
        @Path("lon") lon: Double,
        @Path("lat") lat: Double,
        @Query("lang") language: String
    ): Call<WeatherData>
}