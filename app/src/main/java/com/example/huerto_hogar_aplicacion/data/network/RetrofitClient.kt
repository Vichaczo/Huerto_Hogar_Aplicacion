package com.example.huerto_hogar_aplicacion.data.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    //  Cambiar ip cada vez que se inicie el lab de aws
    private const val BASE_URL = "http://54.81.202.30:8080/api/"

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    val api: HuertoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(HuertoApi::class.java)
    }
}