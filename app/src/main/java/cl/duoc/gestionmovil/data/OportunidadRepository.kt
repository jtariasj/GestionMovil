package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.flow.Flow

// Define CÓMO accedemos a los datos de oportunidades
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

    suspend fun obtenerPorId(id: Int): Oportunidad? {
        return oportunidadDao.obtenerPorId(id)
    }
}
