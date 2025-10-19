package com.example.huerto_hogar_aplicacion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.UsuarioDao
import com.example.huerto_hogar_aplicacion.data.usuarioPackage.Usuario



@Database(
    entities = [Usuario::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huerto_hogar.db" // Cambié el nombre para que sea más descriptivo
                )

                    .fallbackToDestructiveMigration()
                    /*
                    .allowMainThreadQueries()
                    */

                    .build()
                    .also { INSTANCE = it }
            }
    }
}
