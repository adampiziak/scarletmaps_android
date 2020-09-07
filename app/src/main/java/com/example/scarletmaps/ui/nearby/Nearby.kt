package com.example.scarletmaps.ui.nearby


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.utils.ArrivalUtils
import com.example.scarletmaps.utils.TextUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import javax.inject.Inject

const val NEARBY_STOP_RADIUS = 800
const val LOCATION_PERMISSION_CODE = 53923

@AndroidEntryPoint
class Nearby: Fragment() {
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: NearbyViewModel by viewModels()
    private val location: MutableLiveData<ArrayList<Double>> = MutableLiveData(
        arrayListOf(
            0.0,
            0.0
        )
    )

    lateinit var permissionMessage: ConstraintLayout
    lateinit var epoxyRecyclerView: EpoxyRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.nearby, container, false)

        // Populate lists
        var buildingList = viewModel.buildingsInitial
        var stopList = viewModel.stopsInitial
        var routeList = viewModel.routesInitial

        // Listen for changes to data
        viewModel.buildings.observe(viewLifecycleOwner, Observer {buildings ->
            buildingList = buildings
        })
        viewModel.stops.observe(viewLifecycleOwner, Observer {stops ->
            stopList = stops
        })
        viewModel.routes.observe(viewLifecycleOwner, Observer {routes ->
            routeList = routes
        })

        // Setup Epoxy
        epoxyRecyclerView = v.findViewById<EpoxyRecyclerView>(R.id.nearby_epoxy)
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
            val nearbyBuildingList = buildingList.map {
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


            //  Find active stops nearer than NEARBY_STOP_RADIUS to user
            val nearbyStopList: List<Pair<Stop, Float>> = stopList.map { stop ->
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
                it.second < NEARBY_STOP_RADIUS && it.first.active
            }.sortedBy {
                it.second
            }

            // Find active routes based on active nearby stops
            val nearbyRouteList = arrayListOf<NearbyRoute>()
            nearbyStopList.forEach { stop ->
                for (routeId in stop.first.routes) {
                    if (!nearbyRouteList.any { it.id == routeId }) {
                        val route = routeList.find { it.id == routeId }

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


            // Turn off animation on empty -> populated list
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

            // Set controller data
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


        // Start location updates if permission granted
        // Show message + button to allow user to grant permission
        permissionMessage = v.findViewById<ConstraintLayout>(R.id.location_permission_message)
        val requestButton = v.findViewById<Button>(R.id.button)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionMessage.visibility = View.GONE
            epoxyRecyclerView.visibility = View.VISIBLE

            // Start location updates
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
        } else {
            permissionMessage.visibility = View.VISIBLE
            epoxyRecyclerView.visibility = View.GONE
            requestButton.setOnClickListener {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
            }
        }

        return v
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permissionMessage.visibility = View.GONE
                    epoxyRecyclerView.visibility = View.VISIBLE

                    // Start location updates
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
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }
                    viewModel.refreshArrivals()
                } else {
                    permissionMessage.visibility = View.VISIBLE
                    epoxyRecyclerView.visibility = View.GONE
                }
                return
            }
        }
    }
}