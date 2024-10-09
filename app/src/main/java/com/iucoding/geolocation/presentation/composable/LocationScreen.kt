package com.iucoding.geolocation.presentation.composable

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iucoding.geolocation.R
import com.iucoding.geolocation.presentation.LocationState
import com.iucoding.geolocation.presentation.action.GeoLocationAction
import com.iucoding.geolocation.presentation.model.LocationItemData
import com.iucoding.geolocation.presentation.util.hasLocationPermissions
import com.iucoding.geolocation.presentation.util.hasNotificationPermission
import com.iucoding.geolocation.presentation.util.requestRuniquePermissions
import com.iucoding.geolocation.presentation.util.shouldShowLocationPermissionRationale
import com.iucoding.geolocation.presentation.util.shouldShowNotificationPermissionRationale
import kotlinx.collections.immutable.persistentListOf

@Composable
fun LocationScreen(
    state: LocationState,
    onAction: (GeoLocationAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val hasCourseLocationPermission = it[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val hasFineLocationPermission = it[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            it[Manifest.permission.POST_NOTIFICATIONS] == true
        } else true

        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            GeoLocationAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCourseLocationPermission && hasFineLocationPermission,
                showLocationRationale = showLocationRationale
            )
        )

        onAction(
            GeoLocationAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotificationPermission,
                showNotificationRationale = showNotificationRationale
            )
        )
    }

    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            GeoLocationAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermissions(),
                showLocationRationale = showLocationRationale
            )
        )

        onAction(
            GeoLocationAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )

        if (!showLocationRationale && !showNotificationRationale) {
            permissionLauncher.requestRuniquePermissions(context)
        }
    }

    if (state.showLocationRationale || state.showNotificationRationale) {
        LocationDialog(
            title = stringResource(id = R.string.permission_required),
            onDismiss = {
                /* Normal dismissing not allowed for permissions */
            },
            description = when {
                state.showLocationRationale && state.showNotificationRationale ->
                    stringResource(id = R.string.location_notification_rationale)

                state.showLocationRationale ->
                    stringResource(id = R.string.location_rationale)

                else -> stringResource(id = R.string.notification_rationale)
            },
            primaryButton = {
                Button(
                    onClick = {
                        onAction(GeoLocationAction.DismissRationaleDialog)
                        permissionLauncher.requestRuniquePermissions(context)
                    }
                ) {
                    Text(text = stringResource(id = R.string.ok))
                }
            })
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwitchLabel(
                checked = state.shouldTrack,
                onAction = onAction
            )
        }
        LocationList(
            locations = state.locations,
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun SwitchLabel(
    checked: Boolean,
    onAction: (GeoLocationAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = { observe ->
            val action = if (observe) {
                GeoLocationAction.StartTracking
            } else GeoLocationAction.StopTracking
            onAction(action)
        },
        modifier = modifier
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
        text = stringResource(R.string.observe_location),
        fontSize = 18.sp
    )
}

@Composable
private fun LocationList(
    locations: List<LocationItemData>,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(locations) {
            LocationItem(
                locationItemData = it,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun LocationScreenPreview() {
    LocationScreen(
        state = LocationState(
            locations = persistentListOf(
                LocationItemData(
                    latitude = "1.0",
                    longitude = "2.0",
                    altitude = "-3.0"
                )
            )
        ),
        onAction = {}
    )
}
