package com.example.scarletmaps.data.models.building

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Building(@PrimaryKey val id: Int, 
                    val name: String,
                    val area: String,
                    val lng: Double,
                    val lat: Double)