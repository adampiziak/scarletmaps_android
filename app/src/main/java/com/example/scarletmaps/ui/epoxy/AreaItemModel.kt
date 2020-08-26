package com.example.scarletmaps.ui.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.scarletmaps.R

@EpoxyModelClass(layout = R.layout.item_area_chip)
abstract class AreaItemModel: EpoxyModelWithHolder<AreaItemModel.Holder>() {
    @EpoxyAttribute
    lateinit var name: String

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.area.text = name
    }

    class Holder: EpoxyHolder() {
        lateinit var area: TextView

        override fun bindView(itemView: View) {
            area = itemView.findViewById(R.id.area_chip_text)
        }
    }
}