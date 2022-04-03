package com.asurspace.vehicledata_boundsofhamburg

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.asurspace.vehicledata_boundsofhamburg.datasource.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.LocalizationServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MainVM @Inject constructor(
    private val localizationServiceRepository: LocalizationServiceRepository,
    @ApplicationContext val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {



}