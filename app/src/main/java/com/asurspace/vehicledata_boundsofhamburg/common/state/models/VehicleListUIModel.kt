package com.asurspace.vehicledata_boundsofhamburg.common.state.models

import com.asurspace.vehicledata_boundsofhamburg.data.model.Poi
import com.google.android.gms.maps.model.LatLng


data class VehicleListUIModel(
    var city: String = "",
    var requestCoordinates: List<LatLng> = mutableListOf(),
    var poiList: List<Poi> = listOf()
)