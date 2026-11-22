package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.RolUsuario
import cl.duoc.gestionmovil.model.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UsuarioRepositoryTest {

    private lateinit var dao: UsuarioDao
    private lateinit var repository: UsuarioRepository

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = UsuarioRepository(dao)
    }

    @Test
    fun `contarUsuarios delega en usuarioDao`() = runTest {
        coEvery { dao.contarUsuarios() } returns 4

        val total = repository.contarUsuarios()

        assertEquals(4, total)
        coVerify(exactly = 1) { dao.contarUsuarios() }
    }

    @Test
    fun `asegurarUsuariosIniciales inserta 4 usuarios cuando la base esta vacia`() = runTest {
        coEvery { dao.contarUsuarios() } returns 0

        repository.asegurarUsuariosIniciales()

        coVerify(exactly = 4) { dao.insert(any()) }
        coVerify(exactly = 1) {
            dao.insert(match { it.correo == "supervisor@test.com" && it.rol == RolUsuario.SUPERVISOR })
        }
    }

    @Test
    fun `asegurarUsuariosIniciales no inserta nada si ya existen usuarios`() = runTest {
        coEvery { dao.contarUsuarios() } returns 4

        repository.asegurarUsuariosIniciales()

        coVerify(exactly = 0) { dao.insert(any()) }
    }

    @Test
    fun `login asegura usuarios iniciales y limpia espacios en credenciales`() = runTest {
        val supervisor = Usuario(
            id = 1,
            nombre = "Supervisor Uno",
            correo = "supervisor@test.com",
            contrasena = "1234",
            rol = RolUsuario.SUPERVISOR
        )
        coEvery { dao.contarUsuarios() } returns 4
        coEvery { dao.login("supervisor@test.com", "1234") } returns supervisor

        val resultado = repository.login("  supervisor@test.com  ", " 1234 ")

        assertEquals(supervisor, resultado)
        coVerify(exactly = 1) { dao.login("supervisor@test.com", "1234") }
    }

    @Test
    fun `login retorna null si las credenciales no coinciden`() = runTest {
        coEvery { dao.contarUsuarios() } returns 4
        coEvery { dao.login("invalido@test.com", "0000") } returns null

        val resultado = repository.login("invalido@test.com", "0000")

        assertNull(resultado)
    }

    @Test
    fun `getAllUsuarios retorna el Flow desde usuarioDao`() = runTest {
        val lista = listOf(
            Usuario(1, "Supervisor Uno", "supervisor@test.com", "1234", RolUsuario.SUPERVISOR)
        )
        every { dao.getAllUsuarios() } returns flowOf(lista)

        val resultado = repository.getAllUsuarios().first()

        assertEquals(1, resultado.size)
        verify(exactly = 1) { dao.getAllUsuarios() }
    }
}