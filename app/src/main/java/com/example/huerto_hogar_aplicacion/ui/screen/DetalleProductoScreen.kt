package com.example.huerto_hogar_aplicacion.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CarritoViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.ProductoViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.SessionState

@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productoId: Long,
    homeViewModel: HomeViewModel,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel
) {
    LaunchedEffect(productoId) {
        productoViewModel.cargarProductoPorId(productoId)
    }

    val producto by productoViewModel.productoSeleccionado.collectAsState()
    val isLoading by productoViewModel.isLoading.collectAsState()

    val sessionState by homeViewModel.sessionState.collectAsState()
    val uid = (sessionState as? SessionState.LoggedIn)?.uid
    val isInvitado = uid == null

    var cantidad by remember { mutableStateOf(1) }
    val context = LocalContext.current

    // FONDO CÁLIDO (Crema suave) en lugar de Blanco
    Box(Modifier.fillMaxSize().background(Color(0xFFFFF8E1))) {
        if (isLoading || producto == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center), color = Color(0xFF6D4C41))
        } else {
            val p = producto!!
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. TÍTULO CENTRADO EN CAFÉ
                Text(
                    text = p.nombre,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037), // Café Título
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // 2. IMAGEN
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    AsyncImage(
                        model = p.img ?: "https://via.placeholder.com/300",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(24.dp))

                // 3. DESCRIPCIÓN
                if (!p.descripcion.isNullOrEmpty()) {
                    Text(
                        text = p.descripcion,
                        color = Color(0xFF4E342E), // Café oscuro
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                // 4. CUADRO DE STOCK Y PRECIO
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$${p.precio.toInt()}",
                            fontSize = 28.sp,
                            color = Color(0xFF2E7D32), // Verde Precio
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(Modifier.height(16.dp))

                        if (p.stock > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledIconButton(
                                    onClick = { if (cantidad > 1) cantidad-- },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFEFEBE9)),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Text("-", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                Text(
                                    text = "$cantidad",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 30.dp),
                                    color = Color(0xFF5D4037)
                                )

                                FilledIconButton(
                                    onClick = { if (cantidad < p.stock) cantidad++ },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF6D4C41)), // Café
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("${p.stock} disponibles", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            Text("AGOTADO", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(24.dp))

                // 5. BOTÓN AGREGAR (Combinado)
                Button(
                    onClick = {
                        if (isInvitado) {
                            Toast.makeText(context, "Inicia sesión para comprar", Toast.LENGTH_SHORT).show()
                        } else {
                            carritoViewModel.agregarProducto(uid!!, p.id, cantidad)
                            Toast.makeText(context, "Agregado al carrito", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    enabled = p.stock > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41), // Café Principal
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Agregar al Carrito", fontSize = 18.sp)
                }
            }
        }
    }
}