package cl.duoc.gestionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cl.duoc.gestionmovil.data.AppDatabase
import cl.duoc.gestionmovil.data.ContactoRepository
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.data.OrganizacionRepository
import cl.duoc.gestionmovil.data.SeguimientoRepository
import cl.duoc.gestionmovil.data.UsuarioRepository
import cl.duoc.gestionmovil.ui.navegacion.AppNavGraph
import cl.duoc.gestionmovil.ui.theme.GestionMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos la base de datos de Room y los Repositorios
        val database = AppDatabase.getDatabase(applicationContext)
        val usuarioRepository = UsuarioRepository(database.usuarioDao())
        val oportunidadRepository = OportunidadRepository(database.oportunidadDao())
        val seguimientoRepository = SeguimientoRepository(database.seguimientoDao())
        val organizacionRepository = OrganizacionRepository(database.organizacionDao())
        val contactoRepository = ContactoRepository(database.contactoDao())

        setContent {
            GestionMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        usuarioRepository = usuarioRepository,
                        oportunidadRepository = oportunidadRepository,
                        seguimientoRepository = seguimientoRepository,
                        organizacionRepository = organizacionRepository,
                        contactoRepository = contactoRepository
                    )
                }
            }
        }
    }
}
