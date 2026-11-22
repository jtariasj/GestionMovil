package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.TipoOportunidad
import cl.duoc.gestionmovil.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OportunidadListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: OportunidadRepository

    private val listaEjemplo = listOf(
        crearOportunidadPrueba(1, "Feria Tecnológica Duoc", "Carlos Ruiz"),
        crearOportunidadPrueba(2, "Activación Mall Plaza", "María Soto"),
        crearOportunidadPrueba(3, "Charla Empresa TI", "Carlos Ruiz")
    )

    @Before
    fun setUp() {
        repository = mockk()
    }

    private fun crearOportunidadPrueba(id: Int, nombre: String, responsable: String): Oportunidad {
        return Oportunidad(
            id = id,
            nombre = nombre,
            tipo = TipoOportunidad.FERIA_EMPRESARIAL,
            descripcion = "Descripción de prueba",
            fuenteInformacion = "Web",
            fechaDeteccion = 1700000000000L,
            responsable = responsable,
            prioridad = Prioridad.ALTA,
            potencialComercial = PotencialComercial.ALTO,
            fechaActualizacion = 1700000000000L
        )
    }

    @Test
    fun `rol SUPERVISOR obtiene todas las oportunidades del repositorio`() = runTest {
        every { repository.getAllOportunidades() } returns flowOf(listaEjemplo)

        val viewModel = OportunidadListViewModel(
            oportunidadRepository = repository,
            rolUsuario = "SUPERVISOR",
            nombreUsuario = "Supervisor Uno"
        )

        // Hace una simulacion para ejecutarlo como si estuviera en un sistema android
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.oportunidadesFiltradas.collect {}
        }
        advanceUntilIdle()

        assertEquals(3, viewModel.oportunidadesFiltradas.value.size)
        verify(exactly = 1) { repository.getAllOportunidades() }
        verify(exactly = 0) { repository.obtenerPorResponsable(any()) }
    }

    @Test
    fun `rol EJECUTIVO consulta solo las oportunidades de su responsable`() = runTest {
        val soloCarlos = listaEjemplo.filter { it.responsable == "Carlos Ruiz" }
        every { repository.obtenerPorResponsable("Carlos Ruiz") } returns flowOf(soloCarlos)

        val viewModel = OportunidadListViewModel(
            oportunidadRepository = repository,
            rolUsuario = "EJECUTIVO",
            nombreUsuario = "Carlos Ruiz"
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.oportunidadesFiltradas.collect {}
        }
        advanceUntilIdle()

        assertEquals(2, viewModel.oportunidadesFiltradas.value.size)
        verify(exactly = 1) { repository.obtenerPorResponsable("Carlos Ruiz") }
        verify(exactly = 0) { repository.getAllOportunidades() }
    }

    @Test
    fun `onSearchQueryChange filtra oportunidades por nombre ignorando mayusculas`() = runTest {
        every { repository.getAllOportunidades() } returns flowOf(listaEjemplo)

        val viewModel = OportunidadListViewModel(
            oportunidadRepository = repository,
            rolUsuario = "SUPERVISOR",
            nombreUsuario = "Supervisor Uno"
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.oportunidadesFiltradas.collect {}
        }
        advanceUntilIdle()

        // Filtramos por "mall"
        viewModel.onSearchQueryChange("mall")
        advanceUntilIdle()

        assertEquals("mall", viewModel.searchQuery.value)
        assertEquals(1, viewModel.oportunidadesFiltradas.value.size)
        assertEquals("Activación Mall Plaza", viewModel.oportunidadesFiltradas.value.first().nombre)

        // Limpiamos el buscador y vuelve la lista completa
        viewModel.onSearchQueryChange("")
        advanceUntilIdle()
        assertEquals(3, viewModel.oportunidadesFiltradas.value.size)
    }

    @Test
    fun `factory crea OportunidadListViewModel correctamente`() {
        every { repository.getAllOportunidades() } returns flowOf(emptyList())
        val factory = OportunidadListViewModelFactory(repository, "SUPERVISOR", "Admin")
        val creado = factory.create(OportunidadListViewModel::class.java)
        assertNotNull(creado)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `factory lanza excepcion si se solicita un ViewModel desconocido`() {
        val factory = OportunidadListViewModelFactory(repository, "SUPERVISOR", "Admin")
        factory.create(OportunidadFormViewModel::class.java)
    }
}