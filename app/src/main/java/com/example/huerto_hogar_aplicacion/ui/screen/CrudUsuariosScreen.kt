package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.Usuario
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudUsuarioViewModel

@Composable
fun CrudUsuariosScreen(
    navController: NavController,
    crudUsuarioViewModel: CrudUsuarioViewModel
) {
    //  RECARGA AUTOMÁTICA:
    // Cada vez que se abre esta pantalla, llamamos a la API para traer datos
    LaunchedEffect(Unit) {
        crudUsuarioViewModel.fetchUsuarios()
    }

    // Observamos el estado del ViewModel (Lista, Loading, Filtro)
    val state by crudUsuarioViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // BARRA SUPERIOR (BÚSQUEDA Y AGREGAR) ---
        SearchBarAndActions(
            query = state.currentQuery,
            onQueryChanged = crudUsuarioViewModel::onQueryChanged,
            onAddClicked = {
                // Para agregar un usuario nuevo, lo mejor es enviarlo al Registro
                // ya que necesitamos crear primero la cuenta en Firebase.
                navController.navigate("registro")
            }
        )

        Spacer(Modifier.height(16.dp))

        // --- CONTENIDO VARIABLE ---
        if (state.isLoading) {
            // Caso A: Cargando
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6D4C41))
            }
        }
        else if (state.users.isEmpty()) {
            // Caso B: Lista Vacía (o filtro sin resultados)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (state.currentQuery.isBlank()) "No hay usuarios registrados."
                    else "No se encontraron resultados.",
                    color = Color.Gray
                )
            }
        }
        else {
            // Caso C: Mostrar Lista
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Usamos 'uid' como llave única
                items(state.users, key = { it.uid }) { user ->
                    UserCard(
                        user = user,
                        onEditClicked = {
                            // Navegamos pasando el UID (String)
                            navController.navigate("CrudUsuariosEditarScreen/${user.uid}")
                        },
                        onDeleteClicked = {
                            crudUsuarioViewModel.delete(user)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun SearchBarAndActions(query: String, onQueryChanged: (String) -> Unit, onAddClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Barra de Búsqueda
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = { Text("Buscar por nombre o email...") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedBorderColor = Color(0xFF6D4C41),
                unfocusedBorderColor = Color(0xFF6D4C41)
            )
        )

        Spacer(Modifier.width(8.dp))

        // Botón Agregar (Azul)
        Button(
            onClick = onAddClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            modifier = Modifier.size(56.dp),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = Color.White)
        }
    }
}

@Composable
fun UserCard(
    user: Usuario,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF81C784) // Verde Huerto
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Datos del Usuario
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.nombre ?: "Sin Nombre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = Color(0xFFE8F5E9)
                )
                Text(
                    text = "Tel: ${user.telefono ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color(0xFFE8F5E9)
                )
                if (user.rol == "admin") {
                    Text(
                        text = "★ ADMIN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFEB3B) // Amarillo
                    )
                }
            }

            // Botones de Acción
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Botón Editar (Amarillo)
                Button(
                    onClick = onEditClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.Black)
                }

                // Botón Eliminar (Rojo)
                Button(
                    onClick = onDeleteClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.White)
                }
            }
        }
    }
}