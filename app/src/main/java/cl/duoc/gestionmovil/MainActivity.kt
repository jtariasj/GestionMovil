package cl.duoc.gestionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cl.duoc.gestionmovil.data.AppDatabase
import cl.duoc.gestionmovil.ui.navegacion.AppNavGraph
import cl.duoc.gestionmovil.ui.theme.GestionMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos la base de datos de Room (Patrón Singleton)
        val database = AppDatabase.getDatabase(applicationContext)
        val usuarioDao = database.usuarioDao()
        val oportunidadDao = database.oportunidadDao()
        val seguimientoDao = database.seguimientoDao()
        val organizacionDao = database.organizacionDao()
        val contactoDao = database.contactoDao()

        setContent {
            GestionMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 2. Invocamos el grafo de navegación modularizado
                    AppNavGraph(
                        usuarioDao = usuarioDao,
                        oportunidadDao = oportunidadDao,
                        seguimientoDao = seguimientoDao,
                        organizacionDao = organizacionDao,
                        contactoDao = contactoDao
                    )
                }
            }
        }
    }
}