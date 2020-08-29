package com.example.scarletmaps.ui.buildinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BuildingList : Fragment() {
    private val viewModel: BuildingListViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.buildinglist, container, false)
        val recyclerView = v.findViewById<RecyclerView>(R.id.buildingListRecyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = BuildingListAdapter(ArrayList(viewModel.buildingList.sortedBy { it.name }))
        }
        return v
    }
}