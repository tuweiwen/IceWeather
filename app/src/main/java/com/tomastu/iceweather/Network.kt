package com.tomastu.iceweather

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "Network.kt"

private val caiyunRetrofit = Retrofit.Builder()
    .baseUrl("https://api.caiyunapp.com/v2.5/qRAzNzgluiHbsTnJ/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

suspend fun <T> Call<T>.await(): T =
    suspendCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: ${response.body()}")
                    continuation.resume(response.body()!!)
                } else {
                    continuation.resumeWithException(RuntimeException("response is null"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }

object Network {
    fun getCaiyunService(): CaiyunService = caiyunRetrofit.create(CaiyunService::class.java)
}
