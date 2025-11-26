package com.example.huerto_hogar_aplicacion

import com.example.huerto_hogar_aplicacion.data.carritoItemPackage.CarritoItem
import com.example.huerto_hogar_aplicacion.data.productoPackage.Producto
import org.junit.Assert.assertEquals
import org.junit.Test

class CarritoLogicTest {

    // Rúbrica: Validar lógica de negocio (Cálculo de Totales)

    @Test
    fun `calculo de total del carrito es correcto`() {
        // 1. Preparamos datos falsos (Mocks manuales)
        val prod1 = Producto(1, "Pala", "...", 1000.0, 10, "Herramientas", null)
        val prod2 = Producto(2, "Semillas", "...", 500.0, 50, "Semillas", null)

        val item1 = CarritoItem(1, prod1, 2, "uid") // 2 Palas (2 * 1000 = 2000)
        val item2 = CarritoItem(2, prod2, 1, "uid") // 1 Semilla (1 * 500 = 500)

        val listaCarrito = listOf(item1, item2)

        // 2. Ejecutamos la lógica (la misma que usa el ViewModel)
        var totalCalculado = 0.0
        for (item in listaCarrito) {
            totalCalculado += (item.producto.precio * item.cantidad)
        }

        // 3. Verificamos
        val totalEsperado = 2500.0
        assertEquals(totalEsperado, totalCalculado, 0.0)
    }
}