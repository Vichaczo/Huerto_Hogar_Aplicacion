package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class SessionState {

    //  Estado "Logueado": Contiene los datos del usuario como id y nombre, tambien si es admin o no
    data class LoggedIn(
        val userId: Long,
        val userName: String,
        val isAdmin: Boolean
    ) : SessionState()

    // Estado "No Logueado": Vacio
    object LoggedOut : SessionState()
}

class HomeViewModel : ViewModel() {

    // Un solo StateFlow que empieza como 'LoggedOut'
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.LoggedOut)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    // Cuando se validan los datos y hay exito al inicar sesion se llama a esta funcion
    fun onLoginSuccess(userId: Long, userName: String, isAdmin: Boolean) {
        // Al cambiar el valor, todas las pantallas que observen 'sessionState' se actualizarán
        _sessionState.value = SessionState.LoggedIn(
            userId = userId,
            userName = userName,
            isAdmin = isAdmin
        )
    }

    //Funcion para cerrar sesion, simplemente lo vuelve a LoggedOut
    fun onLogout() {
        _sessionState.value = SessionState.LoggedOut
    }
}