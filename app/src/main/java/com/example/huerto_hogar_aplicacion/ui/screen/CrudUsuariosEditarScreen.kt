package com.example.huerto_hogar_aplicacion.ui.screen

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudUsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun CrudUsuariosEditarScreen(
    navController: NavController,
    crudUsuarioViewModel: CrudUsuarioViewModel,
    userId: Long
) {
    LaunchedEffect(userId) {
        if (userId > 0) {
            crudUsuarioViewModel.loadUserById(userId)
        }
    }
    val editState by crudUsuarioViewModel.editUserState.collectAsState()
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            editState.isLoadingUser -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            editState.error != null -> {
                Text("Error: ${editState.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            }
            editState.id == 0L && !editState.isLoadingUser -> {
                Text("Error al cargar el usuario para editar.", modifier = Modifier.align(Alignment.Center))
            }
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
    onFieldChanged: (String, String, String, String, String) -> Unit,
    onUpdateClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Editar Usuario",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Nombre
        NombreField(editState.nombre) { newNombre ->
            onFieldChanged(newNombre, editState.apellido, editState.email, editState.password, editState.telefono)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Apellido
        ApellidoField(editState.apellido) { newApellido ->
            onFieldChanged(editState.nombre, newApellido, editState.email, editState.password, editState.telefono)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Email
        EmailFieldRegister(editState.email) { newEmail ->
            onFieldChanged(editState.nombre, editState.apellido, newEmail, editState.password, editState.telefono)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Contraseña
        PasswordFieldRegister(editState.password) { newPassword ->
            onFieldChanged(editState.nombre, editState.apellido, editState.email, newPassword, editState.telefono)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Teléfono
        TelefonoField(editState.telefono) { newTelefono ->
            onFieldChanged(editState.nombre, editState.apellido, editState.email, editState.password, newTelefono)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onUpdateClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6D4C41),
                disabledContainerColor = Color(0xFFA1887F),
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            enabled = editState.isUpdateEnabled
        ) {
            Text(text = "Guardar Cambios")
        }
    }
}



