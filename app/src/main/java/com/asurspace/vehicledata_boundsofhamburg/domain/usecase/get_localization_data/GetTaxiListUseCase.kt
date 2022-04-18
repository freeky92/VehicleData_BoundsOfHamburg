package com.asurspace.vehicledata_boundsofhamburg.domain.usecase.get_localization_data

import android.location.Geocoder
import com.asurspace.vehicledata_boundsofhamburg.data.model.TaxiInfo
import com.asurspace.vehicledata_boundsofhamburg.domain.repository.LocalizationDataRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class GetTaxiListUseCase @Inject constructor(
    private val localizationDataRepository: LocalizationDataRepository,
    private val geocoder: Geocoder,
) {

    suspend fun getAddressList(
        coordinates: List<LatLng>,
        coroutineScope: CoroutineScope
    ) = coroutineScope.async(Dispatchers.IO) {
        val poiList = localizationDataRepository.fetchPoiByCoordinates(
            coordinates[0],
            coordinates[1],
        ).poiList

        val taxiInfoList = poiList.map { poi ->
            val address = geocoder.getFromLocation(
                poi.coordinate.latitude,
                poi.coordinate.longitude,
                1
            )[0]

            return@map TaxiInfo(poi, address)
        }.toMutableList()
        return@async taxiInfoList
    }.await()

}