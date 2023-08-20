package com.tomastu.iceweather

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// dataStore的文件名
private const val WEATHER_DS = "weather"
// dataStore置于顶层，作为Context的拓展函数
val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = WEATHER_DS)

object LocalStorage {
    // 存储数据的key
    val KEY_LAT = stringPreferencesKey("lat")
    val KET_LON = stringPreferencesKey("lon")
//
//    // Key<T>的泛型是指存储在dataStore里面的类型
    fun putValue(dataStore: DataStore<Preferences>, content: Double, key: Preferences.Key<Double>) {
        CoroutineScope(Job()).launch(Dispatchers.IO) {
            dataStore.edit { settings ->
                settings[key] = content
            }
        }
    }
}