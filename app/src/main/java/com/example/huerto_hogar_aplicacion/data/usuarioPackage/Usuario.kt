package com.example.huerto_hogar_aplicacion.data.usuarioPackage

data class Usuario(

    val uid: String,
    val nombre: String?,
    val apellido: String?,
    val email: String,
    val telefono: String?,
    val direccion: String?,
    val rol: String? = "usuario"
)
