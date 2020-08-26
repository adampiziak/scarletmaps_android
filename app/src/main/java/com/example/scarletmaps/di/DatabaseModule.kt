package com.example.scarletmaps.di

import android.app.Application
import androidx.room.Room
import com.example.scarletmaps.data.local.ScarletMapsDatabase
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.arrival.ArrivalDao
import com.example.scarletmaps.data.models.building.BuildingDao
import com.example.scarletmaps.data.models.route.RouteDao
import com.example.scarletmaps.data.models.stop.StopDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideScarletMapsDatabase(application: Application): ScarletMapsDatabase {
        return Room.databaseBuilder(application, ScarletMapsDatabase::class.java, "ScarletMaps Database").allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideRouteDao(scarletMapsDatabase: ScarletMapsDatabase): RouteDao {
        return scarletMapsDatabase.routeDao()
    }

    @Provides
    @Singleton
    fun provideStopDao(scarletMapsDatabase: ScarletMapsDatabase): StopDao {
        return scarletMapsDatabase.stopDao()
    }

    @Provides
    @Singleton
    fun provideArrivalDao(scarletmapsDatabase: ScarletMapsDatabase): ArrivalDao {
        return scarletmapsDatabase.arrivalDao()
    }

    @Provides
    @Singleton
    fun provideBuildingDao(scarletmapsDatabase: ScarletMapsDatabase): BuildingDao {
        return scarletmapsDatabase.buildingDao()
    }
}