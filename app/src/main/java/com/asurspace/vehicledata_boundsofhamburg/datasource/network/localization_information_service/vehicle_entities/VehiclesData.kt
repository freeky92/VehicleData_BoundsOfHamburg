package com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehiclesData(
    @SerializedName("poiList") val poiList: List<Poi>
) : Parcelable

@Parcelize
data class Poi(
    @SerializedName("id") val id: Int,
    @SerializedName("heading") val heading: Double,
    @SerializedName("fleetType") val fleetType: String,
    @SerializedName("coordinate") val coordinate: LatLng,
) : Parcelable