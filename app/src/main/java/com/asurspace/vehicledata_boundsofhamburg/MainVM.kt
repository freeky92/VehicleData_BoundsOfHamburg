package com.asurspace.vehicledata_boundsofhamburg

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asurspace.vehicledata_boundsofhamburg.datasource.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.LocalizationServiceRepository
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P1LAT
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P1LON
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P2LAT
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService.Companion.P2LON
import com.asurspace.vehicledata_boundsofhamburg.ui.state.MapUiState
import com.asurspace.vehicledata_boundsofhamburg.ui.state.model.MapUiModel
import com.asurspace.vehicledata_boundsofhamburg.utils.share
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val localizationServiceRepository: LocalizationServiceRepository,
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private var city: String = "Hamburg, Germany"

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Empty)
    val uiState = _uiState.share()

    init {
        fetchPoiByCoordinates()
    }

    fun fetchPoiByCoordinates(
        coordinate1: LatLng = LatLng(P1LAT, P1LON),
        coordinate2: LatLng = LatLng(P2LAT, P2LON)
    ) {
        _uiState.value = MapUiState.Pending
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response =
                    localizationServiceRepository.fetchPoiByCoordinates(coordinate1, coordinate2)
                _uiState.value = MapUiState.Loaded(
                    MapUiModel(
                        city = city,
                        poiList = response.poiList
                    )
                )
            } catch (ex: Exception) {
                when {
                    (ex is HttpException && ex.code() == 429) -> {
                        onQueryTimeLimit()
                    }
                    else -> {
                        onError()
                        Log.d("MainVM", ex.message.toString())
                    }
                }
            }
        }
    }

    private fun onQueryTimeLimit() {
        _uiState.value = MapUiState.Error(
            applicationContext.getString(R.string.query_limit_reached)
        )
    }

    private fun onError() {
        _uiState.value = MapUiState.Error(
            applicationContext.getString(R.string.something_went_wrong)
        )
    }

}