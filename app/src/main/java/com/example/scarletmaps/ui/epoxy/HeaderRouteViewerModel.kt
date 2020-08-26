package com.example.scarletmaps.ui.epoxy

import android.widget.TextView
import com.airbnb.epoxy.*
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.route.Route

@EpoxyModelClass(layout = R.layout.header_routeviewer)
abstract class HeaderRouteViewerModel(models: List<EpoxyModel<*>>): EpoxyModelGroup(R.layout.header_routeviewer, models)  {

    @EpoxyAttribute
    lateinit var route: String

    override fun bind(holder: ModelGroupHolder) {
        super.bind(holder)
        with(route) {
            holder.rootView.findViewById<TextView>(R.id.route_name).text = route
        }
    }
}