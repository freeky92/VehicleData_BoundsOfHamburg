package com.asurspace.vehicledata_boundsofhamburg.ui.screens.mapcompanents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.ui.navigation.POI
import com.asurspace.vehicledata_boundsofhamburg.ui.navigation.Screen
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.MapUIModel
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.DkBlue
import com.asurspace.vehicledata_boundsofhamburg.viewmodels.MapVehicleViewVM
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.BitmapDescriptor
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng

const val TPM_TAG = "TaxiPoiMap"

typealias OnMapReady = (GoogleMap) -> Unit

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

val defaultCameraPosition: CameraPosition = CameraPosition.fromLatLngZoom(center, 11f)

@Composable
fun TaxiPoiMap(
    navController: NavController,
    viewModel: MapVehicleViewVM,
    data: MapUIModel,
) {

    val onMapReady: OnMapReady = { map ->



    }


    Box(Modifier.fillMaxSize()) {

        data.poiList.forEach { poi ->

            Log.d(TPM_TAG, poi.id.toString())
            navController.currentBackStackEntry?.arguments?.putParcelable(
                POI,
                poi
            )
            navController.navigate(Screen.VehicleDetail.route)



            if (poi.fleetType == "TAXI") {
                Surface(
                    color = Color.Yellow,
                    shape = CircleShape,
                    elevation = 1.dp,
                ) {
                    Text(
                        text = "${poi.fleetType} ",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
            } else if (poi.fleetType == "POOLING") {
                Surface(
                    color = DkBlue,
                    shape = CircleShape,
                    elevation = 1.dp,
                    contentColor = Color.White,
                ) {
                    Text(
                        text = poi.fleetType,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
        }

    }

}

@Composable
fun rememberMapViewWithLifecycle(
    modifier: Modifier = Modifier,
    onMapReady: OnMapReady
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val lifecycleObserver = rememberMapLifecycle(mapView)

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    AndroidView(factory = {
        mapView.apply {
            mapView.getMapAsync { gMap ->
                onMapReady(gMap)
            }
        }
    }, modifier = Modifier.fillMaxSize())
}

@Composable
fun rememberMapLifecycle(map: MapView): LifecycleEventObserver {
    return remember {
        LifecycleEventObserver { source, event ->
            when (event) {
                ON_CREATE -> {
                    map.onCreate(Bundle())
                }
                ON_START -> {
                    map.onStart()
                }
                ON_RESUME -> {
                    map.onResume()
                }
                ON_PAUSE -> {
                    map.onPause()
                }
                ON_STOP -> {
                    map.onStop()
                }
                ON_DESTROY -> {
                    map.onDestroy()
                }
                ON_ANY -> throw IllegalStateException("Map invalid map state onAny()")
            }
        }
    }
}
