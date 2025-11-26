package com.example.huerto_hogar_aplicacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudUsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun CrudUsuariosEditarScreen(
    navController: NavController,
    crudUsuarioViewModel: CrudUsuarioViewModel,
    userUid: String
) {
    LaunchedEffect(userUid) {
        if (userUid.isNotBlank()) {
            crudUsuarioViewModel.loadUserByUid(userUid)
        }
    }

    val editState by crudUsuarioViewModel.editUserState.collectAsState()
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize().padding(16.dp)) {
        when {
            editState.isLoadingUser -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            editState.error != null -> Text("Error: ${editState.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            else -> {
                EditarUsuarioForm(
                    modifier = Modifier.align(Alignment.Center),
                    editState = editState,
                    onFieldChanged = crudUsuarioViewModel::onEditFieldChanged,
                    onUpdateClicked = {
                        scope.launch {
                            crudUsuarioViewModel.onUpdateUserClicked()
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EditarUsuarioForm(
    modifier: Modifier,
    editState: com.example.huerto_hogar_aplicacion.ui.viewModelPackage.EditUserState,
    onFieldChanged: (String, String, String, String, String, String, String) -> Unit,
    onUpdateClicked: () -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Editar Usuario", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        // NOMBRE
        OutlinedTextField(
            value = editState.nombre,
            onValueChange = { onFieldChanged(it, editState.apellido, editState.email, editState.password, editState.telefono, editState.direccion, editState.rol) },
            label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // APELLIDO
        OutlinedTextField(
            value = editState.apellido,
            onValueChange = { onFieldChanged(editState.nombre, it, editState.email, editState.password, editState.telefono, editState.direccion, editState.rol) },
            label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // EMAIL (Read Only)
        OutlinedTextField(
            value = editState.email,
            onValueChange = {},
            label = { Text("Email (No editable)") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // TELEFONO
        OutlinedTextField(
            value = editState.telefono,
            onValueChange = { onFieldChanged(editState.nombre, editState.apellido, editState.email, editState.password, it, editState.direccion, editState.rol) },
            label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // CAMPO: DIRECCIÓN ---
        OutlinedTextField(
            value = editState.direccion,
            onValueChange = { onFieldChanged(editState.nombre, editState.apellido, editState.email, editState.password, editState.telefono, it, editState.rol) },
            label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // CAMPO: SELECCIÓN DE ROL ---
        Text("Rol de Usuario:", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            // Opción Usuario
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = editState.rol == "usuario",
                    onClick = { onFieldChanged(editState.nombre, editState.apellido, editState.email, editState.password, editState.telefono, editState.direccion, "usuario") }
                )
                Text("Usuario")
            }
            // Opción Admin
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = editState.rol == "admin",
                    onClick = { onFieldChanged(editState.nombre, editState.apellido, editState.email, editState.password, editState.telefono, editState.direccion, "admin") }
                )
                Text("Admin")
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onUpdateClicked,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41)),
            enabled = editState.isUpdateEnabled
        ) {
            Text("Guardar Cambios")
        }
    }
}