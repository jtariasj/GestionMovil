package cl.duoc.gestionmovil.data

import android.util.Log
import cl.duoc.gestionmovil.model.Organizacion
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.OrganizacionRequest
import cl.duoc.gestionmovil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class OrganizacionRepository(
    private val organizacionDao: OrganizacionDao,
    private val api: GestionMovilApiService = RetrofitClient.api
) {
    private val refrescoRemoto = MutableStateFlow(0)

    fun getAllOrganizaciones(): Flow<List<Organizacion>> = flow {
        try {
            refrescoRemoto.collect {
                emit(obtenerListaSeguraApi())
            }
        } catch (e: Exception) {
            runCatching { Log.w("OrganizacionRepo", "Leyendo organizaciones desde Room: ${e.message}") }
            organizacionDao.getAllOrganizaciones().collect { emit(it) }
        }
    }

    suspend fun guardar(organizacion: Organizacion) {
        try {
            val resp = api.crearOrganizacion(
                OrganizacionRequest(organizacion.nombreEmpresa, organizacion.rut, organizacion.direccion)
            )
            if (!resp.isSuccessful) throw HttpException(resp)
            refrescoRemoto.value++
        } catch (e: Exception) {
            runCatching { Log.w("OrganizacionRepo", "Fallo API al guardar organización, usando Room: ${e.message}") }
            organizacionDao.insert(organizacion)
        }
    }

    suspend fun eliminar(id: Int) {
        val resp = api.eliminarOrganizacion(id)
        if (!resp.isSuccessful) throw HttpException(resp)
        refrescoRemoto.value++
    }

    private suspend fun obtenerListaSeguraApi(): List<Organizacion> {
        return try {
            api.obtenerOrganizaciones()
        } catch (e: HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }
    }
}