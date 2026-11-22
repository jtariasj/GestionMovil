package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Organizacion
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OrganizacionRepositoryTest {

    private lateinit var dao: OrganizacionDao
    private lateinit var repository: OrganizacionRepository

    private val organizacionEjemplo = Organizacion(
        id = 1,
        nombreEmpresa = "Minera Norte",
        rut = "76.123.456-7",
        direccion = "Av. Providencia 100"
    )

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = OrganizacionRepository(dao)
    }

    @Test
    fun `getAllOrganizaciones retorna flujo desde organizacionDao`() = runTest {
        every { dao.getAllOrganizaciones() } returns flowOf(listOf(organizacionEjemplo))

        val resultado = repository.getAllOrganizaciones().first()

        assertEquals(1, resultado.size)
        assertEquals("Minera Norte", resultado.first().nombreEmpresa)
        verify(exactly = 1) { dao.getAllOrganizaciones() }
    }

    @Test
    fun `guardar inserta organizacion en organizacionDao`() = runTest {
        repository.guardar(organizacionEjemplo)

        coVerify(exactly = 1) { dao.insert(organizacionEjemplo) }
    }
}