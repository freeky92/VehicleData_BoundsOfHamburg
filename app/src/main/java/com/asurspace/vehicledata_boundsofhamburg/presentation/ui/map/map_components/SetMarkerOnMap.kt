package com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.map_components

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.asurspace.vehicledata_boundsofhamburg.data.model.TaxiInfo
import com.asurspace.vehicledata_boundsofhamburg.navigation.Screen
import com.asurspace.vehicledata_boundsofhamburg.navigation.TAXI_INFO
import com.asurspace.vehicledata_boundsofhamburg.presentation.theme.DkBlue
import com.asurspace.vehicledata_boundsofhamburg.common.utils.bitmapDescriptorFromVector
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.view.MAP_TAG
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.viewmodel.MapVehicleViewVM
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SetMarkerOnMap(
    navController: NavController,
    viewModel: MapVehicleViewVM,
    listOfVehicles: MutableState<List<TaxiInfo>>,
    cameraPositionState: CameraPositionState,
    isMapLoaded: Boolean
) {

    val viewModelScope = viewModel.viewModelScope

    val markerClick: (Marker) -> Boolean = { marker ->
        Log.d(MAP_TAG, "${marker.title} was clicked")
        cameraPositionState.projection?.let { projection ->
            Log.d(MAP_TAG, "The current projection is: ${projection.visibleRegion}")
        }

        false
    }

    listOfVehicles.value.forEach { taxiInfo ->
        MarkerInfoWindow(
            state = MarkerState(
                LatLng(
                    taxiInfo.address.latitude,
                    taxiInfo.address.longitude
                )
            ),
            title = taxiInfo.poi.fleetType,
            icon = bitmapDescriptorFromVector(
                LocalContext.current,
                taxiInfo.poi.fleetType, taxiInfo.poi.id
            ),
            onClick = markerClick,
            onInfoWindowClick = {
                if (isMapLoaded) {
                    viewModelScope.launch(Dispatchers.Main) {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            TAXI_INFO,
                            taxiInfo
                        )
                        navController.navigate(Screen.VehicleDetail.route)
                    }
                }
            },
        ) {
            if (taxiInfo.poi.fleetType == "TAXI") {
                Surface(
                    color = Color.Yellow,
                    shape = CircleShape,
                    elevation = 1.dp,
                    modifier = Modifier,
                ) {
                    Row {
                        Text(
                            text = "${it.title} №${taxiInfo.poi.id}",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .padding(10.dp)
                        )
                    }
                }
            } else if (taxiInfo.poi.fleetType == "POOLING") {
                Surface(
                    shape = CircleShape,
                    elevation = 1.dp,
                    modifier = Modifier,
                    color = DkBlue,
                    contentColor = Color.White
                ) {
                    Text(
                        text = it.title ?: taxiInfo.poi.fleetType,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}