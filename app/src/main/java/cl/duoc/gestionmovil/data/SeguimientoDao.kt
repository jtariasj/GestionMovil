package cl.duoc.gestionmovil.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.gestionmovil.model.Seguimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguimientoDao {
    @Insert
    suspend fun insert(seguimiento: Seguimiento)

    @Query("SELECT * FROM seguimientos WHERE oportunidadId = :oportunidadId ORDER BY fecha DESC")
    fun getSeguimientosPorOportunidad(oportunidadId: Int): Flow<List<Seguimiento>>
}