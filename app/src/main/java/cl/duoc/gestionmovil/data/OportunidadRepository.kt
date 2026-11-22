package cl.duoc.gestionmovil.data

import android.util.Log
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.OportunidadRequest
import cl.duoc.gestionmovil.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import retrofit2.Response

class OportunidadRepository(
    private val oportunidadDao: OportunidadDao? = null,
    private val api: GestionMovilApiService = RetrofitClient.api
) {
    private val refrescoRemoto = MutableStateFlow(0)

    // =========================================================================
    // 1. OPERACIONES DIRECTAS API (Compatibilidad con pruebas unitarias)
    // =========================================================================

    suspend fun obtenerOportunidadesApi(): List<Oportunidad> {
        val listaRemota = obtenerListaSeguraApi()
        oportunidadDao?.let { dao -> listaRemota.forEach { runCatching { dao.insertar(it) } } }
        return listaRemota
    }

    suspend fun obtenerOportunidadApi(id: Int): Oportunidad = api.obtenerOportunidad(id)

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
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }

    // =========================================================================
    // 2. OPERACIONES HÍBRIDAS USADAS POR LOS VIEWMODELS (RETROFIT + PLAN B ROOM)
    // =========================================================================

    suspend fun guardar(oportunidad: Oportunidad) {
        try {
            comprobarRespuesta(api.crearOportunidad(oportunidad.aRequest()))
            refrescoRemoto.value++
        } catch (e: Exception) {
            runCatching { Log.w("OportunidadRepo", "API no disponible al guardar, usando Room: ${e.message}") }
            oportunidadDao?.insertar(oportunidad)
        }
    }

    suspend fun actualizar(oportunidad: Oportunidad) {
        try {
            comprobarRespuesta(api.actualizarOportunidad(oportunidad.id, oportunidad.aRequest()))
            refrescoRemoto.value++
        } catch (e: Exception) {
            runCatching { Log.w("OportunidadRepo", "API no disponible al actualizar, usando Room: ${e.message}") }
            oportunidadDao?.actualizar(oportunidad)
        }
    }

    fun obtenerOportunidades(): Flow<List<Oportunidad>> = flow {
        try {
            refrescoRemoto.collect {
                emit(obtenerListaSeguraApi().sortedByDescending { it.fechaActualizacion })
            }
        } catch (e: Exception) {
            runCatching { Log.w("OportunidadRepo", "Leyendo oportunidades desde Plan B (Room): ${e.message}") }
            requireNotNull(oportunidadDao).obtenerOportunidades().collect { emit(it) }
        }
    }

    fun getAllOportunidades(): Flow<List<Oportunidad>> = flow {
        try {
            refrescoRemoto.collect {
                emit(obtenerListaSeguraApi())
            }
        } catch (e: Exception) {
            runCatching { Log.w("OportunidadRepo", "Leyendo todas las oportunidades desde Plan B (Room): ${e.message}") }
            requireNotNull(oportunidadDao).getAllOportunidades().collect { emit(it) }
        }
    }

    fun obtenerPorResponsable(nombreResponsable: String): Flow<List<Oportunidad>> {
        return getAllOportunidades().map { lista ->
            lista.filter { it.responsable.equals(nombreResponsable, ignoreCase = true) }
        }
    }

    suspend fun obtenerPorId(id: Int): Oportunidad? {
        return try {
            api.obtenerOportunidad(id)
        } catch (e: Exception) {
            oportunidadDao?.obtenerPorId(id)
        }
    }

    suspend fun obtenerPorNombre(nombre: String): List<Oportunidad> {
        return try {
            obtenerListaSeguraApi().filter { it.nombre.equals(nombre, ignoreCase = true) }
        } catch (e: Exception) {
            oportunidadDao?.obtenerPorNombre(nombre) ?: emptyList()
        }
    }

    suspend fun eliminar(oportunidad: Oportunidad) {
        try {
            comprobarRespuesta(api.eliminarOportunidad(oportunidad.id))
            refrescoRemoto.value++
        } catch (e: Exception) {
            runCatching { Log.w("OportunidadRepo", "API no disponible al eliminar, usando Room: ${e.message}") }
            oportunidadDao?.delete(oportunidad)
        }
    }

    private suspend fun obtenerListaSeguraApi(): List<Oportunidad> {
        return try {
            api.obtenerOportunidades()
        } catch (e: HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }
    }

    private fun Oportunidad.aRequest() = OportunidadRequest(
        nombre = nombre,
        tipo = tipo.name,
        descripcion = descripcion,
        fuenteInformacion = fuenteInformacion,
        fechaDeteccion = fechaDeteccion,
        responsable = responsable,
        prioridad = prioridad.name,
        potencialComercial = potencialComercial.name,
        estado = estado.name,
        fechaActualizacion = fechaActualizacion,
        observaciones = observaciones,
        fotoPath = fotoPath
    )
}