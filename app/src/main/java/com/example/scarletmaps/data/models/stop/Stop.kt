package com.example.scarletmaps.data.models.stop

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stop(@PrimaryKey val id: Int,
                 val name: String,
                 val area: String,
                 val active: Boolean,
                 val routes: List<Int>)
