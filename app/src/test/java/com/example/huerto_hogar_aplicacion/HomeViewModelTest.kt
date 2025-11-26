package com.example.huerto_hogar_aplicacion

import com.example.huerto_hogar_aplicacion.ui.viewModelPackage.SessionState
import org.junit.Assert.*
import org.junit.Test

class HomeViewModelTest {

    // Rúbrica: Validar lógica del ViewModel (Estados de Sesión)

    @Test
    fun `usuario con rol admin debe ser identificado como admin`() {
        // 1. Simulamos un estado de sesión LoggedIn con rol 'admin'
        val estado = SessionState.LoggedIn(
            uid = "123",
            userName = "Profesor",
            rol = "admin"
        )

        // 2. Verificamos que la propiedad computada 'isAdmin' sea true
        assertTrue("El usuario debería ser admin", estado.isAdmin)
    }

    @Test
    fun `usuario con rol cliente NO debe ser admin`() {
        // 1. Simulamos un estado con rol 'usuario'
        val estado = SessionState.LoggedIn(
            uid = "456",
            userName = "Alumno",
            rol = "usuario"
        )

        // 2. Verificamos que 'isAdmin' sea false
        assertFalse("El usuario NO debería ser admin", estado.isAdmin)
    }

    @Test
    fun `validacion de rol debe ignorar mayusculas`() {
        // 1. Probamos con 'ADMIN' en mayúsculas
        val estado = SessionState.LoggedIn(
            uid = "789",
            userName = "Jefe",
            rol = "ADMIN"
        )

        // 2. Debería seguir siendo true
        assertTrue("Debe detectar admin aunque esté en mayúsculas", estado.isAdmin)
    }
}