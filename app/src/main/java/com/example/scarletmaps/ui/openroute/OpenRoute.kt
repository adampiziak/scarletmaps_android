package com.example.scarletmaps.ui.openroute

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.Vehicle
import com.example.scarletmaps.data.models.stop.Stop
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.route_open.*
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


@AndroidEntryPoint
class OpenRoute : Fragment() {
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModel: OpenRouteViewModel by viewModels()
    private var polylines: ArrayList<Polyline> = ArrayList()

    data class VehicleMarker(val polyline: Polyline, val vehicle: Vehicle)
    private var vehicleMarkers: HashMap<Int, Marker> = HashMap()

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(
                intrinsicWidth,
                intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        // Retrieve location updates if user has granted permission
        val finePermission: Int = ActivityCompat.checkSelfPermission(
            activity as AppCompatActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarsePermission: Int = ActivityCompat.checkSelfPermission(
            activity as AppCompatActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (finePermission == PackageManager.PERMISSION_GRANTED && coarsePermission == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }
        viewModel.getFilteredList().observe(viewLifecycleOwner, Observer { stopList ->
            stopList.forEach { stop ->
                val markerPosition = LatLng(stop.location.lat, stop.location.lng)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(markerPosition)
                        .title(stop.name)
                )
            }


        })

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
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(vehiclePosition)
                                .icon(bitmapDescriptorFromVector(fragContext, R.drawable.ic_bus))
                        )

                        vehicleMarkers.put(vehicle.id, marker)
                    }
                }
            }
        })


        viewModel.segments.observe(viewLifecycleOwner, Observer { list ->
            polylines.forEach {
                it.remove()
            }

            list.forEach { segment ->
                val options = PolylineOptions().width(5.0f)
                val coordinatePath: List<LatLng> = PolyUtil.decode(segment.path)
                coordinatePath.forEach {
                    options.add(it)
                }

                polylines.add(googleMap.addPolyline(options))
            }

            zoomToPolylines(googleMap, polylines)
        })
    }

    fun zoomToPolylines(googleMap: GoogleMap, polylines: ArrayList<Polyline>) {
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
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.route_open, container, false)

        // Set name
        val title = v.findViewById<TextView>(R.id.route_open_name)
        val areas = v.findViewById<TextView>(R.id.route_open_areas)
        title.text = viewModel.routeImmediate.name

        var areaMessage = ""
        viewModel.routeImmediate.areas.forEachIndexed { i, a ->
            if (i == viewModel.routeImmediate.areas.size - 1) {
                areaMessage += a.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()
            } else {
                areaMessage += "${a.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()}, "
            }
        }
        areas.text = areaMessage

        val recyclerView = v.findViewById<EpoxyRecyclerView>(R.id.route_viewer_recyclerview)
        val controller = OpenRouteController(viewModel.routeImmediate)
        recyclerView.setController(controller)
        viewModel.getFilteredList().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                return@Observer
            }
            var stopsByArea: ArrayList<ArrayList<Stop>> = ArrayList()
            var currentAreaList: ArrayList<Stop> = ArrayList()
            var currentArea = it[0].area
            for (stop in it) {
                if (stop.area == currentArea) {
                    currentAreaList.add(stop)
                } else {
                    stopsByArea.add(ArrayList(currentAreaList))
                    currentAreaList.clear()
                    currentAreaList.add(stop)
                    currentArea = stop.area
                }
            }
            stopsByArea.add(ArrayList(currentAreaList))
            controller.stopByArea = stopsByArea


            //viewAdapter.setStops(it)
        })
        val bottomSheetView = v.findViewById<LinearLayout>(R.id.route_open_bottom_sheet)
        viewModel.arrivals.observe(viewLifecycleOwner, Observer {
            //viewAdapter.setArrivals(it)
            Log.d("ADAMSKI", "setting ${it.size} arrivals")
            controller.arrivals = ArrayList(it)
        })
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.apply {
            halfExpandedRatio = 0.5f
            isFitToContents = false
        }

        val header = v.findViewById<LinearLayout>(R.id.route_info)
        header.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            Log.d("ADAMSKI", bottomSheetBehavior.state.toString())
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        val mapContainer = v.findViewById<FrameLayout>(R.id.map_container)


        val bottomSheetElevation = dpToPx(16)
        var initialSlideOffset: Float = -1f
        val headerHeight = dpToPx(100).toFloat()
        mapContainer.translationY = -headerHeight
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
                mapContainer.translationY = -offset*400 - headerHeight
                if (slideOffset > 0.9f) {
                    bottomSheet.elevation = bottomSheetElevation * 10f * progress
                } else {
                    bottomSheet.elevation = bottomSheetElevation.toFloat()
                }
            }
        })
        val headerElevation = dpToPx(6).toFloat()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerViewRef: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val offset = recyclerView.computeVerticalScrollOffset()
                val min = min((offset*0.025).toFloat(), 20f)
                header.translationZ = min

                Log.d("ADAMSKI", min.toString())
            }
        })


        return v
    }

    fun dpToPx(dp: Int): Int {
        val displayMetrics: DisplayMetrics = requireContext().resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt().toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


    }
}