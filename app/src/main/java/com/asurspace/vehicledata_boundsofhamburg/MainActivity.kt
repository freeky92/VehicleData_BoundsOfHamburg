package com.asurspace.vehicledata_boundsofhamburg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Coordinate
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
import com.asurspace.vehicledata_boundsofhamburg.ui.VehicleDataBoundsOfHamburgApp
import com.asurspace.vehicledata_boundsofhamburg.ui.screens.PoiListLoadedScreen
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.VehicleListUIModel
import com.asurspace.vehicledata_boundsofhamburg.ui.theme.VehicleData_BoundsOfHamburgTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehicleData_BoundsOfHamburgTheme {
                VehicleDataBoundsOfHamburgApp()
            }
        }
    }

    companion object {
        @JvmStatic
        val TAG_COM: String = MainActivity::class.java.simpleName
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
                data = VehicleListUIModel(
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
