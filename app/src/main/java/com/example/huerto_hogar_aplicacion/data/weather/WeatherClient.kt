package com.example.huerto_hogar_aplicacion.data.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Definimos la interfaz aquí mismo para ir rápido
interface OpenMeteoApi {
    @GET("v1/forecast?current_weather=true&daily=weathercode,temperature_2m_max&timezone=auto")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ): WeatherResponse
}

object WeatherClient {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val api: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }
}