package com.example.scarletmaps.data.models.segment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Segment(@PrimaryKey val id: Int, val path: String)