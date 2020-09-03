package com.example.scarletmaps.ui.openstop

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scarletmaps.data.ScarletMapsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class OpenStopViewModel
@ViewModelInject constructor(val repository: ScarletMapsRepository,
                             @Assisted private val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    val id = savedStateHandle.get<Int>("id")!!
    val stop = repository.getStop(id)
    val routes = repository.getStopRoutes(id)
    val arrivals = repository.getStopArrivals(id)

    init {
        viewModelScope.launch {
            while (true) {
                repository.refreshStopArrivals(id)
                delay(30000)
            }
        }
    }
}