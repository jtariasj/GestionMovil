package cl.duoc.gestionmovil.ui.oportunidad

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadDetailScreen(
    oportunidadId: Int,
    viewModel: OportunidadDetailViewModel,
    onNavigateBack: () -> Unit
) {
    // Le decimos al ViewModel que busque los datos de esta oportunidad apenas se abre la pantalla
    LaunchedEffect(oportunidadId) {
        viewModel.cargarOportunidad(oportunidadId)
    }

    val oportunidad by viewModel.oportunidad.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Oportunidad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón para eliminar
                    IconButton(onClick = { viewModel.eliminarOportunidad(onNavigateBack) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (oportunidad == null) {
            // Mientras carga la info desde la BD mostramos un círculo de carga
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Cuando ya tenemos los datos, los mostramos
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Nombre: ${oportunidad!!.nombre}", style = MaterialTheme.typography.headlineSmall)
                Text("Tipo: ${oportunidad!!.tipo.etiqueta}", style = MaterialTheme.typography.bodyLarge)
                Text("Descripción: ${oportunidad!!.descripcion}", style = MaterialTheme.typography.bodyLarge)
                Text("Fecha Detección: ${oportunidad!!.fechaDeteccion}", style = MaterialTheme.typography.bodyLarge)
                Text("Responsable: ${oportunidad!!.responsable}", style = MaterialTheme.typography.bodyLarge)
                Text("Prioridad: ${oportunidad!!.prioridad.etiqueta}", style = MaterialTheme.typography.bodyLarge)
                Text("Potencial: ${oportunidad!!.potencialComercial.etiqueta}", style = MaterialTheme.typography.bodyLarge)

                if (!oportunidad!!.observaciones.isNullOrEmpty()) {
                    Text("Observaciones: ${oportunidad!!.observaciones}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}