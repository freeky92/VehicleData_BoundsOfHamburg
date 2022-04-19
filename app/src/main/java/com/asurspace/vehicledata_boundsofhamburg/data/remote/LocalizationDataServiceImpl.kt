package com.asurspace.vehicledata_boundsofhamburg.data.remote

import com.asurspace.vehicledata_boundsofhamburg.data.remote.LocalizationDataApi.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LocalizationDataServiceImpl @Inject constructor() :
    LocalizationDataService {

    override fun getLocalizationDataApi(): LocalizationDataApi {

        val bodyInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val headersInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", "VehicleData_BoundsOfHamburg v0.0.1").build()
                )
            }
            .addInterceptor(bodyInterceptor)
            .addInterceptor(headersInterceptor)
            .build()

        val retrofitInstance = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofitInstance.create(LocalizationDataApi::class.java)
    }

}