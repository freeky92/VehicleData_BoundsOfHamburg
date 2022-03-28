package com.asurspace.vehicledata_boundsofhamburg.ui.state

import com.asurspace.vehicledata_boundsofhamburg.ui.state.model.MapUiModel

sealed class MapUiState {
    object Empty : MapUiState()
    object Pending : MapUiState()
    class Loaded(val data: MapUiModel) : MapUiState()
    class Error(val message: String) : MapUiState()
}