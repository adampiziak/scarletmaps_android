package com.example.scarletmaps.ui.epoxy

import android.widget.TextView
import com.airbnb.epoxy.*
import com.example.scarletmaps.R

@EpoxyModelClass(layout = R.layout.campus_stop_list)
abstract class CampusStopList(models: List<EpoxyModel<*>>): EpoxyModelGroup(R.layout.campus_stop_list, models) {
    @EpoxyAttribute
    lateinit var title: String

    override fun bind(holder: ModelGroupHolder) {
        super.bind(holder)
        holder.rootView.findViewById<TextView>(R.id.group_area).text = title
    }
}