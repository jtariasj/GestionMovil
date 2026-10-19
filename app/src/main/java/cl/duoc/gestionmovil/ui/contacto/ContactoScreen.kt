package cl.duoc.gestionmovil.ui.contacto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoScreen(
    viewModel: ContactoViewModel,
    onNavigateBack: () -> Unit
) {
    val contactos by viewModel.contactos.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            mostrarDialogo = false
            android.widget.Toast.makeText(context, "Contacto creado exitosamente", android.widget.Toast.LENGTH_SHORT).show()
            viewModel.resetExito()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contactos de la Empresa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar Contacto") }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (contactos.isEmpty()) {
                Text(
                    text = "No hay contactos registrados para esta organización.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(contactos) { contacto ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = contacto.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Cargo: ${contacto.cargo}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = contacto.telefono, style = MaterialTheme.typography.bodySmall)
                                }
                                Row {
                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = contacto.correo, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Nuevo Contacto") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.nombre,
                            onValueChange = { viewModel.cambiarNombre(it) },
                            label = { Text("Nombre Completo *") },
                            isError = uiState.errorNombre != null,
                            supportingText = { uiState.errorNombre?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.cargo,
                            onValueChange = { viewModel.cambiarCargo(it) },
                            label = { Text("Cargo *") },
                            isError = uiState.errorCargo != null,
                            supportingText = { uiState.errorCargo?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.telefono,
                            onValueChange = { viewModel.cambiarTelefono(it) },
                            label = { Text("Teléfono") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = uiState.errorTelefono != null,
                            supportingText = { uiState.errorTelefono?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.correo,
                            onValueChange = { viewModel.cambiarCorreo(it) },
                            label = { Text("Correo Electrónico") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = uiState.errorCorreo != null,
                            supportingText = { uiState.errorCorreo?.let { Text(it) } }
                        )
                    }
                },
                confirmButton = { Button(onClick = { viewModel.validarYGuardar() }) { Text("Guardar") } },
                dismissButton = { TextButton(onClick = { mostrarDialogo = false; viewModel.resetExito() }) { Text("Cancelar") } }
            )
        }
    }
}