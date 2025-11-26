package com.example.huerto_hogar_aplicacion.data.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    @SerializedName("daily") val daily: DailyForecast
)

data class CurrentWeather(
    val temperature: Double,
    @SerializedName("weathercode") val weatherCode: Int,
    val windspeed: Double
)

data class DailyForecast(
    val time: List<String>, // Fechas ["2023-11-25", ...]
    @SerializedName("weathercode") val weatherCodes: List<Int>,
    @SerializedName("temperature_2m_max") val maxTemps: List<Double>
)