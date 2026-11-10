package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.Contacto
import kotlinx.coroutines.flow.Flow

class ContactoRepository(private val contactoDao: ContactoDao) {

    fun getContactosPorOrganizacion(organizacionId: Int): Flow<List<Contacto>> {
        return contactoDao.getContactosPorOrganizacion(organizacionId)
    }

    suspend fun guardar(contacto: Contacto) {
        contactoDao.insert(contacto)
    }
}
