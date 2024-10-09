package com.iucoding.geolocation.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class LocationItemData(
    val latitude: String,
    val longitude: String,
    val altitude: String
)
