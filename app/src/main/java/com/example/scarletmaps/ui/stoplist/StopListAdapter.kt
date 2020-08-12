package com.example.scarletmaps.ui.stoplist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.stop.Stop

class StopListAdapter(private var stops: ArrayList<Stop>) :
    RecyclerView.Adapter<StopListAdapter.MyViewHolder>() {

    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.stoplist_viewholder, parent, false) as TextView

        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = stops[position].name
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