package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Contacto
import cl.duoc.gestionmovil.network.ContactoRequest
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import retrofit2.Response

class ContactoRepository(
    private val contactoDao: ContactoDao? = null,
    private val api: GestionMovilApiService = RetrofitClient.api
) {

    // =========================================================================
    // 1. OPERACIONES REMOTAS (RETROFIT -> SPRING BOOT)
    // =========================================================================

    suspend fun obtenerContactosApi(): List<Contacto> {
        val listaRemota = api.obtenerContactos()
        // Respaldo local en Room si está disponible
        contactoDao?.let { dao ->
            listaRemota.forEach { dao.insert(it) }
        }
        return listaRemota
    }

    suspend fun obtenerContactosPorOrganizacionApi(organizacionId: Int): List<Contacto> {
        return obtenerContactosApi().filter { it.organizacionId == organizacionId }
    }

    suspend fun obtenerContactoApi(id: Int): Contacto {
        return api.obtenerContacto(id)
    }

    suspend fun crearContactoApi(contacto: ContactoRequest) {
        comprobarRespuesta(api.crearContacto(contacto))
    }

    suspend fun actualizarContactoApi(id: Int, contacto: ContactoRequest) {
        comprobarRespuesta(api.actualizarContacto(id, contacto))
    }

    suspend fun eliminarContactoApi(id: Int) {
        comprobarRespuesta(api.eliminarContacto(id))
    }

    private fun comprobarRespuesta(respuesta: Response<Void>) {
        if (!respuesta.isSuccessful) {
            throw HttpException(respuesta)
        }
    }

    // =========================================================================
    // 2. OPERACIONES LOCALES
    // =========================================================================

    fun getContactosPorOrganizacion(organizacionId: Int): Flow<List<Contacto>> {
        return requireNotNull(contactoDao).getContactosPorOrganizacion(organizacionId)
    }

    suspend fun guardar(contacto: Contacto) {
        contactoDao?.insert(contacto)
    }
}