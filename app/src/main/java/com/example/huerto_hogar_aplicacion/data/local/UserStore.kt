package com.example.huerto_hogar_aplicacion.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class UserStore(private val context: Context) {

    // Definimos las claves para guardar los datos
    companion object {
        val USER_UID = stringPreferencesKey("user_uid")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    //  Función para GUARDAR datos (Login)
    suspend fun saveUser(uid: String, name: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_UID] = uid
            preferences[USER_NAME] = name
            preferences[USER_ROLE] = role
        }
    }

    //  Función para LEER datos (Auto-Login)
    val userDetails: Flow<UserData> = context.dataStore.data
        .map { preferences ->
            UserData(
                uid = preferences[USER_UID] ?: "",
                name = preferences[USER_NAME] ?: "",
                role = preferences[USER_ROLE] ?: ""
            )
        }

    // 5. Función para BORRAR datos (Logout)
    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}

// Modelo simple para transportar los datos
data class UserData(val uid: String, val name: String, val role: String)