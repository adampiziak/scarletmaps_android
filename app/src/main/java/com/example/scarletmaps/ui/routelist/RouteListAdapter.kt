package com.example.scarletmaps.ui.routelist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.route.Route
import kotlinx.android.synthetic.main.routelist_viewholder.view.*
import kotlinx.coroutines.delay

class RouteListAdapter(private var myDataset: ArrayList<Route>) :
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
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.routelist_viewholder, parent, false) as LinearLayout


        return RouteHolder(root)
    }

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        holder.name.text = myDataset[position].name

        var area_message = ""
        myDataset[position].areas.forEachIndexed { i, a ->
            if (i == myDataset[position].areas.size - 1) {
                area_message += "${a.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()}"
            } else {
                area_message += "${a.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()}, "
            }
        }
        holder.areas.text = area_message
        holder.root.transitionName = "route_item_transition_${myDataset[position].id}"
        holder.itemView.setOnClickListener{ view ->

            Log.d("ADAMSKI1", holder.root.transitionName)
            val extras = FragmentNavigatorExtras(holder.root to holder.root.transitionName)
            view.findNavController().navigate(R.id.routeView, bundleOf("id" to myDataset[position].id), null,  extras)
        }
    }

    override fun getItemCount() = myDataset.size

    fun setRoutes(routes: List<Route>) {
        val diffCallback = RouteListDiff(myDataset, routes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        myDataset.clear()
        myDataset.addAll(routes)
        diffResult.dispatchUpdatesTo(this)
    }
}