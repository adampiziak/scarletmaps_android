package com.example.scarletmaps.ui.epoxy

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.scarletmaps.R
import com.example.scarletmaps.ui.nearby.NearbyRoute


@EpoxyModelClass(layout = R.layout.nearby_route_item)
abstract  class NearbyRouteItemModel: EpoxyModelWithHolder<NearbyRouteItemModel.Holder>() {
    @EpoxyAttribute
    lateinit var route: NearbyRoute

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.textView.text = route.name
        holder.arrivalText.text = route.arrival
        if (route.color != null) {
            val pL: Int = holder.routeRoot.getPaddingLeft()
            val pT: Int = holder.routeRoot.getPaddingTop()
            val pR: Int = holder.routeRoot.getPaddingRight()
            val pB: Int = holder.routeRoot.getPaddingBottom()
            holder.routeRoot.setBackgroundResource(R.drawable.nearby_route_item_background)

            val drawable: GradientDrawable = holder.routeRoot.background as GradientDrawable
            val color = Color.parseColor("#${route.color}")
            drawable.setColor(color)
            holder.routeRoot.background = drawable
            holder.routeRoot.setPadding(pL, pT, pR, pB)
        }
        holder.routeRoot.setOnClickListener {
            it.findNavController().navigate(
                R.id.action_fragmentNearMe_to_fragmentOpenRoute,
                bundleOf("id" to route.id)
            )
        }
    }

    class Holder: EpoxyHolder() {
        lateinit var routeRoot: LinearLayout
        lateinit var textView: TextView
        lateinit var arrivalText: TextView

        override fun bindView(itemView: View) {
            textView = itemView.findViewById(R.id.nearby_route_name)
            arrivalText = itemView.findViewById(R.id.nearby_route_arrival)
            routeRoot = itemView.findViewById(R.id.nearby_route_root)
        }
    }

}