package com.example.huerto_hogar_aplicacion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Usuario::class], // Declara las entidades que forman parte de la BD (solo una: Producto).
    version = 1,                  // Versión de la base de datos (importante para migraciones futuras).
    exportSchema = false          // No exporta el esquema a un archivo JSON.
)
abstract class AppDatabase : RoomDatabase() {
    // Clase abstracta que hereda de RoomDatabase.
    // Room genera automáticamente la implementación real en tiempo de compilación.

    abstract fun usuarioDao(): UsuarioDao
    // Método abstracto que devuelve la interfaz DAO (acceso a la tabla productos).

    companion object {
        // Bloque companion que permite acceder a la base de datos como un singleton.

        @Volatile private var INSTANCE: com.example.huerto_hogar_aplicacion.data.AppDatabase? = null
        // Variable que guarda la instancia única de la base de datos.
        // @Volatile asegura visibilidad entre hilos (evita problemas de concurrencia).

        fun get(context: Context): com.example.huerto_hogar_aplicacion.data.AppDatabase =
            // Función pública para obtener la instancia de la base de datos.
            INSTANCE ?: synchronized(this) {
                // Si INSTANCE es null, entra en una sección sincronizada
                // para evitar que múltiples hilos creen instancias simultáneamente.

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,  // Usa el contexto de aplicación (no de actividad).
                    com.example.huerto_hogar_aplicacion.data.AppDatabase::class.java,     // Clase de la base de datos a crear.
                    "productos.db"               // Nombre físico del archivo de la BD.
                ).build().also { INSTANCE = it }
                // Crea la base de datos con Room y guarda la instancia en INSTANCE.
                // El método also se usa para asignar la instancia recién creada.
            }
    }
}

