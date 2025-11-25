package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    private val repo = UsuarioRepository()
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Estado para notificar a la UI si hubo error
    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && password.length > 5
    }

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // FUNCION PRINCIPAL MODIFICADA
    fun realizarLogin(auth: FirebaseAuth, homeViewModel: HomeViewModel, onSuccess: () -> Unit) {
        _isLoading.value = true
        _loginError.value = null

        val currentEmail = _email.value ?: ""
        val currentPass = _password.value ?: ""

        // 1. Intentar Login con Firebase
        auth.signInWithEmailAndPassword(currentEmail, currentPass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""

                    // 2. Si Firebase dice OK, buscamos los datos en TU Backend (Spring Boot)
                    viewModelScope.launch {
                        try {
                            val usuarioBackend = repo.obtenerUsuario(uid)

                            if (usuarioBackend != null) {
                                // 3. ¡Éxito total! Actualizamos la sesión global
                                homeViewModel.onLoginSuccess(
                                    uid = usuarioBackend.uid,
                                    userName = usuarioBackend.nombre ?: "Usuario",
                                    rol = usuarioBackend.rol ?: "usuario"
                                )
                                _isLoading.value = false
                                onSuccess() // Navegar al Home
                            } else {
                                // Caso raro: Está en Firebase pero no en tu MySQL
                                _loginError.value = "Usuario no encontrado en la base de datos."
                                _isLoading.value = false
                            }
                        } catch (e: Exception) {
                            // Error de conexión con tu Backend (EC2 apagado, etc.)
                            _loginError.value = "Error conectando al servidor: ${e.message}"
                            _isLoading.value = false
                        }
                    }
                } else {
                    // Error de Firebase (Contraseña mal, usuario no existe)
                    _loginError.value = "Error de autenticación: ${task.exception?.message}"
                    _isLoading.value = false
                }
            }
    }
}