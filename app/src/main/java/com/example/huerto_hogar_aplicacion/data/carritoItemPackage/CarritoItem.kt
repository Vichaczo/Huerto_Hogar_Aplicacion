package com.example.huerto_hogar_aplicacion.data.carritoItemPackage

import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import com.google.gson.annotations.SerializedName

data class CarritoItem(
    val id: Long,
    val producto: Producto,
    var cantidad: Int,
    @SerializedName("usuarioUid")
    val usuarioUid: String? = null
)