package com.example.ppi

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitSetting {
    private const val API_BASE_URL = "http://59.15.198.238:1234"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <S> createBaseService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }
}
