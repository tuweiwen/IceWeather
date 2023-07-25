package com.example.weatherapitest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CaiyunService {
    @GET("{lon},{lat}/weather.json")
    fun getWeather(@Path("lon") lon: Double, @Path("lat") lat: Double,): Call<WeatherData>
}