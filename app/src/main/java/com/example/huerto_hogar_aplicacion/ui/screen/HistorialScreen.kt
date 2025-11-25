package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CarritoViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.SessionState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistorialScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    carritoViewModel: CarritoViewModel
) {
    val sessionState by homeViewModel.sessionState.collectAsState()
    val uid = (sessionState as? SessionState.LoggedIn)?.uid ?: ""

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) carritoViewModel.cargarHistorial(uid)
    }

    val historial by carritoViewModel.historial.collectAsState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mis Compras Pasadas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        if (historial.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes compras.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(historial) { orden ->
                    Card(
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(Modifier.padding(16.dp).fillMaxWidth()) {
                            Text("Orden #${orden.id}", fontWeight = FontWeight.Bold)
                            Text("Fecha: ${dateFormat.format(orden.fecha)}")
                            Spacer(Modifier.height(4.dp))
                            Text("Total Pagado: $${orden.total.toInt()}", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}