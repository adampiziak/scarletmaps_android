package com.example.scarletmaps.data.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.time.Instant.now
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUtils @Inject constructor(application: Application) {
    private val prefs: SharedPreferences =
        application.getSharedPreferences("scarletmaps_state", Context.MODE_PRIVATE)
    private var routeListUpdated: Long
    private var stopListUpdated: Long
    private var buildingListUpdated: Long
    private var segmentListUpdated: Long

    init {
        routeListUpdated = prefs.getLong("route_list_updated", 0)
        stopListUpdated = prefs.getLong("stop_list_updated", 0)
        buildingListUpdated = prefs.getLong("building_list_updated", 0)
        segmentListUpdated = prefs.getLong("segment_list_updated", 0)
    }

    fun shouldRefreshRouteList(): Boolean {
        val timeout = TimeUnit.MINUTES.toMillis(5)
        val now = now().toEpochMilli()

        return now - routeListUpdated > timeout
    }

    fun shouldRefreshStopList(): Boolean {
        val timeout = TimeUnit.MINUTES.toMillis(5)
        val now = now().toEpochMilli()

        return now - stopListUpdated > timeout
    }

    fun shouldRefreshBuildingList(): Boolean {
        val timeout = TimeUnit.MINUTES.toMillis(60*48)
        val now = now().toEpochMilli()

        return now - buildingListUpdated > timeout
    }

    fun shouldRefreshSegmentList(): Boolean {
        val timeout = TimeUnit.MINUTES.toMillis(5)
        val now = now().toEpochMilli()

        return now - segmentListUpdated > timeout
    }

    fun setBuildingListUpdateTime() {
        buildingListUpdated = now().toEpochMilli()
        prefs.edit().putLong("building_list_updated", buildingListUpdated).apply()
    }

    fun setRouteListUpdateTime() {
        routeListUpdated = now().toEpochMilli()
        prefs.edit().putLong("route_list_updated", routeListUpdated).apply()
    }

    fun setStopListUpdateTime() {
        stopListUpdated = now().toEpochMilli()
        prefs.edit().putLong("stop_list_updated", stopListUpdated).apply()
    }

    fun setSegmentListUpdateTime() {
        segmentListUpdated = now().toEpochMilli()
        prefs.edit().putLong("segment_list_updated", segmentListUpdated).apply()
    }
}