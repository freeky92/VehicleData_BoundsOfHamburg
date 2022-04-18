package com.asurspace.vehicledata_boundsofhamburg.data.remote

import com.asurspace.vehicledata_boundsofhamburg.data.model.VehiclesData
import retrofit2.http.GET
import retrofit2.http.Query

interface LocalizationDataApi {

    @GET(".")
    suspend fun fetchPoiData(
        @Query("p1Lat") p1Lat: Double = P1LAT,
        @Query("p1Lon") p1Lon: Double = P1LON,
        @Query("p2Lat") p2Lat: Double = P2LAT,
        @Query("p2Lon") p2Lon: Double = P2LON,
    ) : VehiclesData

    companion object{
        const val BASE_URL = "https://fake-poi-api.mytaxi.com/"

        // P1
        const val P1LAT = 53.66
        const val P1LON = 9.97
        // P2
        const val P2LAT = 53.54
        const val P2LON = 10.20
    }

}