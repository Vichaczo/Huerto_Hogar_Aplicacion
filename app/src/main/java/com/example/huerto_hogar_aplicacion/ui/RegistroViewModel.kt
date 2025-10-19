package com.example.huerto_hogar_aplicacion.ui

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.Usuario
import com.example.huerto_hogar_aplicacion.data.UsuarioRepository
import kotlinx.coroutines.launch

class RegistroViewModel(private val repo: UsuarioRepository) : ViewModel() {
    private val _nombre = MutableLiveData<String>()
    val nombre: LiveData<String> = _nombre

    // ... (el resto de tus LiveData para apellido, email, etc. están bien)
    private val _apellido = MutableLiveData<String>()
    val apellido: LiveData<String> = _apellido
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password
    private val _telefono = MutableLiveData<String>()
    val telefono: LiveData<String> = _telefono

    private val _registroEnable = MutableLiveData<Boolean>()
    val registroEnable: LiveData<Boolean> = _registroEnable


    fun onRegisterChanged(nombre: String, apellido: String, email: String, password: String, telefono: String) {
        _nombre.value = nombre
        _apellido.value = apellido
        _email.value = email
        _password.value = password
        _telefono.value = telefono
        _registroEnable.value = isValidNombre(nombre) && isValidApellido(apellido) && isValidEmail(email) && isValidPassword(password) && isValidTelefono(telefono)
    }

    suspend fun onRegisterButtonClicked(): Usuario {
        // Creamos el objeto Usuario usando los valores actuales de los LiveData.
        val nuevoUsuario = Usuario(
            nombre = nombre.value ?: "",
            apellido = apellido.value ?: "",
            email = email.value ?: "",
            password = password.value ?: "",
            telefono = telefono.value ?: ""
            // id se deja por defecto (usualmente 0)
        )

        // Llamamos a la función suspend del repositorio, que devuelve el nuevo ID.
        val newId = repo.agregar(nuevoUsuario) // repo.agregar() ahora devuelve Long

        // Devolvemos una copia del usuario, pero ahora con el ID correcto.
        return nuevoUsuario.copy(id = newId)
    }


    // Las funciones de validación están bien
    private fun isValidNombre(nombre: String): Boolean = nombre.length > 3 && nombre.any { it.isLetter() }
    private fun isValidApellido(nombre: String): Boolean = nombre.length > 3 && nombre.any { it.isLetter() }
    private fun isValidPassword(password: String): Boolean = (password.length > 6 && password.length < 20) && password.any { it.isDigit() }
    private fun isValidEmail(email: String): Boolean  = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidTelefono(telefono: String): Boolean = telefono.length == 10
}
