package com.example.scarletmaps.ui.nearby

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scarletmaps.data.ScarletMapsRepository
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.building.Building
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.stop.Stop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NearbyViewModel @ViewModelInject constructor(val repository: ScarletMapsRepository) : ViewModel() {
    val buildings: ArrayList<Building> = repository.getBuildingList()
    val routes: ArrayList<Route> = repository.getRouteListImmediate()
    val stops: List<Stop> = repository.getStopListImmediate()

    fun getArrivalPair(route: Int, stop: Int): Arrival? {
        return repository.getArrivalPair(route, stop)
    }

    init {
        viewModelScope.launch {
            while (true) {
                repository.getRouteListImmediate().filter { it.active }.forEach { route ->
                    repository.refreshRouteArrivals(route.id)
                }
                delay(30000)
            }
        }
    }
}