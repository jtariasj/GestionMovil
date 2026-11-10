package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.flow.Flow

class OportunidadRepository(private val oportunidadDao: OportunidadDao) {

    suspend fun guardar(oportunidad: Oportunidad) {
        oportunidadDao.insertar(oportunidad)
    }

    suspend fun actualizar(oportunidad: Oportunidad) {
        oportunidadDao.actualizar(oportunidad)
    }

    fun obtenerOportunidades(): Flow<List<Oportunidad>> {
        return oportunidadDao.obtenerOportunidades()
    }

    fun getAllOportunidades(): Flow<List<Oportunidad>> {
        return oportunidadDao.getAllOportunidades()
    }

    fun obtenerPorResponsable(nombreResponsable: String): Flow<List<Oportunidad>> {
        return oportunidadDao.obtenerPorResponsable(nombreResponsable)
    }

    suspend fun obtenerPorId(id: Int): Oportunidad? {
        return oportunidadDao.obtenerPorId(id)
    }

    suspend fun obtenerPorNombre(nombre: String): List<Oportunidad> {
        return oportunidadDao.obtenerPorNombre(nombre)
    }

    suspend fun eliminar(oportunidad: Oportunidad) {
        oportunidadDao.delete(oportunidad)
    }
}
