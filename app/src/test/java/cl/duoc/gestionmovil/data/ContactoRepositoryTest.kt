package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Contacto
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

class ContactoRepositoryTest {

    private lateinit var dao: ContactoDao
    private lateinit var repository: ContactoRepository

    private val contactoEjemplo = Contacto(
        id = 1,
        nombre = "Ana Torres",
        cargo = "Jefa de Marketing",
        telefono = "+56911112222",
        correo = "ana@gmail.com",
        organizacionId = 3
    )

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = ContactoRepository(dao)
    }

    @Test
    fun `getContactosPorOrganizacion filtra por id en contactoDao`() = runTest {
        every { dao.getContactosPorOrganizacion(3) } returns flowOf(listOf(contactoEjemplo))

        val resultado = repository.getContactosPorOrganizacion(3).first()

        assertEquals(1, resultado.size)
        assertEquals("Ana Torres", resultado.first().nombre)
        verify(exactly = 1) { dao.getContactosPorOrganizacion(3) }
    }

    @Test
    fun `guardar inserta contacto en contactoDao`() = runTest {
        repository.guardar(contactoEjemplo)

        coVerify(exactly = 1) { dao.insert(contactoEjemplo) }
    }
}