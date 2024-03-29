package com.example.scarletmaps.ui.stoplist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.ui.epoxy.HomeController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.stoplist.*

@AndroidEntryPoint
class StopList : Fragment() {

    private val viewModel: StopListViewModel by viewModels()
    private var previousLoadState: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.stoplist, container, false)

        // Set up RecyclerView
        val recyclerView = v.findViewById<EpoxyRecyclerView>(R.id.stop_list_recyclerview)
        val controller = HomeController()
        recyclerView.setController(controller)

        // Get data from repository
        viewModel.stops.observe(viewLifecycleOwner, Observer<List<Stop>> { stops ->
            //viewAdapter.setStops(stops.sortedBy { it.name })
            controller.stopList = stops.sortedBy { it.name }.filter { it.active }
        })

        viewModel.loaded.observe(viewLifecycleOwner, Observer<Boolean> { loaded ->
            if (loaded != previousLoadState) {
                if (loaded) {
                    stop_list_progress_indicator.animate()
                        .setStartDelay(150)
                        .alpha(0f)
                        .setDuration(200)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                if (stop_list_progress_indicator != null) {
                                    stop_list_progress_indicator.visibility = View.GONE
                                }
                            }
                        })
                } else {
                    stop_list_progress_indicator.visibility = View.VISIBLE
                }
            }
            previousLoadState = loaded
        })



        return v
    }
}