package com.asurspace.vehicledata_boundsofhamburg.ui.navigation

sealed class Screen(val route: String) {
    object MapVehicleView : Screen("MapVehicleView")
    object VehiclePoiList : Screen("VehiclePoiList")
    object VehicleDetail : Screen("VehicleDetail")
}