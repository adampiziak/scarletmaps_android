package com.example.scarletmaps.di

import com.example.scarletmaps.data.remote.ScarletMapsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    fun provideScarletMapsService() : ScarletMapsService {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.11")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScarletMapsService::class.java)
    }

}