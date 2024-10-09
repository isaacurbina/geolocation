package com.iucoding.geolocation.presentation

import com.iucoding.geolocation.presentation.model.LocationItemData

data class LocationState(
    val locations: List<LocationItemData> = emptyList(),
    val shouldTrack: Boolean = false,
    val showLocationRationale: Boolean = false,
    val showNotificationRationale: Boolean = false
)
