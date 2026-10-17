package cl.duoc.gestionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormScreen
import cl.duoc.gestionmovil.ui.theme.GestionMovilTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestionMovilTheme {   // el nombre que ya tenga tu proyecto
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OportunidadFormScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}