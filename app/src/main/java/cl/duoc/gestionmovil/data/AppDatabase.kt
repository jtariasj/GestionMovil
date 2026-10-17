package cl.duoc.gestionmovil.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import cl.duoc.gestionmovil.model.Oportunidad

@Database(
    entities = [Oportunidad::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Una sola instancia de la base de datos para toda la app
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestionmovil.db"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
