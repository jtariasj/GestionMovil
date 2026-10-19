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
    onNavigateToContactos: (Int) -> Unit
) {
    val organizaciones by viewModel.organizaciones.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    // SNACKBAR HOST STATE PARA LA ALERTA
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            mostrarDialogo = false
            snackbarHostState.showSnackbar("Creado exitosamente")
            viewModel.resetExito()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Organizaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar Organización") }
        },
        // AGREGAR EL SNACKBAR HOST
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                                .clickable { onNavigateToContactos(org.id) },
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

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Nueva Organización") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.nombre,
                            onValueChange = { viewModel.cambiarNombre(it) },
                            label = { Text("Nombre Empresa *") },
                            isError = uiState.errorNombre != null,
                            supportingText = { uiState.errorNombre?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.rut,
                            onValueChange = { viewModel.cambiarRut(it) },
                            label = { Text("RUT *") },
                            isError = uiState.errorRut != null,
                            supportingText = { uiState.errorRut?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.direccion,
                            onValueChange = { viewModel.cambiarDireccion(it) },
                            label = { Text("Dirección") }
                        )
                    }
                },
                confirmButton = { Button(onClick = { viewModel.validarYGuardar() }) { Text("Guardar") } },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarDialogo = false
                        viewModel.resetExito()
                    }) { Text("Cancelar") }
                }
            )
        }
    }
}