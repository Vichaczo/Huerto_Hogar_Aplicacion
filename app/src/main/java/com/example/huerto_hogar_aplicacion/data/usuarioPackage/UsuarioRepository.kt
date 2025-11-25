package com.example.huerto_hogar_aplicacion.data.usuarioPackage

import com.example.huerto_hogar_aplicacion.data.network.RetrofitClient

class UsuarioRepository {

    private val api = RetrofitClient.api

    // --- CREAR ---
    suspend fun crearUsuario(usuario: Usuario): Usuario {
        return api.crearUsuario(usuario)
    }

    // --- LEER (Individual) ---
    suspend fun obtenerUsuario(uid: String): Usuario? {
        return try {
            api.obtenerUsuario(uid)
        } catch (e: Exception) {
            null
        }
    }

    // --- LEER (Todos - Para el CRUD) ---
    suspend fun listarUsuarios(): List<Usuario> {
        return try {
            api.listarUsuarios()
        } catch (e: Exception) {
            emptyList() // Si falla, devuelve lista vacía para no romper la UI
        }
    }

    // --- ACTUALIZAR ---
    suspend fun actualizarUsuario(uid: String, usuario: Usuario): Usuario {
        // Asumiendo que tu backend devuelve el usuario actualizado
        return api.actualizarUsuario(uid, usuario)
    }

    // --- ELIMINAR ---
    suspend fun eliminarUsuario(uid: String) {
        api.eliminarUsuario(uid)
    }
}