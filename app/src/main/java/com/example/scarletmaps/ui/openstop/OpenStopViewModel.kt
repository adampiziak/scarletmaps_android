package com.example.scarletmaps.ui.openstop

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.scarletmaps.data.ScarletMapsRepository


class OpenStopViewModel
@ViewModelInject constructor(val repository: ScarletMapsRepository,
                             @Assisted private val savedStateHandle: SavedStateHandle) :
    ViewModel() {

    val id = savedStateHandle.get<Int>("id")!!
    val stop = repository.getStop(id)
    val routes = repository.getStopRoutes(id)
}