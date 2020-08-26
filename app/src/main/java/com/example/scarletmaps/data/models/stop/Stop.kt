package com.example.scarletmaps.data.models.stop

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.scarletmaps.data.models.Location

@Entity
data class Stop(@PrimaryKey val id: Int,
                 val name: String,
                 val area: String,
                 val location: Location,
                 val active: Boolean,
                 val routes: List<Int>)
