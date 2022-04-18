package com.asurspace.vehicledata_boundsofhamburg.presentation.ui

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
import androidx.navigation.compose.rememberNavController
import com.asurspace.vehicledata_boundsofhamburg.common.state.models.VehicleListUIModel
import com.asurspace.vehicledata_boundsofhamburg.data.model.Poi
import com.asurspace.vehicledata_boundsofhamburg.navigation.VehicleDataBoundsOfHamburgNavController
import com.asurspace.vehicledata_boundsofhamburg.presentation.theme.VehicleData_BoundsOfHamburgTheme
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.vehicles_list.view.PoiListLoadedScreen
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehicleData_BoundsOfHamburgTheme {
                VehicleDataBoundsOfHamburgNavController()
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
    val navController = rememberNavController()
    VehicleData_BoundsOfHamburgTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            PoiListLoadedScreen(
                navController,
                data = VehicleListUIModel(
                    city = "Hamburg, Germany",
                    poiList = listOf(
                        Poi(1, 1235.4, "First", LatLng(54.2, 20.1)),
                        Poi(2, 1235.4, "Second", LatLng(24.2, 25.1)),
                        Poi(3, 1235.4, "Third", LatLng(11.2, 10.1)),
                        Poi(4, 1235.4, "Forth", LatLng(14.2, 45.1)),
                        Poi(5, 1235.4, "Fifth", LatLng(33.2, 34.1)),
                    )
                )
            )
        }
    }
}
