package com.iucoding.geolocation.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

fun ComponentActivity.shouldShowLocationPermissionRationale(): Boolean {
    return shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
}

fun ComponentActivity.shouldShowNotificationPermissionRationale(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
}

private fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasLocationPermissions(): Boolean {
    return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
}

fun Context.hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        hasPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else true
}

fun ActivityResultLauncher<Array<String>>.requestRuniquePermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermissions()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermission = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else emptyArray()

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermission + notificationPermission)
        }

        !hasLocationPermission -> launch(locationPermission)
        !hasNotificationPermission -> launch(notificationPermission)
    }
}
