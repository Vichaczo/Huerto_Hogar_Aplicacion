package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.Usuario
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.UsuarioRepository
import kotlinx.coroutines.delay

class LoginViewModel(private val repo: UsuarioRepository) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    suspend fun buscarUsuarioEmail(email: String): Usuario? {
        return repo.findUserByEmail(email)
    }

    private fun isValidPassword(password: String): Boolean = password.length > 6

    private fun isValidEmail(email: String): Boolean  = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    suspend fun onLoginSelected() {
        _isLoading.value = true
        delay(2000)
        _isLoading.value = false
    }

}