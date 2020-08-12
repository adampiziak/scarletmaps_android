package com.example.scarletmaps.ui.routeviewer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteViewer : Fragment() {
    private val viewModel: RouteViewerViewModel by viewModels()
    lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 280
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            scrimColor = 0
            endElevation = 1.0f
            startElevation = 0.0f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.route_viewer, container, false)
        setupSharedTransition(v)

        // Setup RecyclerView
        val recyclerView = v.findViewById<RecyclerView>(R.id.route_viewer_recyclerview)
        val viewAdapter = RouteViewerAdapter(ArrayList(), ArrayList())
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.route.observe(viewLifecycleOwner, Observer {
            v.findViewById<TextView>(R.id.route_viewer_name).text= it.name

            var area_message = ""
            it.areas.forEachIndexed { i, a ->
                if (i == it.areas.size - 1) {
                    area_message += "${a.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()}"
                } else {
                    area_message += "${a.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()}, "
                }
            }
            v.findViewById<TextView>(R.id.route_viewer_areas).text = area_message
        })

        viewModel.getFilteredList().observe(viewLifecycleOwner, Observer {
            viewAdapter.setStops(it)
        })

        viewModel.arrivals.observe(viewLifecycleOwner, Observer {
            viewAdapter.setArrivals(it)
            Log.d("ADAMSKI", "setting ${it.size} arrivals")
        })
        startPostponedEnterTransition()

        content = v.findViewById<LinearLayout>(R.id.route_viewer_content)

        content.apply {
            scaleX = 0.95f
            scaleY = 0.95f
            visibility = View.VISIBLE
            alpha = 0f

            /*
            animate()
                .alpha(1f)
                .setStartDelay(300)
                .setDuration(200)
                .setListener(null)
             */

            animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setStartDelay(100)
                .setDuration(150)
                .setListener(null)
        }


        return v
    }

    fun setupSharedTransition(v: View) {
        val transitionName = "route_item_transition_${arguments?.getInt("id")}"
        val root = v.findViewById<ConstraintLayout>(R.id.route_viewer_root)

        root.transitionName = transitionName
    }

}