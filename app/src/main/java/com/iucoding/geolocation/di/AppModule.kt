package com.iucoding.geolocation.di

import android.content.Context
import com.iucoding.geolocation.domain.AndroidLocationObserver
import com.iucoding.geolocation.domain.LocationObserver
import com.iucoding.geolocation.domain.LocationTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocationObserver(@ApplicationContext context: Context): LocationObserver {
        return AndroidLocationObserver(context)
    }

    @Provides
    @Singleton
    fun provideLocationTracker(observer: LocationObserver): LocationTracker {
        return LocationTracker(observer)
    }
}
