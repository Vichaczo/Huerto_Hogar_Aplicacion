package com.example.huerto_hogar_aplicacion.data.usuarioPackage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insert(usuario: Usuario): Long

    @Query("SELECT * FROM Usuario WHERE email = :email")
    fun getByEmail(email: String): Flow<Usuario?>

    @Query("SELECT * FROM Usuario WHERE id = :id")
    fun getById(id: Long): Flow<Usuario?>

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("SELECT * FROM Usuario ORDER BY id ASC")
    fun getAll(): Flow<List<Usuario>>

    @Query("SELECT * FROM Usuario WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): Usuario?
}

