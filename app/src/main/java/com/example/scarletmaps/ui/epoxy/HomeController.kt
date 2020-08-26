package com.example.scarletmaps.ui.epoxy

import com.airbnb.epoxy.AsyncEpoxyController
import com.example.scarletmaps.data.models.stop.Stop

class HomeController : AsyncEpoxyController() {

    var stopList: List<Stop> = emptyList()
    set(value) {
        field = value
        requestModelBuild()
    }

    override fun buildModels() {

        stopList.forEach {
            stopItem {
                id(it.id)
                stop(it)
            }
        }
    }
}