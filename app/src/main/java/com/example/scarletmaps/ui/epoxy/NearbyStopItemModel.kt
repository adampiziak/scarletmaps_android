package com.example.scarletmaps.ui.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.scarletmaps.R
import kotlin.math.roundToInt

@EpoxyModelClass(layout = R.layout.nearby_stop_item)
abstract class NearbyStopItemModel: EpoxyModelWithHolder<NearbyStopItemModel.Holder>() {
    @EpoxyAttribute
    lateinit var name: String
    @EpoxyAttribute
    lateinit var area: String

    @EpoxyAttribute
    var order: Int = 0

    @EpoxyAttribute
    var distance: Float = 0.0f


    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.nameText.text = name
        holder.areaText.text = area.capitalize()
        holder.distanceText.text = "${distance.roundToInt()} m"
    }

    class Holder: EpoxyHolder() {
        lateinit var nameText: TextView
        lateinit var areaText: TextView
        lateinit var distanceText: TextView

        override fun bindView(itemView: View) {
            nameText = itemView.findViewById(R.id.name)
            areaText = itemView.findViewById(R.id.area)
            distanceText = itemView.findViewById(R.id.distance)
        }

    }
}