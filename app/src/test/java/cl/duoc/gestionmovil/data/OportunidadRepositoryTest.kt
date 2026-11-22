package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.TipoOportunidad
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.OportunidadRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class OportunidadRepositoryTest {

    private lateinit var dao: OportunidadDao
    private lateinit var api: GestionMovilApiService
    private lateinit var repository: OportunidadRepository

    private val oportunidadEjemplo = Oportunidad(
        id = 1,
        nombre = "Feria Duoc",
        tipo = TipoOportunidad.FERIA_EMPRESARIAL,
        descripcion = "Evento sede",
        fuenteInformacion = "Web",
        fechaDeteccion = 1700000000000L,
        responsable = "Carlos Ruiz",
        prioridad = Prioridad.ALTA,
        potencialComercial = PotencialComercial.ALTO,
        fechaActualizacion = 1700000000000L
    )

    private val requestEjemplo = mockk<OportunidadRequest>(relaxed = true)

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        api = mockk()
        repository = OportunidadRepository(oportunidadDao = dao, api = api)
    }

    // --- PRUEBAS REMOTO (RETROFIT) ---

    @Test
    fun `obtenerOportunidadesApi descarga lista y respalda cada item en Room`() = runTest {
        coEvery { api.obtenerOportunidades() } returns listOf(oportunidadEjemplo)

        val resultado = repository.obtenerOportunidadesApi()

        assertEquals(1, resultado.size)
        assertEquals("Feria Duoc", resultado.first().nombre)
        coVerify(exactly = 1) { dao.insertar(oportunidadEjemplo) }
    }

    @Test
    fun `obtenerOportunidadApi retorna oportunidad por id desde API`() = runTest {
        coEvery { api.obtenerOportunidad(1) } returns oportunidadEjemplo

        val resultado = repository.obtenerOportunidadApi(1)

        assertEquals(oportunidadEjemplo, resultado)
    }

    @Test
    fun `crear, actualizar y eliminar API completan cuando respuesta es exitosa`() = runTest {
        coEvery { api.crearOportunidad(requestEjemplo) } returns Response.success(null)
        coEvery { api.actualizarOportunidad(1, requestEjemplo) } returns Response.success(null)
        coEvery { api.eliminarOportunidad(1) } returns Response.success(null)

        repository.crearOportunidadApi(requestEjemplo)
        repository.actualizarOportunidadApi(1, requestEjemplo)
        repository.eliminarOportunidadApi(1)

        coVerify(exactly = 1) { api.crearOportunidad(requestEjemplo) }
        coVerify(exactly = 1) { api.actualizarOportunidad(1, requestEjemplo) }
        coVerify(exactly = 1) { api.eliminarOportunidad(1) }
    }

    @Test(expected = HttpException::class)
    fun `crearOportunidadApi lanza HttpException cuando servidor responde error`() = runTest {
        val respuestaError = Response.error<Void>(500, "Error interno".toResponseBody(null))
        coEvery { api.crearOportunidad(requestEjemplo) } returns respuestaError

        repository.crearOportunidadApi(requestEjemplo)
    }

    // --- PRUEBAS LOCAL (ROOM) ---

    @Test
    fun `operaciones locales delegan correctamente en OportunidadDao`() = runTest {
        every { dao.obtenerOportunidades() } returns flowOf(listOf(oportunidadEjemplo))
        every { dao.getAllOportunidades() } returns flowOf(listOf(oportunidadEjemplo))
        every { dao.obtenerPorResponsable("Carlos Ruiz") } returns flowOf(listOf(oportunidadEjemplo))
        coEvery { dao.obtenerPorId(1) } returns oportunidadEjemplo
        coEvery { dao.obtenerPorNombre("Feria Duoc") } returns listOf(oportunidadEjemplo)

        repository.guardar(oportunidadEjemplo)
        repository.actualizar(oportunidadEjemplo)
        repository.eliminar(oportunidadEjemplo)

        assertEquals(1, repository.obtenerOportunidades().first().size)
        assertEquals(1, repository.getAllOportunidades().first().size)
        assertEquals(1, repository.obtenerPorResponsable("Carlos Ruiz").first().size)
        assertEquals(oportunidadEjemplo, repository.obtenerPorId(1))
        assertEquals(1, repository.obtenerPorNombre("Feria Duoc").size)

        coVerify(exactly = 1) { dao.insertar(oportunidadEjemplo) }
        coVerify(exactly = 1) { dao.actualizar(oportunidadEjemplo) }
        coVerify(exactly = 1) { dao.delete(oportunidadEjemplo) }
        verify(exactly = 1) { dao.obtenerOportunidades() }
    }
}