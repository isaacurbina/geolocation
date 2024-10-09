package com.iucoding.geolocation.data

import android.location.Location
import android.os.Build
import com.iucoding.geolocation.presentation.model.LocationItemData
import kotlin.math.pow
import kotlin.math.round

fun Location.toLocationWithAltitude(): LocationWithAltitude {
    return LocationWithAltitude(
        location = Location(
            lat = latitude,
            long = longitude
        ),
        altitude = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            mslAltitudeMeters
        } else altitude
    )
}

fun LocationWithAltitude.toLocationItemData(): LocationItemData {
    return LocationItemData(
        latitude = location.lat.formatted(),
        longitude = location.long.formatted(),
        altitude = altitude.formatted()
    )
}

private fun Double.formatted(): String {
    val result = this.roundToDecimals(4)
    return result.toString()
}

private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}
