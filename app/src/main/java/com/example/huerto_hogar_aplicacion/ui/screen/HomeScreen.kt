package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = viewModel()) {

    // El estado 'isAdmin' del ViewModel.
    // Cada vez que 'isAdmin' cambia, esta variable se actualiza y la UI se recompone.
    val isAdmin by homeViewModel.isAdmin.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Huerto Hogar") },
                actions = {
                    // Muestra un botón u otro dependiendo del estado de login.
                    if (isAdmin) {
                        TextButton(onClick = { homeViewModel.onLogout() }) {
                            Text("Cerrar Sesión")
                        }
                    } else {
                        TextButton(onClick = { navController.navigate("login") }) {
                            Text("Iniciar Sesión")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isAdmin) "Bienvenido, Administrador" else "Bienvenido a Huerto Hogar",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAdmin) "¿Qué deseas hacer hoy?" else "Tu tienda de productos frescos a la puerta de tu casa.",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón que siempre es visible
            Button(
                onClick = { navController.navigate("product_list") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Productos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // El botón de CRUD solo se añade a la UI si 'isAdmin' es true.
            if (isAdmin) {
                Button(
                    onClick = { navController.navigate("crud_management") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Gestionar Productos (CRUD)")
                }
            }
        }
    }
}