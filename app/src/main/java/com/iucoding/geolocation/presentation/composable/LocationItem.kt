package com.iucoding.geolocation.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iucoding.geolocation.R
import com.iucoding.geolocation.presentation.model.LocationItemData

@Composable
fun LocationItem(
    locationItemData: LocationItemData,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Text(
            text = stringResource(
                R.string.coordinates,
                locationItemData.latitude,
                locationItemData.longitude,
                locationItemData.altitude
            ),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun LocationItemPreview() {
    LocationItem(
        locationItemData = LocationItemData(
            latitude = "1.0",
            longitude = "2.0",
            altitude = "-3.0"
        )
    )
}
