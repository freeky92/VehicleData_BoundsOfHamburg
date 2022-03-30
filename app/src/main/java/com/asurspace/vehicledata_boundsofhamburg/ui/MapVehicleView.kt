package com.asurspace.vehicledata_boundsofhamburg.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.MapVehicleView
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.VehicleDetails
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.VehiclePoiList
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.VehicleData_BoundsOfHamburgTheme

@Composable
fun VehicleDataBoundsOfHamburgApp() {
    val navController = rememberNavController()

    VehicleData_BoundsOfHamburgTheme {
        NavHost(navController = navController, startDestination = MAP) {
            composable(MAP) {
                MapVehicleView(hiltViewModel())
            }
            composable(Destinations.VEHICLE_POI_LIST.name) {
                VehiclePoiList(hiltViewModel())
            }
            composable(Destinations.VEHICLE_DETAILS.name) {
                VehicleDetails()
            }

        }
    }


}