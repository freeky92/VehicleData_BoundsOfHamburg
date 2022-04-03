package com.asurspace.vehicledata_boundsofhamburg.ui.state

import androidx.annotation.DrawableRes
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.MapUIModel
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.VehicleListUIModel

sealed class MapUIState {
    object Empty : MapUIState()
    object Pending : MapUIState()
    class Loaded(val data: MapUIModel) : MapUIState()
    class Error(@DrawableRes val resId: Int,val message: String) : MapUIState()
}