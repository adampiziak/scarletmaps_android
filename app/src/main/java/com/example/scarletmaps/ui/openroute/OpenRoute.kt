package com.example.scarletmaps.ui.openroute

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Explode
import androidx.transition.Fade
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.utils.DisplayUtils
import com.example.scarletmaps.utils.TextUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.route_open.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule
import kotlin.math.exp
import kotlin.math.min


@AndroidEntryPoint
class OpenRoute : Fragment() {
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModel: OpenRouteViewModel by viewModels()
    private var polylines: ArrayList<Polyline> = ArrayList()
    private var vehicleMarkers: HashMap<Int, Marker> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate fragment
        val v = inflater.inflate(R.layout.route_open, container, false)

        // Bind views
        val title = v.findViewById<TextView>(R.id.route_open_name)
        val areas = v.findViewById<TextView>(R.id.route_open_areas)
        val epoxyRecyclerView = v.findViewById<EpoxyRecyclerView>(R.id.route_viewer_recyclerview)
        val bottomSheetView = v.findViewById<LinearLayout>(R.id.route_open_bottom_sheet)
        val header = v.findViewById<LinearLayout>(R.id.route_info)

        // Setup Epoxy
        val controller = OpenRouteController()
        epoxyRecyclerView.setController(controller)

        // BottomSheet setup
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        var expanded = false
        bottomSheetBehavior.apply {
            halfExpandedRatio = 0.5f
            isFitToContents = false
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        // Observe Data from view model
        // Update route info
        viewModel.route.observe(viewLifecycleOwner, Observer { route ->
            title.text = route.name
            areas.text = route.areas.joinToString { TextUtils().capitalizeWords(it) }
        })

        // Update stop list views
        viewModel.stopListByArea.observe(viewLifecycleOwner, Observer {
            controller.stopListByArea = it
            if (!expanded) {
                expanded = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        })

        // Observe arrival data for route
        viewModel.arrivals.observe(viewLifecycleOwner, Observer {
            controller.arrivals = ArrayList(it)
        })



        // On header click, toggle bottom sheet open/close
        header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        val mapContainer = v.findViewById<LinearLayout>(R.id.map_container)


        val bottomSheetElevation = DisplayUtils.dpToPx(16)
        var initialSlideOffset: Float = -1f
        mapContainer.translationY = 0f
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    val headerDrawable =
                        getDrawable(requireContext(), R.drawable.route_open_bottom_sheet_expanded)

                    if (headerDrawable != null) {
                        header.background = headerDrawable
                    }
                } else {
                    val headerDrawable =
                        getDrawable(requireContext(), R.drawable.route_open_bottom_sheet)

                    if (headerDrawable != null) {
                        header.background = headerDrawable
                        header.translationZ = 0f
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val progress = 1.0f - slideOffset
                mapContainer.alpha = min(2f - slideOffset * 2, 1f)
                if (initialSlideOffset == -1f) {
                    initialSlideOffset = slideOffset
                    return
                }
                val offset = slideOffset - initialSlideOffset
                if (offset > 0 ) {
                    mapContainer.translationY = -offset * 400
                }

                if (slideOffset > 0.9f) {
                    bottomSheet.elevation = bottomSheetElevation * 10f * progress
                } else {
                    bottomSheet.elevation = bottomSheetElevation
                }
            }
        })

        val headerElevation = DisplayUtils.dpToPx(4)
        epoxyRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(epoxyRecyclerView, dx, dy)
                val offset = epoxyRecyclerView.computeVerticalScrollOffset()
                header.translationZ = min((offset * 0.025).toFloat(), headerElevation)
            }
        })

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapContainer = view.findViewById<LinearLayout>(R.id.map_container)
        // Update map on view model updates
        val callback = OnMapReadyCallback { map ->

            mapContainer.apply {
                alpha = 0f
            }.animate().alpha(1f).setDuration(200).setStartDelay(20).start()
            // Enable current location marker
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                map.isMyLocationEnabled = true
            }

            // On stop list update, update markers on map
            viewModel.stopList.observe(viewLifecycleOwner, Observer { stopList ->
                stopList.forEach { stop ->
                    val markerPosition = LatLng(stop.location.lat, stop.location.lng)
                    map.addMarker(
                        MarkerOptions()
                            .position(markerPosition)
                            .icon(BitmapDescriptorFactory.defaultMarker(getRouteHue()))
                            .title(stop.name)
                    )
                }
            })


            // On vehicle list update, update vehicle views
            viewModel.vehicles.observe(viewLifecycleOwner, Observer { vehicleList ->
                vehicleMarkers.forEach { (key, value) ->
                    val vehicle = vehicleList.find { it.id == key }
                    if (vehicle == null) {
                        value.remove()
                    } else {
                        val vehiclePosition = LatLng(vehicle.location.lat, vehicle.location.lng)
                        value.position = vehiclePosition
                    }
                }

                vehicleList.forEach { vehicle ->
                    if (!vehicleMarkers.containsKey(vehicle.id)) {
                        val vehiclePosition = LatLng(vehicle.location.lat, vehicle.location.lng)
                        val fragContext = context
                        if (fragContext != null) {
                            val bitmapDescriptor = getMarkerIconFromDrawable(getRouteColor())
                            if (bitmapDescriptor == null) {
                                Log.d("ADAMSKI", "AHHHHHH")
                            } else {
                                Log.d("ADAMSKI", "$bitmapDescriptor")
                            }
                            val marker = map.addMarker(

                                MarkerOptions()
                                    .position(vehiclePosition)
                                    .icon(bitmapDescriptor)
                                    .anchor(0.5f, 0.5f)
                            )

                            vehicleMarkers[vehicle.id] = marker
                        }
                    }
                }
            })


            // Add segments
            viewModel.segments.observe(viewLifecycleOwner, Observer { list ->

                // Clear current segments
                polylines.forEach {
                    it.remove()
                }

                // Add new segments
                val color = getRouteColor()
                list.forEach { segment ->
                    val options = PolylineOptions().width(5.0f).color(color)
                    val coordinatePath: List<LatLng> = PolyUtil.decode(segment.path)
                    coordinatePath.forEach {
                        options.add(it)
                    }

                    polylines.add(map.addPolyline(options))
                }


                // Zoom to segment bounds
                zoomToPolylines(map, polylines)
            })
        }


        GlobalScope.launch(Dispatchers.Main) {
            delay(150)
            val mapFragment = SupportMapFragment()
            childFragmentManager.commit {
                add(R.id.map_container, mapFragment)
            }
            mapFragment.getMapAsync(callback)
        }
    }


    private fun getMarkerIconFromDrawable(color: Int): BitmapDescriptor? {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
            ?: return null
        vectorDrawable.setTint(color)
        val bitmap = vectorDrawable.toBitmap()
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getRouteColor(): Int {
        val route = viewModel.route.value
        var colorString = "#000000"
        if (route?.color != null) {
            colorString = "#${route.color}"
        }

        return Color.parseColor(colorString)
    }

    private fun getRouteHue(): Float {
        val colorInt = getRouteColor()
        val hsv = FloatArray(3)
        Color.colorToHSV(colorInt, hsv)

        return hsv[0]
    }

    private fun zoomToPolylines(googleMap: GoogleMap, polylines: ArrayList<Polyline>) {
        val builder = LatLngBounds.builder()
        var canBuild = false
        for (polyline in polylines) {
            for (point in polyline.points) {
                builder.include(point)
                canBuild = true
            }
        }
        if (canBuild) {
            val bounds = builder.build()
            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 100))
        }
    }
}