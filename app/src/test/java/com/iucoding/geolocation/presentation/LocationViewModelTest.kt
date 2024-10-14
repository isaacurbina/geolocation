package com.iucoding.geolocation.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.iucoding.geolocation.MainCoroutineExtension
import com.iucoding.geolocation.data.Location
import com.iucoding.geolocation.data.LocationWithAltitude
import com.iucoding.geolocation.domain.LocationTracker
import com.iucoding.geolocation.presentation.action.GeoLocationAction
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class LocationViewModelTest {

    private lateinit var viewModel: LocationViewModel
    private lateinit var locationTracker: LocationTracker
    private lateinit var currentLocation: MutableStateFlow<LocationWithAltitude?>

    // region common
    @BeforeEach
    fun setUp() {
        locationTracker = mockk(relaxed = true, relaxUnitFun = true)
        currentLocation = MutableStateFlow(null)
        every { locationTracker.currentLocation } returns currentLocation
        initViewModel()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    private fun initViewModel() {
        viewModel = LocationViewModel(locationTracker)
    }
    // endregion

    // region init {} tests
    @Test
    fun `if there are no available locations when viewModel is initialized, initial state has empty list`() =
        runTest {
            viewModel.state.test {
                val emission1 = awaitItem()
                assertThat(emission1.locations.isEmpty())
            }
        }

    @Test
    fun `if there are available locations when viewModel is initialized, initial state has a list`() =
        runTest {
            currentLocation.value = getLocationWithAltitude(1.0)
            initViewModel()
            viewModel.state.test {
                val emission1 = awaitItem()
                assertThat(emission1.locations.isNotEmpty())
            }
        }

    @Test
    fun `state gets updated everytime a new location is received`() =
        runTest {
            viewModel.state.test {
                val emission1 = awaitItem()
                assertThat(emission1.locations.isEmpty())

                currentLocation.value = getLocationWithAltitude(1.0)
                val emission2 = awaitItem()
                assertThat(emission2.locations.size).isEqualTo(1)

                currentLocation.value = getLocationWithAltitude(2.0)
                val emission3 = awaitItem()
                assertThat(emission3.locations.size).isEqualTo(2)
            }
        }
    // endregion

    // region onAction() tests
    @Test
    fun `when DismissRationaleDialog action is received, dialog flags are turned OFF`() = runTest {
        viewModel.onAction(
            GeoLocationAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = false,
                showLocationRationale = true
            )
        )
        viewModel.onAction(
            GeoLocationAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = false,
                showNotificationRationale = true
            )
        )
        viewModel.state.test {
            delay(10)
            val emission1 = awaitItem()
            assertThat(emission1.showLocationRationale).isTrue()
            assertThat(emission1.showNotificationRationale).isTrue()

            viewModel.onAction(GeoLocationAction.DismissRationaleDialog)
            val emission2 = awaitItem()
            assertThat(emission2.showLocationRationale).isFalse()
            assertThat(emission2.showNotificationRationale).isFalse()
        }
    }

    @Test
    fun `when StartTracking action is received, should track changes to ON`() = runTest {
        viewModel.state.test {
            val emission1 = awaitItem()
            assertThat(emission1.shouldTrack).isFalse()

            viewModel.onAction(GeoLocationAction.StartTracking)

            val emission2 = awaitItem()
            assertThat(emission2.shouldTrack).isTrue()
        }
    }

    @Test
    fun `when StopTracking action is received, should track changes to ON`() = runTest {
        viewModel.state.test {
            val emission1 = awaitItem()
            assertThat(emission1.shouldTrack).isFalse()

            viewModel.onAction(GeoLocationAction.StartTracking)

            val emission2 = awaitItem()
            assertThat(emission2.shouldTrack).isTrue()

            viewModel.onAction(GeoLocationAction.StopTracking)

            val emission3 = awaitItem()
            assertThat(emission3.shouldTrack).isFalse()
        }
    }
    // endregion

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()

        private fun getLocationWithAltitude(value: Double) = LocationWithAltitude(
            location = Location(value, value),
            altitude = value
        )
    }
}
