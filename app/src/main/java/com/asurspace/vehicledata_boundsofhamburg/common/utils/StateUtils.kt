package com.asurspace.vehicledata_boundsofhamburg.common.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun <T> MutableStateFlow<T>.share(): StateFlow<T> = this

fun <T> MutableLiveData<T>.share(): LiveData<T> = this