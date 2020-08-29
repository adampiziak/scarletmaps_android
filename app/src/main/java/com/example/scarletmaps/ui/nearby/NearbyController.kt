package com.example.scarletmaps.ui.nearby

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.CarouselModel_
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.messageNoNearbyPlaces
import com.example.scarletmaps.messageNoNearbyTransport
import com.example.scarletmaps.ui.epoxy.*
import com.example.scarletmaps.ui.nearby.views.routeContainer

// Epoxy controller for Nearby fragment
class NearbyController : AsyncEpoxyController() {

    // Active routes serving stops within 500m of user
    var routes: List<NearbyRoute> = emptyList()
    set(value) {
        field = value
        requestModelBuild()
    }

    // Active bus stops within 500m of user
    var stops: List<Pair<Stop, Float>> = emptyList()
    set(value) {
        field = value
        requestModelBuild()
    }

    // Nearby places/buildings
    var places: List<NearbyPlace> = emptyList()
    set(value) {
        field = value
        requestModelBuild()
    }

    // Model layout
    override fun buildModels() {
        if (places.isNotEmpty()) {

            nearbyPlace {
                id(places[0].id)
                place(places[0])
            }
        } else {
            messageNoNearbyPlaces { id() }
        }

        if (stops.isNotEmpty()) {
            val routeModels: List<NearbyRouteItemModel_> = routes.map {
                NearbyRouteItemModel_().id(it.id).route(it)
            }
            if (routeModels.isNotEmpty()) {
                Carousel.setDefaultGlobalSnapHelperFactory(null)
                CarouselModel_().id(123123).models(routeModels).padding(Carousel.Padding.dp(20, 0, 0,30, 0)).addTo(this)
            }
                //CarouselModel_().id(123123).models(routeModels).padding(Carousel.Padding.dp(20, 0, 0,30, 0)).addTo(this)

            sectionTitle {
                id()
                title("nearest stops")
            }
            stops.forEachIndexed { index, stop ->
                nearbyStopItem {
                    id(stop.first.id)
                    name(stop.first.name)
                    area(stop.first.area)
                    distance(stop.second)
                    order(index)
                    stop(stop.first)
                }
            }
        } else {
            messageNoNearbyTransport { id() }
        }


    }

}