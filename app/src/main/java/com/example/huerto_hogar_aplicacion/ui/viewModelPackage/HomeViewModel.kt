package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class SessionState {
    data class LoggedIn(
        val uid: String,
        val userName: String,
        val rol: String
    ) : SessionState() {
        // Propiedad calculada para que tu HomeScreen funcione sin cambios
        val isAdmin: Boolean
            get() = rol.equals("admin", ignoreCase = true)
    }

    object Invitado : SessionState()
}

class HomeViewModel : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Invitado)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    fun onLoginSuccess(uid: String, userName: String, rol: String) {
        _sessionState.value = SessionState.LoggedIn(
            uid = uid,
            userName = userName,
            rol = rol
        )
    }

    fun onLogout() {
        _sessionState.value = SessionState.Invitado
    }
}