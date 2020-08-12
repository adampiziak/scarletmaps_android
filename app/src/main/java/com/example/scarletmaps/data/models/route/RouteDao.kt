package com.example.scarletmaps.data.models.route

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.scarletmaps.data.models.route.Route

@Dao
interface RouteDao {
    @Insert(onConflict = REPLACE)
    fun save(route: Route)

    @Query("SELECT * FROM route WHERE id = :routeId")
    fun get(routeId: Int): Route

    @Query("SELECT * FROM route WHERE id = :id")
    fun getObservable(id: Int): LiveData<Route>

    @Query("SELECT * FROM route")
    fun getAll(): LiveData<List<Route>>

    @Query("SELECT * FROM route")
    fun getAllImmediate(): List<Route>
}