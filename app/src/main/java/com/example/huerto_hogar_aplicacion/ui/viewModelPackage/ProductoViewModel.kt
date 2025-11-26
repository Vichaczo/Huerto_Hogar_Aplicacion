package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import com.example.huerto_hogar_aplicacion.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {
    private val api = RetrofitClient.api

    private val _allProductos = MutableStateFlow<List<Producto>>(emptyList())
    private val _productosFiltrados = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productosFiltrados.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val categorias = listOf("Todo", "Frutas", "Verduras", "Snacks", "Especias", "Otros")

    private val categoriasEstandar = listOf("Frutas", "Verduras", "Snacks", "Especias")

    private val _categoriaSeleccionada = MutableStateFlow("Todo")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada.asStateFlow()

    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lista = api.listarProductos()
                _allProductos.value = lista
                filtrarPorCategoria(_categoriaSeleccionada.value)
            } catch (e: Exception) {
                _allProductos.value = emptyList()
                _productosFiltrados.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCategoriaSelected(categoria: String) {
        _categoriaSeleccionada.value = categoria
        filtrarPorCategoria(categoria)
    }

    private fun filtrarPorCategoria(categoria: String) {
        val todos = _allProductos.value

        _productosFiltrados.value = when (categoria) {
            "Todo" -> todos
            "Otros" -> {
                // Muestra productos cuya categoría NO esté en la lista estándar (ej: Herramientas)
                todos.filter { prod ->
                    val catProd = prod.categoria ?: ""
                    // Verificamos si la categoría del producto existe en la lista estándar (ignorando mayúsculas)
                    val esEstandar = categoriasEstandar.any { it.equals(catProd, ignoreCase = true) }
                    !esEstandar // Si NO es estándar, va a "Otros"
                }
            }
            else -> {
                // Filtro normal (Frutas, Verduras, etc.)
                todos.filter { it.categoria.equals(categoria, ignoreCase = true) }
            }
        }
    }

    fun cargarProductoPorId(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _productoSeleccionado.value = null
            try {
                val local = _allProductos.value.find { it.id == id }
                if (local != null) {
                    _productoSeleccionado.value = local
                } else {
                    val remoto = api.obtenerProducto(id)
                    _productoSeleccionado.value = remoto
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}