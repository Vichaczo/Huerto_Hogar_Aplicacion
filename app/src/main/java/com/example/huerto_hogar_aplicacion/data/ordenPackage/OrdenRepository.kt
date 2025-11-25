package com.example.huerto_hogar_aplicacion.data.repository

import com.example.huerto_hogar_aplicacion.data.ordenPackage.Orden
import com.example.huerto_hogar_aplicacion.data.network.RetrofitClient

class OrdenRepository {
    private val api = RetrofitClient.api

    // Generar la compra (Checkout)
    suspend fun comprar(uid: String): Orden? {
        return try {
            api.comprar(uid)
        } catch (e: Exception) {
            null
        }
    }

    // Ver mis compras pasadas
    suspend fun historialCompras(uid: String): List<Orden> {
        return try {
            api.historialCompras(uid)
        } catch (e: Exception) {
            emptyList()
        }
    }
}