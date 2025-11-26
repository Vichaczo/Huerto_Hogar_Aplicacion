package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.Usuario
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// Estado de la Lista
data class CrudState(
    val users: List<Usuario> = emptyList(),
    val currentQuery: String = "",
    val isLoading: Boolean = false
)

// Estado de Edición ACTUALIZADO
data class EditUserState(
    val uid: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val rol: String = "usuario",
    val isUpdateEnabled: Boolean = false,
    val isLoadingUser: Boolean = false,
    val error: String? = null
)

class CrudUsuarioViewModel : ViewModel() {

    private val repository = UsuarioRepository()

    private val _allUsers = MutableStateFlow<List<Usuario>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _state = MutableStateFlow(CrudState())
    val state: StateFlow<CrudState> = _state.asStateFlow()

    private val _editUserState = MutableStateFlow(EditUserState())
    val editUserState: StateFlow<EditUserState> = _editUserState.asStateFlow()

    init {
        fetchUsuarios()
        setupSearchFilter()
    }

    fun fetchUsuarios() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val usuariosApi = repository.listarUsuarios()
                _allUsers.value = usuariosApi
            } catch (e: Exception) {
                _allUsers.value = emptyList()
            } finally {
                if (_allUsers.value.isEmpty()) {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }

    private fun setupSearchFilter() {
        viewModelScope.launch {
            combine(_allUsers, _searchQuery) { users, query ->
                if (query.isBlank()) {
                    users
                } else {
                    val lower = query.lowercase()
                    users.filter {
                        (it.nombre ?: "").lowercase().contains(lower) ||
                                it.email.lowercase().contains(lower)
                    }
                }
            }.collect { filteredList ->
                _state.value = _state.value.copy(users = filteredList, isLoading = false, currentQuery = _searchQuery.value)
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun delete(usuario: Usuario) = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)
        try {
            repository.eliminarUsuario(usuario.uid)
            fetchUsuarios()
        } catch (e: Exception) {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun loadUserByUid(uid: String) {
        viewModelScope.launch {
            _editUserState.value = _editUserState.value.copy(isLoadingUser = true, error = null)
            try {
                val user = repository.obtenerUsuario(uid)
                if (user != null) {
                    _editUserState.value = EditUserState(
                        uid = user.uid,
                        nombre = user.nombre ?: "",
                        apellido = user.apellido ?: "",
                        email = user.email,
                        password = "",
                        telefono = user.telefono ?: "",
                        direccion = user.direccion ?: "", // Cargamos dirección
                        rol = user.rol ?: "usuario",      // Cargamos rol
                        isUpdateEnabled = true,
                        isLoadingUser = false
                    )
                } else {
                    _editUserState.value = _editUserState.value.copy(isLoadingUser = false, error = "Usuario no encontrado")
                }
            } catch (e: Exception) {
                _editUserState.value = _editUserState.value.copy(isLoadingUser = false, error = e.message)
            }
        }
    }

    // --- MANEJO DE CAMBIOS EN EL FORMULARIO DE EDICIÓN ---
    fun onEditFieldChanged(nombre: String, apellido: String, email: String, password: String, telefono: String, direccion: String, rol: String) {
        val isValid = isValidNombre(nombre) && isValidApellido(apellido) && isValidEmail(email) && isValidTelefono(telefono) && isValidDireccion(direccion)

        _editUserState.value = _editUserState.value.copy(
            nombre = nombre,
            apellido = apellido,
            email = email,
            password = password,
            telefono = telefono,
            direccion = direccion, // Actualizamos dirección
            rol = rol,             // Actualizamos rol
            isUpdateEnabled = isValid
        )
    }

    // --- GUARDAR CAMBIOS ---
    fun onUpdateUserClicked() = viewModelScope.launch {
        val current = _editUserState.value
        _editUserState.value = current.copy(isLoadingUser = true)

        try {
            val userToUpdate = Usuario(
                uid = current.uid,
                nombre = current.nombre,
                apellido = current.apellido,
                email = current.email,
                telefono = current.telefono,
                direccion = current.direccion, // Enviamos nueva dirección
                rol = current.rol              // Enviamos nuevo rol (admin/usuario)
            )

            repository.actualizarUsuario(current.uid, userToUpdate)
            fetchUsuarios()

        } catch (e: Exception) {
            _editUserState.value = current.copy(error = "Error al actualizar: ${e.message}")
        } finally {
            _editUserState.value = _editUserState.value.copy(isLoadingUser = false)
        }
    }

    private fun isValidNombre(valStr: String) = valStr.length > 2
    private fun isValidApellido(valStr: String) = valStr.length > 2
    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidTelefono(tel: String) = tel.length >= 9
    private fun isValidDireccion(dir: String) = dir.length > 3
}