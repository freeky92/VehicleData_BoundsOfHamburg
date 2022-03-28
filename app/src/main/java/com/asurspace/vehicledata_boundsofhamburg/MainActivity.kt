package com.asurspace.vehicledata_boundsofhamburg

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asurspace.vehicledata_boundsofhamburg.MainActivity.Companion.TAG_COM
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Coordinate
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.ui.state.MapUiState
import com.asurspace.vehicledata_boundsofhamburg.ui.state.model.MapUiModel
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.VehicleData_BoundsOfHamburgTheme
import dagger.hilt.android.AndroidEntryPoint

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
                Text(
                    text = stringResource(R.string.no_data),
                    modifier = Modifier.padding(16.dp)
                )
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
                PoiListLoadedScreen(state.data)
                Log.d(TAG_COM, "MapUiState on loaded.")
            }
        }
    }
}

@Composable
fun PoiListLoadedScreen(data: MapUiModel) {
    Text(
        text = data.city,
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
    )
    PoiList(poiList = data.poiList)
}


@Composable
fun PoiList(poiList: List<Poi>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 12.dp)
    ) {
        items(items = poiList) { poi ->
            PoiItem(poi)
        }
    }
}

@Composable
fun PoiItem(poi: Poi) {
    Column {
        Text(
            text = "fleetType: ${poi.fleetType}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        Text(
            text = "id: ${poi.id}",
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun ErrorDialog(message: String) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.error))
            },
            text = {
                Text(message)
            },
            confirmButton = {
                openDialog.value = false
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
                MapUiModel(
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
