package com.iucoding.geolocation.domain


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.iucoding.geolocation.data.LocationWithAltitude
import com.iucoding.geolocation.data.toLocationWithAltitude
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidLocationObserver(
    private val context: Context
) : LocationObserver {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    override fun observeLocation(interval: Long): Flow<LocationWithAltitude> {
        return callbackFlow {
            (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)?.let { locationManager ->
                var isGpsEnabled = false
                var isNetworkEnabled = false
                while (!isGpsEnabled && !isNetworkEnabled) {
                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    isNetworkEnabled =
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                    if (!isGpsEnabled && !isNetworkEnabled) {
                        delay(3000L)
                    }
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    close()
                    return@callbackFlow
                }
                client.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        Log.i("AndroidLocationObserver", "received location: $location")
                        trySend(it.toLocationWithAltitude())
                    }
                }
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                    .build()
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.lastOrNull()?.let {
                            trySend(it.toLocationWithAltitude())
                        }
                    }
                }
                client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

                awaitClose {
                    client.removeLocationUpdates(locationCallback)
                }
            }
        }
    }
}
