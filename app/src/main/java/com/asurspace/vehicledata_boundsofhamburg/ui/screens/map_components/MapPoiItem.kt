package com.asurspace.vehicledata_boundsofhamburg.ui.screens.map_components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.TaxiInfo
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.MAP_TAG
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.DkBlue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OnMapPoiItem(taxiInfo: TaxiInfo, cameraPositionState: CameraPositionState) {
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    val poi = taxiInfo.poi
    val address = taxiInfo.address
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        modifier = Modifier
            .padding(end = 5.dp)
            .width(width = 180.dp)
            .height(150.dp),
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
                .padding(16.dp)
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
                    modifier = Modifier.size(width = 19.dp, height = 18.dp)
                )
                Surface(
                    color = Color.Black,
                    contentColor = Color.White,
                    shape = CircleShape,
                ) {
                    Text(
                        text = "id: ${poi.id}",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(
                            start = 6.dp,
                            top = 2.dp,
                            end = 6.dp,
                            bottom = 2.dp
                        )
                    )
                }
            }
            Text(
                maxLines = 3,
                softWrap = true,
                text = address.getAddressLine(0),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 4.dp)
            )
            Log.d(MAP_TAG, address.getAddressLine(0))
        }
    }
}