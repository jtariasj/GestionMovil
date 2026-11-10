package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Seguimiento
import kotlinx.coroutines.flow.Flow

class SeguimientoRepository(private val seguimientoDao: SeguimientoDao) {

    fun getSeguimientosPorOportunidad(oportunidadId: Int): Flow<List<Seguimiento>> {
        return seguimientoDao.getSeguimientosPorOportunidad(oportunidadId)
    }

    suspend fun guardar(seguimiento: Seguimiento) {
        seguimientoDao.insert(seguimiento)
    }
}
