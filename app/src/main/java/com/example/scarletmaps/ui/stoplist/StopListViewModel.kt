package com.example.scarletmaps.ui.stoplist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.scarletmaps.data.ScarletMapsRepository
import com.example.scarletmaps.data.models.stop.Stop

class StopListViewModel @ViewModelInject constructor(repository: ScarletMapsRepository) : ViewModel() {
    val stops: LiveData<List<Stop>> = repository.getStopList()
    val loaded: LiveData<Boolean> = repository.getStopListStatus()
}