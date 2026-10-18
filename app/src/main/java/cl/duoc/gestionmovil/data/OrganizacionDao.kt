package cl.duoc.gestionmovil.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.gestionmovil.model.Organizacion
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizacionDao {
    @Insert
    suspend fun insert(organizacion: Organizacion)

    @Query("SELECT * FROM organizaciones")
    fun getAllOrganizaciones(): Flow<List<Organizacion>>
}