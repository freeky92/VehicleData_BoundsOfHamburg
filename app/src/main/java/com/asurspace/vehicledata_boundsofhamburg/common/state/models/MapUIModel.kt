package com.asurspace.vehicledata_boundsofhamburg.common.state.models

import com.asurspace.vehicledata_boundsofhamburg.data.model.TaxiInfo
import com.google.android.gms.maps.model.LatLng

data class MapUIModel(
    var city: String = "",
    var requestCoordinates: List<LatLng> = mutableListOf(),
    var taxiList: List<TaxiInfo> = listOf()
)