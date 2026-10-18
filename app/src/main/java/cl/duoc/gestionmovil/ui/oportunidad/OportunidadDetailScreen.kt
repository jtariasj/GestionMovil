package cl.duoc.gestionmovil.ui.oportunidad

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.gestionmovil.util.formatearFecha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadDetailScreen(
    oportunidadId: Int,
    rolUsuario: String, // NUEVO: Recibimos el rol para aplicar restricciones
    viewModel: OportunidadDetailViewModel,
    onNavigateBack: () -> Unit
) {
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
                    // RESTRICCIÓN: Solo el SUPERVISOR puede eliminar (descartar definitivamente)
                    if (rolUsuario == "SUPERVISOR") {
                        IconButton(onClick = { viewModel.eliminarOportunidad(onNavigateBack) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (oportunidad == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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
                Text("Fecha Detección: ${formatearFecha(oportunidad!!.fechaDeteccion)}", style = MaterialTheme.typography.bodyLarge)
                Text("Prioridad: ${oportunidad!!.prioridad.etiqueta}", style = MaterialTheme.typography.bodyLarge)
                Text("Potencial: ${oportunidad!!.potencialComercial.etiqueta}", style = MaterialTheme.typography.bodyLarge)

                if (!oportunidad!!.observaciones.isNullOrEmpty()) {
                    Text("Observaciones: ${oportunidad!!.observaciones}", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // LÓGICA DE REASIGNACIÓN
                if (rolUsuario == "SUPERVISOR") {
                    Text("Reasignar Ejecutivo (Solo Supervisor):", style = MaterialTheme.typography.titleMedium)

                    val listaEjecutivos = listOf("Ejecutivo Uno", "Ejecutivo Dos", "Ejecutivo Tres")
                    var expandido by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expandido,
                        onExpandedChange = { expandido = !expandido }
                    ) {
                        OutlinedTextField(
                            value = oportunidad!!.responsable,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Responsable actual") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandido,
                            onDismissRequest = { expandido = false }
                        ) {
                            listaEjecutivos.forEach { ejecutivo ->
                                DropdownMenuItem(
                                    text = { Text(ejecutivo) },
                                    onClick = {
                                        // Llamamos a la función de reasignar
                                        viewModel.reasignarOportunidad(ejecutivo)
                                        expandido = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // Si es ejecutivo, solo ve el texto normal
                    Text("Responsable: ${oportunidad!!.responsable}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}