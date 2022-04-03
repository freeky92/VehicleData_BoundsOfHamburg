package com.asurspace.vehicledata_boundsofhamburg.domain.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.TaxiInfo
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.IOException
import javax.inject.Inject

class CoordinateConverter @Inject constructor(
    private val localizationDataService: LocalizationDataService,
    context: Context
) {

    private val geoCoder = Geocoder(context)

    suspend fun getAddressList(
        coordinates: List<LatLng>,
        coroutineScope: CoroutineScope
    ) = coroutineScope.async(Dispatchers.IO) {
        val poiList = localizationDataService.fetchPoiData(
            coordinates[0].latitude,
            coordinates[0].longitude,
            coordinates[1].latitude,
            coordinates[1].longitude,
        ).poiList

        val taxiInfoList = poiList.map { poi ->
            val address = geoCoder.getFromLocation(
                poi.coordinate.latitude,
                poi.coordinate.longitude,
                1
            )[0]

            return@map TaxiInfo(poi, address)
        }.toMutableList()
        return@async taxiInfoList
    }.await()

    suspend fun getAddress(coordinate: LatLng, coroutineScope: CoroutineScope) =
        coroutineScope.async(Dispatchers.IO) {
            var address = Address(null)
            try {
                address = geoCoder.getFromLocation(
                    coordinate.latitude,
                    coordinate.longitude,
                    1
                )[0]

            } catch (e: IOException) {
                Log.d(this::class.java.simpleName, e.message.toString())
            } catch (e: Exception) {
                Log.d(this::class.java.simpleName, e.message.toString())
            }
            return@async address
        }.await()

    suspend fun getCoordinate(address: String, coroutineScope: CoroutineScope) =
        coroutineScope.async(Dispatchers.IO) {
            var coord = Address(null)
            try {
                coord = geoCoder.getFromLocationName(address, 1)[0]
            } catch (e: IOException) {
                Log.d(this::class.java.simpleName, e.message.toString())
            } catch (e: Exception) {
                Log.d(this::class.java.simpleName, e.message.toString())
            }
            return@async coord
        }.await()
}