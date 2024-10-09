package com.iucoding.geolocation.presentation.action

sealed interface GeoLocationAction {

    data object StartTracking : GeoLocationAction
    data object StopTracking : GeoLocationAction
    data object OnBackClick : GeoLocationAction
    data object DismissRationaleDialog : GeoLocationAction

    data class SubmitLocationPermissionInfo(
        val acceptedLocationPermission: Boolean,
        val showLocationRationale: Boolean
    ) : GeoLocationAction

    data class SubmitNotificationPermissionInfo(
        val acceptedNotificationPermission: Boolean,
        val showNotificationRationale: Boolean
    ) : GeoLocationAction
}
