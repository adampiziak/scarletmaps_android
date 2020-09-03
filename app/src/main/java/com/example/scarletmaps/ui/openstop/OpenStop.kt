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
        val toolbar: TextView = v.findViewById(R.id.open_stop_title)
        val epoxyRecyclerView: EpoxyRecyclerView = v.findViewById(R.id.openStopEpoxy)
        val controller = OpenStopController()
        epoxyRecyclerView.setController(controller)

        viewModel.stop.observe(viewLifecycleOwner, Observer {stop ->
            toolbar.text = stop.name
            Log.d("ADAMSKI", "stop ${stop.id}")
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