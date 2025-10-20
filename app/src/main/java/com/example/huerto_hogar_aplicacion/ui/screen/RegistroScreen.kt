package com.example.huerto_hogar_aplicacion.ui.screen

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.RegistroViewModel
import androidx.compose.runtime.rememberCoroutineScope //
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.huerto_hogar_aplicacion.ui.theme.CafeSombraTexto
import kotlinx.coroutines.launch //

@Composable
fun RegistroScreen(navController: NavController, registroViewModel: RegistroViewModel,homeViewModel: HomeViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Registro(Modifier.align(Alignment.Center), registroViewModel, navController,homeViewModel)
    }
}

@Composable
fun Registro(modifier: Modifier, registroViewModel: RegistroViewModel, navController: NavController,homeViewModel: HomeViewModel) {

    val scope = rememberCoroutineScope()

    val nombre: String by registroViewModel.nombre.observeAsState(initial = "")
    val apellido: String by registroViewModel.apellido.observeAsState(initial = "")
    val email: String by registroViewModel.email.observeAsState(initial = "")
    val password: String by registroViewModel.password.observeAsState(initial = "")
    val telefono: String by registroViewModel.telefono.observeAsState(initial = "")
    val registroEnable: Boolean by registroViewModel.registroEnable.observeAsState(initial = false)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()), // Hacemos la columna "scrolleable"
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TituloRegistro()
        Spacer(modifier = Modifier.padding(16.dp))

        NombreField(nombre) { newNombre ->
            registroViewModel.onRegisterChanged(newNombre, apellido, email, password, telefono)
        }
        Spacer(modifier = Modifier.padding(4.dp))

        ApellidoField(apellido) { newApellido ->
            registroViewModel.onRegisterChanged(nombre, newApellido, email, password, telefono)
        }
        Spacer(modifier = Modifier.padding(4.dp))

        EmailFieldRegister(email) { newEmail ->
            registroViewModel.onRegisterChanged(nombre, apellido, newEmail, password, telefono)
        }
        Spacer(modifier = Modifier.padding(4.dp))

        PasswordFieldRegister(password) { newPassword ->
            registroViewModel.onRegisterChanged(nombre, apellido, email, newPassword, telefono)
        }
        Spacer(modifier = Modifier.padding(4.dp))

        TelefonoField(telefono) { newTelefono ->
            registroViewModel.onRegisterChanged(nombre, apellido, email, password, newTelefono)
        }
        Spacer(modifier = Modifier.padding(16.dp))

        // --- BOTÓN DE REGISTRO ---
        Button(
            onClick = {

                scope.launch {


                    val usuarioRegistrado = registroViewModel.onRegisterButtonClicked()

                    if(usuarioRegistrado.email == "HuertoHogar@gmail.com") {
                        homeViewModel.onLoginSuccess(
                            userId = usuarioRegistrado.id,
                            userName = usuarioRegistrado.nombre,
                            isAdmin = true // Admin, porque tiene HuertoHogar@gmail.com dominio
                        )
                    }
                    else
                    {
                        homeViewModel.onLoginSuccess(
                            userId = usuarioRegistrado.id,
                            userName = usuarioRegistrado.nombre,
                            isAdmin = false // Usuario nuevo que no es admin
                        )
                    }

                    navController.navigate("home")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6D4C41),
                disabledContainerColor = Color(0xFFA1887F),
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            enabled = registroEnable
        ) {
            Text(text = "Registrarse")
        }
    }
}

// CAMPOS DE TEXTO VALIDADOS

@Composable
fun NombreField(value: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    // Replicamos la lógica de validación del VM
    val isValid = value.length > 3 && value.any { it.isLetter() }
    val helperText = "Debe tener más de 3 letras."

    // Lógica de colores
    val containerColor = when {
        !isDirty -> Color(0xFFDEDDDD) // Color inicial
        isValid -> Color(0xFFC8E6C9) // Verde si es válido
        else -> Color(0xFFFFCDD2) // Rojo si es inválido
    }

    Column {
        TextField(
            value = value,
            onValueChange = {
                isDirty = true // Se marca como dirty
                onTextFieldChanged(it)
            },
            placeholder = { Text(text = "Nombre") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color(0xFF636262),
                unfocusedTextColor = Color(0xFF636262),
                unfocusedContainerColor = containerColor,
                focusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        // Se muestra el texto de ayuda solo si está "dirty" y es inválido
        if (isDirty && !isValid) {
            Text(
                text = helperText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ApellidoField(value: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = value.length > 3 && value.any { it.isLetter() }
    val helperText = "Debe tener más de 3 letras."

    val containerColor = when {
        !isDirty -> Color(0xFFDEDDDD)
        isValid -> Color(0xFFC8E6C9)
        else -> Color(0xFFFFCDD2)
    }

    Column {
        TextField(
            value = value,
            onValueChange = {
                isDirty = true
                onTextFieldChanged(it)
            },
            placeholder = { Text(text = "Apellido") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color(0xFF636262),
                unfocusedTextColor = Color(0xFF636262),
                unfocusedContainerColor = containerColor,
                focusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (isDirty && !isValid) {
            Text(
                text = helperText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun EmailFieldRegister(value: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = Patterns.EMAIL_ADDRESS.matcher(value).matches()
    val helperText = "Debe ser un email válido (ej: vicente123@gmail.com)."

    val containerColor = when {
        !isDirty -> Color(0xFFDEDDDD)
        isValid -> Color(0xFFC8E6C9)
        else -> Color(0xFFFFCDD2)
    }

    Column {
        TextField(
            value = value,
            onValueChange = {
                isDirty = true
                onTextFieldChanged(it)
            },
            placeholder = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color(0xFF636262),
                unfocusedTextColor = Color(0xFF636262),
                unfocusedContainerColor = containerColor,
                focusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (isDirty && !isValid) {
            Text(
                text = helperText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun PasswordFieldRegister(value: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = (value.length > 6 && value.length < 20) && value.any { it.isDigit() }
    val helperText = "Debe ser entre 7 y 19 caracteres, y tener un número."

    val containerColor = when {
        !isDirty -> Color(0xFFDEDDDD)
        isValid -> Color(0xFFC8E6C9)
        else -> Color(0xFFFFCDD2)
    }

    Column {
        TextField(
            value = value,
            onValueChange = {
                isDirty = true
                onTextFieldChanged(it)
            },
            placeholder = { Text(text = "Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            maxLines = 1,
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color(0xFF636262),
                unfocusedTextColor = Color(0xFF636262),
                unfocusedContainerColor = containerColor,
                focusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (isDirty && !isValid) {
            Text(
                text = helperText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun TelefonoField(value: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = value.length == 10
    val helperText = "Debe tener 10 dígitos (ej: 912345678)."

    val containerColor = when {
        !isDirty -> Color(0xFFDEDDDD)
        isValid -> Color(0xFFC8E6C9)
        else -> Color(0xFFFFCDD2)
    }

    Column {
        TextField(
            value = value,
            onValueChange = {
                isDirty = true
                onTextFieldChanged(it)
            },
            placeholder = { Text(text = "Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color(0xFF636262),
                unfocusedTextColor = Color(0xFF636262),
                unfocusedContainerColor = containerColor,
                focusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (isDirty && !isValid) {
            Text(
                text = helperText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
@Composable
fun TituloRegistro() {
    Text(
        text = "¡Unetenos!",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            shadow = Shadow(
                color = CafeSombraTexto,
                offset = Offset(x = 2f, y = 2f),
                blurRadius = 4f
            )
        )
    )
}



