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
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.HomeViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.LoginViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.RegistroViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudUsuarioViewModel
import com.example.huerto_hogar_aplicacion.ui.ViewModelFactory
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.UsuarioRepository
import com.example.huerto_hogar_aplicacion.ui.screen.HomeScreen
import com.example.huerto_hogar_aplicacion.ui.screen.SplashScreen
import com.example.huerto_hogar_aplicacion.ui.screen.LoginScreen
import com.example.huerto_hogar_aplicacion.ui.screen.RegistroScreen
import com.example.huerto_hogar_aplicacion.ui.screen.CrudUsuariosScreen
import com.example.huerto_hogar_aplicacion.ui.theme.Huerto_Hogar_AplicacionTheme
import com.example.huerto_hogar_aplicacion.data.AppDatabase
import com.example.huerto_hogar_aplicacion.ui.screen.CrudUsuariosEditarScreen

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.get(this) }
    private val repository by lazy { UsuarioRepository(database.usuarioDao()) }
    private val viewModelFactory by lazy { ViewModelFactory(repository) }
    private val homeViewModel: HomeViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }
    private val registroViewModel: RegistroViewModel by viewModels { viewModelFactory }

    private val crudUsuarioViewModel: CrudUsuarioViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Huerto_Hogar_AplicacionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    
                    AppNavigation(homeViewModel,loginViewModel,registroViewModel, crudUsuarioViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(homeViewModel: HomeViewModel,loginViewModel: LoginViewModel, registroViewModel : RegistroViewModel, crudUsuarioViewModel: CrudUsuarioViewModel) {
    // Se crea el controlador de navegación que gestiona el historial de pantallas.
    //El traspaso de HomeViewModel constante es para poder acceder al estado de login desde cualquier lugar.
    val navController = rememberNavController()

    // NavHost es el contenedor que intercambia las pantallas.
    // 'startDestination = "splash"' define cuál es la primera pantalla en mostrarse.
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController, homeViewModel = homeViewModel)
        }


        composable("login") {
            LoginScreen(navController = navController, homeViewModel = homeViewModel, loginViewModel = loginViewModel)
        }

        composable("registro") {
            RegistroScreen(navController = navController,  registroViewModel = registroViewModel,homeViewModel = homeViewModel)
        }

        composable("lista_productos") {
        }

        composable("CrudUsuariosScreen") {
             CrudUsuariosScreen(navController = navController,crudUsuarioViewModel = crudUsuarioViewModel)
        }
        composable(
            route = "CrudUsuariosEditarScreen/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            CrudUsuariosEditarScreen(
                navController = navController,
                crudUsuarioViewModel = crudUsuarioViewModel,
                userId = userId
            )
        }
    }
}
