package com.example.scarletmaps.ui.openstop

import android.util.Log
import com.airbnb.epoxy.AsyncEpoxyController
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.stopRoute
import com.example.scarletmaps.utils.ArrivalUtils
import com.example.scarletmaps.utils.TextUtils

class OpenStopController: AsyncEpoxyController()  {
    var routes: List<Route> = emptyList()
    set(updatedList) {
        field = updatedList
        requestModelBuild()
    }

    var arrivals: List<Arrival> = emptyList()
    set(value) {
        field = value
        requestModelBuild()
        Log.d("ADAMSKI", "setting ${value.size} arrivals")
    }

    override fun buildModels() {
        routes.forEach { route ->
            val areas = route.areas.map { TextUtils().capitalizeWords(it) }.joinToString { "${it}" }
            val times = arrivals.find { it.route_id == route.id }
            val arrivalTimes = times?.arrivals ?: emptyList()
            val arrivalText = ArrivalUtils().createArrivalsMessage(arrivalTimes)
            stopRoute {
                id(route.id)
                route(route)
                areas(areas)
                arrivals(arrivalText)
                presenter(OpenStopPresenter())
            }
        }
    }
}