package com.example.scarletmaps.ui.nearby

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.ui.routelist.RouteListAdapter
import kotlinx.android.synthetic.main.nearby_viewholder.view.*

class NearbyAdapter(private var dataset: ArrayList<Int>) :
    RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {
    class ViewHolder(private val v: LinearLayout) : RecyclerView.ViewHolder(v) {
        val root = v
        val name: TextView = v.nearby_name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.nearby_viewholder
        , parent, false) as LinearLayout

        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = dataset[position].toString()
        holder.root.transitionName = "transition${position}"
        holder.root.setOnClickListener{view ->
            val extras = FragmentNavigatorExtras(holder.root to holder.root.transitionName)
            view.findNavController().navigate(R.id.testFragment, bundleOf("id" to position), null, extras)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
