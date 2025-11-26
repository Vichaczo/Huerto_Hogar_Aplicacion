package com.example.huerto_hogar_aplicacion.ui.screen

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.ClimaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimaScreen(
    navController: NavController,
    viewModel: ClimaViewModel = viewModel()
) {
    val weatherData by viewModel.weatherData.collectAsState()
    val ubicacion by viewModel.ubicacionNombre.collectAsState()
    val gpsActivado by viewModel.gpsActivado.collectAsState() // Estado del botón

    val context = LocalContext.current

    // Launcher de Permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.obtenerUbicacionGPS(context)
            Toast.makeText(context, "Buscando tu ubicación...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Es necesario el permiso para localizarte", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Clima del Huerto",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0277BD) // Azul Cielo
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFE1F5FE))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- SECCIÓN 1: CLIMA ACTUAL ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)), // Celeste suave
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // El título cambia dinámicamente (Santiago -> Tu Ubicación)
                    Text(
                        text = ubicacion,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF455A64) // Gris Azulado
                    )
                    Spacer(Modifier.height(16.dp))

                    if (weatherData != null) {
                        val current = weatherData!!.currentWeather
                        Text(
                            text = viewModel.getWeatherIcon(current.weatherCode),
                            fontSize = 72.sp
                        )
                        Text(
                            text = "${current.temperature}°C",
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0277BD)
                        )
                        Text("Viento: ${current.windspeed} km/h", fontSize = 16.sp, color = Color(0xFF546E7A))
                    } else {
                        CircularProgressIndicator(color = Color(0xFF0277BD))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- SECCIÓN 2: BOTÓN GPS (ROJO/VERDE) ---
            Text("Ubicación del Dispositivo", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    // Solo pedimos permiso si no está activado ya
                    if (!gpsActivado) {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    // Lógica de colores pedida: Rojo si apagado, Verde si prendido
                    containerColor = if (gpsActivado) Color(0xFF43A047) else Color(0xFFD32F2F)
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(
                    imageVector = if (gpsActivado) Icons.Filled.CheckCircle else Icons.Filled.LocationOn,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (gpsActivado) "GPS Activo - Ubicación Encontrada" else "GPS Desactivado (Presiona para activar)",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            // --- SECCIÓN 3: PRONÓSTICO SEMANAL ---
            Text(
                text = "Pronóstico Semanal",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF37474F)
            )
            Spacer(Modifier.height(16.dp))

            if (weatherData != null) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val daily = weatherData!!.daily
                    // Mostramos los próximos 5 días
                    val diasMostrar = daily.time.take(5)

                    itemsIndexed(diasMostrar) { index, dateStr ->
                        val tempMax = daily.maxTemps.getOrNull(index) ?: 0.0
                        val code = daily.weatherCodes.getOrNull(index) ?: 0

                        ForecastCard(
                            dayName = viewModel.getDayName(dateStr),
                            icon = viewModel.getWeatherIcon(code),
                            temp = "${tempMax.toInt()}°"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastCard(dayName: String, icon: String, temp: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(70.dp).wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dayName, fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text(temp, fontWeight = FontWeight.Bold, color = Color(0xFF0277BD), fontSize = 16.sp)
        }
    }
}