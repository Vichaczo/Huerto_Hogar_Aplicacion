package com.example.huerto_hogar_aplicacion.data.repository

import com.example.huerto_hogar_aplicacion.data.carritoItemPackage.CarritoItem
import com.example.huerto_hogar_aplicacion.data.network.CarritoRequest
import com.example.huerto_hogar_aplicacion.data.network.RetrofitClient

class CarritoRepository {
    private val api = RetrofitClient.api

    suspend fun verCarrito(uid: String): List<CarritoItem> {
        return try {
            api.verCarrito(uid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun agregarProducto(uid: String, productoId: Long, cantidad: Int): CarritoItem? {
        return try {
            val request = CarritoRequest(uid, productoId, cantidad)
            api.agregarAlCarrito(request)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun eliminarItem(id: Long) {
        try {
            api.eliminarItemCarrito(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun vaciarCarrito(uid: String) {
        try {
            api.vaciarCarrito(uid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}