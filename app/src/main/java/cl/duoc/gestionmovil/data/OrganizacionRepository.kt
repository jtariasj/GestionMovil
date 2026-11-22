package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Organizacion
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.OrganizacionRequest
import cl.duoc.gestionmovil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import retrofit2.Response

class OrganizacionRepository(
    private val organizacionDao: OrganizacionDao? = null,
    private val api: GestionMovilApiService = RetrofitClient.api
) {

    // =========================================================================
    // 1. OPERACIONES REMOTAS (RETROFIT -> SPRING BOOT)
    // =========================================================================

    suspend fun obtenerOrganizacionesApi(): List<Organizacion> {
        val listaRemota = api.obtenerOrganizaciones()
        // Respaldo local en Room si está disponible
        organizacionDao?.let { dao ->
            listaRemota.forEach { dao.insert(it) }
        }
        return listaRemota
    }

    suspend fun obtenerOrganizacionApi(id: Int): Organizacion {
        return api.obtenerOrganizacion(id)
    }

    suspend fun crearOrganizacionApi(organizacion: OrganizacionRequest) {
        comprobarRespuesta(api.crearOrganizacion(organizacion))
    }

    suspend fun actualizarOrganizacionApi(id: Int, organizacion: OrganizacionRequest) {
        comprobarRespuesta(api.actualizarOrganizacion(id, organizacion))
    }

    suspend fun eliminarOrganizacionApi(id: Int) {
        comprobarRespuesta(api.eliminarOrganizacion(id))
    }

    private fun comprobarRespuesta(respuesta: Response<Void>) {
        if (!respuesta.isSuccessful) {
            throw HttpException(respuesta)
        }
    }

    // =========================================================================
    // 2. OPERACIONES LOCALES
    // =========================================================================

    fun getAllOrganizaciones(): Flow<List<Organizacion>> {
        return requireNotNull(organizacionDao).getAllOrganizaciones()
    }

    suspend fun guardar(organizacion: Organizacion) {
        organizacionDao?.insert(organizacion)
    }
}