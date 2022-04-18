package com.asurspace.vehicledata_boundsofhamburg.domain

import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi

interface LocalizationDataService {

        fun getLocalizationDataApi(): LocalizationDataApi

}