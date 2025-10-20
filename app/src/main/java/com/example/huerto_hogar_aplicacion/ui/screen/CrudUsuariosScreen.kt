package com.example.huerto_hogar_aplicacion.ui.screen

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.Usuario
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudUsuarioViewModel
// Importa tus funciones SearchBarAndActions y UserCard del ejemplo anterior

@Composable
fun CrudUsuariosScreen(crudUsuarioViewModel: CrudUsuarioViewModel) {

    // Observar el estado de la UI
    val state by crudUsuarioViewModel.state.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        // --- BARRA SUPERIOR (BUSQUEDA Y BOTÓN AGREGAR) ---
        SearchBarAndActions(
            query = state.currentQuery,
            onQueryChanged = crudUsuarioViewModel::onQueryChanged,
            onAddClicked = {
                // Lógica para abrir el diálogo/navegación de añadir
            }
        )

        Spacer(Modifier.height(16.dp))

        // --- INDICADOR DE CARGA INICIAL ---
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // --- LISTA DE USUARIOS (CUBOS VERDES) ---
        else if (state.users.isEmpty() && state.currentQuery.isBlank()) {
            Text("No hay usuarios registrados.", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else if (state.users.isEmpty() && state.currentQuery.isNotBlank()) {
            Text("No se encontraron resultados para '${state.currentQuery}'.", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio restante
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Iterar sobre la lista filtrada
                items(state.users, key = { it.id }) { user ->
                    UserCard(
                        user = user,
                        onEditClicked = {
                            // Abrir diálogo de editar (debo implementar el diálogo)
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
            placeholder = { Text("Buscar usuario...") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFDEDDDD), //
                unfocusedContainerColor = Color(0xFFDEDDDD),
                focusedBorderColor = Color(0xFF6D4C41), //
                unfocusedBorderColor = Color(0xFF6D4C41)
            )
        )

        Spacer(Modifier.width(8.dp))

        // Botón Agregar Usuario (Azul)
        Button(
            onClick = onAddClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)), // Azul
            modifier = Modifier.size(56.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = Color.White)
        }
    }
}

// ---  TARJETA DE USUARIO (UserCard - Cubo Verde) ---

@Composable
fun UserCard(
    user: Usuario,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF81C784) // Verde principal para la tarjeta
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text(text = user.email, fontSize = 14.sp, color = Color(0xFFE8F5E9)) // Verde muy claro
                Text(text = "Password: ${user.password}", fontSize = 14.sp, color = Color(0xFFE8F5E9))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Botón Editar (Amarillo)
                Button(
                    onClick = onEditClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)), // Amarillo
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.Black)
                }

                // Botón Eliminar (Rojo)
                Button(
                    onClick = onDeleteClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), // Rojo
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.White)
                }
            }
        }
    }
}