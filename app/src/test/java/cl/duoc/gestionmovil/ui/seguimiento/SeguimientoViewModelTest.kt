package cl.duoc.gestionmovil.ui.seguimiento

import cl.duoc.gestionmovil.data.SeguimientoRepository
import cl.duoc.gestionmovil.model.Seguimiento
import cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormViewModel
import cl.duoc.gestionmovil.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeguimientoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: SeguimientoRepository

    private val historialInicial = listOf(
        Seguimiento(
            id = 1,
            fecha = 1700000000000L,
            descripcion = "Llamada inicial de contacto",
            tipoGestion = "Llamada",
            oportunidadId = 5
        )
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
    }

    @Test
    fun `al inicializar carga el historial de la oportunidad desde el repositorio`() = runTest {
        every { repository.getSeguimientosPorOportunidad(5) } returns flowOf(historialInicial)

        val viewModel = SeguimientoViewModel(repository, oportunidadId = 5)
        advanceUntilIdle()

        assertEquals(1, viewModel.historial.value.size)
        assertEquals("Llamada inicial de contacto", viewModel.historial.value.first().descripcion)
    }

    @Test
    fun `agregarSeguimiento con datos nuevos guarda en repositorio`() = runTest {
        every { repository.getSeguimientosPorOportunidad(5) } returns flowOf(historialInicial)
        val viewModel = SeguimientoViewModel(repository, oportunidadId = 5)
        advanceUntilIdle()

        var duplicadoDetectado = false
        viewModel.agregarSeguimiento(
            descripcion = "  Envío de cotización formal  ",
            tipoGestion = "Correo",
            onDuplicado = { duplicadoDetectado = true }
        )
        advanceUntilIdle()

        assertEquals(false, duplicadoDetectado)
        coVerify(exactly = 1) {
            repository.guardar(
                match {
                    it.oportunidadId == 5 &&
                        it.descripcion == "Envío de cotización formal" &&
                        it.tipoGestion == "Correo"
                }
            )
        }
    }

    @Test
    fun `agregarSeguimiento duplicado ejecuta onDuplicado y no vuelve a guardar`() = runTest {
        every { repository.getSeguimientosPorOportunidad(5) } returns flowOf(historialInicial)
        val viewModel = SeguimientoViewModel(repository, oportunidadId = 5)
        advanceUntilIdle()

        var duplicadoDetectado = false
        viewModel.agregarSeguimiento(
            descripcion = "  LLAMADA INICIAL DE CONTACTO ",
            tipoGestion = "llamada",
            onDuplicado = { duplicadoDetectado = true }
        )
        advanceUntilIdle()

        assertTrue(duplicadoDetectado)
        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    @Test
    fun `factory crea SeguimientoViewModel correctamente`() {
        every { repository.getSeguimientosPorOportunidad(5) } returns flowOf(emptyList())
        val factory = SeguimientoViewModelFactory(repository, oportunidadId = 5)
        assertNotNull(factory.create(SeguimientoViewModel::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `factory lanza excepcion con ViewModel desconocido`() {
        val factory = SeguimientoViewModelFactory(repository, oportunidadId = 5)
        factory.create(OportunidadFormViewModel::class.java)
    }
}