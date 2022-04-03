package com.asurspace.vehicledata_boundsofhamburg.di

import android.content.Context
import com.asurspace.vehicledata_boundsofhamburg.datasource.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataProvider
import com.asurspace.vehicledata_boundsofhamburg.datasource.network.localization_information_service.service.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.domain.map.CoordinateConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppDIModule {

    @Provides
    fun provideLocalizationService(): LocalizationDataService {
        return LocalizationDataProvider().getRadioBrowser()
    }

    @Provides
    fun provideCoroutineDispatcher() = CoroutineDispatcherProvider()

    @Provides
    fun getCoordinateConverter(@ApplicationContext context: Context) =
        CoordinateConverter(provideLocalizationService(), context)

}