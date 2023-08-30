package com.tomastu.iceweather.firstOpen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.amap.api.location.AMapLocationClient
import com.tomastu.iceweather.LocalStorage
import com.tomastu.iceweather.WeatherApplication
import com.tomastu.iceweather.main.MainActivity
import com.tomastu.iceweather.ui.theme.WeatherApiTestTheme
import kotlinx.coroutines.launch

private const val TAG = "FirstOpenActivity"

class FirstOpenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherApiTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val isFirstOpen by LocalStorage.isFirstOpenFlow().collectAsState(initial = null)
                    Log.e(TAG, "isFirstOpen -> ${isFirstOpen}")
                    val context = LocalContext.current
                    if (isFirstOpen != null) {
                        if (isFirstOpen as Boolean) {
                            PrivacyAgreementDialog(
                                onAgreeClick = {
                                    lifecycleScope.launch {
                                        LocalStorage.updateFirstOpenValue(this@FirstOpenActivity)
                                    }
                                    AMapLocationClient.updatePrivacyShow(WeatherApplication.context, true, true)
                                    AMapLocationClient.updatePrivacyAgree(WeatherApplication.context, true)
                                    startActivity(Intent(context, MainActivity::class.java))
                                },
                                onDisAgreeClick = {
                                    finish()
                                })
                        } else {
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                    }
                }
            }
        }
    }
}