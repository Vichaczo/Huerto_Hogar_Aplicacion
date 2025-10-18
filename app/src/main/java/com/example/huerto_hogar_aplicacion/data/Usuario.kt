package com.example.huerto_hogar_aplicacion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var nombre: String = "",
    var apellido: String = "",
    var email: String = "",
    var password: String = "",
    var telefono: String = ""
)
