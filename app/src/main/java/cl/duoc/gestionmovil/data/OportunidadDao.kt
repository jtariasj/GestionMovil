package cl.duoc.gestionmovil.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.flow.Flow

// Define QUÉ operaciones podemos hacer sobre la tabla "oportunidades"
@Dao
interface OportunidadDao {

    @Insert
    suspend fun insertar(oportunidad: Oportunidad)

    @Update
    suspend fun actualizar(oportunidad: Oportunidad)

    @Query("SELECT * FROM oportunidades ORDER BY fechaActualizacion DESC")
    fun obtenerOportunidades(): Flow<List<Oportunidad>>

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Oportunidad?

    @Query("SELECT * FROM oportunidades")
    fun getAllOportunidades(): Flow<List<Oportunidad>>
}
