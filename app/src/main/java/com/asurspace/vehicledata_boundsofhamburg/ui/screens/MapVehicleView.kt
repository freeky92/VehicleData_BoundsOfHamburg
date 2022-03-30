package com.asurspace.vehicledata_boundsofhamburg.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.mapcompanents.TaxiPoiMap
import com.asurspace.vehicledata_boundsofhamburg.ui.state.MapUIState
import com.asurspace.vehicledata_boundsofhamburg.utils.ErrorDialog
import com.asurspace.vehicledata_boundsofhamburg.viewmodels.MapVehicleViewVM

const val MAP_TAG = "MapVehicleView"

@Composable
fun MapVehicleView(
    navController: NavController,
    viewModel: MapVehicleViewVM = viewModel()
) {
    when (val state = viewModel.uiState.collectAsState().value) {

        is MapUIState.Empty -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_data),
                    modifier = Modifier.padding(16.dp)
                )
            }
            Log.d(MAP_TAG, "MapUiState on empty.")
        }

        is MapUIState.Pending -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
            Log.d(MAP_TAG, "MapUiState on pending.")
        }

        is MapUIState.Error -> {
            ErrorDialog(state.message)
            Log.d(MAP_TAG, "MapUiState on error.")
        }

        is MapUIState.Loaded -> {
            TaxiPoiMap(navController, viewModel, state.data, modifier = Modifier.fillMaxSize())
            Log.d(MAP_TAG, "MapUiState on loaded.")
        }
    }
}




