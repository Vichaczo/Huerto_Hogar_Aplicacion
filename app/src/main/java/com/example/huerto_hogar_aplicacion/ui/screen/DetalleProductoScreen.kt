package com.example.huerto_hogar_aplicacion.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    Box(Modifier.fillMaxSize()) {
        if (isLoading || producto == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            val p = producto!!
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = p.img,
                    contentDescription = null,
                    modifier = Modifier.height(250.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(16.dp))

                Text(p.nombre, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("$${p.precio.toInt()}", fontSize = 24.sp, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(8.dp))

                if (!p.descripcion.isNullOrEmpty()) {
                    Text(p.descripcion, color = Color.Gray, fontSize = 16.sp)
                }

                Spacer(Modifier.height(32.dp))

                if (p.stock > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).padding(8.dp)
                    ) {
                        Button(onClick = { if (cantidad > 1) cantidad-- }) { Text("-") }
                        Text(text = "$cantidad", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp))
                        Button(onClick = { if (cantidad < p.stock) cantidad++ }) { Text("+") }
                    }
                    Text("Disponible: ${p.stock}", fontSize = 12.sp, color = Color.Gray)
                } else {
                    Text("AGOTADO", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                Spacer(Modifier.weight(1f))

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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                ) {
                    Text("Agregar al Carrito - $${(p.precio * cantidad).toInt()}")
                }
            }
        }
    }
}