package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class SessionState {

    // 1. Estado "Logueado": Contiene TODOS los datos del usuario
    data class LoggedIn(
        val userId: Long,
        val userName: String,
        val isAdmin: Boolean
    ) : SessionState()

    // 2. Estado "No Logueado": No contiene nada
    object LoggedOut : SessionState()
}

class HomeViewModel : ViewModel() {

    // 1. Un solo StateFlow que empieza como 'LoggedOut'
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.LoggedOut)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()


    /**
     * Esta es la ÚNICA función que necesitas para iniciar sesión.
     * La llamas desde tu LoginScreen O tu RegistroScreen
     * cuando el usuario entra con éxito.
     *
     * (Probablemente le pases un objeto 'Usuario' real de tu base de datos)
     */
    fun onLoginSuccess(userId: Long, userName: String, isAdmin: Boolean) {
        // Al cambiar el valor, todas las pantallas que observen 'sessionState' se actualizarán
        _sessionState.value = SessionState.LoggedIn(
            userId = userId,
            userName = userName,
            isAdmin = isAdmin
        )
    }

    /**
     * Función para cerrar la sesión.
     * Simplemente vuelve al estado 'LoggedOut'.
     */
    fun onLogout() {
        _sessionState.value = SessionState.LoggedOut
    }
}