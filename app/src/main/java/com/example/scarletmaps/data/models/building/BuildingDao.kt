package com.example.scarletmaps.data.models.building

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface BuildingDao {
    @Insert(onConflict = REPLACE)
    fun save(building: Building)

    @Query("SELECT * FROM building")
    fun allImmediate(): List<Building>

    @Query("SELECT * FROM building")
    fun all(): LiveData<List<Building>>
}