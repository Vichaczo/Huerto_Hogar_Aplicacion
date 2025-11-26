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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CarritoViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.SessionState
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val sessionState by homeViewModel.sessionState.collectAsState()
    val uid = (sessionState as? SessionState.LoggedIn)?.uid ?: ""

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) carritoViewModel.cargarHistorial(uid)
    }

    val historial by carritoViewModel.historial.collectAsState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                // TÍTULO CORREGIDO Y COLOR CAFÉ
                title = {
                    Text(
                        "Historial de Compras",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037) // Café
                    )
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (historial.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes compras registradas.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(historial) { orden ->
                        Card(
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)) // Fondo suave
                        ) {
                            Column(Modifier.padding(16.dp).fillMaxWidth()) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Orden #${orden.id}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    Text("$${orden.total.toInt()}", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text("Fecha: ${dateFormat.format(orden.fecha)}", color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }
}