package com.asurspace.vehicledata_boundsofhamburg.di

import android.app.Application
import android.content.Context
import android.location.Geocoder
import com.asurspace.vehicledata_boundsofhamburg.data.CoroutineDispatcherProvider
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataServiceImpl
import com.asurspace.vehicledata_boundsofhamburg.data.repository.remote.LocalizationDataRepositoryImpl
import com.asurspace.vehicledata_boundsofhamburg.domain.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.domain.repository.LocalizationDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VehicleDataAppDiModule {

    @Singleton
    @Provides
    fun provideCoroutineDispatcher() = CoroutineDispatcherProvider()

    @Singleton
    @Provides
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder = Geocoder(context)

}