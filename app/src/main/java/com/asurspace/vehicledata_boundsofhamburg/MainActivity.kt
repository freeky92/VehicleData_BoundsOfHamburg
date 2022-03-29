package com.asurspace.vehicledata_boundsofhamburg

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asurspace.vehicledata_boundsofhamburg.MainActivity.Companion.TAG_COM
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Coordinate
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.ui.state.MapUiState
import com.asurspace.vehicledata_boundsofhamburg.ui.state.model.MapUiModel
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.VehicleData_BoundsOfHamburgTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehicleData_BoundsOfHamburgTheme {
                MainBody()
            }
        }
    }

    companion object {
        @JvmStatic
        val TAG_COM: String = MainActivity::class.java.simpleName
    }
}

@Composable
fun MainBody(mainVM: MainVM = viewModel()) {
    Column(modifier = Modifier.background(Color.White)) {
        when (val state = mainVM.uiState.collectAsState().value) {

            is MapUiState.Empty -> {
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
                Log.d(TAG_COM, "MapUiState on empty.")
            }

            is MapUiState.Pending -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
                Log.d(TAG_COM, "MapUiState on pending.")
            }

            is MapUiState.Error -> {
                ErrorDialog(state.message)
                Log.d(TAG_COM, "MapUiState on error.")
            }

            is MapUiState.Loaded -> {
                MainLayer(mainVM, state.data)
                Log.d(TAG_COM, "MapUiState on loaded.")
            }
        }
    }
}

@Composable
fun MainLayer(mainVM: MainVM = viewModel(), data: MapUiModel) {
    Column() {
        //TaxiPoiMap(mainVM)
        PoiListLoadedScreen(mainVM, data)
    }
}


val hamburg1 = LatLng(1.35, 103.87)
val hamburg2 = LatLng(1.40, 103.77)
val defaultCameraPosition = CameraPosition.fromLatLngZoom(hamburg1, 10f)

@Composable
fun TaxiPoiMap(
    mainVM: MainVM, data: List<Poi>, modifier: Modifier,
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
) {
    /*var isMapLoaded by remember { mutableStateOf(false) }
    // Observing and controlling the camera's state can be done with a CameraPositionState
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }

    val singaporeState = rememberMarkerState(position = singapore)
    val singapore2State = rememberMarkerState(position = singapore2)
    val singapore3State = rememberMarkerState(position = singapore3)
    var circleCenter by remember { mutableStateOf(singapore) }
    if (singaporeState.dragState == DragState.END) {
        circleCenter = singaporeState.position
    }*/

}

@Composable
fun PoiListLoadedScreen(mainVM: MainVM = viewModel(), data: MapUiModel) {

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
            Button(
                onClick = { scope.launch { taxiListState.scrollToItem(0) } },
                shape = CircleShape,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_up_24),
                    contentDescription = null,
                )
            }
        }
    }

}


@Composable
fun PoiItem(poi: Poi) {
    Column(modifier = Modifier
        .padding(start = 16.dp, top = 16.dp)
        .clickable {  }) {
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
            Surface(shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                modifier = Modifier) {
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

@Composable
fun ErrorDialog(message: String) {
    val viewModel: MainVM = viewModel()
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                //openDialog.value
            },
            title = {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_out_connection_24),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.error),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            text = {
                Text(message, modifier = Modifier.padding(8.dp))

            },
            confirmButton = {
                Button(modifier = Modifier.padding(8.dp), onClick = {
                    viewModel.fetchPoiByCoordinates()
                    openDialog.value = false
                }, content = {
                    Text(text = "Try again.")
                })
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VehicleData_BoundsOfHamburgTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            PoiListLoadedScreen(
                data = MapUiModel(
                    city = "Hamburg, Germany",
                    poiList = listOf(
                        Poi(1, 1235.4, "First", Coordinate(54.2, 20.1)),
                        Poi(2, 1235.4, "Second", Coordinate(24.2, 25.1)),
                        Poi(3, 1235.4, "Third", Coordinate(11.2, 10.1)),
                        Poi(4, 1235.4, "Forth", Coordinate(14.2, 45.1)),
                        Poi(5, 1235.4, "Fifth", Coordinate(33.2, 34.1)),
                    )
                )
            )
        }
    }
}
