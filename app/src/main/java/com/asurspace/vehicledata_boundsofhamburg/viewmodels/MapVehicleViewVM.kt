package com.asurspace.vehicledata_boundsofhamburg.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.datasource.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P1LAT
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P1LON
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P2LAT
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P2LON
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.TaxiInfo
import com.asurspace.vehicledata_boundsofhamburg.domain.map.CoordinateConverter
import com.asurspace.vehicledata_boundsofhamburg.ui.state.MapUIState
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.MapUIModel
import com.asurspace.vehicledata_boundsofhamburg.utils.share
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MapVehicleViewVM @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    private val coordinateConverter: CoordinateConverter,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {
    private var city: String = "Hamburg, Germany"

    private val _uiState = MutableStateFlow<MapUIState>(MapUIState.Empty)
    val uiState = _uiState.share()

    private var camPosition = listOf(LatLng(P1LAT, P1LON), LatLng(P2LAT, P2LON))

    init {
        initPoiByCoordinates()
    }

    fun setCameraPosition(positionList: List<LatLng>) {
        if (positionList != camPosition) {
            camPosition = positionList
        }
    }

    suspend fun fetchPoiByCoordinates(): List<TaxiInfo> =
        viewModelScope.async(coroutineDispatcherProvider.IO()) {
            var taxiInfoList: List<TaxiInfo> = emptyList()
            try {
                taxiInfoList = coordinateConverter.getAddressList(camPosition, viewModelScope)
                    .sortedBy { it.poi.fleetType }.asReversed()
            } catch (ex: HttpException) {
                when {
                    ex.code() == 429 -> {
                        onQueryTimeLimit()
                        Log.d("MainVM", "ex.code 429: ${ex.message.toString()} {ex.code()}")
                    }
                    else -> Log.d(TAG, "else: ${ex.message.toString()} {ex.code()}")

                }
            } catch (ex: IOException) {
                Log.d(TAG, "IOException: ${ex.message.toString()} ${ex.localizedMessage}")
            } catch (ex: Exception) {
                onError()
                Log.d(TAG, "Exception: ${ex.message.toString()} ${ex.localizedMessage}")
            } finally {

            }
            return@async taxiInfoList
        }.await()

    private fun initPoiByCoordinates() {
        _uiState.value = MapUIState.Pending
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val taxiInfoList = coordinateConverter.getAddressList(
                    camPosition, viewModelScope
                )

                _uiState.value = MapUIState.Loaded(
                    MapUIModel(
                        city = city,
                        poiList = taxiInfoList.sortedBy { it.poi.fleetType }.asReversed()
                    )
                )
            } catch (ex: HttpException) {
                when {
                    ex.code() == 429 -> {
                        onQueryTimeLimit()
                        Log.d("MainVM", "ex.code 429: ${ex.message.toString()} {ex.code()}")
                    }
                    else -> Log.d(TAG, "else: ${ex.message.toString()} {ex.code()}")

                }
            } catch (ex: IOException) {
                Log.d(TAG, "IOException: ${ex.message.toString()} ${ex.localizedMessage}")
            } catch (ex: Exception) {
                onError()
                Log.d(TAG, "Exception: ${ex.message.toString()} ${ex.localizedMessage}")
            } finally {

            }
        }
    }

    private fun onQueryTimeLimit() {
        _uiState.value = MapUIState.Error(
            applicationContext.getString(R.string.query_limit_reached)
        )
    }

    private fun onError() {
        _uiState.value = MapUIState.Error(
            applicationContext.getString(R.string.something_went_wrong)
        )
    }

    companion object {
        @JvmStatic
        val TAG = "MapVehicleViewVM"
    }
}