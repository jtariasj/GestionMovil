package cl.duoc.gestionmovil.ui.login

import cl.duoc.gestionmovil.data.UsuarioRepository
import cl.duoc.gestionmovil.model.RolUsuario
import cl.duoc.gestionmovil.model.Usuario
import cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormViewModel
import cl.duoc.gestionmovil.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: UsuarioRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
    }

    @Test
    fun `al inicializar invoca asegurarUsuariosIniciales en repositorio`() {
        LoginViewModel(repository)
        Thread.sleep(100)

        coVerify(exactly = 1) { repository.asegurarUsuariosIniciales() }
    }

    @Test
    fun `iniciarSesion con credenciales validas actualiza usuarioLogueado`() {
        val usuarioMock = Usuario(
            id = 1,
            nombre = "Supervisor Uno",
            correo = "supervisor@test.com",
            contrasena = "1234",
            rol = RolUsuario.SUPERVISOR
        )
        coEvery { repository.login("supervisor@test.com", "1234") } returns usuarioMock

        val viewModel = LoginViewModel(repository)
        viewModel.iniciarSesion("supervisor@test.com", "1234")
        Thread.sleep(100)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(usuarioMock, state.usuarioLogueado)
        assertNull(state.error)
    }

    @Test
    fun `iniciarSesion con credenciales invalidas muestra mensaje de error`() {
        coEvery { repository.login("malo@test.com", "0000") } returns null

        val viewModel = LoginViewModel(repository)
        viewModel.iniciarSesion("malo@test.com", "0000")
        Thread.sleep(100)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.usuarioLogueado)
        assertNotNull(state.error)
    }

    @Test
    fun `iniciarSesion captura excepcion de repositorio y muestra error`() {
        coEvery { repository.login(any(), any()) } throws RuntimeException("Fallo red")

        val viewModel = LoginViewModel(repository)
        viewModel.iniciarSesion("supervisor@test.com", "1234")
        Thread.sleep(100)

        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `cerrarSesion reinicia el estado de UI`() {
        val usuarioMock = Usuario(1, "Admin", "supervisor@test.com", "1234", RolUsuario.SUPERVISOR)
        coEvery { repository.login("supervisor@test.com", "1234") } returns usuarioMock

        val viewModel = LoginViewModel(repository)
        viewModel.iniciarSesion("supervisor@test.com", "1234")
        Thread.sleep(100)

        viewModel.cerrarSesion()

        assertNull(viewModel.uiState.value.usuarioLogueado)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `factory crea LoginViewModel y lanza excepcion con clase invalida`() {
        val factory = LoginViewModelFactory(repository)
        assertNotNull(factory.create(LoginViewModel::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `factory falla con ViewModel desconocido`() {
        val factory = LoginViewModelFactory(repository)
        factory.create(OportunidadFormViewModel::class.java)
    }
}