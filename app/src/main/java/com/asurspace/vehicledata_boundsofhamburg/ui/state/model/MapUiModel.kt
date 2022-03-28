package com.asurspace.vehicledata_boundsofhamburg.ui.state.model

import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Coordinate
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi


data class MapUiModel(
    var city: String = "",
    var requestCoordinates: List<Coordinate> = mutableListOf(),
    var poiList: List<Poi> = listOf()
)