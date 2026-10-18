package cl.duoc.gestionmovil.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.gestionmovil.model.Contacto
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {
    @Insert
    suspend fun insert(contacto: Contacto)

    @Query("SELECT * FROM contactos WHERE organizacionId = :orgId")
    fun getContactosPorOrganizacion(orgId: Int): Flow<List<Contacto>>
}