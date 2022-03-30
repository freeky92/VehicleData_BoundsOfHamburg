package com.asurspace.vehicledata_boundsofhamburg.ui.state

import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.VehicleListUIModel

sealed class VehicleListUIState {
    object Empty : VehicleListUIState()
    object Pending : VehicleListUIState()
    class Loaded(val data: VehicleListUIModel) : VehicleListUIState()
    class Error(val message: String) : VehicleListUIState()
}