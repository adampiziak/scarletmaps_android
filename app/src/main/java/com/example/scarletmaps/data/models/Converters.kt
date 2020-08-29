package com.example.scarletmaps.data.models

import androidx.room.TypeConverter
import com.example.scarletmaps.data.models.location.Location
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.collections.ArrayList

class Converters {
    private val converter = Gson();

    @TypeConverter
    fun fromLocation(value: Location): String {
        return converter.toJson(value)
    }


    @TypeConverter
    fun toLocation(value: String): Location {
        if (value == null) {
            return Location(0.0, 0.0)
        }

        val locationType: Type = object: TypeToken<Location>() {}.type
        return converter.fromJson(value, locationType)
    }

    @TypeConverter
    fun fromList(value: List<Int>): String {
        return converter.toJson(value)
    }

    @TypeConverter
    fun fromString(value: String?): List<Int> {
        if (value == null) {
            return ArrayList<Int>()
        }

        val listType: Type = object: TypeToken<List<Int>>() {}.type
        return converter.fromJson(value, listType)
    }

    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return converter.toJson(value)
    }

    @TypeConverter
    fun fromLongString(value: String?): List<Long> {
        if (value == null) {
            return ArrayList<Long>()
        }

        val listType: Type = object: TypeToken<List<Long>>() {}.type
        return converter.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return converter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value == null) {
            return ArrayList<String>()
        }

        val listType: Type = object : TypeToken<List<String>>() {}.type
        return converter.fromJson(value, listType)
    }
}