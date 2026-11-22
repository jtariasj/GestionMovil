package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Seguimiento
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

class SeguimientoRepositoryTest {

    private lateinit var dao: SeguimientoDao
    private lateinit var repository: SeguimientoRepository

    private val seguimientoEjemplo = Seguimiento(
        id = 1,
        fecha = 1700000000000L,
        descripcion = "Reunión inicial con cliente",
        tipoGestion = "Reunión",
        oportunidadId = 5
    )

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = SeguimientoRepository(dao)
    }

    @Test
    fun `getSeguimientosPorOportunidad consulta por oportunidadId en seguimientoDao`() = runTest {
        every { dao.getSeguimientosPorOportunidad(5) } returns flowOf(listOf(seguimientoEjemplo))

        val resultado = repository.getSeguimientosPorOportunidad(5).first()

        assertEquals(1, resultado.size)
        assertEquals("Reunión inicial con cliente", resultado.first().descripcion)
        verify(exactly = 1) { dao.getSeguimientosPorOportunidad(5) }
    }

    @Test
    fun `guardar inserta seguimiento en seguimientoDao`() = runTest {
        repository.guardar(seguimientoEjemplo)

        coVerify(exactly = 1) { dao.insert(seguimientoEjemplo) }
    }
}