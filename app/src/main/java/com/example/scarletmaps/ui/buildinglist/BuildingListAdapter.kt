package com.example.scarletmaps.ui.buildinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.building.Building
import kotlinx.android.synthetic.main.buildinglist_viewholder.view.*

class BuildingListAdapter(private var buildings: ArrayList<Building>) :
    RecyclerView.Adapter<BuildingListAdapter.MyViewHolder>() {

    class MyViewHolder(val v: LinearLayout) : RecyclerView.ViewHolder(v) {
        val name = v.building_name
        val area = v.building_area
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.buildinglist_viewholder, parent, false) as LinearLayout

        return MyViewHolder(root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = buildings[position].name
        holder.area.text = buildings[position].area
            .split(" ").joinToString(" ") { it.capitalize() }
    }

    override fun getItemCount() = buildings.size

    fun setStops(newBuildings: List<Building>) {
        val diffCallback = BuildingListDiff(buildings, newBuildings)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        buildings.clear()
        buildings.addAll(newBuildings)
        diffResult.dispatchUpdatesTo(this)
    }
}