package com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.data.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi.Companion.P1LAT
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi.Companion.P1LON
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi.Companion.P2LAT
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi.Companion.P2LON
import com.asurspace.vehicledata_boundsofhamburg.domain.usecase.get_localization_data.GetTaxiListUseCase
import com.asurspace.vehicledata_boundsofhamburg.common.state.MapUIState
import com.asurspace.vehicledata_boundsofhamburg.common.state.models.MapUIModel
import com.asurspace.vehicledata_boundsofhamburg.common.utils.share
import com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.view.center
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MapVehicleViewVM @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    private val coordinateConverter: GetTaxiListUseCase,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {
    private var city: String = "Hamburg, Germany"

    private val _uiState = MutableStateFlow<MapUIState>(MapUIState.Empty)
    val uiState = _uiState.share()

    private var camPosition = listOf(LatLng(P1LAT, P1LON), LatLng(P2LAT, P2LON))
    private var camCenter = center

    init {
        fetchByCoordinate()
    }

    fun setCameraPosition(positionList: List<LatLng>, camCenter: LatLng) {
        if (positionList != camPosition) {
            camPosition = positionList
            this.camCenter = camCenter
        }
    }

    fun fetchByCoordinate() {
        _uiState.value = MapUIState.Pending
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val taxiInfoList = coordinateConverter.getAddressList(
                    camPosition, viewModelScope
                )

                _uiState.value = MapUIState.Loaded(
                    MapUIModel(
                        city = city,
                        taxiList = taxiInfoList.sortedBy { it.poi.coordinate.distance(camCenter) }
                    )
                )

                Log.d(TAG, camCenter.toString())
                Log.d(TAG, taxiInfoList.sortedBy { it.poi.coordinate.distance(camCenter) }.toString())
            } catch (ex: HttpException) {
                when {
                    ex.code() == 429 -> {
                        onQueryTimeLimit()
                        Log.d("MainVM", "ex.code 429: ${ex.message.toString()} {ex.code()}")
                    }
                    else -> Log.d(TAG, "else: ${ex.message.toString()} {ex.code()}")

                }
            } catch (ex: IndexOutOfBoundsException) {
                Log.d(TAG, "IOOBException: ${ex.message.toString()} ${ex.localizedMessage}")
                thereAreNoVehiclesQuery()
            } catch (ex: IOException) {
                Log.d(TAG, "IOException: ${ex.message.toString()} ${ex.localizedMessage}")
                onError()
            } catch (ex: Exception) {
                Log.d(TAG, "Exception: ${ex.message.toString()} ${ex.localizedMessage}")
                onError()
            } finally {

            }
        }
    }

    private fun LatLng.distance(point: LatLng): Double {
        val distanceX = this.latitude - point.latitude
        val distanceY = this.longitude - point.longitude

        return sqrt(distanceX * distanceX + distanceY * distanceY)
    }

    private fun thereAreNoVehiclesQuery() {
        _uiState.value = MapUIState.Error(
            R.drawable.ic_warning_24,
            R.string.no_vehicles_there
        )
    }


    private fun onQueryTimeLimit() {
        _uiState.value = MapUIState.Error(
            R.drawable.ic_time_24,
            R.string.query_limit_reached
        )
    }

    private fun onError() {
        _uiState.value = MapUIState.Error(
            R.drawable.ic_out_connection_24,
            R.string.something_went_wrong
        )
    }

    companion object {
        @JvmStatic
        val TAG = "MapVehicleViewVM"
    }
}