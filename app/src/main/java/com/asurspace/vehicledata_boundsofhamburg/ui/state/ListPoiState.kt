package com.asurspace.vehicledata_boundsofhamburg.ui.state

import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.MapUIModel

sealed class ListPoiState {
    object Empty : ListPoiState()
    object Pending : ListPoiState()
    class Loaded(val data: List<Poi>) : ListPoiState()
    class Error(val message: String): ListPoiState()
}