package com.example.scarletmaps.data.remote

import com.example.scarletmaps.data.models.Vehicle
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.building.Building
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.segment.Segment
import com.example.scarletmaps.data.models.stop.Stop
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ScarletMapsService {
    @GET("routes")
    fun getRoutes(): Call<List<Route>>

    @GET("stops")
    fun getStops(): Call<List<Stop>>

    @GET("route_arrivals/{id}")
    fun routeArrivals(@Path("id") id: Int): Call<List<Arrival>>

    @GET("stop_arrivals/{id}")
    fun stopArrivals(@Path("id") id: Int): Call<List<Arrival>>

    @GET("buildings")
    fun buildings(): Call<List<Building>>

    @GET("segments")
    fun segments(): Call<List<Segment>>

    @GET("route_vehicles/{id}")
    fun vehicles(@Path("id") id: Int): Call<List<Vehicle>>
}