package com.example.scarletmaps.ui.nearby


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.utils.ArrivalUtils
import com.example.scarletmaps.utils.TextUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import javax.inject.Inject


@AndroidEntryPoint
class Nearby : Fragment() {
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: NearbyViewModel by viewModels()
    private val location: MutableLiveData<ArrayList<Double>> = MutableLiveData(
        arrayListOf(
            0.0,
            0.0
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.nearby, container, false)


        // Setup Epoxy
        val epoxyRecyclerView = v.findViewById<EpoxyRecyclerView>(R.id.nearby_epoxy)
        val controller = NearbyController()
        epoxyRecyclerView.setController(controller)
        epoxyRecyclerView.itemAnimator = FadeInAnimator().apply {
            addDuration = 150
            removeDuration = 50
        }

        var items = 0
        // Update controller when location changes
        location.observe(viewLifecycleOwner, Observer<ArrayList<Double>> { currentLocation ->

            // Find buildings nearer than 800m to user
            val nearbyBuildingList: List<NearbyPlace> = viewModel.buildings.map {
                val distance = FloatArray(5)
                Location.distanceBetween(
                    currentLocation[0],
                    currentLocation[1],
                    it.lat,
                    it.lng,
                    distance
                )
                NearbyPlace(it.id, it.name, TextUtils().capitalizeWords(it.area), distance[0])
            }.filter {
                it.distance < 800
            }.sortedBy { it.distance }

            //  Find active stops nearer than 500m to user
            val nearbyStopList = viewModel.stops.map { stop ->
                val distance = FloatArray(5)
                Location.distanceBetween(
                    currentLocation[0],
                    currentLocation[1],
                    stop.location.lat,
                    stop.location.lng,
                    distance
                )
                Pair(stop, distance[0])
            }.filter {
                it.second < 500 && it.first.active
            }.sortedBy {
                it.second
            }

            // Find active routes based on active nearby stops
            val nearbyRouteList = arrayListOf<NearbyRoute>()
            val routes = viewModel.routes
            nearbyStopList.forEach { stop ->
                for (routeId in stop.first.routes) {
                    if (!nearbyRouteList.any { it.id == routeId }) {
                        val route = routes.find { it.id == routeId }

                        if (route != null) {
                            if (!route.active) {
                                continue
                            }
                            val arrival = viewModel.getArrivalPair(routeId, stop.first.id)
                            var arrivalMessage = "..."
                            var arrivalMinutes = Int.MAX_VALUE
                            if (arrival != null) {
                                arrivalMessage =
                                    ArrivalUtils().createSingleArrivalMessage(arrival.arrivals)
                                arrivalMinutes =
                                    ArrivalUtils().getMinutesToNextBus(arrival.arrivals)
                            }
                            nearbyRouteList.add(
                                NearbyRoute(
                                    route.id,
                                    route.name,
                                    arrivalMessage,
                                    route.color,
                                    arrivalMinutes
                                )
                            )
                        }
                    }
                }
            }
            val sortedNearbyRouteList = nearbyRouteList.sortedBy { it.timeTo }

            // Set controller data
            val previousSize = items
            items = nearbyStopList.size + nearbyBuildingList.size
            if (previousSize == 0) {
                epoxyRecyclerView.itemAnimator = null
            } else {
                epoxyRecyclerView.itemAnimator = FadeInAnimator().apply {
                    addDuration = 150
                    removeDuration = 50
                }
            }
            controller.routes = sortedNearbyRouteList
            controller.stops = nearbyStopList
            controller.places = nearbyBuildingList
        })

        // Prevent scrolling to bottom on initial population
        controller.adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0 || items == 0) {
                    epoxyRecyclerView.scrollToPosition(0)
                }
            }
        })

        // Retrieve location updates if user has granted permission
        val permission: Int = ContextCompat.checkSelfPermission(
            activity as AppCompatActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (l in locationResult.locations) {
                        location.value = arrayListOf(l.latitude, l.longitude)
                        break
                    }
                }
            }

            val locationRequest = LocationRequest.create()?.apply {
                interval = 5000
                fastestInterval = 500
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        return v
    }
}