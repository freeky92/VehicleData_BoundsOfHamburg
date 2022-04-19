package com.asurspace.vehicledata_boundsofhamburg.di

import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataServiceImpl
import com.asurspace.vehicledata_boundsofhamburg.data.repository.remote.LocalizationDataRepositoryImpl
import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataService
import com.asurspace.vehicledata_boundsofhamburg.domain.repository.LocalizationDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetWorkDiModule {

    @Singleton
    @Provides
    fun provideLocalizationService(): LocalizationDataService {
        return LocalizationDataServiceImpl()
    }

    @Singleton
    @Provides
    fun provideLocalizationDataApi(localizationDataService: LocalizationDataService): LocalizationDataApi {
        return localizationDataService.getLocalizationDataApi()
    }

    @Singleton
    @Provides
    fun provideLocalizationDataRepository(localizationApi: LocalizationDataApi): LocalizationDataRepository {
        return LocalizationDataRepositoryImpl(localizationApi)
    }

}