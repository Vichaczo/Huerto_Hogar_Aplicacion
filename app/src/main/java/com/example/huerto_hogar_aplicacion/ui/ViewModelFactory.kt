package com.example.huerto_hogar_aplicacion.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.UsuarioRepository
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.LoginViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.RegistroViewModel
import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.CrudUsuarioViewModel

// La factory ahora recibe el repositorio, no el 'Application'
class ViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegistroViewModel::class.java) -> {
                RegistroViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CrudUsuarioViewModel::class.java) -> {
                CrudUsuarioViewModel(repository) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
