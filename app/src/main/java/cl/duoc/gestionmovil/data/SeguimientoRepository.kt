package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Seguimiento
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.RetrofitClient
import cl.duoc.gestionmovil.network.SeguimientoRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import retrofit2.Response

class SeguimientoRepository(
    private val seguimientoDao: SeguimientoDao? = null,
    private val api: GestionMovilApiService = RetrofitClient.api
) {

    // =========================================================================
    // 1. OPERACIONES REMOTAS (RETROFIT -> SPRING BOOT)
    // =========================================================================

    suspend fun obtenerSeguimientosApi(): List<Seguimiento> {
        val listaRemota = api.obtenerSeguimientos()
        // Respaldo local en Room si está disponible
        seguimientoDao?.let { dao ->
            listaRemota.forEach { dao.insert(it) }
        }
        return listaRemota
    }

    suspend fun obtenerSeguimientosPorOportunidadApi(oportunidadId: Int): List<Seguimiento> {
        return obtenerSeguimientosApi().filter { it.oportunidadId == oportunidadId }
    }

    suspend fun crearSeguimientoApi(seguimiento: SeguimientoRequest) {
        comprobarRespuesta(api.crearSeguimiento(seguimiento))
    }

    suspend fun actualizarSeguimientoApi(id: Int, seguimiento: SeguimientoRequest) {
        comprobarRespuesta(api.actualizarSeguimiento(id, seguimiento))
    }

    suspend fun eliminarSeguimientoApi(id: Int) {
        comprobarRespuesta(api.eliminarSeguimiento(id))
    }

    private fun comprobarRespuesta(respuesta: Response<Void>) {
        if (!respuesta.isSuccessful) {
            throw HttpException(respuesta)
        }
    }

    // =========================================================================
    // 2. OPERACIONES LOCALES
    // =========================================================================

    fun getSeguimientosPorOportunidad(oportunidadId: Int): Flow<List<Seguimiento>> {
        return requireNotNull(seguimientoDao).getSeguimientosPorOportunidad(oportunidadId)
    }

    suspend fun guardar(seguimiento: Seguimiento) {
        seguimientoDao?.insert(seguimiento)
    }
}