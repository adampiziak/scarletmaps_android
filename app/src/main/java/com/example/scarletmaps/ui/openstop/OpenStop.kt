package com.example.scarletmaps.ui.openstop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.utils.TextUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpenStop: Fragment() {

    val viewModel: OpenStopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_stop_open, container, false)
        val title: TextView = v.findViewById(R.id.open_stop_title)
        val area: TextView = v.findViewById(R.id.open_stop_area)
        val epoxyRecyclerView: EpoxyRecyclerView = v.findViewById(R.id.openStopEpoxy)
        val controller = OpenStopController()
        epoxyRecyclerView.setController(controller)

        viewModel.stop.observe(viewLifecycleOwner, Observer {stop ->
            title.text = stop.name
            area.text = TextUtils().capitalizeWords(stop.area)
        })

        viewModel.routes.observe(viewLifecycleOwner, Observer {routeList ->
            val activeRoutes = routeList.filter { it.active }
            controller.routes = activeRoutes
        })

        viewModel.arrivals.observe(viewLifecycleOwner, Observer {arrivalList ->
            controller.arrivals = arrivalList
        })

        return v
    }
}