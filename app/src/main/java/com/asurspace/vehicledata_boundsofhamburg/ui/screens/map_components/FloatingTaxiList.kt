package com.asurspace.vehicledata_boundsofhamburg.ui.screens.map_components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.TaxiInfo
import com.asurspace.vehicledata_boundsofhamburg.viewmodels.MapVehicleViewVM
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.launch

@Composable
fun FloatingTaxiList(
    viewModel: MapVehicleViewVM,
    listOfVehicles: MutableState<List<TaxiInfo>>,
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier,
    isMapLoaded: Boolean,
) {
    val viewModelScope = viewModel.viewModelScope

    AnimatedVisibility(
        visible = !cameraPositionState.isMoving && isMapLoaded,
        modifier = modifier
            .padding(bottom = 3.dp),
        exit = slideOutVertically(
            targetOffsetY = { fullSize ->
                fullSize / 2
            }, animationSpec = tween(durationMillis = 350)
        ) + fadeOut(),
        enter = slideInVertically(
            initialOffsetY = { fullSize ->
                fullSize * 2
            },
            animationSpec = tween(
                delayMillis = 650,
                durationMillis = 350
            )
        ) + fadeIn(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                onClick = {
                    val projection = cameraPositionState.projection?.visibleRegion
                    if (projection != null) {
                        viewModelScope.launch {
                            viewModel.setCameraPosition(
                                listOf(
                                    projection.farLeft,
                                    projection.nearRight
                                ),
                                projection.latLngBounds.center
                            )
                            viewModel.fetchByCoordinate()
                        }
                    }
                },
                modifier = Modifier.wrapContentSize()
            ) {
                Text(text = "Search in the area", style = MaterialTheme.typography.body1)
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 8.dp, end = 3.dp)
            ) {
                items(items = listOfVehicles.value) { taxiInfo ->
                    OnMapPoiItem(taxiInfo, cameraPositionState)
                }
            }
        }

    }
}