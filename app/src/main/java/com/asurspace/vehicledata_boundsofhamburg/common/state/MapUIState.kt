package com.asurspace.vehicledata_boundsofhamburg.common.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.asurspace.vehicledata_boundsofhamburg.common.state.models.MapUIModel

sealed class MapUIState {
    object Empty : MapUIState()
    object Pending : MapUIState()
    class Loaded(val data: MapUIModel) : MapUIState()
    class Error(@DrawableRes val resId: Int,@StringRes val message: Int) : MapUIState()
}