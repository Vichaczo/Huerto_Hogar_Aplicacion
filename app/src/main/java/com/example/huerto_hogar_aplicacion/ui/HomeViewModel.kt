package com.example.huerto_hogar_aplicacion.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    // Estado privado y mutable que solo el ViewModel puede modificar.
    // Inicia como 'false' (el usuario no es admin por defecto).
    private val _isAdmin = MutableStateFlow(false)

    // Estado público e inmutable que la UI puede leer de forma segura.
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // Función que se llamaría desde la pantalla de Login cuando el inicio de sesión es exitoso.
    fun onAdminLoginSuccess() {
        _isAdmin.value = true
    }

    // Función para simular el cierre de sesión.
    fun onLogout() {
        _isAdmin.value = false
    }
}