package com.example.scarletmaps.ui.routeviewer

import com.airbnb.epoxy.AsyncEpoxyController
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.ui.epoxy.*

class RouteViewerController(routeInitial: Route): AsyncEpoxyController() {
    var route: Route = routeInitial
        set (value) {
            field = value
            requestModelBuild()
        }
    var stopByArea: ArrayList<ArrayList<Stop>> = ArrayList()
        set(value) {
            field = value
            requestModelBuild()
        }

    var arrivals: ArrayList<Arrival> = ArrayList()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        var baseNumeral = 0;
        val areaModels = route.areas.mapIndexed { i, v -> AreaItemModel_().apply {
            id(v)
            name(v)
        } }
        headerRouteViewer(areaModels) {
            id(route.id)
            route(route.name)
        }
        stopByArea.forEachIndexed { i, lists ->
            val models = lists.mapIndexed { index, stop ->
                val times = arrivals.find { it.stop_id == stop.id }
                val arrivalTimes = if (times != null) {
                    times.arrivals
                } else {
                    emptyList<Long>()
                }
                RouteStopItemModel_().apply {
                    id(stop.id)
                    stop(stop)
                    arrivals(arrivalTimes.take(5))
                    stop_index(index+baseNumeral+1)
                    excludeDivider(index == 0)
                }
            }
            baseNumeral += lists.size
            campusStopList(models) {
                id("$i")
                title(lists[0].area.capitalize())
            }

        }
    }

}