package com.example.huerto_hogar_aplicacion.data.usuarioPackage

import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val dao: UsuarioDao) {
    // Recibe una instancia del DAO para acceder a la base de datos.
    val usuarios: Flow<List<Usuario>> = dao.getAll()

    // FUNCIÓN PARA AGREGAR UN USUARIO
     suspend fun agregar(usuario : Usuario) : Long {
        // Se marca como 'suspend' porque ejecuta operaciones de base de datos (debe ir en coroutine).
        require(usuario.nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(usuario.apellido.isNotBlank()) { "El apellido no puede estar vacío" }
        require(usuario.email.isNotBlank()) { "El email no puede estar vacío" }
        require(usuario.password.isNotBlank()) { "El password no puede estar vacío" }
        require(usuario.telefono.isNotBlank()) { "El telefono no puede estar vacío" }

        return dao.insert(usuario)
        // Inserta el nuevo usuario en la base de datos con los valores limpios (sin espacios extras).
        }

    // FUNCIÓN PARA ACTUALIZAR UN USUARIO EXISTENTE
    suspend fun actualizar(id: Long,nombre: String, apellido: String, email: String, password: String, telefono: String) {
        // Recibe todos los datos actualizados y el ID del producto.
        //Validaciones de datos

        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(apellido.isNotBlank()) { "El apellido no puede estar vacío" }
        require(email.isNotBlank()) { "El email no puede estar vacío" }
        require(password.isNotBlank()) { "El password no puede estar vacío" }
        require(telefono.isNotBlank()) { "El telefono no puede estar vacío" }

        dao.update(
            Usuario(id = id, nombre = nombre.trim(),apellido = apellido.trim(), email = email.trim(), password = password.trim(), telefono = telefono.trim())
        )
    }

    // FUNCIÓN PARA ELIMINAR UN PRODUCTO
    suspend fun eliminar(usuario: Usuario) = dao.delete(usuario)

    // FUNCIÓN PARA OBTENER UN Usuario por su ID
    suspend fun obtener(id: Long) = dao.getById(id)

    // FUNCION PARA OBTENER USUARIO POR SU EMAIL
    suspend fun findUserByEmail(email: String): Usuario? {
        return dao.findByEmail(email)
    }
}