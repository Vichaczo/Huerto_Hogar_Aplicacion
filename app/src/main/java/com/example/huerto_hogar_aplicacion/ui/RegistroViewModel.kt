package com.example.huerto_hogar_aplicacion.ui

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistroViewModel : ViewModel() {
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

    private fun isValidNombre(nombre: String): Boolean = nombre.length > 3 && nombre.any { it.isLetter() }

    private fun isValidApellido(nombre: String): Boolean = nombre.length > 3 && nombre.any { it.isLetter() }

    private fun isValidPassword(password: String): Boolean = (password.length > 6 && password.length < 20) && password.any { it.isDigit() }

    private fun isValidEmail(email: String): Boolean  = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidTelefono(telefono: String): Boolean = telefono.length == 10

    private val _loginEnable = MutableLiveData<Boolean>()

    val loginEnable: LiveData<Boolean> = _loginEnable

    fun onRegisterChanged(nombre: String, apellido: String, email: String, password: String, telefono: String) {
        _nombre.value = nombre
        _apellido.value = apellido
        _email.value = email
        _password.value = password
        _telefono.value = telefono
        _loginEnable.value = isValidNombre(nombre) && isValidApellido(apellido) && isValidEmail(email) && isValidPassword(password) && isValidTelefono(telefono)
    }

    private fun validarFormulario(): Boolean {
        val nombreValido = isValidNombre(_nombre.value.orEmpty())
        val apellidoValido = isValidApellido(_apellido.value.orEmpty())
        val emailValido = isValidEmail(_email.value.orEmpty())
        val passwordValido = isValidPassword(_password.value.orEmpty())
        val telefonoValido = isValidTelefono(_telefono.value.orEmpty())
        return nombreValido && apellidoValido && emailValido && passwordValido && telefonoValido
    }

}