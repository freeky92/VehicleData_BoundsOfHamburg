package com.asurspace.vehicledata_boundsofhamburg.ui.state.models

import android.location.Address
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.TaxiInfo
import com.google.android.gms.maps.model.LatLng

data class MapUIModel(
    var city: String = "",
    var requestCoordinates: List<LatLng> = mutableListOf(),
    var poiList: List<TaxiInfo> = listOf()
)