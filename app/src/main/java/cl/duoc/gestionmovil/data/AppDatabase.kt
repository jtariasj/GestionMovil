package cl.duoc.gestionmovil.data

import android.content.Context
import androidx.room.TypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

import cl.duoc.gestionmovil.model.Contacto
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.Organizacion
import cl.duoc.gestionmovil.model.Seguimiento
import cl.duoc.gestionmovil.model.Usuario

@Database(
    entities = [
        Oportunidad::class,
        Usuario::class,
        Organizacion::class,
        Contacto::class,
        Seguimiento::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun organizacionDao(): OrganizacionDao
    abstract fun contactoDao(): ContactoDao
    abstract fun seguimientoDao(): SeguimientoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestion_movil_v3.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}