package com.example.scarletmaps.ui.routeviewer

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.scarletmaps.data.ScarletMapsRepository
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.stop.Stop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RouteViewerViewModel
@ViewModelInject constructor(private val repository: ScarletMapsRepository,
                             @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val id = savedStateHandle.get<Int>("id")!!
    val routeImmediate = repository.getRouteImmediate(id)
    val route = repository.getRoute(id)
    val stopList = repository.getStopList()
    fun getFilteredList(): LiveData<List<Stop>> {
        return Transformations.map(stopList) {
            val filteredStops = ArrayList<Stop>()
            for (id in routeImmediate.stops) {
                val stop = it.find { stop -> stop.id == id }
                if (stop != null) {
                    filteredStops.add(stop)
                }
            }
            filteredStops
        }
    }


    val arrivals = repository.getRouteArrivals(id)

    init {
        viewModelScope.launch {
            while (true) {
                repository.refreshRouteArrivals(id)
                delay(30000)
            }
        }
    }
}