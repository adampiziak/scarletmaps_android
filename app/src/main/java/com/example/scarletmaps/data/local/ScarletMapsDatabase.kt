package com.example.scarletmaps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.scarletmaps.data.models.Converters
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.arrival.ArrivalDao
import com.example.scarletmaps.data.models.building.Building
import com.example.scarletmaps.data.models.building.BuildingDao
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.route.RouteDao
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.data.models.stop.StopDao

@Database(entities = [Route::class, Stop::class, Arrival::class, Building::class], version = 1)
@TypeConverters(Converters::class)
abstract class ScarletMapsDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao
    abstract fun stopDao(): StopDao
    abstract fun arrivalDao(): ArrivalDao
    abstract fun buildingDao(): BuildingDao
}