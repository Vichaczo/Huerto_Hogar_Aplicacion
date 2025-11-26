package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.weather.WeatherClient
import com.example.huerto_hogar_aplicacion.data.weather.WeatherResponse
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ClimaViewModel : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    // Texto de ubicación dinámico
    private val _ubicacionNombre = MutableStateFlow("Huerto Hogar (Santiago)")
    val ubicacionNombre: StateFlow<String> = _ubicacionNombre

    // Estado del GPS para cambiar el color del botón (Rojo/Verde)
    private val _gpsActivado = MutableStateFlow(false)
    val gpsActivado: StateFlow<Boolean> = _gpsActivado

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // 1. Carga inicial: Santiago de Chile (Por defecto)
        fetchClima(-33.4489, -70.6693, "Huerto Hogar (Santiago)")
    }

    private fun fetchClima(lat: Double, lon: Double, nombre: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _ubicacionNombre.value = nombre
            try {
                val response = WeatherClient.api.getWeather(lat, lon)
                _weatherData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para obtener GPS real y traducir a Ciudad
    @SuppressLint("MissingPermission")
    fun obtenerUbicacionGPS(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Marcamos el GPS como activo (Botón se pondrá verde)
                _gpsActivado.value = true

                // Lanzamos corrutina para buscar el nombre de la ciudad (Geocoding)
                viewModelScope.launch {
                    val ciudad = obtenerNombreCiudad(context, location.latitude, location.longitude)
                    val titulo = "Tu ubicación actual: $ciudad"

                    fetchClima(location.latitude, location.longitude, titulo)
                }
            }
        }
    }

    // Función auxiliar para obtener nombre de ciudad desde coordenadas (Nativo)
    private suspend fun obtenerNombreCiudad(context: Context, lat: Double, lon: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                // Obtenemos 1 resultado
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    // Retorna la localidad (ej: Villa Alemana) o el área admin (ej: Valparaíso)
                    addresses[0].locality ?: addresses[0].subAdminArea ?: "Tu Zona"
                } else {
                    "Tu Zona"
                }
            } catch (e: Exception) {
                "Tu Zona"
            }
        }
    }

    // Iconos según código WMO
    fun getWeatherIcon(code: Int): String {
        return when (code) {
            0 -> "☀️" // Despejado
            1, 2, 3 -> "⛅" // Nublado
            45, 48 -> "🌫️" // Niebla
            in 51..67 -> "🌧️" // Lluvia
            in 71..77 -> "❄️" // Nieve
            in 95..99 -> "⛈️" // Tormenta
            else -> "❓"
        }
    }

    fun getDayName(dateString: String): String {
        return try {
            val date = java.time.LocalDate.parse(dateString)
            when(date.dayOfWeek.value) {
                1 -> "LUN" 2 -> "MAR" 3 -> "MIE" 4 -> "JUE" 5 -> "VIE" 6 -> "SAB" 7 -> "DOM"
                else -> "-"
            }
        } catch (e: Exception) { "-" }
    }
}