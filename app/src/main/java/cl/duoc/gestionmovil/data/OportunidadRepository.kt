package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.OportunidadRequest
import cl.duoc.gestionmovil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import retrofit2.Response

class OportunidadRepository(
    private val oportunidadDao: OportunidadDao? = null,
    private val api: GestionMovilApiService = RetrofitClient.api
) {

    // =========================================================================
    // 1. OPERACIONES REMOTAS (RETROFIT -> SPRING BOOT)
    // =========================================================================

    suspend fun obtenerOportunidadesApi(): List<Oportunidad> {
        val listaRemota = api.obtenerOportunidades()
        // Respaldo local en Room si está disponible
        oportunidadDao?.let { dao ->
            listaRemota.forEach { dao.insertar(it) }
        }
        return listaRemota
    }

    suspend fun obtenerOportunidadApi(id: Int): Oportunidad {
        return api.obtenerOportunidad(id)
    }

    suspend fun crearOportunidadApi(oportunidad: OportunidadRequest) {
        comprobarRespuesta(api.crearOportunidad(oportunidad))
    }

    suspend fun actualizarOportunidadApi(id: Int, oportunidad: OportunidadRequest) {
        comprobarRespuesta(api.actualizarOportunidad(id, oportunidad))
    }

    suspend fun eliminarOportunidadApi(id: Int) {
        comprobarRespuesta(api.eliminarOportunidad(id))
    }

    private fun comprobarRespuesta(respuesta: Response<Void>) {
        if (!respuesta.isSuccessful) {
            throw HttpException(respuesta)
        }
    }

    // =========================================================================
    // 2. OPERACIONES LOCALES
    // =========================================================================

    suspend fun guardar(oportunidad: Oportunidad) {
        oportunidadDao?.insertar(oportunidad)
    }

    suspend fun actualizar(oportunidad: Oportunidad) {
        oportunidadDao?.actualizar(oportunidad)
    }

    fun obtenerOportunidades(): Flow<List<Oportunidad>> {
        return requireNotNull(oportunidadDao).obtenerOportunidades()
    }

    fun getAllOportunidades(): Flow<List<Oportunidad>> {
        return requireNotNull(oportunidadDao).getAllOportunidades()
    }

    fun obtenerPorResponsable(nombreResponsable: String): Flow<List<Oportunidad>> {
        return requireNotNull(oportunidadDao).obtenerPorResponsable(nombreResponsable)
    }

    suspend fun obtenerPorId(id: Int): Oportunidad? {
        return oportunidadDao?.obtenerPorId(id)
    }

    suspend fun obtenerPorNombre(nombre: String): List<Oportunidad> {
        return oportunidadDao?.obtenerPorNombre(nombre) ?: emptyList()
    }

    suspend fun eliminar(oportunidad: Oportunidad) {
        oportunidadDao?.delete(oportunidad)
    }
}