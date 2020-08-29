package com.example.scarletmaps.ui.openstop

import com.airbnb.epoxy.AsyncEpoxyController
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.stopRoute

class OpenStopController: AsyncEpoxyController()  {
    var routes: List<Route> = emptyList()
    set(updatedList) {
        field = updatedList
        requestModelBuild()
    }

    override fun buildModels() {
        routes.forEach { route ->
            stopRoute {
                id(route.id)
                route(route)
                presenter(OpenStopPresenter())
            }
        }
    }
}