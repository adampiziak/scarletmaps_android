package com.example.scarletmaps.ui.buildinglist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.scarletmaps.data.ScarletMapsRepository

class BuildingListViewModel @ViewModelInject constructor(repository: ScarletMapsRepository) : ViewModel() {

    val buildingList = repository.getBuildingList()
}