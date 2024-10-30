package com.example.houseconnect.retrofit

import AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL =  "http://192.168.8.138:8091" ;// "http://10.0.2.2:8000/" //"http://192.168.0.103:8090/"

    fun create(token: String?): ApiService {
        val httpClient = OkHttpClient.Builder()
        if (!token.isNullOrEmpty()) {
            httpClient.addInterceptor(AuthInterceptor(token))
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        return retrofit.create(ApiService::class.java)
    }


    fun api(): ApiService {
        val httpClient = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
