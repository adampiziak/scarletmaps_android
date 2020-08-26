package com.example.scarletmaps.ui.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.scarletmaps.R

@EpoxyModelClass(layout = R.layout.section_title)
abstract class SectionTitleModel: EpoxyModelWithHolder<SectionTitleModel.Holder>() {
    @EpoxyAttribute
    lateinit var title: String

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.title.text = title
    }


    class Holder: EpoxyHolder() {
        lateinit var title: TextView

        override fun bindView(itemView: View) {
            title = itemView.findViewById(R.id.title)
        }
    }
}