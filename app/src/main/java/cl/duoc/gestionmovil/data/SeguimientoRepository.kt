package cl.duoc.gestionmovil.data

import android.util.Log
import cl.duoc.gestionmovil.model.Seguimiento
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.RetrofitClient
import cl.duoc.gestionmovil.network.SeguimientoRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class SeguimientoRepository(
    private val seguimientoDao: SeguimientoDao,
    private val api: GestionMovilApiService = RetrofitClient.api
) {
    private val refrescoRemoto = MutableStateFlow(0)

    fun getSeguimientosPorOportunidad(oportunidadId: Int): Flow<List<Seguimiento>> = flow {
        try {
            refrescoRemoto.collect {
                emit(
                    obtenerListaSeguraApi()
                        .filter { it.oportunidadId == oportunidadId }
                        .sortedByDescending { it.fecha }
                )
            }
        } catch (e: Exception) {
            runCatching { Log.w("SeguimientoRepo", "Leyendo seguimientos desde Room: ${e.message}") }
            seguimientoDao.getSeguimientosPorOportunidad(oportunidadId).collect { emit(it) }
        }
    }

    suspend fun guardar(seguimiento: Seguimiento) {
        try {
            val resp = api.crearSeguimiento(
                SeguimientoRequest(
                    fecha = seguimiento.fecha,
                    descripcion = seguimiento.descripcion,
                    tipoGestion = seguimiento.tipoGestion,
                    oportunidadId = seguimiento.oportunidadId
                )
            )
            if (!resp.isSuccessful) throw HttpException(resp)
            refrescoRemoto.value++
        } catch (e: Exception) {
            runCatching { Log.w("SeguimientoRepo", "Fallo API al guardar seguimiento, usando Room: ${e.message}") }
            seguimientoDao.insert(seguimiento)
        }
    }

    private suspend fun obtenerListaSeguraApi(): List<Seguimiento> {
        return try {
            api.obtenerSeguimientos()
        } catch (e: HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }
    }
}