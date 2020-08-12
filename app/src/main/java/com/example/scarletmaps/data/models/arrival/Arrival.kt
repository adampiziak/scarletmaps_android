package com.example.scarletmaps.data.models.arrival

import androidx.room.Entity

@Entity(primaryKeys = ["route_id", "stop_id"])
data class Arrival(
    val route_id: Int,
    val stop_id: Int,
    val arrivals: List<Long>
)