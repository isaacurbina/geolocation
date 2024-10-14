package com.iucoding.geolocation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iucoding.geolocation.data.toLocationItemData
import com.iucoding.geolocation.domain.LocationTracker
import com.iucoding.geolocation.presentation.action.GeoLocationAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _state = MutableStateFlow(LocationState())
    val state = _state.asStateFlow()

    private val hasLocationPermission = MutableStateFlow(false)

    init {
        locationTracker.currentLocation.onEach { location ->
            location?.let {
                val updatedLocations = state.value.locations.toMutableList().apply {
                    add(0, it.toLocationItemData())
                }
                _state.update { it.copy(locations = updatedLocations.toImmutableList()) }
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: GeoLocationAction) {
        when (action) {
            GeoLocationAction.DismissRationaleDialog -> {
                _state.update {
                    it.copy(
                        showNotificationRationale = false,
                        showLocationRationale = false
                    )
                }
            }

            GeoLocationAction.StartTracking -> {
                locationTracker.startObservingLocation()
                _state.update {
                    it.copy(shouldTrack = true)
                }
            }

            GeoLocationAction.StopTracking,
            GeoLocationAction.OnBackClick -> {
                locationTracker.stopObservingLocation()
                _state.update {
                    it.copy(shouldTrack = false)
                }
            }

            is GeoLocationAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.acceptedLocationPermission
                _state.update {
                    it.copy(showLocationRationale = action.showLocationRationale)
                }
            }

            is GeoLocationAction.SubmitNotificationPermissionInfo -> {
                _state.update {
                    it.copy(showNotificationRationale = action.showNotificationRationale)
                }
            }
        }
    }
}
