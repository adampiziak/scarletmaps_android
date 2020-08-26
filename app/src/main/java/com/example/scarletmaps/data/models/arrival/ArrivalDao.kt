package com.example.scarletmaps.data.models.arrival

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.stop.Stop

@Dao
interface ArrivalDao {
    @Insert(onConflict = REPLACE)
    fun save(arrival: Arrival)

    @Query("SELECT * FROM arrival WHERE route_id = :routeId AND stop_id IN (:stopIds)")
    fun getSelected(routeId: Int, stopIds: List<Int>): LiveData<List<Arrival>>

    @Query("SELECT * FROM arrival WHERE route_id = :routeId AND stop_id = :stopId")
    fun getPair(routeId: Int, stopId: Int): Arrival?

    @Query("DELETE FROM arrival")
    fun clear()
}