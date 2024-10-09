package com.iucoding.geolocation.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iucoding.geolocation.data.toLocationItemData
import com.iucoding.geolocation.domain.LocationTracker
import com.iucoding.geolocation.presentation.action.GeoLocationAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationViewModel(
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val hasLocationPermission = MutableStateFlow(false)

    var state by mutableStateOf(LocationState())
        private set

    init {
        hasLocationPermission.onEach {
            if (it) {
                locationTracker.startObservingLocation()
            } else locationTracker.stopObservingLocation()
        }.launchIn(viewModelScope)

        locationTracker.currentLocation.onEach { location ->
            location?.let {
                val updatedLocations = state.locations.toMutableList().apply {
                    add(0, it.toLocationItemData())
                }
                state = state.copy(locations = updatedLocations)
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: GeoLocationAction) {
        when (action) {
            GeoLocationAction.DismissRationaleDialog -> {
                state = state.copy(
                    showNotificationRationale = false,
                    showLocationRationale = false
                )
            }

            GeoLocationAction.StartTracking -> {
                state = state.copy(
                    shouldTrack = true
                )
            }

            GeoLocationAction.StopTracking,
            GeoLocationAction.OnBackClick -> {
                state = state.copy(
                    shouldTrack = false
                )
            }

            is GeoLocationAction.SubmitLocationPermissionInfo -> {
                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }

            is GeoLocationAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationRationale = action.showNotificationRationale
                )
            }
        }
    }
}