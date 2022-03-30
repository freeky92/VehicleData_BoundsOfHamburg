package com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service

import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.VehiclesData
import com.google.android.libraries.maps.model.LatLng
import javax.inject.Inject

class LocalizationServiceRepository @Inject constructor(
    private val localizationDataService: LocalizationDataService
) {

    suspend fun fetchPoiByCoordinates(
        coordinate1: LatLng,
        coordinate2: LatLng
    ): VehiclesData = localizationDataService.fetchPoiData(
        coordinate1.latitude,
        coordinate1.longitude,
        coordinate2.latitude,
        coordinate2.longitude
    )


}