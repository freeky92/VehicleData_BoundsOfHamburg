package com.asurspace.vehicledata_boundsofhamburg.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asurspace.vehicledata_boundsofhamburg.data.model.TaxiInfo
import com.asurspace.vehicledata_boundsofhamburg.presentation.theme.VehicleData_BoundsOfHamburgTheme
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.view.MapVehiclesScreen
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.vehicle_details.view.VehicleDetails
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.vehicles_list.view.VehiclePoiList

//entities
const val POI_LIST = "poiList"
const val TAXI_INFO = "taxi_info"

@Composable
fun VehicleDataBoundsOfHamburgNavController() {
    val navController = rememberNavController()

    VehicleData_BoundsOfHamburgTheme {
        NavHost(
            navController = navController,
            startDestination = Screen.MapVehicleView.route
        ) {
            composable(route = Screen.MapVehicleView.route) {
                MapVehiclesScreen(navController, hiltViewModel())
            }
            composable(route = Screen.VehiclePoiList.route) {
                VehiclePoiList(navController, hiltViewModel())
            }
            composable(route = Screen.VehicleDetail.route) {
                val taxiInfo =
                    navController.previousBackStackEntry?.savedStateHandle?.get<TaxiInfo>(TAXI_INFO)
                taxiInfo?.let {
                    VehicleDetails(navController, it)
                }
            }
        }
    }


}