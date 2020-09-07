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
    val buildings: LiveData<List<Building>> = repository.getBuildingList()
    val routes: LiveData<List<Route>> = repository.getRouteList()
    val stops: LiveData<List<Stop>> = repository.getStopList()

    val buildingsInitial: List<Building> = repository.getBuildingListImmediate()
    val routesInitial: List<Route> = repository.getRouteListImmediate()
    val stopsInitial: List<Stop> = repository.getStopListImmediate()

    fun getArrivalPair(route: Int, stop: Int): Arrival? {
        return repository.getArrivalPair(route, stop)
    }

    fun refreshArrivals() {
        repository.getRouteListImmediate().filter { it.active }.forEach { route ->
            repository.refreshRouteArrivals(route.id)
        }
    }

    init {
        viewModelScope.launch {
            while (true) {
                refreshArrivals()
                delay(30000)
            }
        }
    }
}