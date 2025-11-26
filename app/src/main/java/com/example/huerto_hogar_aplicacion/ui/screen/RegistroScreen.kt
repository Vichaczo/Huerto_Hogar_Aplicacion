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
import com.example.huerto_hogar_aplicacion.data.local.UserStore
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.SessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    val direccion: String by registroViewModel.direccion.observeAsState(initial = "")

    val registroEnable: Boolean by registroViewModel.registroEnable.observeAsState(initial = false)
    val isLoading: Boolean by registroViewModel.isLoading.observeAsState(initial = false)
    val errorMensaje: String? by registroViewModel.errorMensaje.observeAsState(initial = null)

    // Contexto para DataStore
    val context = LocalContext.current

    LaunchedEffect(errorMensaje) {
        errorMensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF6D4C41) // Café
            )
        }
    } else {
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TituloRegistro()
            Spacer(modifier = Modifier.padding(16.dp))

            // CAMPOS DE TEXTO
            NombreField(nombre) { registroViewModel.onRegisterChanged(it, apellido, email, password, telefono, direccion) }
            Spacer(modifier = Modifier.padding(4.dp))

            ApellidoField(apellido) { registroViewModel.onRegisterChanged(nombre, it, email, password, telefono, direccion) }
            Spacer(modifier = Modifier.padding(4.dp))

            EmailFieldRegister(email) { registroViewModel.onRegisterChanged(nombre, apellido, it, password, telefono, direccion) }
            Spacer(modifier = Modifier.padding(4.dp))

            PasswordFieldRegister(password) { registroViewModel.onRegisterChanged(nombre, apellido, email, it, telefono, direccion) }
            Spacer(modifier = Modifier.padding(4.dp))

            TelefonoField(telefono) { registroViewModel.onRegisterChanged(nombre, apellido, email, password, it, direccion) }
            Spacer(modifier = Modifier.padding(4.dp))

            DireccionField(direccion) { registroViewModel.onRegisterChanged(nombre, apellido, email, password, telefono, it) }
            Spacer(modifier = Modifier.padding(16.dp))

            // BOTÓN DE REGISTRO
            Button(
                onClick = {
                    registroViewModel.performRegistro(auth, homeViewModel) {
                        // LÓGICA DE ÉXITO:

                        // 1. Obtenemos la sesión recién creada en memoria
                        val state = homeViewModel.sessionState.value

                        // 2. Guardamos en DataStore (Disco Local)
                        if (state is SessionState.LoggedIn) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val userStore = UserStore(context)
                                userStore.saveUser(
                                    uid = state.uid,
                                    name = state.userName,
                                    role = state.rol
                                )
                            }
                        }

                        // 3. Navegar al Home
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
                enabled = registroEnable,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun NombreField(value: String, onTextFieldChanged: (String) -> Unit) {
    GenericTextField(value, onTextFieldChanged, "Nombre")
}

@Composable
fun ApellidoField(value: String, onTextFieldChanged: (String) -> Unit) {
    GenericTextField(value, onTextFieldChanged, "Apellido")
}

@Composable
fun EmailFieldRegister(value: String, onTextFieldChanged: (String) -> Unit) {
    GenericTextField(value, onTextFieldChanged, "Email", KeyboardType.Email)
}

@Composable
fun TelefonoField(value: String, onTextFieldChanged: (String) -> Unit) {
    GenericTextField(value, onTextFieldChanged, "Teléfono", KeyboardType.Phone)
}

@Composable
fun DireccionField(value: String, onTextFieldChanged: (String) -> Unit) {
    GenericTextField(value, onTextFieldChanged, "Dirección")
}

@Composable
fun PasswordFieldRegister(value: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = (value.length >= 6)
    val helperText = "Mínimo 6 caracteres."

    val containerColor = when {
        !isDirty -> Color(0xFFDEDDDD)
        isValid -> Color(0xFFC8E6C9) // Verde
        else -> Color(0xFFFFCDD2) // Rojo
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
            ),
            shape = MaterialTheme.shapes.small
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
fun GenericTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = value.isNotBlank()
    val containerColor = if (!isDirty) Color(0xFFDEDDDD) else if (isValid) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)

    Column {
        TextField(
            value = value,
            onValueChange = {
                isDirty = true
                onValueChange(it)
            },
            placeholder = { Text(text = placeholder) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color(0xFF636262),
                unfocusedTextColor = Color(0xFF636262),
                unfocusedContainerColor = containerColor,
                focusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.small
        )
    }
}

@Composable
fun TituloRegistro() {
    Text(
        text = "¡Únetenos!",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        style = TextStyle(
            color = Color(0xFF6D4C41), // Café
            shadow = Shadow(
                color = CafeSombraTexto,
                offset = Offset(x = 2f, y = 2f),
                blurRadius = 4f
            )
        )
    )
}