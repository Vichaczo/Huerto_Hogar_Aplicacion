package com.example.huerto_hogar_aplicacion.data.repository

import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import com.example.huerto_hogar_aplicacion.data.network.RetrofitClient

class ProductoRepository {
    private val api = RetrofitClient.api

    suspend fun listarProductos(): List<Producto> {
        return try {
            api.listarProductos()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerProducto(id: Long): Producto? {
        return try {
            api.obtenerProducto(id)
        } catch (e: Exception) {
            null
        }
    }

    // NUEVO: Guardar (Sirve para Crear y Editar gracias a Spring Boot)
    suspend fun guardarProducto(producto: Producto): Producto? {
        return try {
            api.guardarProducto(producto)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // NUEVO: Eliminar
    suspend fun eliminarProducto(id: Long) {
        try {
            api.eliminarProducto(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}