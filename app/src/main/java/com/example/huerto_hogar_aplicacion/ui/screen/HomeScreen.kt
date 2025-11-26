package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.SessionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = viewModel()) {

    val sessionState by homeViewModel.sessionState.collectAsState()

    val currentState = sessionState
    var isAdmin = false
    var welcomeText = "Bienvenido a Huerto Hogar"
    val isInvitado = sessionState is SessionState.Invitado

    if (currentState is SessionState.LoggedIn) {
        isAdmin = currentState.isAdmin
        welcomeText = if (isAdmin) "Bienvenido, Administrador" else "Bienvenido, ${currentState.userName}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    if (sessionState is SessionState.LoggedIn) {
                        TextButton(onClick = { homeViewModel.onLogout() }) {
                            Text("Cerrar Sesión", color = Color(0xFF5D4037)) // Café
                        }
                    } else {
                        TextButton(onClick = { navController.navigate("login") }) {
                            Text("Iniciar Sesión", color = Color(0xFF2E7D32)) // Verde
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
                text = welcomeText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF6D4C41) // Café Título
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAdmin) "Panel de Gestión" else "Tu huerto en casa, a un click.",
                textAlign = TextAlign.Center,
                color = Color(0xFF8D6E63),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 1. BOTÓN AL MERCADITO
            Button(
                onClick = { navController.navigate("catalogo") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32), // Fondo Verde
                    contentColor = Color(0xFF5D4037)    // <--- LETRAS CAFÉ (Solicitado)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                // Forzamos el color aquí también por seguridad
                Text("Ir al Mercadito", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. BOTÓN CARRITO
            Button(
                onClick = { navController.navigate("carrito") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D4C41), // Café
                    disabledContainerColor = Color.LightGray
                ),
                shape = MaterialTheme.shapes.medium,
                enabled = !isInvitado
            ) {
                Text(if (isInvitado) "Carrito (Inicia Sesión)" else "Ver mi Carrito", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. BOTÓN CLIMA (Cielo)
            Button(
                onClick = { /* Futura implementación */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE1F5FE), // <--- FONDO CELESTE CLARO (Cielo)
                    contentColor = Color(0xFF0277BD)    // <--- TEXTO AZUL INTACTO
                ),
                shape = MaterialTheme.shapes.medium,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81D4FA))
            ) {
                Text("☀️ Revisar el Clima del Huerto")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. ZONA ADMIN
            if (isAdmin) {
                HorizontalDivider(color = Color(0xFFD7CCC8))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Administración", fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                Spacer(modifier = Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { navController.navigate("CrudUsuariosScreen") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B))
                    ) {
                        Text("Usuarios")
                    }
                    Button(
                        onClick = { navController.navigate("crud_productos") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B))
                    ) {
                        Text("Productos")
                    }
                }
            }
        }
    }
}