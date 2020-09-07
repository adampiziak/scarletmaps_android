package com.example.scarletmaps.ui.openroute

import com.airbnb.epoxy.AsyncEpoxyController
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.ui.epoxy.*
import com.example.scarletmaps.utils.TextUtils

class OpenRouteController(): AsyncEpoxyController() {
    var stopListByArea: List<List<Stop>> = ArrayList()
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
        var baseNumeral = 0

        stopListByArea.forEachIndexed { i, lists ->
            val models = lists.mapIndexed { index, stop ->
                val times = arrivals.find { it.stop_id == stop.id }
                val arrivalTimes = times?.arrivals ?: emptyList()
                RouteStopItemModel_().apply {
                    id(stop.id)
                    stop(stop)
                    arrivals(arrivalTimes.take(5))
                    excludeDivider(index == 0)
                }
            }
            baseNumeral += lists.size
            campusStopList(models) {
                id("$i")
                title(TextUtils().capitalizeWords(lists[0].area))
            }

        }
    }

}