package com.asurspace.vehicledata_boundsofhamburg.ui.screens.mapcompanents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P1LAT
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P1LON
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P2LAT
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P2LON
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.TaxiInfo
import com.asurspace.vehicledata_boundsofhamburg.ui.navigation.Screen
import com.asurspace.vehicledata_boundsofhamburg.ui.navigation.TAXI_INFO
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.MapUIModel
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.DkBlue
import com.asurspace.vehicledata_boundsofhamburg.viewmodels.MapVehicleViewVM
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val GMT_TAG = "GMapTry"

private fun bitmapDescriptorFromVector(
    context: Context,
    vehicleType: String,
    id: Int
): BitmapDescriptor {
    val vectorResId = when {
        vehicleType == "TAXI" -> R.drawable.ic_yellow_car
        (vehicleType == "POOLING" && id % 1000 in (0..250)) -> R.drawable.ic_black_car
        else -> R.drawable.ic_white_car
    }
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

var hamburg10 = LatLng(P1LAT, P1LON)
var hamburg20 = LatLng(P2LAT, P2LON)
val center1 = LatLng(
    ((hamburg10.latitude + hamburg20.latitude) / 2),
    ((hamburg10.longitude + hamburg20.longitude) / 2)
)

val defaultCameraPosition1 = CameraPosition.fromLatLngZoom(center1, 11f)

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterialApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun GMapTry(
    navController: NavController,
    viewModel: MapVehicleViewVM,
    data: MapUIModel,
) {
    val localContext = LocalContext.current
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    var isMapLoaded by remember { mutableStateOf(false) }

    val taxiListState = rememberLazyListState()
    var listIsVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition1
    }

    val searchEnabled = remember { mutableStateOf(true) }

    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = false,
                zoomControlsEnabled = false
            )
        )
    }
    val emptyPointOfInterest = PointOfInterest(LatLng(0.0, 0.0), "", "")
    var shouldAnimateZoom by remember { mutableStateOf(true) }
    var ticker by remember { mutableStateOf(0) }
    var onPointClickInfo by remember { mutableStateOf(emptyPointOfInterest) }
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isTrafficEnabled = true
            )
        )
    }
    val listOfVehicles = remember {
        mutableStateOf(data.poiList)
    }

    val onMapMarkerState = rememberMarkerState()

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
                onPointClickInfo = it
            },
            onMapClick = {
                onPointClickInfo = emptyPointOfInterest
                onMapMarkerState.position = LatLng(0.0, 0.0)
            }, onMapLongClick = {

            }
        ) {

            val markerClick: (Marker) -> Boolean = { marker ->
                Log.d(TPM_TAG, "${marker.title} was clicked")
                cameraPositionState.projection?.let { projection ->
                    Log.d(TPM_TAG, "The current projection is: ${projection.visibleRegion}")
                }

                lifecycleScope.launch {

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
                        lifecycleScope.launch(Dispatchers.Main) {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                TAXI_INFO,
                                taxiInfo
                            )
                            navController.navigate(Screen.VehicleDetail.route)
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

            if (!cameraPositionState.isMoving) {
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

        AnimatedVisibility(
            visible = !cameraPositionState.isMoving,
            modifier = Modifier
                .padding(bottom = 3.dp)
                .align(Alignment.BottomCenter),
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
                    colors = buttonColors(backgroundColor = Color.White),
                    onClick = {
                        val projection = cameraPositionState.projection?.visibleRegion
                        if (projection != null) {
                            viewModel.viewModelScope.launch {
                                viewModel.setCameraPosition(
                                    listOf(
                                        projection.farLeft,
                                        projection.nearRight
                                    )
                                )
                                listOfVehicles.value = viewModel.fetchPoiByCoordinates()
                            }
                        }
                    },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(text = "Search in the area", style = MaterialTheme.typography.body1)
                }
                LazyRow(
                    state = taxiListState,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(start = 8.dp, end = 3.dp)
                ) {
                    items(items = listOfVehicles.value) { taxiInfo ->
                        MapPoiItem(taxiInfo, cameraPositionState)
                    }
                }
            }

        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = onPointClickInfo.placeId != "",
            enter = scaleIn(
                transformOrigin = TransformOrigin.Center,
                animationSpec = tween(durationMillis = 350)
            ),
            exit = scaleOut(
                transformOrigin = TransformOrigin.Center,
                animationSpec = tween(durationMillis = 350)
            ) + fadeOut(),
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
                elevation = 1.dp,
                onClick = {
                    onPointClickInfo = emptyPointOfInterest
                }) {
                Text(
                    text = onPointClickInfo.name,
                    Modifier
                        .padding(12.dp)
                        .align(Alignment.Center)
                        .background(Color.White)
                )
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

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapPoiItem(taxiInfo: TaxiInfo, cameraPositionState: CameraPositionState) {
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    val poi = taxiInfo.poi
    val address = taxiInfo.address
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        modifier = Modifier.padding(end = 5.dp),
        onClick = {
            lifecycleScope.launch(Dispatchers.Main) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            LatLng(address.latitude, address.longitude),
                            15f
                        )
                    )
                )
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
            Text(
                text = "${address.thoroughfare ?: ""} ${address.featureName ?: ""}\n${address.postalCode ?: ""} ${address.adminArea ?: ""}\n${address.countryName ?: ""}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}