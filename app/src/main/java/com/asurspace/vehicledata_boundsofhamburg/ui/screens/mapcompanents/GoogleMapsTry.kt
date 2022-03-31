package com.asurspace.vehicledata_boundsofhamburg.ui.screens.mapcompanents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.ui.navigation.POI
import com.asurspace.vehicledata_boundsofhamburg.ui.navigation.Screen
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.MapUIModel
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.DkBlue
import com.asurspace.vehicledata_boundsofhamburg.viewmodels.MapVehicleViewVM
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val GMT_TAG = "GMapTry"

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

var hamburg10 = LatLng(LocalizationDataService.P1LAT, LocalizationDataService.P1LON)
var hamburg20 = LatLng(LocalizationDataService.P2LAT, LocalizationDataService.P2LON)
val center1 = LatLng(
    ((hamburg10.latitude + hamburg20.latitude) / 2),
    ((hamburg10.longitude + hamburg20.longitude) / 2)
)

val defaultCameraPosition1 = CameraPosition.fromLatLngZoom(center1, 11f)

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GMapTry(
    navController: NavController,
    viewModel: MapVehicleViewVM,
    data: MapUIModel,
) {

    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    var isMapLoaded by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        this.position = defaultCameraPosition1
    }

    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = false,
                zoomControlsEnabled = false
            )
        )
    }
    var shouldAnimateZoom by remember { mutableStateOf(true) }
    var ticker by remember { mutableStateOf(0) }
    var onPointClickInfo by remember { mutableStateOf("") }
    var mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL, isTrafficEnabled = true))
    }

    val taxiListState = rememberLazyListState()

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
                onPointClickInfo = it.name
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
                MarkerInfoWindow(
                    state = MarkerState(LatLng(poi.coordinate.latitude, poi.coordinate.longitude)),
                    title = poi.fleetType,
                    icon = bitmapDescriptorFromVector(
                        LocalContext.current,
                        R.drawable.car_top_small
                    ),
                    onClick = markerClick,
                    onInfoWindowClick = {
                        lifecycleScope.launch(Dispatchers.Main) {
                            navController.currentBackStackEntry?.savedStateHandle?.set(POI, poi)
                            navController.navigate(Screen.VehicleDetail.route)
                        }
                    },
                ) {
                    if (poi.fleetType == "TAXI") {
                        Surface(
                            color = Color.Yellow,
                            shape = CircleShape,
                            elevation = 1.dp,
                            modifier = Modifier,
                        ) {
                            Row {
                                Text(
                                    text = "${it.title} №${poi.id}",
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier
                                        .padding(10.dp)
                                )
                            }
                        }
                    } else if (poi.fleetType == "POOLING") {
                        Surface(
                            shape = CircleShape,
                            elevation = 1.dp,
                            modifier = Modifier,
                            color = DkBlue,
                            contentColor = Color.White
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
        LazyRow(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(items = data.poiList) { poi ->
                MapPoiItem(navController, poi, cameraPositionState)
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
                        .wrapContentSize()
                )
            }
        }
    }
}

@Composable
fun OnPointInfoWindow(pointName: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.size(300.dp, 300.dp)) {
        Text(text = pointName)
    }
}

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapPoiItem(navController: NavController, poi: Poi, cameraPositionState: CameraPositionState) {
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        modifier = Modifier.padding(5.dp),
        onClick = {
            lifecycleScope.launch {
                cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(poi.coordinate.toLatLng(), 14f)))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (poi.fleetType == "TAXI") {
                Surface(
                    color = Color.Yellow,
                    shape = CircleShape,
                    elevation = 1.dp,
                ) {
                    Text(
                        text = poi.fleetType,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            } else if (poi.fleetType == "POOLING") {
                Surface(
                    shape = CircleShape,
                    elevation = 1.dp,
                    color = DkBlue,
                    contentColor = Color.White,
                    modifier = Modifier
                ) {
                    Text(
                        text = poi.fleetType,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_marker12),
                    contentDescription = null,
                    modifier = Modifier.size(width = 18.dp, height = 18.dp)
                )
                Text(
                    text = "id: ${poi.id}",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}