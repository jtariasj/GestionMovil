package cl.duoc.gestionmovil.ui.contacto

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.gestionmovil.model.Contacto

/**
 * Stateful Composable:
 * Administra el ViewModel y los side-effects.
 */
@Composable
fun ContactoScreen(
    viewModel: ContactoViewModel,
    onNavigateBack: () -> Unit
) {
    val contactos by viewModel.contactos.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            Toast.makeText(context, "Contacto creado exitosamente", Toast.LENGTH_SHORT).show()
            viewModel.resetExito()
        }
    }

    ContactoContent(
        contactos = contactos,
        uiState = uiState,
        onCambiarNombre = viewModel::cambiarNombre,
        onCambiarCargo = viewModel::cambiarCargo,
        onCambiarTelefono = viewModel::cambiarTelefono,
        onCambiarCorreo = viewModel::cambiarCorreo,
        onValidarYGuardar = viewModel::validarYGuardar,
        onResetExito = viewModel::resetExito,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Stateless Composable:
 * UI pura para visualizar y agregar contactos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoContent(
    contactos: List<Contacto>,
    uiState: ContactoUiState,
    onCambiarNombre: (String) -> Unit,
    onCambiarCargo: (String) -> Unit,
    onCambiarTelefono: (String) -> Unit,
    onCambiarCorreo: (String) -> Unit,
    onValidarYGuardar: () -> Unit,
    onResetExito: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            mostrarDialogo = false
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
            modifier = modifier
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
                            onValueChange = onCambiarNombre,
                            label = { Text("Nombre Completo *") },
                            isError = uiState.errorNombre != null,
                            supportingText = { uiState.errorNombre?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.cargo,
                            onValueChange = onCambiarCargo,
                            label = { Text("Cargo *") },
                            isError = uiState.errorCargo != null,
                            supportingText = { uiState.errorCargo?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.telefono,
                            onValueChange = onCambiarTelefono,
                            label = { Text("Teléfono") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = uiState.errorTelefono != null,
                            supportingText = { uiState.errorTelefono?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.correo,
                            onValueChange = onCambiarCorreo,
                            label = { Text("Correo Electrónico") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = uiState.errorCorreo != null,
                            supportingText = { uiState.errorCorreo?.let { Text(it) } }
                        )
                    }
                },
                confirmButton = { Button(onClick = onValidarYGuardar) { Text("Guardar") } },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarDialogo = false
                        onResetExito()
                    }) { Text("Cancelar") }
                }
            )
        }
    }
}
