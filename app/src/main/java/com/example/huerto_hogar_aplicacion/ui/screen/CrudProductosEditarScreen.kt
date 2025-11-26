package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrudProductosEditarScreen(
    navController: NavController,
    viewModel: CrudProductoViewModel,
    productoId: Long
) {
    LaunchedEffect(productoId) {
        viewModel.prepareForEdit(productoId)
    }

    val state by viewModel.editState.collectAsState()
    val categorias = viewModel.categoriasDisponibles
    var expandedCategoria by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (productoId == 0L) "Nuevo Producto" else "Editar Producto") }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (state.isSaving) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Si el usuario pega un link, intentamos mostrarlo. Si no, placeholder.
                    Card(
                        modifier = Modifier.size(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                    ) {
                        AsyncImage(
                            model = if (state.imagenUrl.isNotBlank()) state.imagenUrl else "https://via.placeholder.com/150",
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text("Previsualización", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

                    Spacer(Modifier.height(16.dp))

                    // CAMPO: IMAGEN URL
                    OutlinedTextField(
                        value = state.imagenUrl,
                        onValueChange = { viewModel.onFieldChanged(state.nombre, state.descripcion, state.precio, state.stock, state.categoria, it) },
                        label = { Text("URL de Imagen (Opcional)") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    // CAMPO: NOMBRE
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = { viewModel.onFieldChanged(it, state.descripcion, state.precio, state.stock, state.categoria, state.imagenUrl) },
                        label = { Text("Nombre del Producto *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    // CAMPO: PRECIO
                    OutlinedTextField(
                        value = state.precio,
                        onValueChange = { viewModel.onFieldChanged(state.nombre, state.descripcion, it, state.stock, state.categoria, state.imagenUrl) },
                        label = { Text("Precio ($) *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(Modifier.height(8.dp))

                    // CAMPO: STOCK
                    OutlinedTextField(
                        value = state.stock,
                        onValueChange = { viewModel.onFieldChanged(state.nombre, state.descripcion, state.precio, it, state.categoria, state.imagenUrl) },
                        label = { Text("Stock Actual *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(Modifier.height(8.dp))

                    // CAMPO: CATEGORÍA
                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = !expandedCategoria },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.categoria,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false }
                        ) {
                            categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria) },
                                    onClick = {
                                        viewModel.onFieldChanged(state.nombre, state.descripcion, state.precio, state.stock, categoria, state.imagenUrl)
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // CAMPO: DESCRIPCIÓN
                    OutlinedTextField(
                        value = state.descripcion,
                        onValueChange = { viewModel.onFieldChanged(state.nombre, it, state.precio, state.stock, state.categoria, state.imagenUrl) },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 5
                    )

                    Spacer(Modifier.height(24.dp))

                    if (state.error != null) {
                        Text(state.error!!, color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            viewModel.guardarProducto {
                                navController.popBackStack() // Volver a la lista al terminar
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                    ) {
                        Text(if (productoId == 0L) "Crear Producto" else "Guardar Cambios")
                    }
                }
            }
        }
    }
}