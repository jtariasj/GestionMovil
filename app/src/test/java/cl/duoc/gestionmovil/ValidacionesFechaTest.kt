package cl.duoc.gestionmovil.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ValidacionesFechaTest {

    @Test
    fun `parsearFecha convierte fechas validas y rechaza formatos incorrectos`() {
        assertNotNull(parsearFecha("15/05/2026"))
        assertNull(parsearFecha("32/01/2026")) // día inexistente
        assertNull(parsearFecha("2026-05-15")) // formato distinto a dd/MM/yyyy
        assertNull(parsearFecha(""))
    }

    @Test
    fun `formatearFecha retorna string en formato dd MM yyyy`() {
        val millis = parsearFecha("20/11/2026")!!
        assertEquals("20/11/2026", formatearFecha(millis))
    }

    @Test
    fun `validarFechaDeteccion acepta fechas pasadas o actuales y rechaza futuras`() {
        val hoy = formatearFecha(System.currentTimeMillis())
        assertTrue(validarFechaDeteccion(hoy))
        assertTrue(validarFechaDeteccion("01/01/2020"))

        val manana = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis
        assertFalse(validarFechaDeteccion(formatearFecha(manana)))
        assertFalse(validarFechaDeteccion("fecha-invalida"))
    }
}