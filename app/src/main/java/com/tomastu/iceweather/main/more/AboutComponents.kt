package com.tomastu.iceweather.main.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AboutPage(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(Color.Blue)) {
        Text(text = "about page")
    }
}