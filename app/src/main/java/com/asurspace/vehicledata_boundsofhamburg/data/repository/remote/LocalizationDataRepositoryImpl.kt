package com.asurspace.vehicledata_boundsofhamburg.data.repository.remote

import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi
import com.asurspace.vehicledata_boundsofhamburg.data.model.VehiclesData
import com.asurspace.vehicledata_boundsofhamburg.domain.repository.LocalizationDataRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class LocalizationDataRepositoryImpl @Inject constructor(
    private val localizationDataApi: LocalizationDataApi
): LocalizationDataRepository {

    override suspend fun fetchPoiByCoordinates(
        coordinate1: LatLng,
        coordinate2: LatLng
    ): VehiclesData = localizationDataApi.fetchPoiData(
        coordinate1.latitude,
        coordinate1.longitude,
        coordinate2.latitude,
        coordinate2.longitude
    )

}