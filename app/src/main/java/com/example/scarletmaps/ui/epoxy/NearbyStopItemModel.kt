package com.example.scarletmaps.ui.epoxy

import android.provider.Settings.Global.getString
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
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.utils.TextUtils
import kotlin.math.roundToInt

@EpoxyModelClass(layout = R.layout.nearby_stop_item)
abstract class NearbyStopItemModel: EpoxyModelWithHolder<NearbyStopItemModel.Holder>() {
    @EpoxyAttribute
    lateinit var name: String
    @EpoxyAttribute
    lateinit var area: String
    @EpoxyAttribute
    lateinit var stop: Stop

    @EpoxyAttribute
    var order: Int = 0

    @EpoxyAttribute
    var distance: Float = 0.0f


    override fun bind(holder: Holder) {
        super.bind(holder)
        val distanceText = "${distance.roundToInt()} m"
        holder.nameText.text = name
        holder.areaText.text = TextUtils().capitalizeWords(area)
        holder.distanceText.text = distanceText
        holder.root.setOnClickListener {
            it.findNavController().navigate(R.id.action_fragmentNearMe_to_fragmentOpenStop, bundleOf("id" to stop.id))
        }
    }

    class Holder: EpoxyHolder() {
        lateinit var nameText: TextView
        lateinit var areaText: TextView
        lateinit var distanceText: TextView
        lateinit var root: LinearLayout

        override fun bindView(itemView: View) {
            nameText = itemView.findViewById(R.id.name)
            areaText = itemView.findViewById(R.id.area)
            distanceText = itemView.findViewById(R.id.distance)
            root = itemView.rootView as LinearLayout
        }

    }
}