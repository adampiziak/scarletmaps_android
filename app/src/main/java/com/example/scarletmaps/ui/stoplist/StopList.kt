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
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.stop.Stop
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
        val viewAdapter = StopListAdapter(ArrayList())
        val viewManager = LinearLayoutManager(activity)
        val recyclerView = v.findViewById<RecyclerView>(R.id.stop_list_recyclerview)
        recyclerView.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // Get data from repository
        viewModel.stops.observe(viewLifecycleOwner, Observer<List<Stop>> { stops ->
            viewAdapter.setStops(stops.sortedBy { it.name })
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