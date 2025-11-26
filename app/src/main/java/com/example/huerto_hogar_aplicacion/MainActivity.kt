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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// --- VIEWMODELS ---
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.LoginViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.RegistroViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudUsuarioViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.ProductoViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CarritoViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudProductoViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.ClimaViewModel // <--- NUEVO IMPORT
// --- PANTALLAS ---
import com.example.huerto_hogar_aplicacion.ui.screen.HomeScreen
import com.example.huerto_hogar_aplicacion.ui.screen.SplashScreen
import com.example.huerto_hogar_aplicacion.ui.screen.LoginScreen
import com.example.huerto_hogar_aplicacion.ui.screen.RegistroScreen
import com.example.huerto_hogar_aplicacion.ui.screen.CrudUsuariosScreen
import com.example.huerto_hogar_aplicacion.ui.screen.CrudUsuariosEditarScreen
import com.example.huerto_hogar_aplicacion.ui.screen.CatalogoScreen
import com.example.huerto_hogar_aplicacion.ui.screen.DetalleProductoScreen
import com.example.huerto_hogar_aplicacion.ui.screen.CarritoScreen
import com.example.huerto_hogar_aplicacion.ui.screen.HistorialScreen
import com.example.huerto_hogar_aplicacion.ui.screen.CrudProductosScreen
import com.example.huerto_hogar_aplicacion.ui.screen.CrudProductosEditarScreen
import com.example.huerto_hogar_aplicacion.ui.screen.ClimaScreen // <--- NUEVO IMPORT
// --- TEMA Y FIREBASE ---
import com.example.huerto_hogar_aplicacion.ui.theme.Huerto_Hogar_AplicacionTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    // Declaración de ViewModels
    private val homeViewModel: HomeViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val registroViewModel: RegistroViewModel by viewModels()
    private val crudUsuarioViewModel: CrudUsuarioViewModel by viewModels()

    // Nuevos ViewModels para el E-commerce y Admin
    private val productoViewModel: ProductoViewModel by viewModels()
    private val carritoViewModel: CarritoViewModel by viewModels()
    private val crudProductoViewModel: CrudProductoViewModel by viewModels()

    // ViewModel para Clima
    private val climaViewModel: ClimaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        auth = Firebase.auth

        setContent {
            Huerto_Hogar_AplicacionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        homeViewModel = homeViewModel,
                        loginViewModel = loginViewModel,
                        registroViewModel = registroViewModel,
                        crudUsuarioViewModel = crudUsuarioViewModel,
                        productoViewModel = productoViewModel,
                        carritoViewModel = carritoViewModel,
                        crudProductoViewModel = crudProductoViewModel,
                        climaViewModel = climaViewModel, // Pasamos el VM
                        auth = auth
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    homeViewModel: HomeViewModel,
    loginViewModel: LoginViewModel,
    registroViewModel: RegistroViewModel,
    crudUsuarioViewModel: CrudUsuarioViewModel,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    crudProductoViewModel: CrudProductoViewModel,
    climaViewModel: ClimaViewModel, // Recibimos el VM
    auth: FirebaseAuth
) {
    val navController = rememberNavController()

    // Definimos el grafo de navegación. "splash" es la primera pantalla.
    NavHost(navController = navController, startDestination = "splash") {

        // --- PANTALLAS DE INICIO Y AUTENTICACIÓN ---

        composable("splash") {
            SplashScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController, homeViewModel = homeViewModel)
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                loginViewModel = loginViewModel,
                auth = auth
            )
        }

        composable("registro") {
            RegistroScreen(
                navController = navController,
                registroViewModel = registroViewModel,
                homeViewModel = homeViewModel,
                auth = auth
            )
        }

        // --- ZONA DE CLIMA (NUEVO) ---
        composable("clima") {
            ClimaScreen(navController = navController, viewModel = climaViewModel)
        }

        // --- ZONA DE COMPRAS (CLIENTE) ---

        composable("catalogo") {
            CatalogoScreen(navController = navController, viewModel = productoViewModel)
        }

        composable(
            route = "detalle_producto/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            DetalleProductoScreen(
                navController = navController,
                productoId = id,
                homeViewModel = homeViewModel,
                productoViewModel = productoViewModel,
                carritoViewModel = carritoViewModel
            )
        }

        composable("carrito") {
            CarritoScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                carritoViewModel = carritoViewModel
            )
        }

        composable("historial") {
            HistorialScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                carritoViewModel = carritoViewModel
            )
        }


        // --- ZONA ADMINISTRATIVA (ADMIN) ---

        // 1. Gestión de Usuarios
        composable("CrudUsuariosScreen") {
            CrudUsuariosScreen(navController = navController, crudUsuarioViewModel = crudUsuarioViewModel)
        }

        composable(
            route = "CrudUsuariosEditarScreen/{userUid}",
            arguments = listOf(navArgument("userUid") { type = NavType.StringType })
        ) { backStackEntry ->
            val userUid = backStackEntry.arguments?.getString("userUid") ?: ""
            CrudUsuariosEditarScreen(navController, crudUsuarioViewModel, userUid)
        }

        // 2. Gestión de Productos (Inventario)
        composable("crud_productos") {
            CrudProductosScreen(navController = navController, viewModel = crudProductoViewModel)
        }

        composable(
            route = "CrudProductosEditarScreen/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("productoId") ?: 0L
            CrudProductosEditarScreen(navController, crudProductoViewModel, id)
        }
    }
}