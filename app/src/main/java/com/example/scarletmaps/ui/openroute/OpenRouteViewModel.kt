package com.example.scarletmaps.ui.openroute

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.scarletmaps.data.ScarletMapsRepository
import com.example.scarletmaps.data.models.Vehicle
import com.example.scarletmaps.data.models.stop.Stop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpenRouteViewModel
@ViewModelInject constructor(private val repository: ScarletMapsRepository,
                             @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val id = savedStateHandle.get<Int>("id")!!
    val routeImmediate = repository.getRouteImmediate(id)
    val route = repository.getRoute(id)
    val segments = repository.getRouteSegments(routeImmediate)
    val segmentsImmediate = repository.getRouteSegmentsImmediate(routeImmediate)
    val stopList = repository.getStopList()
    val vehicles: MutableLiveData<List<Vehicle>> = MutableLiveData(emptyList())


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
                delay(10000)
            }
        }

        viewModelScope.launch {
            while (true) {
                repository.refreshRouteArrivals(id)
                delay(30000)
            }
        }
    }
}