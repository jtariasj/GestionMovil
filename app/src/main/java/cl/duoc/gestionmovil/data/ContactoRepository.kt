package cl.duoc.gestionmovil.data

import android.util.Log
import cl.duoc.gestionmovil.model.Contacto
import cl.duoc.gestionmovil.network.ContactoRequest
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class ContactoRepository(
    private val contactoDao: ContactoDao,
    private val api: GestionMovilApiService = RetrofitClient.api
) {
    private val refrescoRemoto = MutableStateFlow(0)

    fun getContactosPorOrganizacion(organizacionId: Int): Flow<List<Contacto>> = flow {
        try {
            refrescoRemoto.collect {
                emit(obtenerListaSeguraApi().filter { it.organizacionId == organizacionId })
            }
        } catch (e: Exception) {
            runCatching { Log.w("ContactoRepo", "Leyendo contactos desde Room: ${e.message}") }
            contactoDao.getContactosPorOrganizacion(organizacionId).collect { emit(it) }
        }
    }

    suspend fun guardar(contacto: Contacto) {
        try {
            val resp = api.crearContacto(
                ContactoRequest(
                    nombre = contacto.nombre,
                    cargo = contacto.cargo,
                    telefono = contacto.telefono,
                    correo = contacto.correo,
                    organizacionId = contacto.organizacionId
                )
            )
            if (!resp.isSuccessful) throw HttpException(resp)
            refrescoRemoto.value++
        } catch (e: Exception) {
            runCatching { Log.w("ContactoRepo", "Fallo API al guardar contacto, usando Room: ${e.message}") }
            contactoDao.insert(contacto)
        }
    }

    private suspend fun obtenerListaSeguraApi(): List<Contacto> {
        return try {
            api.obtenerContactos()
        } catch (e: HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }
    }
}