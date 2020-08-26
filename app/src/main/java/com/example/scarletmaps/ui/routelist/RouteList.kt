package com.example.scarletmaps.ui.routelist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.route.Route
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.routelist.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class RouteList : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel: RouteListViewModel by viewModels()
    private var loaded: Boolean = true
    private val routeList: ArrayList<Route> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.routelist, container, false)

        val initialRouteList: ArrayList<Route> =
            ArrayList(viewModel.initialRouteList.sortedBy { !it.active })

        // Set up RecyclerView
        val viewAdapter = RouteListAdapter(initialRouteList)
        recyclerView = v.findViewById(R.id.route_list_recyclerview)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)


        // Get data from repository
        viewModel.routes.observe(viewLifecycleOwner, Observer<List<Route>> { routes ->
            val sortedRoutes = routes.sortedBy { !it.active }
            viewAdapter.setRoutes(sortedRoutes)
        })

        // Observe loading state
        viewModel.loaded.observe(viewLifecycleOwner, Observer<Boolean> { value ->
            if (loaded != value) {
                if (value) {
                    route_list_progress_indicator.animate()
                        .setStartDelay(150)
                        .alpha(0f)
                        .setDuration(200)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                if (route_list_progress_indicator != null) {
                                    route_list_progress_indicator.visibility = View.GONE
                                }
                            }
                        })
                } else {
                    route_list_progress_indicator.visibility = View.VISIBLE
                }
            }

            loaded = value
        })


        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        postponeEnterTransition()
        recyclerView.apply {
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

         */
    }
}