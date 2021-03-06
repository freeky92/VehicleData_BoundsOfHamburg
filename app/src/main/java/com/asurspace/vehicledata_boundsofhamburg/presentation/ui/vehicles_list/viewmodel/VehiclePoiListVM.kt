package com.asurspace.vehicledata_boundsofhamburg.presentation.ui.vehicles_list.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asurspace.vehicledata_boundsofhamburg.R
import com.asurspace.vehicledata_boundsofhamburg.data.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.data.repository.remote.LocalizationDataRepositoryImpl
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi
import com.asurspace.vehicledata_boundsofhamburg.common.state.VehicleListUIState
import com.asurspace.vehicledata_boundsofhamburg.common.state.models.VehicleListUIModel
import com.asurspace.vehicledata_boundsofhamburg.common.utils.share
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class VehiclePoiListVM @Inject constructor(
    private val localizationServiceRepository: LocalizationDataRepositoryImpl,
    @ApplicationContext val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private var city: String = "Hamburg, Germany"

    private val _uiState = MutableStateFlow<VehicleListUIState>(VehicleListUIState.Empty)
    val uiState = _uiState.share()

    init {
        fetchPoiByCoordinates()
    }

    fun fetchPoiByCoordinates(
        coordinate1: LatLng = LatLng(LocalizationDataApi.P1LAT, LocalizationDataApi.P1LON),
        coordinate2: LatLng = LatLng(LocalizationDataApi.P2LAT, LocalizationDataApi.P2LON)
    ) {
        _uiState.value = VehicleListUIState.Pending
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            try {
                val response = localizationServiceRepository.fetchPoiByCoordinates(coordinate1, coordinate2)
                _uiState.value = VehicleListUIState.Loaded(
                    VehicleListUIModel(
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
        _uiState.value = VehicleListUIState.Error(
            applicationContext.getString(R.string.query_limit_reached)
        )
    }

    private fun onError() {
        _uiState.value = VehicleListUIState.Error(
            applicationContext.getString(R.string.something_went_wrong)
        )
    }

}