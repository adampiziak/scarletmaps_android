package com.example.scarletmaps.ui.routeviewer

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.ui.stoplist.StopListDiff
import kotlinx.android.synthetic.main.route_stop_view.view.*
import java.time.Instant
import kotlin.math.ceil

class RouteViewerAdapter(private var stops: ArrayList<Stop>, private var arrivals: ArrayList<Arrival>) :
    RecyclerView.Adapter<RouteViewerAdapter.RouteHolder>() {

    class RouteHolder(private val v: ConstraintLayout) : RecyclerView.ViewHolder(v) {
        val root: ConstraintLayout = v
        val name: TextView = v.route_stop_name
        val times: TextView = v.route_stop_times
        val area: TextView = v.route_stop_area
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.route_stop_view, parent, false) as ConstraintLayout
        return RouteHolder(root)
    }

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        holder.name.text = stops[position].name
        val times = getArrivalsText(stops[position].id)

        if (times != null) {
            holder.times.text = times
            holder.itemView.alpha = 1.0f
        } else {
            holder.times.text = "no arrivals"
            holder.itemView.alpha = 0.3f
        }
        val area = stops[position]
            .area.split(" ").joinToString(" ") { it.capitalize() }
        holder.area.text = "$area campus"
    }

    override fun getItemCount() = stops.size

    fun setStops(newStops: List<Stop>) {
        val diffCallback = StopListDiff(stops, newStops)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stops.clear()
        stops.addAll(newStops)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setArrivals(newArrivals: List<Arrival>) {
        arrivals.clear()
        arrivals.addAll(newArrivals)
        notifyDataSetChanged()
    }

    private fun getArrivalsText(stopId: Int): String? {
        val arrival = arrivals.find { it.stop_id == stopId }
        if (arrival != null) {
            var message = ""
            arrival.arrivals.forEachIndexed { i, a ->
                val now: Long = Instant.now().toEpochMilli()
                val difference = a - now
                if (difference < 0 && i == arrivals.size - 1) {
                    return null
                }
                if (difference < 0) {
                    return@forEachIndexed
                }
                val timeTo = ceil((difference.toDouble() / 1000) / 60).toInt()
                if (i == arrival.arrivals.size - 1) {
                    message += "$timeTo min"
                } else {
                    message += "$timeTo min, "
                }


            }
            return message
        } else {
            return null
        }
    }
}