package cl.duoc.gestionmovil.ui.organizacion

import cl.duoc.gestionmovil.data.OrganizacionRepository
import cl.duoc.gestionmovil.model.Organizacion
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrganizacionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: OrganizacionRepository

    private val existentes = listOf(
        Organizacion(id = 1, nombreEmpresa = "Minera Norte", rut = "76.123.456-7", direccion = "Av. Providencia 100")
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        every { repository.getAllOrganizaciones() } returns flowOf(existentes)
    }

    @Test
    fun `validarYGuardar con campos vacios o RUT invalido marca errores`() = runTest {
        val viewModel = OrganizacionViewModel(repository)
        advanceUntilIdle()

        viewModel.cambiarNombre("")
        viewModel.cambiarRut("123")
        viewModel.validarYGuardar()

        assertEquals("El nombre de la empresa es obligatorio", viewModel.uiState.value.errorNombre)
        assertEquals("Ingrese un RUT válido (8 a 9 caracteres)", viewModel.uiState.value.errorRut)
        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    @Test
    fun `validarYGuardar rechaza organizacion duplicada por nombre o RUT`() = runTest {
        val viewModel = OrganizacionViewModel(repository)
        advanceUntilIdle()

        viewModel.cambiarNombre("minera norte")
        viewModel.cambiarRut("99.888.777-6")
        viewModel.validarYGuardar()

        assertEquals("Ya existe una organización con este nombre o RUT", viewModel.uiState.value.errorNombre)
        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    @Test
    fun `validarYGuardar con datos validos persiste en repositorio y resetExito limpia campos`() = runTest {
        val viewModel = OrganizacionViewModel(repository)
        advanceUntilIdle()

        viewModel.cambiarNombre("Constructora Sur")
        viewModel.cambiarRut("77.654.321-0")
        viewModel.cambiarDireccion("Alameda 500")
        viewModel.validarYGuardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.guardadoExitoso)
        coVerify(exactly = 1) {
            repository.guardar(
                match { it.nombreEmpresa == "Constructora Sur" && it.rut == "77.654.321-0" }
            )
        }

        viewModel.resetExito()
        assertFalse(viewModel.uiState.value.guardadoExitoso)
        assertEquals("", viewModel.uiState.value.nombre)
    }

    @Test
    fun `factory crea OrganizacionViewModel y falla con clase invalida`() {
        val factory = OrganizacionViewModelFactory(repository)
        assertNotNull(factory.create(OrganizacionViewModel::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `factory lanza excepcion con ViewModel desconocido`() {
        val factory = OrganizacionViewModelFactory(repository)
        factory.create(OportunidadFormViewModel::class.java)
    }
}