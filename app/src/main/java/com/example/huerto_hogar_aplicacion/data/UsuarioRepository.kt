package com.example.huerto_hogar_aplicacion.data

import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val dao: UsuarioDao) {
    // Recibe una instancia del DAO para acceder a la base de datos.

    val usuarios: Flow<List<Usuario>> = dao.getAll()
    // Flujo de lista de productos.
    // Permite observar los cambios en la base de datos en tiempo real (reactividad con Flow).

    // ------------------------------------------------------------
    // FUNCIÓN PARA AGREGAR UN NUEVO PRODUCTO
    // ------------------------------------------------------------
     suspend fun agregar(usuario : Usuario) : Long {
        // Se marca como 'suspend' porque ejecuta operaciones de base de datos (debe ir en coroutine).
        // Valida que el ID sea válido (mayor que 0).
        require(usuario.nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        // Valida que el nombre no esté vacío.
        require(usuario.apellido.isNotBlank()) { "El apellido no puede estar vacío" }
        // Valida que el apellido no esté vacío.
        require(usuario.email.isNotBlank()) { "El email no puede estar vacío" }
        // Valida que el email no esté vacío.
        require(usuario.password.isNotBlank()) { "El password no puede estar vacío" }
        // Valida que el password no esté vacío.
        require(usuario.telefono.isNotBlank()) { "El telefono no puede estar vacío" }
        // Valida que el telefono no esté vacío.

        return dao.insert(usuario)
        // Inserta el nuevo producto en la base de datos con los valores limpios (sin espacios extras).
        }

    // ------------------------------------------------------------
    // FUNCIÓN PARA ACTUALIZAR UN PRODUCTO EXISTENTE
    // ------------------------------------------------------------
    suspend fun actualizar(id: Long,nombre: String, apellido: String, email: String, password: String, telefono: String) {
        // Recibe todos los datos actualizados y el ID del producto.

        require(id > 0) { "Id inválido" }
        // Valida que el ID sea válido (mayor que 0).
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        // Valida que el nombre no esté vacío.
        require(apellido.isNotBlank()) { "El apellido no puede estar vacío" }
        // Valida que el apellido no esté vacío.
        require(email.isNotBlank()) { "El email no puede estar vacío" }
        // Valida que el email no esté vacío.
        require(password.isNotBlank()) { "El password no puede estar vacío" }
        // Valida que el password no esté vacío.
        require(telefono.isNotBlank()) { "El telefono no puede estar vacío" }
        // Valida que el telefono no esté vacío.

        dao.update(
            Usuario(id = id, nombre = nombre.trim(),apellido = apellido.trim(), email = email.trim(), password = password.trim(), telefono = telefono.trim())
        )
        // Crea un nuevo objeto Producto con el mismo ID y datos actualizados,
        // y lo pasa al método update() del DAO.
    }

    // ------------------------------------------------------------
    // FUNCIÓN PARA ELIMINAR UN PRODUCTO
    // ------------------------------------------------------------
    suspend fun eliminar(usuario: Usuario) = dao.delete(usuario)
    // Llama directamente al método delete() del DAO para eliminar un registro.


    suspend fun obtener(id: Long) = dao.getById(id)

    suspend fun findUserByEmail(email: String): Usuario? {
        return dao.findByEmail(email)
    }
}