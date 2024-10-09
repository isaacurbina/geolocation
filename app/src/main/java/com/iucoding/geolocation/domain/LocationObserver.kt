package com.iucoding.geolocation.domain

import com.iucoding.geolocation.data.LocationWithAltitude
import kotlinx.coroutines.flow.Flow

interface LocationObserver {
    fun observeLocation(interval: Long): Flow<LocationWithAltitude>
}
