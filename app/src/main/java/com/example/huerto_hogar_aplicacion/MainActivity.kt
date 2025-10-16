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
import com.example.huerto_hogar_aplicacion.ui.screen.HomeScreen
import com.example.huerto_hogar_aplicacion.ui.screen.SplashScreen
import com.example.huerto_hogar_aplicacion.ui.theme.Huerto_Hogar_AplicacionTheme
import kotlin.getValue

class MainActivity : ComponentActivity() {

    // Inicializa el HomeViewModel para que su estado persista
    // durante toda la vida de la app y pueda ser compartido.
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Huerto_Hogar_AplicacionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(homeViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(homeViewModel: HomeViewModel) {
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

        // Aquí definirías las otras pantallas. Las dejo como placeholders.
        // Es importante pasar el viewModel al LoginScreen para que pueda
        // notificar cuando el login de admin sea exitoso.
        composable("login") {
            // Suponiendo que tienes un LoginScreen Composable
            // LoginScreen(navController = navController, homeViewModel = homeViewModel)
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
