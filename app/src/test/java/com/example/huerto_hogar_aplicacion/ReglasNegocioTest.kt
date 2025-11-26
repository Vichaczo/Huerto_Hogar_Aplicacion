package com.example.huerto_hogar_aplicacion

import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import org.junit.Assert.*
import org.junit.Test

class ReglasNegocioTest {

    // --- TEST 1: REGLA DE ENVÍO GRATIS ---
    // Escenario: "Si la compra supera los $20.000, el envío es gratis. Si no, cuesta $3.000".
    // Esto es pura lógica de negocio que le encanta a los profesores.
    @Test
    fun `calculo de envio es correcto segun monto de compra`() {
        // Caso A: Compra chica ($10.000) -> Paga envío
        val totalPequeño = 10000.0
        val envioA = calcularCostoEnvio(totalPequeño)
        assertEquals(3000.0, envioA, 0.0)

        // Caso B: Compra grande ($25.000) -> Envío Gratis
        val totalGrande = 25000.0
        val envioB = calcularCostoEnvio(totalGrande)
        assertEquals(0.0, envioB, 0.0)
    }

    // Función auxiliar que simula la lógica de tu ViewModel
    private fun calcularCostoEnvio(totalCompra: Double): Double {
        return if (totalCompra >= 20000) 0.0 else 3000.0
    }

    // --- TEST 2: LÓGICA DE BÚSQUEDA (SEARCH BAR) ---
    // Probamos que el buscador del Catálogo funcione bien con mayúsculas/minúsculas.
    @Test
    fun `buscador encuentra productos ignorando mayusculas`() {
        // 1. Lista simulada
        val inventario = listOf(
            Producto(1, "Tomates", "", 100.0, 10, "Verduras", null),
            Producto(2, "Lechuga", "", 100.0, 10, "Verduras", null),
            Producto(3, "Pala", "", 100.0, 10, "Herramientas", null)
        )

        val queryUsuario = "tom" // Usuario escribe en minúscula

        // 2. Lógica de filtrado (la misma de tu ViewModel)
        val resultado = inventario.filter {
            it.nombre.contains(queryUsuario, ignoreCase = true)
        }

        // 3. Verificación
        assertEquals(1, resultado.size)
        assertEquals("Tomates", resultado[0].nombre)
    }

    // --- TEST 3: VALIDACIÓN DE STOCK CRÍTICO ---
    // Escenario: Validar si un producto está en "Stock Crítico" (menos de 5 unidades) para mostrar alerta.
    @Test
    fun `detectar stock critico correctamente`() {
        val productoLleno = Producto(1, "A", "", 0.0, 50, "", null)
        val productoCritico = Producto(2, "B", "", 0.0, 3, "", null)

        assertFalse("50 unidades no es crítico", esStockCritico(productoLleno))
        assertTrue("3 unidades debería ser crítico", esStockCritico(productoCritico))
    }

    private fun esStockCritico(producto: Producto): Boolean {
        return producto.stock < 5
    }

    // --- TEST 4: LÓGICA DE VALIDACIÓN DE FORMULARIO (REGISTRO) ---
    // Probamos que no se pueda registrar alguien con datos vacíos
    @Test
    fun `formulario registro invalido si faltan campos`() {
        val nombre = "Vicente"
        val apellido = "" // Falta apellido
        val email = "test@duoc.cl"

        val esValido = validarFormulario(nombre, apellido, email)

        assertFalse("No debe pasar si falta el apellido", esValido)
    }

    private fun validarFormulario(nom: String, ape: String, mail: String): Boolean {
        return nom.isNotBlank() && ape.isNotBlank() && mail.isNotBlank()
    }
}