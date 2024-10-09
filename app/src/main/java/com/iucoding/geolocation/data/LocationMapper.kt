package com.iucoding.geolocation.data

import android.location.Location
import com.iucoding.geolocation.presentation.model.LocationItemData
import kotlin.math.pow
import kotlin.math.round

fun Location.toLocationWithAltitude(): LocationWithAltitude {
    return LocationWithAltitude(
        location = Location(
            lat = latitude,
            long = longitude
        ),
        altitude = altitude
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
    val result = this.roundToDecimals(2)
    return result.toString()
}

private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}
