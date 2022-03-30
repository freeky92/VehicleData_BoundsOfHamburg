package com.asurspace.vehicledata_boundsofhamburg.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.asurspace.vehicledata_boundsofhamburg.*
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.ui.state.VehicleListUIState
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.VehicleListUIModel
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.White1
import com.asurspace.vehicledata_boundsofhamburg.utils.ErrorDialog
import com.asurspace.vehicledata_boundsofhamburg.viewmodels.VehiclePoiListVM
import kotlinx.coroutines.launch

const val VPL_TAG = "VehiclePoiList"

@Composable
fun VehiclePoiList(navController: NavController, viewModel: VehiclePoiListVM = viewModel()){
    when (val state = viewModel.uiState.collectAsState().value) {

        is VehicleListUIState.Empty -> {
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
            Log.d(VPL_TAG, "MapUiState on empty.")
        }

        is VehicleListUIState.Pending -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
            Log.d(VPL_TAG, "MapUiState on pending.")
        }

        is VehicleListUIState.Error -> {
            ErrorDialog(state.message)
            Log.d(VPL_TAG, "MapUiState on error.")
        }

        is VehicleListUIState.Loaded -> {
            PoiListLoadedScreen(viewModel, state.data)
            Log.d(VPL_TAG, "MapUiState on loaded.")
        }
    }
}

@Composable
fun PoiListLoadedScreen(viewModel: VehiclePoiListVM = viewModel(), data:  VehicleListUIModel) {
    Text(
        text = data.city,
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.h1
    )
    PoiList(poiList = data.poiList)
}

@Composable
fun PoiList(poiList: List<Poi>) {
    val taxiListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box() {
        LazyColumn(
            state = taxiListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(items = poiList) { poi ->
                PoiItem(poi)
            }

        }
        val showButton by remember { derivedStateOf { taxiListState.firstVisibleItemIndex > 0 } }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            visible = showButton,
            exit = fadeOut(),
            enter = fadeIn(),
        ) {
            Surface(
                color = White1.compositeOver(Color.White),
                shape = CircleShape,
                elevation = 3.dp,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_up_24),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(14.dp)
                        .clickable {
                            scope.launch { taxiListState.scrollToItem(0) }
                        }
                )
            }
        }
    }

}

@Composable
fun PoiItem(poi: Poi) {
    Column(modifier = Modifier
        .padding(start = 16.dp, top = 16.dp)
        .clickable { }) {
        if (poi.fleetType == "TAXI") {
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
        } else if (poi.fleetType == "POOLING") {
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
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_marker12),
                contentDescription = null,
                modifier = Modifier.size(width = 18.dp, height = 18.dp)
            )
            Text(
                text = "id: ${poi.id}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}