package com.example.scarletmaps.ui.stoplist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.stop.Stop
import kotlinx.android.synthetic.main.stoplist_viewholder.view.*

class StopListAdapter(private var stops: ArrayList<Stop>) :
    RecyclerView.Adapter<StopListAdapter.MyViewHolder>() {

    class MyViewHolder(val v: LinearLayout) : RecyclerView.ViewHolder(v) {
        val name = v.stop_name
        val area = v.stop_area
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.stoplist_viewholder, parent, false) as LinearLayout

        return MyViewHolder(root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = stops[position].name
        holder.area.text = stops[position].area
            .split(" ").joinToString(" ") { it.capitalize() }
    }

    override fun getItemCount() = stops.size

    fun setStops(newStops: List<Stop>) {
        val diffCallback = StopListDiff(stops, newStops)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stops.clear()
        stops.addAll(newStops)
        diffResult.dispatchUpdatesTo(this)
    }
}