package com.asurspace.vehicledata_boundsofhamburg.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.datasource.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.LocalizationServiceRepository
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P1LAT
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P1LON
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P2LAT
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P2LON
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.vehicle_entities.Poi
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
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MapVehicleViewVM @Inject constructor(
    private val localizationServiceRepository: LocalizationServiceRepository,
    @ApplicationContext val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {
    private var city: String = "Hamburg, Germany"

    private val _uiState = MutableStateFlow<MapUIState>(MapUIState.Empty)
    val uiState = _uiState.share()

    init {
        initPoiByCoordinates()
    }

    suspend fun fetchPoiByCoordinates(
        coordinate1: LatLng = LatLng(P1LAT, P1LON),
        coordinate2: LatLng = LatLng(P2LAT, P2LON)
    ): List<Poi> = viewModelScope.async(coroutineDispatcherProvider.IO()) {
        var list: List<Poi> = emptyList()
        try {
            val response =
                localizationServiceRepository.fetchPoiByCoordinates(coordinate1, coordinate2)
            list = response.poiList.sortedBy { it.fleetType }.asReversed()
        } catch (ex: HttpException) {
            when {
                ex.code() == 429 -> {
                    Log.d("MainVM", "ex.code 429: ${ex.message.toString()} {ex.code()}")
                    onQueryTimeLimit()
                }
                else -> Log.d("MainVM", "else: ${ex.message.toString()} {ex.code()}")

            }
        } catch (ex: Exception) {
            Log.d("MainVM", "Exception: ${ex.message.toString()} ${ex.localizedMessage}")
            onError()
        } finally {

        }
        return@async list
    }.await()


    private fun initPoiByCoordinates() {
        _uiState.value = MapUIState.Pending
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response =
                    localizationServiceRepository.fetchPoiByCoordinates(
                        LatLng(P1LAT, P1LON), LatLng(
                            P2LAT, P2LON
                        )
                    )
                _uiState.value = MapUIState.Loaded(
                    MapUIModel(
                        city = city,
                        poiList = response.poiList.sortedBy { it.fleetType }.asReversed()
                    )
                )
            } catch (ex: HttpException) {
                when {
                    ex.code() == 429 -> {
                        onQueryTimeLimit()
                        Log.d("MainVM", "ex.code 429: ${ex.message.toString()} {ex.code()}")
                    }
                    else -> Log.d("MainVM", "else: ${ex.message.toString()} {ex.code()}")

                }
            } catch (ex: Exception) {
                onError()
                Log.d("MainVM", "Exception: ${ex.message.toString()} ${ex.localizedMessage}")
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
}