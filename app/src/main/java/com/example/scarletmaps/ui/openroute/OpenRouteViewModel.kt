package com.example.scarletmaps.ui.openroute

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import com.example.scarletmaps.data.ScarletMapsRepository
import com.example.scarletmaps.data.models.Vehicle
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.segment.Segment
import com.example.scarletmaps.data.models.stop.Stop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OpenRouteViewModel
@ViewModelInject constructor(
    private val repository: ScarletMapsRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // ID passed from another fragment
    private val routeId: LiveData<Int> = savedStateHandle.getLiveData("id")

    val route: LiveData<Route> = switchMap(routeId) { id ->
        repository.getRoute(id)
    }

    val stopList: LiveData<List<Stop>> = switchMap(route) {
        repository.getRouteStops(it)
    }

    val stopListByArea: LiveData<List<List<Stop>>> = map(stopList) {stopList ->
        val stopListByArea = mutableListOf<List<Stop>>()
        val currentList = mutableListOf<Stop>()
        var currentArea = stopList[0].area
        for (stop in stopList) {
            if (stop.area == currentArea) {
                currentList.add(stop)
            } else {
                stopListByArea.add(ArrayList(currentList))
                currentList.clear()
                currentList.add(stop)
                currentArea = stop.area
            }
        }
        stopListByArea.add(ArrayList(currentList))

        stopListByArea
    }

    val segments: LiveData<List<Segment>> = switchMap(route) {
        repository.getRouteSegments(it)
    }

    val vehicles: MutableLiveData<List<Vehicle>> = MutableLiveData(emptyList())

    val arrivals: LiveData<List<Arrival>> = switchMap(routeId) { id ->
        repository.getRouteArrivals(id)
    }

    init {
        // Update vehicle position every 10 seconds
        viewModelScope.launch {
            while (true) {
                val id = routeId.value
                if (id != null) {
                    repository.getRouteVehicles(id, object : Callback<List<Vehicle>> {
                        override fun onResponse(
                            call: Call<List<Vehicle>>,
                            response: Response<List<Vehicle>>
                        ) {
                            val body = response.body()
                            if (body != null) {
                                vehicles.value = body
                            }
                        }

                        override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {

                        }
                    })
                    delay(3000)
                }
            }
        }

        // Update arrival data every 30 seconds
        viewModelScope.launch {
            while (true) {
                val id = routeId.value
                if (id != null) {
                    repository.refreshRouteArrivals(id)
                    delay(30000)
                }
            }
        }
    }
}