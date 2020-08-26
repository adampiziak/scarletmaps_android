package com.example.scarletmaps.ui.epoxy

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.airbnb.epoxy.*
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.ui.nearby.NearbyRoute

@EpoxyModelClass(layout = R.layout.nearby_route_item)
abstract  class NearbyRouteItemModel: EpoxyModelWithHolder<NearbyRouteItemModel.Holder>() {
    @EpoxyAttribute
    lateinit var route: NearbyRoute

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.textView.text = route.name
        holder.arrivalText.text = route.arrival
        holder.textView.setOnClickListener {
            it.findNavController().navigate(R.id.routeView, bundleOf("id" to route.id), null,  null)
        }
    }

    class Holder: EpoxyHolder() {
        lateinit var textView: TextView
        lateinit var arrivalText: TextView

        override fun bindView(itemView: View) {
            textView = itemView.findViewById(R.id.nearby_route_name)
            arrivalText = itemView.findViewById(R.id.nearby_route_arrival)
        }
    }

}