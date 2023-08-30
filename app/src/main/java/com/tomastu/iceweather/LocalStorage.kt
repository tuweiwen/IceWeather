package com.tomastu.iceweather

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private const val TAG = "LocalStorage"
// dataStore的文件名
private const val GLOBAL_CONFIG = "global_config"

// 存储数据的 key
val FIRST_OPEN = booleanPreferencesKey("first_open")

// dataStore 置于顶层，作为 Context 的拓展函数
val Context.globalConfigDataStore: DataStore<Preferences> by preferencesDataStore(name = GLOBAL_CONFIG)

object LocalStorage {
    fun isFirstOpenFlow() = WeatherApplication.context.globalConfigDataStore.data.map { preferences ->
        preferences[FIRST_OPEN] ?: true
    }

    suspend fun updateFirstOpenValue(context: Context) {
        context.globalConfigDataStore.edit { preferences ->
            preferences[FIRST_OPEN] = false
            Log.e(TAG, "updateFirstOpenValue: ${preferences[FIRST_OPEN]}")
        }
    }
}