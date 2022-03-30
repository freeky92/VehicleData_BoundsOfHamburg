package com.asurspace.vehicledata_boundsofhamburg.ui.screens.mapcompanents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.asurspace.vehicledata_boundsofhamburg.MainActivity
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.MapUIModel
import com.asurspace.vehicledata_boundsofhamburg.viewmodels.MapVehicleViewVM
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

const val TPM_TAG = "TaxiPoiMap"

private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

var hamburg1 = LatLng(LocalizationDataService.P1LAT, LocalizationDataService.P1LON)
var hamburg2 = LatLng(LocalizationDataService.P2LAT, LocalizationDataService.P2LON)
val center = LatLng(
    ((hamburg1.latitude + hamburg2.latitude) / 2),
    ((hamburg1.longitude + hamburg2.longitude) / 2)
)

val defaultCameraPosition = CameraPosition.fromLatLngZoom(center, 11f)

@Composable
fun TaxiPoiMap(
    viewModel: MapVehicleViewVM,
    data: MapUIModel,
) {
    var isMapLoaded by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        this.position = defaultCameraPosition
    }

    var uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    var shouldAnimateZoom by remember { mutableStateOf(true) }
    var ticker by remember { mutableStateOf(0) }
    var mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLoaded = {
                isMapLoaded = true
            },
            onPOIClick = {
                Log.d(TPM_TAG, "POI clicked: ${it.name}")
            }
        ) {
            val markerClick: (Marker) -> Boolean = {
                Log.d(TPM_TAG, "${it.title} was clicked")
                cameraPositionState.projection?.let { projection ->
                    Log.d(TPM_TAG, "The current projection is: $projection")
                }
                false
            }

            data.poiList.forEach { poi ->
                MarkerInfoWindowContent(
                    state = MarkerState(poi.coordinate.toLatLng()),
                    title = "${poi.fleetType} - ${poi.id}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                    onClick = markerClick,
                ) {
                    if (poi.fleetType == "TAXI") {
                        Surface(
                            color = Color.Yellow,
                            shape = CircleShape,
                            elevation = 1.dp,
                            modifier = Modifier
                        ) {
                            Text(
                                text = it.title ?: poi.fleetType,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }
                    } else if (poi.fleetType == "POOLING") {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            elevation = 1.dp,
                            modifier = Modifier
                        ) {
                            Text(
                                text = it.title ?: poi.fleetType,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                }

            }
        }
        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier
                    .matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .wrapContentSize()
                )
            }
        }
    }
}