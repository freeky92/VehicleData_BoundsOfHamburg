package com.asurspace.vehicledata_boundsofhamburg.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.ui.navigation.POI

const val VD_TAG = "VehicleDetails"

@Composable
fun VehicleDetails(
    navController: NavController,
    poi: Poi?
) {
    Box {
        Column(modifier = Modifier
            .padding(start = 24.dp, top = 24.dp)
            .clickable {
            }) {
            Text(
                text = "Vehicle",
                style = MaterialTheme.typography.h1,
                modifier = Modifier.padding(start = 4.dp)
            )
            if (poi?.fleetType == "TAXI") {
                Surface(
                    color = Color.Yellow,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 1.dp,
                    modifier = Modifier
                ) {
                    Text(
                        text = poi.fleetType,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
            } else if (poi?.fleetType == "POOLING") {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    elevation = 1.dp,
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
            Text(
                text = "id: ${poi?.id}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = "heading: ${poi?.heading}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 4.dp)
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_marker12),
                    contentDescription = null,
                    modifier = Modifier.size(width = 18.dp, height = 18.dp)
                )
                Text(
                    text = "coordinate: ${poi?.coordinate?.latitude}, ${poi?.coordinate?.longitude}",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

        }
    }
}