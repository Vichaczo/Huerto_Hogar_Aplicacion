package com.example.huerto_hogar_aplicacion.data.network

import com.example.huerto_hogar_aplicacion.data.usuarioPackage.Usuario
// Asegúrate de tener estas Data Classes creadas en tu proyecto:
import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import com.example.huerto_hogar_aplicacion.data.carritoItemPackage.CarritoItem
import com.example.huerto_hogar_aplicacion.data.ordenPackage.Orden
import retrofit2.Response
import retrofit2.http.*

interface HuertoApi {

    // ==========================================
    // USUARIOS (UsuarioController)
    // ==========================================

    @POST("usuarios")
    suspend fun crearUsuario(@Body usuario: Usuario): Usuario

    @GET("usuarios/{uid}")
    suspend fun obtenerUsuario(@Path("uid") uid: String): Usuario

    @GET("usuarios")
    suspend fun listarUsuarios(): List<Usuario>

    @PUT("usuarios/{uid}")
    suspend fun actualizarUsuario(@Path("uid") uid: String, @Body usuario: Usuario): Usuario

    @DELETE("usuarios/{uid}")
    suspend fun eliminarUsuario(@Path("uid") uid: String): Response<Void>


    // ==========================================
    // PRODUCTOS (ProductoController)
    // ==========================================

    @GET("productos")
    suspend fun listarProductos(): List<Producto>

    @GET("productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): Producto

    @GET("productos/categoria/{categoria}")
    suspend fun listarPorCategoria(@Path("categoria") categoria: String): List<Producto>

    @GET("productos/buscar")
    suspend fun buscarProducto(@Query("nombre") nombre: String): List<Producto>

    @POST("productos")
    suspend fun guardarProducto(@Body producto: Producto): Producto

    @PATCH("productos/{id}/stock")
    suspend fun actualizarStock(@Path("id") id: Long, @Query("cantidad") cantidad: Int): Response<Void>

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Void>


    // ==========================================
    // CARRITO (CarritoController)
    // ==========================================

    @GET("carrito/{uid}")
    suspend fun verCarrito(@Path("uid") uid: String): List<CarritoItem>

    // En tu Java usas una clase estática interna "SolicitudAgregar".
    // En Kotlin usamos "CarritoRequest" (definida abajo).
    @POST("carrito/agregar")
    suspend fun agregarAlCarrito(@Body solicitud: CarritoRequest): CarritoItem

    @DELETE("carrito/item/{id}")
    suspend fun eliminarItemCarrito(@Path("id") id: Long): Response<Void>

    @DELETE("carrito/{uid}")
    suspend fun vaciarCarrito(@Path("uid") uid: String): Response<Void>


    // ==========================================
    // ORDENES (OrdenController)
    // ==========================================

    @POST("ordenes/comprar/{uid}")
    suspend fun comprar(@Path("uid") uid: String): Orden

    @GET("ordenes/{uid}")
    suspend fun historialCompras(@Path("uid") uid: String): List<Orden>
}

// --- DTOs (Objetos de Transferencia de Datos) ---

// Necesario para enviar el JSON exacto: { "uid": "...", "productoId": 1, "cantidad": 5 }
data class CarritoRequest(
    val uid: String,
    val productoId: Long,
    val cantidad: Int
)