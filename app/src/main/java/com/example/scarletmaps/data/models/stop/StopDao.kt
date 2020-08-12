package com.example.scarletmaps.data.models.stop

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.scarletmaps.data.models.stop.Stop

@Dao
interface StopDao {
    @Insert(onConflict = REPLACE)
    fun save(stop: Stop)

    @Query("SELECT * FROM stop WHERE id = :stopId")
    fun load(stopId: Int): LiveData<Stop>

    @Query("SELECT * FROM stop")
    fun getAll(): LiveData<List<Stop>>

    @Query("SELECT * FROM stop WHERE id IN (:stopIds) ORDER BY :stopIds")
    fun getSelected(stopIds: List<Int>): List<Stop>
}