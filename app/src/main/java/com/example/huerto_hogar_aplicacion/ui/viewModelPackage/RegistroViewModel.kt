package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.Usuario
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegistroViewModel() : ViewModel() {

    private val repo = UsuarioRepository()

    private val _nombre = MutableLiveData<String>()
    val nombre: LiveData<String> = _nombre
    private val _apellido = MutableLiveData<String>()
    val apellido: LiveData<String> = _apellido
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password
    private val _telefono = MutableLiveData<String>()
    val telefono: LiveData<String> = _telefono

    private val _direccion = MutableLiveData<String>()
    val direccion: LiveData<String> = _direccion

    private val _registroEnable = MutableLiveData<Boolean>()
    val registroEnable: LiveData<Boolean> = _registroEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMensaje = MutableLiveData<String?>()
    val errorMensaje: LiveData<String?> = _errorMensaje

    // Actualizamos la función para recibir la dirección
    fun onRegisterChanged(nombre: String, apellido: String, email: String, password: String, telefono: String, direccion: String) {
        _nombre.value = nombre
        _apellido.value = apellido
        _email.value = email
        _password.value = password
        _telefono.value = telefono
        _direccion.value = direccion

        _registroEnable.value = isValidNombre(nombre) &&
                isValidApellido(apellido) &&
                isValidEmail(email) &&
                isValidPassword(password) &&
                isValidTelefono(telefono) &&
                isValidDireccion(direccion)
    }

    // --- FUNCIÓN PRINCIPAL DE REGISTRO ---
    fun performRegistro(auth: FirebaseAuth, homeViewModel: HomeViewModel, onSuccess: () -> Unit) {
        _isLoading.value = true
        _errorMensaje.value = null

        val emailVal = _email.value ?: ""
        val passVal = _password.value ?: ""

        // 1. Crear usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword(emailVal, passVal)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val newUid = firebaseUser?.uid ?: ""

                    // Truco para la DEMO: Si el correo es el del proyecto, lo hacemos Admin
                    val rolAsignado = if (emailVal.equals("HuertoHogar@gmail.com", ignoreCase = true)) "admin" else "usuario"

                    // 2. Preparar objeto para enviar a Spring Boot (MySQL)
                    val nuevoUsuarioBackend = Usuario(
                        uid = newUid,
                        nombre = _nombre.value ?: "",
                        apellido = _apellido.value ?: "",
                        email = emailVal,
                        telefono = _telefono.value ?: "",
                        direccion = _direccion.value ?: "Sin dirección", // Usamos el valor ingresado
                        rol = rolAsignado
                    )

                    // 3. Guardar en Backend
                    viewModelScope.launch {
                        try {
                            repo.crearUsuario(nuevoUsuarioBackend)

                            // 4. Actualizar sesión en la App
                            homeViewModel.onLoginSuccess(
                                uid = newUid,
                                userName = nuevoUsuarioBackend.nombre ?: "Usuario",
                                rol = rolAsignado
                            )
                            _isLoading.value = false
                            onSuccess()

                        } catch (e: Exception) {
                            _errorMensaje.value = "Cuenta creada en Firebase, pero falló guardar datos: ${e.message}"
                            _isLoading.value = false
                        }
                    }
                } else {
                    _errorMensaje.value = "Error en Firebase: ${task.exception?.message}"
                    _isLoading.value = false
                }
            }
    }

    // Validaciones
    private fun isValidNombre(nombre: String): Boolean = nombre.length > 2
    private fun isValidApellido(apellido: String): Boolean = apellido.length > 2
    private fun isValidPassword(password: String): Boolean = password.length >= 6
    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidTelefono(telefono: String): Boolean = telefono.length >= 9
    private fun isValidDireccion(direccion: String): Boolean = direccion.length > 5 // Mínimo 5 letras
}