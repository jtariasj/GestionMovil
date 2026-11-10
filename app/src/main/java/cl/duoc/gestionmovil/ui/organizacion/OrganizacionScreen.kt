package cl.duoc.gestionmovil.ui.organizacion

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.gestionmovil.model.Organizacion

/**
 * Stateful Composable:
 * Administra ViewModel, estado de guardado y navegación.
 */
@Composable
fun OrganizacionScreen(
    viewModel: OrganizacionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToContactos: (Int) -> Unit
) {
    val organizaciones by viewModel.organizaciones.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            Toast.makeText(context, "Organización creada exitosamente", Toast.LENGTH_SHORT).show()
            viewModel.resetExito()
        }
    }

    OrganizacionContent(
        organizaciones = organizaciones,
        uiState = uiState,
        onCambiarNombre = viewModel::cambiarNombre,
        onCambiarRut = viewModel::cambiarRut,
        onCambiarDireccion = viewModel::cambiarDireccion,
        onValidarYGuardar = viewModel::validarYGuardar,
        onResetExito = viewModel::resetExito,
        onNavigateBack = onNavigateBack,
        onNavigateToContactos = onNavigateToContactos
    )
}

/**
 * Stateless Composable:
 * UI pura para listar y crear organizaciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionContent(
    organizaciones: List<Organizacion>,
    uiState: OrganizacionUiState,
    onCambiarNombre: (String) -> Unit,
    onCambiarRut: (String) -> Unit,
    onCambiarDireccion: (String) -> Unit,
    onValidarYGuardar: () -> Unit,
    onResetExito: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToContactos: (Int) -> Unit,
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
                title = { Text("Organizaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar Organización") }
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
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
                            onValueChange = onCambiarNombre,
                            label = { Text("Nombre Empresa *") },
                            isError = uiState.errorNombre != null,
                            supportingText = { uiState.errorNombre?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.rut,
                            onValueChange = onCambiarRut,
                            label = { Text("RUT *") },
                            isError = uiState.errorRut != null,
                            supportingText = { uiState.errorRut?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = uiState.direccion,
                            onValueChange = onCambiarDireccion,
                            label = { Text("Dirección") }
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
