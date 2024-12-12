package com.example.menuadvisor.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object LocationUtils {
    
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun getCurrentLocation(context: Context): Location? = suspendCancellableCoroutine { continuation ->
        try {
            if (!hasLocationPermission(context)) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateDistanceMeters(10f)
                .setMaxUpdates(1)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        Log.d("EmulatorLocation", "Gerçek zamanlı konum alındı")
                        fusedLocationClient.removeLocationUpdates(this)
                        continuation.resume(location)
                    } else {
                        Log.e("EmulatorLocation", "Konum null geldi")
                        fusedLocationClient.removeLocationUpdates(this)
                        continuation.resume(null)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                context.mainLooper
            ).addOnFailureListener { e ->
                Log.e("EmulatorLocation", "Konum güncellemesi başlatılamadı: ${e.message}")
                continuation.resume(null)
            }

        } catch (e: Exception) {
            Log.e("EmulatorLocation", "Hata oluştu: ${e.message}")
            continuation.resume(null)
        }
    }

    fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude,
            results
        )
        // Mesafeyi metre cinsinden alıp kilometreye çeviriyoruz
        return results[0] / 1000
    }
} 