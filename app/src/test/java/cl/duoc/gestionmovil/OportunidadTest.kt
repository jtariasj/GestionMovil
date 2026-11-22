package cl.duoc.gestionmovil

import cl.duoc.gestionmovil.model.TipoOportunidad
import cl.duoc.gestionmovil.util.*;
import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class OportunidadTest {

    @Test
    fun `validarNombreOportunidad - acepta nombres validos y rechaza nombres de menos de 3 caracteres`() {
        assertTrue(validarNombreOportunidad("Feria Tecnológica"))
        assertTrue(validarNombreOportunidad("  ABC  "))
        assertFalse(validarNombreOportunidad("AB"))
        assertFalse(validarNombreOportunidad("   "))
        // Los assertTrue reciben un booleano
        // En este caso reciben las validaciones que tenemos en
        // app/src/main/java/cl/duoc/gestionmovil/util/ValidacionesOportunidad.kt
        // La condicion que entrega validarnombreoportunidad es return nombre.trim().length >= 3
    }

    @Test
    fun `validarDescripcion - acepta cadenas no vacias ni compuestas solo de espacios`() {
        assertTrue(validarDescripcion("Activación en sede central"))
        assertFalse(validarDescripcion(""))
        assertFalse(validarDescripcion("   "))
    }

    @Test
    fun `validarSeleccion - retorna true solo para elementos no nulos`() {
        //Un enum existente y una tipooportunidad nula
        assertTrue(validarSeleccion(TipoOportunidad.FERIA_EMPRESARIAL))
        assertFalse(validarSeleccion(null))
    }

    @Test
    fun `validarPublicoEsperado - valida numeros enteros positivos mayores a cero`() {
        assertTrue(validarPublicoEsperado("150"))
        assertFalse(validarPublicoEsperado("0"))
        assertFalse(validarPublicoEsperado("-10"))
        assertFalse(validarPublicoEsperado("abc"))
        assertFalse(validarPublicoEsperado(""))
    }

    @Test
    fun `validarCostoNoNegativo - permite cero o montos positivos y rechaza valores negativos o texto`() {
        assertTrue(validarCostoNoNegativo("0"))
        assertTrue(validarCostoNoNegativo("250000"))
        assertFalse(validarCostoNoNegativo("-1"))
        assertFalse(validarCostoNoNegativo("Hola profe"))
        assertFalse(validarCostoNoNegativo(""))
    }
}