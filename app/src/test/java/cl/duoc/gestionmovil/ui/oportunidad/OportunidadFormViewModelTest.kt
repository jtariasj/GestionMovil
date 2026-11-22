package cl.duoc.gestionmovil.ui.oportunidad

import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.TipoOportunidad
import cl.duoc.gestionmovil.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OportunidadFormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: OportunidadRepository
    private lateinit var viewModel: OportunidadFormViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = OportunidadFormViewModel(repository)
    }

    private fun llenarFormularioValido() {
        viewModel.cambiarNombre("Feria Innovación Duoc")
        viewModel.cambiarTipo(TipoOportunidad.FERIA_EMPRESARIAL)
        viewModel.cambiarDescripcion("Activación en hall central")
        viewModel.cambiarFuente("Portal Web")
        viewModel.cambiarFechaDeteccion("10/11/2026")
        viewModel.cambiarResponsable("Carlos Ruiz")
        viewModel.cambiarPrioridad(Prioridad.ALTA)
        viewModel.cambiarPotencial(PotencialComercial.ALTO)
        viewModel.cambiarCosto("150000")
        viewModel.cambiarFechaTermino("20/11/2026")
        viewModel.cambiarObservaciones("Con stand propio")
        viewModel.cambiarFotoPath("/storage/foto.jpg")
    }

    @Test
    fun `guardar con campos vacios activa errores y no llama al repositorio`() {
        viewModel.guardar()

        val state = viewModel.uiState.value
        assertTrue(state.errorNombre)
        assertTrue(state.errorTipo)
        assertTrue(state.errorDescripcion)
        assertTrue(state.errorFuente)
        assertTrue(state.errorResponsable)
        assertTrue(state.errorPrioridad)
        assertTrue(state.errorPotencial)
        assertEquals("El costo referencial es obligatorio.", state.errorCosto)
        assertEquals("La fecha de término es obligatoria.", state.errorFechaTermino)
        assertFalse(state.guardadoExitoso)

        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    @Test
    fun `guardar con costo no numerico o negativo asigna mensaje de error especifico`() {
        viewModel.cambiarCosto("abc")
        viewModel.guardar()
        assertEquals("Debe ser un valor numérico válido.", viewModel.uiState.value.errorCosto)

        viewModel.cambiarCosto("-500")
        viewModel.guardar()
        assertEquals("El costo no puede ser un valor negativo.", viewModel.uiState.value.errorCosto)
    }

    @Test
    fun `guardar con fecha de termino anterior al inicio marca error de coherencia`() {
        viewModel.cambiarFechaDeteccion("15/11/2026")
        viewModel.cambiarFechaTermino("10/11/2026")
        viewModel.guardar()

        assertEquals(
            "La fecha de término no puede ser anterior al inicio.",
            viewModel.uiState.value.errorFechaTermino
        )
    }

    @Test
    fun `guardar cuando oportunidad ya existe marca errorDuplicado y no guarda`() = runTest {
        llenarFormularioValido()
        val duplicada = mockk<Oportunidad>(relaxed = true)
        coEvery { repository.obtenerPorNombre("Feria Innovación Duoc") } returns listOf(duplicada)

        viewModel.guardar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("La oportunidad ya está creada.", state.errorDuplicado)
        assertTrue(state.errorNombre)
        assertFalse(state.guardadoExitoso)
        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    @Test
    fun `guardar con datos validos y sin duplicados guarda en repositorio y resetea estado`() = runTest {
        llenarFormularioValido()
        coEvery { repository.obtenerPorNombre("Feria Innovación Duoc") } returns emptyList()

        viewModel.guardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.guardadoExitoso)
        assertEquals("", viewModel.uiState.value.nombre)
        coVerify(exactly = 1) { repository.guardar(any()) }

        // Verifica mensajeMostrado()
        viewModel.mensajeMostrado()
        assertFalse(viewModel.uiState.value.guardadoExitoso)
        assertNull(viewModel.uiState.value.errorDuplicado)
    }

    @Test
    fun `factory crea instancia de OportunidadFormViewModel y lanza excepcion con otra clase`() {
        val factory = OportunidadFormViewModelFactory(repository)
        val creado = factory.create(OportunidadFormViewModel::class.java)
        assertNotNull(creado)
    }
}