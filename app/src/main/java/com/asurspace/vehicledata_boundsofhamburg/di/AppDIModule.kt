package com.asurspace.vehicledata_boundsofhamburg.di

import com.asurspace.vehicledata_boundsofhamburg.datasource.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppDIModule {

    @Provides
    fun provideLocalizationService(): LocalizationDataService {
        return LocalizationDataProvider().getRadioBrowser()
    }

    @Provides
    fun provideCoroutineDispatcher() = CoroutineDispatcherProvider()

}