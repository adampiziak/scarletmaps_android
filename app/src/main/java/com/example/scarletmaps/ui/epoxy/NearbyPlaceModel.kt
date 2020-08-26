package com.example.scarletmaps.ui.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.scarletmaps.R
import com.example.scarletmaps.ui.nearby.NearbyPlace
import kotlin.math.roundToInt

@EpoxyModelClass(layout = R.layout.nearby_place_widget)
abstract class NearbyPlaceModel: EpoxyModelWithHolder<NearbyPlaceModel.Holder>() {
    @EpoxyAttribute
    lateinit var place: NearbyPlace

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.titleText.text = place.name
        holder.areaTextView.text = place.area
        holder.distanceText.text = place.distance.roundToInt().toString() + " m"
    }

    class Holder : EpoxyHolder(){
        lateinit var titleText: TextView
        lateinit var areaTextView: TextView
        lateinit var distanceText: TextView

        override fun bindView(itemView: View) {
            titleText = itemView.findViewById(R.id.location)
            areaTextView = itemView.findViewById(R.id.area)
            distanceText = itemView.findViewById(R.id.distance)
        }
    }
}