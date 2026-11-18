package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Organizacion
import kotlinx.coroutines.flow.Flow

class OrganizacionRepository(private val organizacionDao: OrganizacionDao) {

    fun getAllOrganizaciones(): Flow<List<Organizacion>> {
        return organizacionDao.getAllOrganizaciones()
    }

    suspend fun guardar(organizacion: Organizacion) {
        organizacionDao.insert(organizacion)
    }
}
