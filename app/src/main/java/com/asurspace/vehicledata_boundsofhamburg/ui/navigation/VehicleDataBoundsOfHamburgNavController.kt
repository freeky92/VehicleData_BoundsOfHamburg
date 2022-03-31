package com.asurspace.vehicledata_boundsofhamburg.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.MapVehicleView
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.VehicleDetails
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.VehiclePoiList
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.VehicleData_BoundsOfHamburgTheme

//entities
const val POI_LIST = "poiList"
const val POI = "poi"

@Composable
fun VehicleDataBoundsOfHamburgNavController() {
    val navController = rememberNavController()

    VehicleData_BoundsOfHamburgTheme {
        NavHost(
            navController = navController,
            startDestination = Screen.MapVehicleView.route
        ) {
            composable(route = Screen.MapVehicleView.route) {
                MapVehicleView(navController, hiltViewModel())
            }
            composable(route = Screen.VehiclePoiList.route) {
                VehiclePoiList(navController, hiltViewModel())
            }
            composable(route = Screen.VehicleDetail.route) {
                val poi = navController.previousBackStackEntry?.savedStateHandle?.get<Poi>(POI)
                VehicleDetails(navController, poi)
            }
        }
    }


}