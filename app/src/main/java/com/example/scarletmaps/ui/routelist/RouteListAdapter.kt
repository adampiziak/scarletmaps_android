package com.example.scarletmaps.ui.routelist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.route.Route
import kotlinx.android.synthetic.main.routelist_viewholder.view.*

class RouteListAdapter(private var routeList: ArrayList<Route>) :
    RecyclerView.Adapter<RouteListAdapter.RouteHolder>() {

    class RouteHolder(private val v: LinearLayout) : RecyclerView.ViewHolder(v) {
        val root = v
        val name: TextView = v.route_name
        val areas: TextView = v.route_areas
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteHolder {
        val root =
            if (viewType == 0) {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.routelist_viewholder, parent, false) as LinearLayout
            } else {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.routelist_viewholder_gray, parent, false) as LinearLayout
            }

        return RouteHolder(root)
    }

    override fun getItemViewType(position: Int): Int {
        return if (routeList[position].active) {
            0
        } else {
            1
        }
    }


    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        holder.name.text = routeList[position].name

        var areaMessage = ""
        routeList[position].areas.forEachIndexed { i, a ->
            if (i == routeList[position].areas.size - 1) {
                areaMessage += a.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()
            } else {
                areaMessage += "${a.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()}, "
            }
        }

        holder.areas.text = areaMessage
        holder.itemView.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.routeView, bundleOf("id" to routeList[position].id), null,  null)
        }
    }

    override fun getItemCount() = routeList.size

    fun setRoutes(routes: List<Route>) {
        val diffCallback = RouteListDiff(routeList, routes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        routeList.clear()
        routeList.addAll(routes)
        diffResult.dispatchUpdatesTo(this)
    }
}