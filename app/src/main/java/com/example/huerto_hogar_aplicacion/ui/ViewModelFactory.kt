package com.example.huerto_hogar_aplicacion.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.huerto_hogar_aplicacion.data.AppDatabase
import com.example.huerto_hogar_aplicacion.data.UsuarioRepository
import kotlin.getValue

class ViewModelFactory(app: Application) : ViewModelProvider.Factory {
    // Esta clase implementa la interfaz ViewModelProvider.Factory,
    // que permite crear manualmente instancias de ViewModel con parámetros personalizados.

    private val repo by lazy {
        // Propiedad "repo" inicializada bajo demanda (lazy).
        // Solo se crea cuando se utiliza por primera vez.

        val dao = AppDatabase.get(app).usuarioDao()
        // Obtiene la base de datos a través del método estático "get()" y accede al DAO (ProductoDao).

        UsuarioRepository(dao)
        // Crea una instancia del repositorio que usará el ViewModel.
    }

    @Suppress("UNCHECKED_CAST")
    // Suprime la advertencia de conversión genérica (cast).
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        // Revisa qué tipo de ViewModel se está pidiendo y lo crea con el repositorio
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repo) as T
            }
            modelClass.isAssignableFrom(RegistroViewModel::class.java) -> {
                RegistroViewModel(repo) as T
            }
            // Puedes añadir más ViewModels aquí en el futuro
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    // Crea y devuelve una nueva instancia de ProductViewModel con el repositorio inyectado.
    // El "as T" es necesario porque el método debe devolver un ViewModel genérico.
}





