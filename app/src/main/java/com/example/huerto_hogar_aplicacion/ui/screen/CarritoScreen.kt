package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List // Cambiado desde History para evitar error
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huerto_hogar_aplicacion.data.carritoItemPackage.CarritoItem
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CarritoViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.SessionState

@Composable
fun CarritoScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    carritoViewModel: CarritoViewModel
) {
    val sessionState by homeViewModel.sessionState.collectAsState()
    val uid = (sessionState as? SessionState.LoggedIn)?.uid ?: ""

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            carritoViewModel.cargarCarrito(uid)
        }
    }

    val items by carritoViewModel.items.collectAsState()
    val total by carritoViewModel.total.collectAsState()
    val isLoading by carritoViewModel.isLoading.collectAsState()
    val compraExitosa by carritoViewModel.compraExitosa.collectAsState()

    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    if (compraExitosa) {
        AlertDialog(
            onDismissRequest = { carritoViewModel.resetCompraState() },
            title = { Text("¡Compra Exitosa!") },
            text = { Text("Gracias por tu compra. Tus productos llegarán pronto.") },
            confirmButton = {
                Button(onClick = {
                    carritoViewModel.resetCompraState()
                    navController.navigate("home")
                }) { Text("Volver al Inicio") }
            }
        )
    }

    if (mostrarDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
            title = { Text("Confirmar Compra") },
            text = {
                Column {
                    Text("¿Estás seguro de procesar esta orden?")
                    Spacer(Modifier.height(8.dp))
                    Text("Total a pagar: $${total.toInt()}", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoConfirmacion = false
                        carritoViewModel.confirmarCompra(uid)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text("Sí, Comprar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mi Carrito", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                // CORRECCIÓN: Usamos Icons.Filled.List en lugar de History
                IconButton(onClick = { navController.navigate("historial") }) {
                    Icon(Icons.Filled.List, contentDescription = "Historial")
                }
            }
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                // CORRECCIÓN: Surface en Material 3 usa shadowElevation, no elevation
                Surface(
                    shadowElevation = 8.dp
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total:", fontSize = 20.sp)
                            Text("$${total.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { mostrarDialogoConfirmacion = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                        ) {
                            Text("Procesar Compra")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else if (items.isEmpty()) {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tu carrito está vacío 🍃", fontSize = 18.sp, color = Color.Gray)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("catalogo") }) {
                        Text("Ir a comprar")
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { item ->
                        CarritoItemCard(
                            item = item,
                            onIncrement = { carritoViewModel.modificarCantidad(uid, item, 1) },
                            onDecrement = { carritoViewModel.modificarCantidad(uid, item, -1) },
                            onDelete = { carritoViewModel.eliminarItem(uid, item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CarritoItemCard(
    item: CarritoItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        // CORRECCIÓN: En Material 3 CardElevation se define así:
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CORRECCIÓN: Usamos imagenUrl (el nombre correcto del modelo)
            AsyncImage(
                model = item.producto.img ?: "https://via.placeholder.com/150",
                contentDescription = item.producto.nombre,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(item.producto.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Precio: $${item.producto.precio.toInt()}", fontSize = 12.sp, color = Color.Gray)
                Text("Subtotal: $${(item.producto.precio * item.cantidad).toInt()}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF388E3C))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                        Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("${item.cantidad}", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                    IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = "Sumar", modifier = Modifier.size(16.dp))
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}