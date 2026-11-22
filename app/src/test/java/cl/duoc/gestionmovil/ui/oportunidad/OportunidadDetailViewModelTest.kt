package cl.duoc.gestionmovil.ui.oportunidad

import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.EstadoOportunidad
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OportunidadDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: OportunidadRepository
    private lateinit var viewModel: OportunidadDetailViewModel

    private val oportunidadBase = Oportunidad(
        id = 10,
        nombre = "Expo Minería",
        tipo = TipoOportunidad.FERIA_EMPRESARIAL,
        descripcion = "Stand corporativo",
        fuenteInformacion = "Invitación directa",
        fechaDeteccion = 1700000000000L,
        responsable = "Carlos Ruiz",
        prioridad = Prioridad.ALTA,
        potencialComercial = PotencialComercial.ALTO,
        estado = EstadoOportunidad.DESCARTADA,
        fechaActualizacion = 1700000000000L
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = OportunidadDetailViewModel(repository)
    }

    @Test
    fun `cargarOportunidad obtiene registro por id y actualiza StateFlow`() {
        coEvery { repository.obtenerPorId(10) } returns oportunidadBase

        viewModel.cargarOportunidad(10)
        // Esperamos brevemente a Dispatchers.IO
        Thread.sleep(100)

        assertEquals(oportunidadBase, viewModel.oportunidad.value)
        coVerify(exactly = 1) { repository.obtenerPorId(10) }
    }

    @Test
    fun `reasignarOportunidad actualiza responsable y persiste en repositorio`() = runTest {
        coEvery { repository.obtenerPorId(10) } returns oportunidadBase
        viewModel.cargarOportunidad(10)
        Thread.sleep(100)

        viewModel.reasignarOportunidad("María Soto")
        advanceUntilIdle()

        assertEquals("María Soto", viewModel.oportunidad.value?.responsable)
        coVerify(exactly = 1) {
            repository.actualizar(match { it.id == 10 && it.responsable == "María Soto" })
        }
    }

    @Test
    fun `actualizarEstado cambia estado a CONFIRMADA y persiste en repositorio`() = runTest {
        coEvery { repository.obtenerPorId(10) } returns oportunidadBase
        viewModel.cargarOportunidad(10)
        Thread.sleep(100)

        viewModel.actualizarEstado(EstadoOportunidad.CONTACTO_REALIZADO)
        advanceUntilIdle()

        assertEquals(EstadoOportunidad.CONTACTO_REALIZADO, viewModel.oportunidad.value?.estado)
        coVerify(exactly = 1) {
            repository.actualizar(match { it.id == 10 && it.estado == EstadoOportunidad.CONTACTO_REALIZADO })
        }
    }

    @Test
    fun `reasignar y actualizarEstado no hacen nada si oportunidad es null`() = runTest {
        assertNull(viewModel.oportunidad.value)

        viewModel.reasignarOportunidad("Pedro Gómez")
        viewModel.actualizarEstado(EstadoOportunidad.DESCARTADA)
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.actualizar(any()) }
    }

    @Test
    fun `eliminarOportunidad borra registro y ejecuta callback onEliminado`() = runTest {
        coEvery { repository.obtenerPorId(10) } returns oportunidadBase
        viewModel.cargarOportunidad(10)
        Thread.sleep(100)

        var callbackEjecutado = false
        viewModel.eliminarOportunidad {
            callbackEjecutado = true
        }
        Thread.sleep(100)
        advanceUntilIdle()

        assertTrue(callbackEjecutado)
        coVerify(exactly = 1) { repository.eliminar(oportunidadBase) }
    }

    @Test
    fun `factory crea OportunidadDetailViewModel y falla con clase invalida`() {
        val factory = OportunidadDetailViewModelFactory(repository)
        assertNotNull(factory.create(OportunidadDetailViewModel::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `factory lanza excepcion con ViewModel desconocido`() {
        val factory = OportunidadDetailViewModelFactory(repository)
        factory.create(OportunidadFormViewModel::class.java)
    }
}