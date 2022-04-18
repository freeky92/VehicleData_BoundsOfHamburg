package com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.common.state.MapUIState
import com.asurspace.vehicledata_boundsofhamburg.common.utils.ErrorDialog
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi
import com.asurspace.vehicledata_boundsofhamburg.data.model.TaxiInfo
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.map_components.FloatingTaxiList
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.map_components.SetMarkerOnMap
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.map_components.ShowPlaceInfo
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.map_components.ShowProgress
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.viewmodel.MapVehicleViewVM
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.*

const val MAP_TAG = "MapVehiclesScreen"

var hamburg10 = LatLng(LocalizationDataApi.P1LAT, LocalizationDataApi.P1LON)
var hamburg20 = LatLng(LocalizationDataApi.P2LAT, LocalizationDataApi.P2LON)
val center = LatLng(
    ((hamburg10.latitude + hamburg20.latitude) / 2),
    ((hamburg10.longitude + hamburg20.longitude) / 2)
)

val defaultCameraPosition1 = CameraPosition.fromLatLngZoom(center, 11f)

@Composable
fun MapVehiclesScreen(
    navController: NavController,
    viewModel: MapVehicleViewVM = viewModel()
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition1
    }
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = true,
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false,
            )
        )
    }
    val listOfVehicles = remember { mutableStateOf(emptyList<TaxiInfo>()) }
    val emptyPointOfInterest = PointOfInterest(LatLng(0.0, 0.0), "", "")

    var onPointClickInfo by remember { mutableStateOf(emptyPointOfInterest) }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isTrafficEnabled = true
            )
        )
    }

    val onMapMarkerState = rememberMarkerState()
    Box(Modifier.fillMaxSize()) {

        GoogleMap(modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLoaded = {
                isMapLoaded = true
            },
            onPOIClick = {
                Log.d(MAP_TAG, "POI clicked: ${it.name}")
                onPointClickInfo = it
            },
            onMapClick = {
                onPointClickInfo = emptyPointOfInterest
                onMapMarkerState.position = LatLng(0.0, 0.0)
            }, onMapLongClick = {

            }) {

            when (val state = viewModel.uiState.collectAsState().value) {
                is MapUIState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_data),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Log.d(MAP_TAG, "MapUiState on empty.")
                }

                is MapUIState.Pending -> {
                    isMapLoaded = false
                    Log.d(MAP_TAG, "MapUiState on pending.")
                }

                is MapUIState.Error -> {
                    isMapLoaded = true
                    ErrorDialog(state.resId, state.message, viewModel)
                    listOfVehicles.value = emptyList()
                    Log.d(MAP_TAG, "MapUiState on error.")
                }
                is MapUIState.Loaded -> {
                    isMapLoaded = true
                    listOfVehicles.value = state.data.taxiList
                    Log.d(MAP_TAG, "MapUiState on loaded.")
                    SetMarkerOnMap(
                        navController,
                        viewModel,
                        listOfVehicles,
                        cameraPositionState,
                        isMapLoaded
                    )
                    SetCentralMarker(cameraPositionState, isMapLoaded)
                }
            }
        }
        FloatingTaxiList(
            viewModel,
            listOfVehicles,
            cameraPositionState,
            modifier = Modifier.align(Alignment.BottomCenter),
            isMapLoaded
        )
        ShowPlaceInfo(
            modifier = Modifier.align(Alignment.Center),
            onPointClickInfo = onPointClickInfo,
            onPointClickListener = {
                onPointClickInfo = it
            })
        ShowProgress(isMapLoaded, Modifier.align(Alignment.Center).size(150.dp))
    }
}

@Composable
fun SetCentralMarker(
    cameraPositionState: CameraPositionState,
    isMapLoaded: Boolean
) {
    if (!cameraPositionState.isMoving && isMapLoaded) {
        MarkerInfoWindow(
            MarkerState(position = cameraPositionState.position.target),
            draggable = true
        ) {
            Column {
                Text(
                    text = it.position.latitude.toString(),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Text(
                    text = it.position.longitude.toString(),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }
}



