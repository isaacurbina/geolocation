package com.iucoding.geolocation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.iucoding.geolocation.domain.AndroidLocationObserver
import com.iucoding.geolocation.domain.LocationTracker
import com.iucoding.geolocation.presentation.LocationViewModel
import com.iucoding.geolocation.presentation.composable.LocationScreen
import com.iucoding.geolocation.ui.theme.GeoLocationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoLocationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val observer = AndroidLocationObserver(this)
                    val tracker = LocationTracker(observer)
                    val viewModel = LocationViewModel(tracker)
                    LocationScreen(
                        state = viewModel.state,
                        onAction = viewModel::onAction,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
