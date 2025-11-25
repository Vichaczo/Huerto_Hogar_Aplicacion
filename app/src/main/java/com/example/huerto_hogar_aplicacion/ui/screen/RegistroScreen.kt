package com.example.huerto_hogar_aplicacion.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.RegistroViewModel
import com.example.huerto_hogar_aplicacion.ui.theme.CafeSombraTexto
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegistroScreen(
    navController: NavController,
    registroViewModel: RegistroViewModel,
    homeViewModel: HomeViewModel,
    auth: FirebaseAuth
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Registro(Modifier.align(Alignment.Center), registroViewModel, navController, homeViewModel, auth)
    }
}

@Composable
fun Registro(
    modifier: Modifier,
    registroViewModel: RegistroViewModel,
    navController: NavController,
    homeViewModel: HomeViewModel,
    auth: FirebaseAuth
) {
    // Observamos los estados del ViewModel
    val nombre: String by registroViewModel.nombre.observeAsState(initial = "")
    val apellido: String by registroViewModel.apellido.observeAsState(initial = "")
    val email: String by registroViewModel.email.observeAsState(initial = "")
    val password: String by registroViewModel.password.observeAsState(initial = "")
    val telefono: String by registroViewModel.telefono.observeAsState(initial = "")

    // NUEVO: Estado para la Dirección
    val direccion: String by registroViewModel.direccion.observeAsState(initial = "")

    val registroEnable: Boolean by registroViewModel.registroEnable.observeAsState(initial = false)

    // Estados para feedback visual (Carga y Errores)
    val isLoading: Boolean by registroViewModel.isLoading.observeAsState(initial = false)
    val errorMensaje: String? by registroViewModel.errorMensaje.observeAsState(initial = null)

    val context = LocalContext.current

    // Efecto para mostrar Toast si ocurre un error
    LaunchedEffect(errorMensaje) {
        errorMensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    if (isLoading) {
        // Spinner de carga
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TituloRegistro()
            Spacer(modifier = Modifier.padding(16.dp))

            // --- CAMPOS DE TEXTO ---
            // Nota: Al cambiar un campo, debemos pasar TODOS los valores actuales al ViewModel
            // para que no se borren los otros datos.

            NombreField(nombre) {
                registroViewModel.onRegisterChanged(it, apellido, email, password, telefono, direccion)
            }
            Spacer(modifier = Modifier.padding(4.dp))

            ApellidoField(apellido) {
                registroViewModel.onRegisterChanged(nombre, it, email, password, telefono, direccion)
            }
            Spacer(modifier = Modifier.padding(4.dp))

            EmailFieldRegister(email) {
                registroViewModel.onRegisterChanged(nombre, apellido, it, password, telefono, direccion)
            }
            Spacer(modifier = Modifier.padding(4.dp))

            PasswordFieldRegister(password) {
                registroViewModel.onRegisterChanged(nombre, apellido, email, it, telefono, direccion)
            }
            Spacer(modifier = Modifier.padding(4.dp))

            TelefonoField(telefono) {
                registroViewModel.onRegisterChanged(nombre, apellido, email, password, it, direccion)
            }
            Spacer(modifier = Modifier.padding(4.dp))

            // NUEVO: Campo de Dirección
            DireccionField(direccion) {
                registroViewModel.onRegisterChanged(nombre, apellido, email, password, telefono, it)
            }
            Spacer(modifier = Modifier.padding(16.dp))

            // --- BOTÓN DE REGISTRO ---
            Button(
                onClick = {
                    registroViewModel.performRegistro(auth, homeViewModel) {
                        // Éxito: Navegar al Home y limpiar stack
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
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
}

// --- COMPONENTES UI AUXILIARES ---

@Composable
fun NombreField(value: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = value.length > 2 && value.any { it.isLetter() }
    val helperText = "Debe tener más de 2 letras."

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
    val isValid = value.length > 2 && value.any { it.isLetter() }
    val helperText = "Debe tener más de 2 letras."

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
    val helperText = "Debe ser un email válido."

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
    val isValid = (value.length >= 6)
    val helperText = "Mínimo 6 caracteres."

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
    val isValid = value.length >= 9
    val helperText = "Mínimo 9 dígitos."

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
fun DireccionField(value: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    // Validación simple: más de 5 caracteres
    val isValid = value.length > 5
    val helperText = "Dirección muy corta."

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
            placeholder = { Text(text = "Dirección (Calle, Número, Ciudad)") },
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
fun TituloRegistro() {
    Text(
        text = "¡Únetenos!",
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