package com.asurspace.vehicledata_boundsofhamburg.ui.state.models

import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Coordinate
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi

data class MapUIModel(
    var city: String = "",
    var requestCoordinates: List<Coordinate> = mutableListOf(),
    var poiList: List<Poi> = listOf()
)