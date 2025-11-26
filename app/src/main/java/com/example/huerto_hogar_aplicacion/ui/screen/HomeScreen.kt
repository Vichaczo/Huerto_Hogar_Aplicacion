package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.SessionState
import com.example.huerto_hogar_aplicacion.data.local.UserStore
import kotlinx.coroutines.launch

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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    if (sessionState is SessionState.LoggedIn) {
                        TextButton(onClick = {
                            // LOGOUT
                            homeViewModel.onLogout()
                            scope.launch {
                                UserStore(context).clearUser()
                            }
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }) {
                            Text("Cerrar Sesión", color = Color(0xFF6D4C41))
                        }
                    } else {
                        TextButton(onClick = { navController.navigate("login") }) {
                            Text("Iniciar Sesión", color = Color(0xFF6D4C41))
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
                color = Color(0xFF6D4C41)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAdmin) "Panel de Gestión" else "Tu huerto en casa, a un click.",
                textAlign = TextAlign.Center,
                color = Color(0xFF8D6E63), // Café suave
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            //  BOTÓN TIENDA
            Button(
                onClick = { navController.navigate("catalogo") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Ir a tienda", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN CARRITO
            Button(
                onClick = { navController.navigate("carrito") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D4C41),
                    disabledContainerColor = Color.LightGray
                ),
                shape = MaterialTheme.shapes.medium,
                enabled = !isInvitado
            ) {
                Text(if (isInvitado) "Carrito (Inicia Sesión)" else "Ver mi Carrito", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN CLIMA
            Button(
                onClick = { navController.navigate("clima") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF81D4FA),
                    contentColor = Color(0xFF01579B)
                ),
                shape = MaterialTheme.shapes.medium,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4FC3F7))
            ) {
                Text("☀️ Revisar el clima", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            //  ZONA ADMIN
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