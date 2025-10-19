package com.example.huerto_hogar_aplicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huerto_hogar_aplicacion.ui.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.LoginViewModel
import com.example.huerto_hogar_aplicacion.ui.RegistroViewModel
import com.example.huerto_hogar_aplicacion.ui.ViewModelFactory
import com.example.huerto_hogar_aplicacion.data.UsuarioRepository
import com.example.huerto_hogar_aplicacion.ui.screen.HomeScreen
import com.example.huerto_hogar_aplicacion.ui.screen.SplashScreen
import com.example.huerto_hogar_aplicacion.ui.screen.LoginScreen
import com.example.huerto_hogar_aplicacion.ui.screen.RegistroScreen
import com.example.huerto_hogar_aplicacion.ui.theme.Huerto_Hogar_AplicacionTheme
import com.example.huerto_hogar_aplicacion.data.AppDatabase

class MainActivity : ComponentActivity() {


    // 1. Crea la instancia de la base de datos de forma 'lazy' (solo cuando se necesita por primera vez)
    private val database by lazy { AppDatabase.get(this) }

    // 2. Crea el repositorio, pasándole el DAO de la base de datos
    private val repository by lazy { UsuarioRepository(database.usuarioDao()) }

    // 3. Crea la factory, pasándole el repositorio
    private val viewModelFactory by lazy { ViewModelFactory(repository) }

    // 4. Ahora, inicializa cada ViewModel correctamente
    private val homeViewModel: HomeViewModel by viewModels() // Este no necesita factory
    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }
    private val registroViewModel: RegistroViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Huerto_Hogar_AplicacionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(homeViewModel,loginViewModel,registroViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(homeViewModel: HomeViewModel,loginViewModel: LoginViewModel, registroViewModel : RegistroViewModel) {
    // Se crea el controlador de navegación que gestiona el historial de pantallas.
    val navController = rememberNavController()

    // NavHost es el contenedor que intercambia las pantallas.
    // 'startDestination = "splash"' define cuál es la primera pantalla en mostrarse.
    NavHost(navController = navController, startDestination = "splash") {

        // Define la ruta "splash" y le asigna el Composable SplashScreen.
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // Define la ruta "home" y le asigna el Composable HomeScreen.
        // Le pasamos el viewModel para que pueda acceder al estado de login.
        composable("home") {
            HomeScreen(navController = navController, homeViewModel = homeViewModel)
        }


        composable("login") {
            LoginScreen(navController = navController, homeViewModel = homeViewModel, loginViewModel = loginViewModel)
        }

        composable("registro") {
            RegistroScreen(navController = navController,  registroViewModel = registroViewModel)
        }

        composable("product_list") {
            // Este screen "product_list" sera para poder ver los productos
            // al hacer click en ver productos.
            // ProductListScreen(navController = navController)
        }

        composable("crud_management") {
            // Esto es para el CrudManagementScreen Composable
            // Desde ese CRUD el admin podra editar productos
            // CrudManagementScreen(navController = navController)
        }
    }
}
