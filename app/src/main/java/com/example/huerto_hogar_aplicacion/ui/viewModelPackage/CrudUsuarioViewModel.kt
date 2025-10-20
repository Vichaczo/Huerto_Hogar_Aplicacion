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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Estado para manejar la UI de CRUD
data class CrudState(
    val users: List<Usuario> = emptyList(),      // Lista filtrada que se muestra
    val currentQuery: String = "",
    val isLoading: Boolean = false
)

data class EditUserState(
    val id: Long = 0L,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val password: String = "",
    val telefono: String = "",
    val isUpdateEnabled: Boolean = false,
    val isLoadingUser: Boolean = false,
    val error: String? = null
)

class CrudUsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val idUsuarioPorActualizar = MutableStateFlow(null)

    private val _editUserState = MutableStateFlow(EditUserState())
    val editUserState: StateFlow<EditUserState> = _editUserState.asStateFlow()

    private val _state = MutableStateFlow(CrudState())
    val state: StateFlow<CrudState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("") // El estado de la barra de búsqueda

    // Une el Flow de todos los usuarios de la DB y la consulta de búsqueda
    init {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            // El Flow 'usuarios' proviene de UsuarioRepository, que observa la DB.
            repository.usuarios
                .combine(_searchQuery) { allUsers, query ->
                    //  APLICA FILTRO EN MEMORIA:
                    if (query.isBlank()) {
                        allUsers // Si no hay búsqueda, devuelve todos.
                    } else {
                        val lowerCaseQuery = query.lowercase()
                        allUsers.filter { user ->
                            user.nombre.lowercase().contains(lowerCaseQuery) ||
                                    user.email.lowercase().contains(lowerCaseQuery) ||
                                    user.telefono.lowercase().contains(lowerCaseQuery)
                        }
                    }
                }
                .collect { filteredList ->
                    // ACTUALIZA el estado de la UI con la lista filtrada
                    _state.value = _state.value.copy(
                        users = filteredList,
                        isLoading = false,
                        currentQuery = _searchQuery.value // Refleja la consulta actual
                    )
                }
        }
    }

    // --- LÓGICA DE BÚSQUEDA ---
    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery // Al cambiar el query, se dispara el combine/collect de arriba.
    }

    // --- LÓGICA CRUD  ---

    fun insert(usuario: Usuario) = viewModelScope.launch {
        repository.agregar(usuario)
    }

    fun delete(usuario: Usuario) = viewModelScope.launch {
        repository.eliminar(usuario)
    }

    //
    fun loadUserById(id: Long) {
        viewModelScope.launch {
            _editUserState.value = _editUserState.value.copy(isLoadingUser = true, error = null)
            try {

                val user = repository.obtener(id).first()
                if (user != null) {
                    val isValidInitial = isValidNombre(user.nombre) && isValidApellido(user.apellido) && isValidEmail(user.email) && isValidPassword(user.password) && isValidTelefono(user.telefono)

                    _editUserState.value = EditUserState(
                        id = user.id,
                        nombre = user.nombre,
                        apellido = user.apellido,
                        email = user.email,
                        password = user.password,
                        telefono = user.telefono,
                        isUpdateEnabled = isValidInitial,
                        isLoadingUser = false
                    )
                } else {
                    _editUserState.value = _editUserState.value.copy(isLoadingUser = false, error = "Usuario no encontrado")
                }
            } catch (e: Exception) {
                _editUserState.value = _editUserState.value.copy(isLoadingUser = false, error = e.localizedMessage ?: "Error al cargar usuario")
            }
        }
    }
    fun onEditFieldChanged(
        nombre: String = _editUserState.value.nombre,
        apellido: String = _editUserState.value.apellido,
        email: String = _editUserState.value.email,
        password: String = _editUserState.value.password,
        telefono: String = _editUserState.value.telefono
    ) {
        val isValid = isValidNombre(nombre) && isValidApellido(apellido) && isValidEmail(email) && isValidPassword(password) && isValidTelefono(telefono)

        _editUserState.value = _editUserState.value.copy(
            nombre = nombre,
            apellido = apellido,
            email = email,
            password = password,
            telefono = telefono,
            isUpdateEnabled = isValid
        )
    }
    fun onUpdateUserClicked() = viewModelScope.launch {
        val state = _editUserState.value
        update(state.id, state.nombre, state.apellido, state.email, state.password, state.telefono)
    }
    fun update(id: Long, nombre: String, apellido: String, email: String, password: String, telefono: String) = viewModelScope.launch {
        repository.actualizar(id, nombre, apellido, email, password, telefono)
    }

    private fun isValidNombre(nombre: String): Boolean = nombre.length > 3 && nombre.any { it.isLetter() }
    private fun isValidApellido(nombre: String): Boolean = nombre.length > 3 && nombre.any { it.isLetter() }
    private fun isValidPassword(password: String): Boolean = (password.length > 6 && password.length < 20) && password.any { it.isDigit() }
    private fun isValidEmail(email: String): Boolean  = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidTelefono(telefono: String): Boolean = telefono.length == 10
}
