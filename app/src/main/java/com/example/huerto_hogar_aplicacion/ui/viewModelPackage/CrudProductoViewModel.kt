package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import com.example.huerto_hogar_aplicacion.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado del Formulario
data class EditProductoState(
    val id: Long = 0, // 0 significa Nuevo Producto
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "", // Usamos String para el input del usuario
    val stock: String = "",
    val categoria: String = "Frutas", // Valor por defecto
    val imagenUrl: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

class CrudProductoViewModel : ViewModel() {

    // Instancia del repo (Opción A)
    private val repository = ProductoRepository()

    private val _allProductos = MutableStateFlow<List<Producto>>(emptyList())
    val allProductos: StateFlow<List<Producto>> = _allProductos.asStateFlow()

    private val _editState = MutableStateFlow(EditProductoState())
    val editState: StateFlow<EditProductoState> = _editState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Lista de categorías disponibles para el Dropdown
    val categoriasDisponibles = listOf("Frutas", "Verduras", "Semillas", "Herramientas", "Otros")

    init {
        fetchProductos()
    }

    fun fetchProductos() {
        _isLoading.value = true
        viewModelScope.launch {
            val lista = repository.listarProductos()
            _allProductos.value = lista
            _isLoading.value = false
        }
    }

    // Cargar datos para editar (si id > 0) o limpiar para crear (si id = 0)
    fun prepareForEdit(productoId: Long) {
        if (productoId == 0L) {
            // Limpiar formulario para nuevo producto
            _editState.value = EditProductoState()
        } else {
            // Cargar producto existente
            viewModelScope.launch {
                val p = repository.obtenerProducto(productoId)
                if (p != null) {
                    _editState.value = EditProductoState(
                        id = p.id,
                        nombre = p.nombre,
                        descripcion = p.descripcion ?: "",
                        precio = p.precio.toInt().toString(), // Convertimos para el TextField
                        stock = p.stock.toString(),
                        categoria = p.categoria ?: "Frutas",
                        imagenUrl = p.img ?: ""
                    )
                }
            }
        }
    }

    fun onFieldChanged(nombre: String, desc: String, precio: String, stock: String, cat: String, img: String) {
        _editState.value = _editState.value.copy(
            nombre = nombre,
            descripcion = desc,
            precio = precio,
            stock = stock,
            categoria = cat,
            imagenUrl = img
        )
    }

    fun guardarProducto(onSuccess: () -> Unit) {
        val current = _editState.value

        // Validaciones básicas
        if (current.nombre.isBlank() || current.precio.isBlank()) {
            _editState.value = current.copy(error = "Nombre y Precio son obligatorios")
            return
        }

        viewModelScope.launch {
            _editState.value = current.copy(isSaving = true, error = null)

            // Si la URL está vacía, mandamos null
            val finalImgUrl = if (current.imagenUrl.isBlank()) null else current.imagenUrl

            val productoToSend = Producto(
                id = current.id, // Si es 0, el backend crea uno nuevo
                nombre = current.nombre,
                descripcion = current.descripcion,
                precio = current.precio.toDoubleOrNull() ?: 0.0,
                stock = current.stock.toIntOrNull() ?: 0,
                categoria = current.categoria,
                img = finalImgUrl
            )

            val result = repository.guardarProducto(productoToSend)

            _editState.value = current.copy(isSaving = false)

            if (result != null) {
                fetchProductos() // Recargar lista
                onSuccess()
            } else {
                _editState.value = current.copy(error = "Error al guardar en el servidor")
            }
        }
    }

    fun eliminarProducto(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.eliminarProducto(id)
            fetchProductos() // Recargar lista
            _isLoading.value = false
        }
    }
}