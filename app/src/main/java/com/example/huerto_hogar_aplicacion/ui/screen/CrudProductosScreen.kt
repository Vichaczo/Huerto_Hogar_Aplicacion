package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudProductoViewModel

@Composable
fun CrudProductosScreen(
    navController: NavController,
    viewModel: CrudProductoViewModel
) {
    // Recargar al entrar
    LaunchedEffect(Unit) {
        viewModel.fetchProductos()
    }

    val productos by viewModel.allProductos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // ID 0 significa CREAR NUEVO
                    navController.navigate("CrudProductosEditarScreen/0")
                },
                containerColor = Color(0xFF6D4C41)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear", tint = Color.White)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("Gestión de Inventario", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (productos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos registrados.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(productos) { producto ->
                        AdminProductoItem(
                            producto = producto,
                            onEdit = { navController.navigate("CrudProductosEditarScreen/${producto.id}") },
                            onDelete = { viewModel.eliminarProducto(producto.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductoItem(producto: Producto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen pequeña
            AsyncImage(
                model = producto.img ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))

            // Datos
            Column(Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text("Stock: ${producto.stock} | $${producto.precio.toInt()}", fontSize = 12.sp, color = Color.Gray)
                Text(producto.categoria ?: "Sin Cat.", fontSize = 10.sp, color = Color(0xFF6D4C41))
            }

            // Botones Accion
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color(0xFFFFA000))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Borrar", tint = Color.Red)
            }
        }
    }
}