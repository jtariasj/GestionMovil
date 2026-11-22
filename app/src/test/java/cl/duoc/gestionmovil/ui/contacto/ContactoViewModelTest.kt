package cl.duoc.gestionmovil.ui.contacto

import cl.duoc.gestionmovil.data.ContactoRepository
import cl.duoc.gestionmovil.model.Contacto
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
class ContactoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ContactoRepository

    private val existentes = listOf(
        Contacto(
            id = 1,
            nombre = "Ana Torres",
            cargo = "Jefa de Marketing",
            telefono = "+56911112222",
            correo = "ana@gmail.com",
            organizacionId = 3
        )
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        every { repository.getContactosPorOrganizacion(3) } returns flowOf(existentes)
    }

    @Test
    fun `validarYGuardar con datos invalidos activa errores por campo`() = runTest {
        val viewModel = ContactoViewModel(repository, organizacionId = 3)
        advanceUntilIdle()

        viewModel.cambiarNombre("")
        viewModel.cambiarCargo("")
        viewModel.cambiarTelefono("abc")
        viewModel.cambiarCorreo("usuario@otrodominio.cl")
        viewModel.validarYGuardar()

        val state = viewModel.uiState.value
        assertEquals("El nombre es obligatorio", state.errorNombre)
        assertEquals("El cargo es obligatorio", state.errorCargo)
        assertEquals("Ingrese un teléfono válido (máx 20 caracteres)", state.errorTelefono)
        assertEquals("El correo debe terminar en @gmail.com o @test.com", state.errorCorreo)
        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    @Test
    fun `validarYGuardar rechaza contacto con correo duplicado`() = runTest {
        val viewModel = ContactoViewModel(repository, organizacionId = 3)
        advanceUntilIdle()

        viewModel.cambiarNombre("Otro Nombre")
        viewModel.cambiarCargo("Analista")
        viewModel.cambiarTelefono("+56933334444")
        viewModel.cambiarCorreo("ANA@GMAIL.COM")
        viewModel.validarYGuardar()

        assertEquals(
            "Ya existe un contacto con este correo o nombre y cargo",
            viewModel.uiState.value.errorCorreo
        )
        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    @Test
    fun `validarYGuardar con datos validos persiste contacto y resetExito limpia bandera`() = runTest {
        val viewModel = ContactoViewModel(repository, organizacionId = 3)
        advanceUntilIdle()

        viewModel.cambiarNombre("Luis Vega")
        viewModel.cambiarCargo("Gerente Comercial")
        viewModel.cambiarTelefono("+56955556666")
        viewModel.cambiarCorreo("luis.vega@test.com")
        viewModel.validarYGuardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.guardadoExitoso)
        coVerify(exactly = 1) {
            repository.guardar(
                match {
                    it.nombre == "Luis Vega" &&
                        it.organizacionId == 3 &&
                        it.correo == "luis.vega@test.com"
                }
            )
        }

        viewModel.resetExito()
        assertFalse(viewModel.uiState.value.guardadoExitoso)
    }

    @Test
    fun `factory crea ContactoViewModel y falla con clase invalida`() {
        val factory = ContactoViewModelFactory(repository, organizacionId = 3)
        assertNotNull(factory.create(ContactoViewModel::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `factory lanza excepcion con ViewModel desconocido`() {
        val factory = ContactoViewModelFactory(repository, organizacionId = 3)
        factory.create(OportunidadFormViewModel::class.java)
    }
}