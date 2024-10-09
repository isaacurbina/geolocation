package com.iucoding.geolocation.presentation

import androidx.compose.runtime.Immutable
import com.iucoding.geolocation.presentation.model.LocationItemData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class LocationState(
    val locations: ImmutableList<LocationItemData> = persistentListOf(),
    val shouldTrack: Boolean = false,
    val showLocationRationale: Boolean = false,
    val showNotificationRationale: Boolean = false
)
