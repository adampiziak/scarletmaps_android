package com.example.scarletmaps.ui.epoxy

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

@EpoxyModelClass(layout = R.layout.view_holder_stop_item)
abstract class StopItemModel : EpoxyModelWithHolder<StopItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var stop: Stop

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(stop) {
            holder.name.text = name
            holder.area.text = TextUtils().capitalizeWords(area)
            holder.root.setOnClickListener {
                it.findNavController().navigate(R.id.openStop, bundleOf("id" to stop.id))
            }
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var name: TextView
        lateinit var area: TextView
        lateinit var root: LinearLayout

        override fun bindView(itemView: View) {
            name = itemView.findViewById(R.id.estop_name)
            area = itemView.findViewById(R.id.estop_area)
            root = itemView.rootView as LinearLayout
        }
    }
}