package com.asurspace.vehicledata_boundsofhamburg.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.datasource.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.LocalizationServiceRepository
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.ui.state.MapUIState
import com.asurspace.vehicledata_boundsofhamburg.ui.state.models.MapUIModel
import com.asurspace.vehicledata_boundsofhamburg.utils.share
import com.google.android.libraries.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
        fetchPoiByCoordinates()
    }

    fun fetchPoiByCoordinates(
        coordinate1: LatLng = LatLng(LocalizationDataService.P1LAT, LocalizationDataService.P1LON),
        coordinate2: LatLng = LatLng(LocalizationDataService.P2LAT, LocalizationDataService.P2LON)
    ) {
        _uiState.value = MapUIState.Pending
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = localizationServiceRepository.fetchPoiByCoordinates(coordinate1, coordinate2)
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