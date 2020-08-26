package com.example.scarletmaps.ui.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.stop.Stop

@EpoxyModelClass(layout = R.layout.view_holder_stop_item)
abstract class StopItemModel : EpoxyModelWithHolder<StopItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var stop: Stop

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(stop) {
            holder.name.text = name
            holder.area.text = area
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var name: TextView
        lateinit var area: TextView

        override fun bindView(itemView: View) {
            name = itemView.findViewById(R.id.estop_name)
            area = itemView.findViewById(R.id.estop_area)
        }
    }
}