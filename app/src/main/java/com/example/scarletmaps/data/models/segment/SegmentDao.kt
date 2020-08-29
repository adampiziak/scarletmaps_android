package com.example.scarletmaps.data.models.segment

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SegmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(segment: Segment)

    @Query("SELECT * FROM segment WHERE id IN (:ids)")
    fun getSelected(ids: List<Int>): LiveData<List<Segment>>

    @Query("SELECT * FROM segment WHERE id IN (:ids)")
    fun getSelectedImmediate(ids: List<Int>): List<Segment>

    @Query("SELECT * FROM segment")
    fun getAll(): List<Segment>
}