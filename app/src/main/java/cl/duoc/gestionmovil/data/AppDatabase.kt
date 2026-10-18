package cl.duoc.gestionmovil.data

import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.Usuario
import cl.duoc.gestionmovil.model.Organizacion
import cl.duoc.gestionmovil.model.Contacto
import cl.duoc.gestionmovil.model.Seguimiento

import androidx.sqlite.SQLiteConnection
import android.content.Context
import cl.duoc.gestionmovil.model.RolUsuario


@Database(
    entities = [Oportunidad::class, Usuario::class, Organizacion::class, Contacto::class, Seguimiento::class],
    version = 2,
    exportSchema = false
)
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
                    "gestion_movil_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback()) // Agregamos el Callback aquí
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override suspend fun onCreate(connection: SQLiteConnection) {
            super.onCreate(connection)

            // Como onCreate ahora es 'suspend', ya no necesitamos CoroutineScope.launch
            INSTANCE?.let { database ->
                val dao = database.usuarioDao()
                // Insertar 1 Supervisor y 3 Ejecutivos
                dao.insert(
                    Usuario(
                        nombre = "Supervisor Uno",
                        correo = "supervisor@test.com",
                        contrasena = "1234",
                        rol = RolUsuario.SUPERVISOR
                    )
                )
                dao.insert(
                    Usuario(
                        nombre = "Ejecutivo Uno",
                        correo = "ejecutivo1@test.com",
                        contrasena = "1234",
                        rol = RolUsuario.EJECUTIVO
                    )
                )
                dao.insert(
                    Usuario(
                        nombre = "Ejecutivo Dos",
                        correo = "ejecutivo2@test.com",
                        contrasena = "1234",
                        rol = RolUsuario.EJECUTIVO
                    )
                )
                dao.insert(
                    Usuario(
                        nombre = "Ejecutivo Tres",
                        correo = "ejecutivo3@test.com",
                        contrasena = "1234",
                        rol = RolUsuario.EJECUTIVO
                    )
                )
            }
        }
    }
}