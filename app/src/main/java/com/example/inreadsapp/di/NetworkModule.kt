package com.example.inreadsapp.di

import APIs
import com.example.inreadsapp.api.QuotesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit
            .Builder()
            .baseUrl(APIs.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideQuotesApi(retrofitBuilder: Retrofit.Builder): QuotesApi {
        return retrofitBuilder
            .build()
            .create(QuotesApi::class.java)
    }


}