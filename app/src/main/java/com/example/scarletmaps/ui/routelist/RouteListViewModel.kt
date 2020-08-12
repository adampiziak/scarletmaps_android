package com.example.scarletmaps.ui.routelist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.scarletmaps.data.ScarletMapsRepository
import com.example.scarletmaps.data.models.route.Route

class RouteListViewModel
@ViewModelInject constructor(repository: ScarletMapsRepository) : ViewModel() {
    val routes: LiveData<List<Route>> = repository.getRouteList()

    val loaded: LiveData<Boolean> = repository.getRouteListStatus()

    val initialRouteList : ArrayList<Route> = repository.getRouteListImmediate()
}