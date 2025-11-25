package com.example.huerto_hogar_aplicacion.data.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 1. CAMBIA LA IP CADA VEZ QUE INICIES EL LAB DE AWS.
    // 2. IMPORTANTE: Mantén el "/api/" al final porque así lo definiste en application.properties
    private const val BASE_URL = "http://54.81.202.30:8080/api/"

    // Configuramos Gson para que entienda las fechas de Java (LocalDateTime)
    // sin que la app se cierre inesperadamente.
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    val api: HuertoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Usamos nuestro Gson configurado
            .build()
            .create(HuertoApi::class.java)
    }
}