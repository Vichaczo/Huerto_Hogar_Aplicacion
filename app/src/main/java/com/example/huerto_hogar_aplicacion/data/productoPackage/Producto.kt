package com.example.huerto_hogar_aplicacion.data.productoPackage

import com.google.gson.annotations.SerializedName

data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int,
    val categoria: String?,
    val img: String? = null
)