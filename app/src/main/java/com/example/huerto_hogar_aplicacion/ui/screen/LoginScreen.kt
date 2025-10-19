package com.example.huerto_hogar_aplicacion.ui.screen

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, homeViewModel: HomeViewModel, loginViewModel: LoginViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Login(Modifier.align(Alignment.Center), loginViewModel, navController,homeViewModel)
    }
}

@Composable
fun Login(modifier: Modifier, loginViewModel: LoginViewModel, navController: NavController,homeViewModel : HomeViewModel) {
    val email: String by loginViewModel.email.observeAsState(initial = "")
    val password: String by loginViewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by loginViewModel.loginEnable.observeAsState(initial = false)
    val isLoading: Boolean by loginViewModel.isLoading.observeAsState(initial = false)

    val coroutineScope = rememberCoroutineScope()

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
            LoginButton(loginEnable) {
                coroutineScope.launch {
                    loginViewModel.onLoginSelected()

                    val usuario = loginViewModel.buscarUsuarioEmail(email)
                    if(usuario?.email == email && usuario.password == password)
                    {
                        if(usuario?.nombre == "administrador") {
                            homeViewModel.onLoginSuccess(
                                userId = usuario.id,
                                userName = usuario.nombre,
                                isAdmin = true
                            )
                            navController.navigate("home")

                        }
                        else{
                            homeViewModel.onLoginSuccess(
                                userId = usuario.id,
                                userName = usuario.nombre,
                                isAdmin = false)
                            navController.navigate("home")
                        }
                    }
                    else{
                        navController.navigate("registro") /*Esto significa que paso algo mal, cambiar luego*/
                    }
                }

            }
            //Aqui iria el pop up de usuario no registrado/valido o si es valido te lleva a home, puede ser una funcion?
            Spacer(modifier = Modifier.padding(8.dp))
            RegisterButton(navController)
        }
    }
}


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
        modifier = modifier.clickable { },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF795548)
    )
}

// CAMPOS DE TEXTO VALIDADOS

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
    // Usamos la misma validación de email que en el registro
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

// LOGO

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

