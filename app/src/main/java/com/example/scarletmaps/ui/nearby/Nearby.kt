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
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.building.Building
import com.example.scarletmaps.data.models.route.Route
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
    private val location: MutableLiveData<ArrayList<Double>> = MutableLiveData(arrayListOf(0.0, 0.0))

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

        // Update controller when location changes
        location.observe(viewLifecycleOwner, Observer<ArrayList<Double>> { currentLocation ->

            // Find buildings nearer than 800m to user
            val nearbyBuildingList: List<NearbyPlace> = viewModel.buildings.map {
                val distance = FloatArray(5)
                Location.distanceBetween(currentLocation[0], currentLocation[1], it.lat, it.lng, distance)
                NearbyPlace(it.id, it.name, TextUtils().capitalizeWords(it.area), distance[0])
            }.filter{
                it.distance < 800
            }.sortedBy { it.distance }

            //  Find active stops nearer than 500m to user
            val nearbyStopList = viewModel.stops.map { stop ->
                val distance = FloatArray(5)
                Location.distanceBetween(currentLocation[0], currentLocation[1], stop.location.lat, stop.location.lng, distance)
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
                    if (!nearbyRouteList.any { it.id == routeId}) {
                        val route = routes.find { it.id == routeId}

                        if (route != null) {
                            if (!route.active) {
                                continue
                            }
                            val arrival = viewModel.getArrivalPair(routeId, stop.first.id)
                            var arrivalMessage = "..."
                            if (arrival != null) {
                                arrivalMessage = ArrivalUtils().createSingleArrivalMessage(arrival.arrivals)
                            }
                            nearbyRouteList.add(NearbyRoute(route.id, route.name, arrivalMessage ))
                        }
                    }
                }
            }

            // Set controller data
            controller.places = nearbyBuildingList
            controller.routes = nearbyRouteList
            controller.stops = nearbyStopList
        })

        // Retrieve location updates if user has granted permission
        val permission: Int = ContextCompat.checkSelfPermission(activity as AppCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (l in locationResult.locations){
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

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }

        return v
    }
}