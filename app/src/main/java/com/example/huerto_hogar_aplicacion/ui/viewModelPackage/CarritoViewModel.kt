package com.example.huerto_hogar_aplicacion.ui.viewModelPackage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar_aplicacion.data.carritoItemPackage.CarritoItem
import com.example.huerto_hogar_aplicacion.data.ordenPackage.Orden
import com.example.huerto_hogar_aplicacion.data.network.CarritoRequest
import com.example.huerto_hogar_aplicacion.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    // Opción A: Instancia directa
    private val api = RetrofitClient.api

    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    val items: StateFlow<List<CarritoItem>> = _items.asStateFlow()

    private val _historial = MutableStateFlow<List<Orden>>(emptyList())
    val historial: StateFlow<List<Orden>> = _historial.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _compraExitosa = MutableStateFlow(false)
    val compraExitosa: StateFlow<Boolean> = _compraExitosa.asStateFlow()

    fun cargarCarrito(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lista = api.verCarrito(uid)
                _items.value = lista
                calcularTotal(lista)
            } catch (e: Exception) {
                _items.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarProducto(uid: String, productoId: Long, cantidad: Int) {
        viewModelScope.launch {
            try {
                val request = CarritoRequest(uid, productoId, cantidad)
                api.agregarAlCarrito(request)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun modificarCantidad(uid: String, item: CarritoItem, delta: Int) {
        viewModelScope.launch {
            try {
                val request = CarritoRequest(uid, item.producto.id, delta)
                api.agregarAlCarrito(request)
                cargarCarrito(uid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminarItem(uid: String, itemId: Long) {
        viewModelScope.launch {
            try {
                api.eliminarItemCarrito(itemId)
                cargarCarrito(uid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun confirmarCompra(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                api.comprar(uid)
                _items.value = emptyList()
                _total.value = 0.0
                _compraExitosa.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetCompraState() {
        _compraExitosa.value = false
    }

    fun cargarHistorial(uid: String) {
        viewModelScope.launch {
            try {
                val ordenes = api.historialCompras(uid)
                _historial.value = ordenes
            } catch (e: Exception) {
                _historial.value = emptyList()
            }
        }
    }

    private fun calcularTotal(lista: List<CarritoItem>) {
        var sum = 0.0
        for (item in lista) {
            sum += (item.producto.precio * item.cantidad)
        }
        _total.value = sum
    }
}