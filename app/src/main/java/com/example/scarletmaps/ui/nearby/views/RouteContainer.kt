package com.example.scarletmaps.ui.nearby.views

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelGroup
import com.example.scarletmaps.R

@EpoxyModelClass(layout = R.layout.nearby_routes_container)
abstract class RouteContainer(models: List<EpoxyModel<*>>): EpoxyModelGroup(R.layout.nearby_routes_container, models) {

}