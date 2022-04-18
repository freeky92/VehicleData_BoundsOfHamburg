package com.asurspace.vehicledata_boundsofhamburg.domain.repository

import com.asurspace.vehicledata_boundsofhamburg.data.model.VehiclesData
import com.google.android.gms.maps.model.LatLng

interface LocalizationDataRepository {

    suspend fun fetchPoiByCoordinates(
        coordinate1: LatLng,
        coordinate2: LatLng
    ): VehiclesData

}