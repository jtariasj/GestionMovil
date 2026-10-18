package cl.duoc.gestionmovil.ui.organizacion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionScreen(
    viewModel: OrganizacionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToContactos: (Int) -> Unit // Para ir a la pantalla 8 (Contactos)
) {
    val organizaciones by viewModel.organizaciones.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Estados para el formulario de nueva organización
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Organizaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Organización")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (organizaciones.isEmpty()) {
                Text(
                    text = "No hay organizaciones registradas.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(organizaciones) { org ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToContactos(org.id) }, // Clic para ver sus contactos
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = org.nombreEmpresa, style = MaterialTheme.typography.titleMedium)
                                Text(text = "RUT: ${org.rut}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "Dir: ${org.direccion}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // Diálogo para registrar una nueva organización
        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Nueva Organización") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Empresa") })
                        OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT") })
                        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nombre.isNotBlank() && rut.isNotBlank()) {
                                viewModel.agregarOrganizacion(nombre, rut, direccion)
                                // Limpiamos campos y cerramos
                                nombre = ""; rut = ""; direccion = ""
                                mostrarDialogo = false
                            }
                        }
                    ) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
                }
            )
        }
    }
}