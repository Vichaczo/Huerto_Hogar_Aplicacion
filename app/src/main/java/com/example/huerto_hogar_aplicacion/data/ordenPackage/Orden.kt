package com.example.huerto_hogar_aplicacion.data.ordenPackage

import java.util.Date

data class Orden(
    val id: Long,
    val usuarioUid: String,
    val fecha: Date,
    val total: Double

)