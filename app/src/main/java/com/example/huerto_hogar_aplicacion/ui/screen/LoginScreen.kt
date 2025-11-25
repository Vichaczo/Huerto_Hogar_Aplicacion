package com.example.huerto_hogar_aplicacion.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.LoginViewModel
import com.example.huerto_hogar_aplicacion.ui.theme.CafeSombraTexto
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    loginViewModel: LoginViewModel,
    auth: FirebaseAuth
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center), loginViewModel, navController, homeViewModel, auth)
    }
}

@Composable
fun Login(
    modifier: Modifier,
    loginViewModel: LoginViewModel,
    navController: NavController,
    homeViewModel: HomeViewModel,
    auth: FirebaseAuth
) {
    // Observamos los estados del ViewModel
    val email: String by loginViewModel.email.observeAsState(initial = "")
    val password: String by loginViewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by loginViewModel.loginEnable.observeAsState(initial = false)
    val isLoading: Boolean by loginViewModel.isLoading.observeAsState(initial = false)
    val loginError: String? by loginViewModel.loginError.observeAsState(initial = null)

    val context = LocalContext.current

    // Efecto para mostrar errores (Toast) si ocurren
    LaunchedEffect(loginError) {
        loginError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Logo()
            Spacer(modifier = Modifier.padding(16.dp))

            EmailField(email) { loginViewModel.onLoginChanged(it, password) }
            Spacer(modifier = Modifier.padding(4.dp))

            PasswordField(password) { loginViewModel.onLoginChanged(email, it) }
            Spacer(modifier = Modifier.padding(8.dp))

            ForgotPassword(Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.padding(16.dp))

            // BOTÓN LOGIN CON LA NUEVA LÓGICA
            LoginButton(loginEnable) {
                // Delegamos toda la lógica al ViewModel (Firebase + Backend)
                loginViewModel.realizarLogin(auth, homeViewModel) {
                    // Este bloque se ejecuta solo si todo sale bien (onSuccess)
                    navController.navigate("home") {
                        // Limpia el stack para que no pueda volver al login con "atrás"
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))
            RegisterButton(navController)
        }
    }
}

// --- COMPONENTES UI AUXILIARES ---

@Composable
fun LoginButton(loginEnable: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6D4C41),
            disabledContainerColor = Color(0xFFA1887F),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ),
        enabled = loginEnable
    ) {
        Text(text = "Iniciar sesión")
    }
}

@Composable
fun RegisterButton(navController: NavController) {
    TextButton(onClick = { navController.navigate("registro") }) {
        Text("¿No tienes cuenta? Regístrate", color = Color(0xFF6D4C41))
    }
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = "¿Olvidaste la contraseña?",
        modifier = modifier.clickable {
            // Opcional: Implementar recuperación de contraseña de Firebase aquí si sobra tiempo
        },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF795548)
    )
}

@Composable
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = password.isNotBlank() && password.length >= 6
    val helperText = "Contraseña no válida"

    val containerColor = when {
        !isDirty -> Color(0xFFDEDDDD) // Color inicial
        isValid -> Color(0xFFC8E6C9) // Verde si es válido
        else -> Color(0xFFFFCDD2) // Rojo si es inválido
    }

    Column {
        TextField(
            value = password,
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
fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    var isDirty by remember { mutableStateOf(false) }
    val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val helperText = "Correo no válido"

    val containerColor = when {
        !isDirty -> Color(0xFFDEDDDD) // Color inicial
        isValid -> Color(0xFFC8E6C9) // Verde si es válido
        else -> Color(0xFFFFCDD2) // Rojo si es inválido
    }

    Column {
        TextField(
            value = email,
            onValueChange = {
                isDirty = true
                onTextFieldChanged(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Email") },
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
fun Logo() {
    Text(
        text = "Huerto Hogar",
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