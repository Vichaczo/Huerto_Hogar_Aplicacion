package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    viewModel: ProductoViewModel
) {
    val productos by viewModel.productos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categoriaActual by viewModel.categoriaSeleccionada.collectAsState()
    val categorias = viewModel.categorias

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mercadito", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D4C41))

        Spacer(Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categorias) { cat ->
                FilterChip(
                    selected = (cat == categoriaActual),
                    onClick = { viewModel.onCategoriaSelected(cat) },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF81C784),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6D4C41))
            }
        } else if (productos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay productos en esta categoría.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productos) { producto ->
                    ProductoItem(producto) {
                        navController.navigate("detalle_producto/${producto.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItem(producto: Producto, onDetailClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = producto.img ?: "https://via.placeholder.com/150",
                contentDescription = producto.nombre,
                modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("$${producto.precio.toInt()}", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                Text("Stock: ${producto.stock}", fontSize = 12.sp, color = Color.Gray)
            }

            IconButton(
                onClick = onDetailClick,
                modifier = Modifier.background(Color(0xFF6D4C41), shape = CircleShape).size(36.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Ver Detalle", tint = Color.White)
            }
        }
    }
}