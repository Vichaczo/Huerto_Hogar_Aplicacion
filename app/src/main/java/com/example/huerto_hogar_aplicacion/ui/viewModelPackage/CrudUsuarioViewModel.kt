package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.Usuario
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// Estado para manejar la UI de CRUD (simplificado para Carga Simple)
data class CrudState(
    val users: List<Usuario> = emptyList(),      // Lista filtrada que se muestra
    val currentQuery: String = "",
    val isLoading: Boolean = false
)

class CrudUsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _state = MutableStateFlow(CrudState())
    val state: StateFlow<CrudState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("") // El estado de la barra de búsqueda

    //  COMBINA: Une el Flow de todos los usuarios de la DB y la consulta de búsqueda
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
    fun update(id: Long, newName: String, newEmail: String) = viewModelScope.launch {
        // ... (lógica de update)
    }
}