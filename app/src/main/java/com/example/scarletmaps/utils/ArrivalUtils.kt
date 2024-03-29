package com.example.scarletmaps.utils

import java.time.Instant
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class ArrivalUtils {
    fun createSingleArrivalMessage(arrival: List<Long>): String {
        val now: Long = Instant.now().toEpochMilli()
        arrival.forEach {
            val difference = it - now
            if (difference < 0) {
                return@forEach
            }
            val timeTo = ceil((difference.toDouble() / 1000) / 60).toInt()

            return "$timeTo min"
        }

        return "..."
    }

    fun createArrivalsMessage(arrival: List<Long>): String {
        val now: Long = Instant.now().toEpochMilli()
        var validTimes = ArrayList<Int>()
        arrival.forEach {
            val difference = it - now
            if (difference < 0) {
                return@forEach
            }
            validTimes.add(ceil((difference.toDouble() / 1000) / 60).toInt())
        }

        if (validTimes.isNotEmpty()) {
            return validTimes.take(3).joinToString { " $it min" }
        }

        return "no arrivals"
    }

    fun getMinutesToNextBus(arrival: List<Long>): Int {
        val now: Long = Instant.now().toEpochMilli()
        arrival.forEach {
            val difference = it - now
            if (difference < 0) {
                return@forEach
            }

            return ceil((difference.toDouble() / 1000) / 60).toInt()
        }

        return Int.MAX_VALUE
    }
}