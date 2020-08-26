package com.example.scarletmaps.di

import android.app.Application
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application) : FusedLocationProviderClient {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 30000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        return LocationServices.getFusedLocationProviderClient(application)
    }
}