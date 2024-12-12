package com.example.menuadvisor.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LocationTest {
    fun getEmulatorLocation(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            val location = LocationUtils.getCurrentLocation(context)
            location?.let {
                Log.d("EmulatorLocation", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
            } ?: Log.e("EmulatorLocation", "Konum alınamadı")
        }
    }
} 