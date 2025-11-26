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
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mercadito",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D4C41), // Café
                        fontSize = 26.sp
                    )
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // FILTROS
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(categorias) { cat ->
                    val isSelected = (cat == categoriaActual)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategoriaSelected(cat) },
                        label = {
                            Text(
                                text = cat,
                                color = if(isSelected) Color.White else Color(0xFF5D4037)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6D4C41), // Café seleccionado
                            containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0xFF6D4C41)
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6D4C41))
                }
            } else if (productos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos aquí 🌱", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = productos,
                        key = { p -> p.id }
                    ) { producto ->
                        ProductoItem(producto) {
                            navController.navigate("detalle_producto/${producto.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItem(producto: Producto, onDetailClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = producto.img ?: "https://via.placeholder.com/150",
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFEBE9)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF4E342E)
                )
                Text(
                    text = "$${producto.precio.toInt()}",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = if(producto.stock > 0) "Disponible" else "Agotado",
                    fontSize = 12.sp,
                    color = if(producto.stock > 0) Color.Gray else Color.Red
                )
            }

            IconButton(
                onClick = onDetailClick,
                modifier = Modifier
                    .background(Color(0xFF6D4C41), shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Ver", tint = Color.White)
            }
        }
    }
}