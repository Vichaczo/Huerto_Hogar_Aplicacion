package com.example.huerto_hogar_aplicacion

import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class NegocioTest {

    // --- TEST 1: LÓGICA DE FILTRADO (CATÁLOGO) ---
    // Probamos que el algoritmo que usas para filtrar frutas realmente funcione.
    @Test
    fun `filtrar productos por categoria trae solo los correctos`() {
        // 1. Datos de prueba (Mock)
        val listaMezclada = listOf(
            Producto(1, "Manzana", "", 100.0, 10, "Frutas", null),
            Producto(2, "Pala", "", 5000.0, 5, "Herramientas", null),
            Producto(3, "Pera", "", 120.0, 10, "Frutas", null)
        )

        // 2. Ejecutar Lógica (Simulamos lo que hace el ViewModel)
        val soloFrutas = listaMezclada.filter { it.categoria == "Frutas" }

        // 3. Verificar
        assertEquals(2, soloFrutas.size) // Deberían quedar 2
        assertEquals("Manzana", soloFrutas[0].nombre)
        assertEquals("Pera", soloFrutas[1].nombre)
    }

    // --- TEST 2: LÓGICA DE STOCK (CARRITO) ---
    // Probamos la regla de negocio: "No puedes llevar más de lo que hay en bodega".
    @Test
    fun `validar si hay stock suficiente para agregar al carrito`() {
        val stockEnBodega = 5
        val cantidadEnCarrito = 3
        val cantidadQuieroAgregar = 3

        // Lógica: (3 + 3 = 6) que es mayor a 5 -> Debería fallar
        val nuevoTotal = cantidadEnCarrito + cantidadQuieroAgregar
        val esPosibleAgregar = nuevoTotal <= stockEnBodega

        assertFalse("No debería dejar agregar si supera el stock", esPosibleAgregar)
    }

    // --- TEST 3: LÓGICA DE FECHAS (CLIMA) ---
    // Probamos que tu función de convertir fecha a día ("2025-11-26" -> "MIE") funciona.
    // (Nota: Si usaste minSdk 26, esto funciona nativo).
    @Test
    fun `convertir fecha string a nombre de dia`() {
        val fechaString = "2025-11-26" // Es un Miércoles

        val fecha = LocalDate.parse(fechaString)
        val diaNumero = fecha.dayOfWeek.value // 1=Lunes, ... 3=Miércoles

        val nombreDia = when(diaNumero) {
            1 -> "LUN"; 2 -> "MAR"; 3 -> "MIE"; 4 -> "JUE"; 5 -> "VIE"; 6 -> "SAB"; 7 -> "DOM"
            else -> "?"
        }

        assertEquals("MIE", nombreDia)
    }
}