package cl.duoc.gestionmovil.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.gestionmovil.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insert(usuario: Usuario)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    @Query(
        """
        SELECT * FROM usuarios 
        WHERE (LOWER(correo) = LOWER(:credencial) OR LOWER(correo) = LOWER(:credencial || '@test.com')) 
          AND contrasena = :contrasena 
        LIMIT 1
        """
    )
    suspend fun login(credencial: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<Usuario>>
}