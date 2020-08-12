package com.example.scarletmaps.data.models.route

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route(@PrimaryKey val id: Int,
                 val name: String,
                 val active: Boolean,
                 val areas: List<String>,
                 val stops: List<Int>)
