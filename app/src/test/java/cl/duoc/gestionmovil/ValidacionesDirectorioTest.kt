package cl.duoc.gestionmovil.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesDirectorioTest {

    @Test
    fun `validarCorreo acepta dominios gmail y test y rechaza otros`() {
        assertTrue(validarCorreo("usuario@gmail.com"))
        assertTrue(validarCorreo("  SUPERVISOR@TEST.COM  "))
        assertFalse(validarCorreo("contacto@empresa.cl"))
        assertFalse(validarCorreo("correo-sin-arroba"))
        assertFalse(validarCorreo(""))
    }

    @Test
    fun `validarTelefono acepta digitos y signo mas hasta 20 caracteres`() {
        assertTrue(validarTelefono("+56912345678"))
        assertTrue(validarTelefono("9 1234 5678"))
        assertFalse(validarTelefono(""))
        assertFalse(validarTelefono("123456789012345678901")) // 21 caracteres
        assertFalse(validarTelefono("telefono123"))
    }

    @Test
    fun `validarRut acepta largos de 8 a 9 caracteres sin puntos ni guion`() {
        assertTrue(validarRut("12.345.678-9"))
        assertTrue(validarRut("9.876.543-K"))
        assertFalse(validarRut("123.456-7")) // 7 caracteres
        assertFalse(validarRut("123.456.789-0")) // 10 caracteres
    }

    @Test
    fun `validarContieneLetras verifica que exista al menos una letra`() {
        assertTrue(validarContieneLetras("Sede 1"))
        assertFalse(validarContieneLetras("12345"))
        assertFalse(validarContieneLetras("   "))
    }
}