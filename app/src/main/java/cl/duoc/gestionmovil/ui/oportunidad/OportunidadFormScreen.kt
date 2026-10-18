package cl.duoc.gestionmovil.ui.oportunidad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.TipoOportunidad
import cl.duoc.gestionmovil.ui.components.CampoDesplegable
import cl.duoc.gestionmovil.ui.components.CampoFormulario

@Composable
fun OportunidadFormScreen(
    modifier: Modifier = Modifier,
    viewModel: OportunidadFormViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendario = java.util.Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, anio, mes, dia ->
            // Cuando el usuario elige la fecha, la formateamos y la mandamos al ViewModel
            val fechaFormateada = String.format("%02d/%02d/%04d", dia, mes + 1, anio)
            viewModel.cambiarFechaDeteccion(fechaFormateada)
        },
        calendario.get(java.util.Calendar.YEAR),
        calendario.get(java.util.Calendar.MONTH),
        calendario.get(java.util.Calendar.DAY_OF_MONTH)
    )

    // Confirmación visual al guardar
    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            snackbarHostState.showSnackbar("Oportunidad registrada correctamente")
            viewModel.mensajeMostrado()
            // AGREGA ESTA LÍNEA AQUÍ ABAJO:
            onNavigateBack()
        }
    }

    // Variable para guardar la foto tomada en la pantalla
    var fotoEvidencia by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf<android.graphics.Bitmap?>(null)
    }

    // Lanzador de la cámara nativa
    val cameraLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // Cuando el usuario toma la foto y acepta, se guarda aquí
        fotoEvidencia = bitmap
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nueva oportunidad",
                style = MaterialTheme.typography.headlineMedium
            )

            CampoFormulario(
                valor = uiState.nombre,
                onValorCambia = viewModel::cambiarNombre,
                etiqueta = "Nombre de la oportunidad",
                icono = Icons.Default.Edit,
                hayError = uiState.errorNombre,
                mensajeError = "Ingrese un nombre de al menos 3 caracteres",
                modifier = Modifier.fillMaxWidth()
            )

            CampoDesplegable(
                etiqueta = "Tipo de oportunidad",
                icono = Icons.Default.Category,
                opciones = TipoOportunidad.entries,
                seleccion = uiState.tipo,
                textoOpcion = { it.etiqueta },
                onSeleccionar = viewModel::cambiarTipo,
                hayError = uiState.errorTipo,
                mensajeError = "Seleccione un tipo de oportunidad",
                modifier = Modifier.fillMaxWidth()
            )

            CampoFormulario(
                valor = uiState.descripcion,
                onValorCambia = viewModel::cambiarDescripcion,
                etiqueta = "Descripción",
                icono = Icons.Default.Description,
                hayError = uiState.errorDescripcion,
                mensajeError = "La descripción es obligatoria",
                lineasMinimas = 2,
                modifier = Modifier.fillMaxWidth()
            )

            CampoFormulario(
                valor = uiState.fuente,
                onValorCambia = viewModel::cambiarFuente,
                etiqueta = "Fuente de información",
                icono = Icons.Default.Public,
                hayError = uiState.errorFuente,
                mensajeError = "Indique la fuente (ej: sitio web, red social)",
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de fecha con Calendario Nativo
            androidx.compose.material3.OutlinedTextField(
                value = uiState.fechaDeteccion,
                onValueChange = { }, // No hacemos nada aquí porque el calendario se encarga
                label = { Text("Fecha de detección (dd/mm/aaaa)") },
                leadingIcon = { androidx.compose.material3.Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                readOnly = true, // Evita que se abra el teclado
                isError = uiState.errorFecha,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }, // Abre el calendario al tocar
                enabled = false, // Truco visual para que el clickable del Modifier funcione mejor en Compose
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (uiState.errorFecha) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            if (uiState.errorFecha) {
                Text(
                    text = "Ingrese una fecha válida (dd/mm/aaaa), no futura",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            CampoFormulario(
                valor = uiState.responsable,
                onValorCambia = viewModel::cambiarResponsable,
                etiqueta = "Responsable interno",
                icono = Icons.Default.Person,
                hayError = uiState.errorResponsable,
                mensajeError = "El responsable es obligatorio",
                modifier = Modifier.fillMaxWidth()
            )

            CampoDesplegable(
                etiqueta = "Prioridad",
                icono = Icons.Default.Flag,
                opciones = Prioridad.entries,
                seleccion = uiState.prioridad,
                textoOpcion = { it.etiqueta },
                onSeleccionar = viewModel::cambiarPrioridad,
                hayError = uiState.errorPrioridad,
                mensajeError = "Seleccione una prioridad",
                modifier = Modifier.fillMaxWidth()
            )

            CampoDesplegable(
                etiqueta = "Potencial comercial",
                icono = Icons.AutoMirrored.Filled.TrendingUp,
                opciones = PotencialComercial.entries,
                seleccion = uiState.potencial,
                textoOpcion = { it.etiqueta },
                onSeleccionar = viewModel::cambiarPotencial,
                hayError = uiState.errorPotencial,
                mensajeError = "Seleccione el potencial comercial",
                modifier = Modifier.fillMaxWidth()
            )

            CampoFormulario(
                valor = uiState.observaciones,
                onValorCambia = viewModel::cambiarObservaciones,
                etiqueta = "Observaciones",
                icono = Icons.AutoMirrored.Filled.Notes,
                hayError = false,
                mensajeError = "",
                lineasMinimas = 2,
                opcional = true,
                modifier = Modifier.fillMaxWidth()
            )
            // Sección de Evidencia Fotográfica (Cámara)
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Evidencia fotográfica (Opcional)", style = MaterialTheme.typography.titleSmall)

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Tomar Foto")
                    }

                    // Si hay una foto tomada, la mostramos
                    fotoEvidencia?.let { bitmap ->
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.foundation.Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Evidencia de la oportunidad",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.guardar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar oportunidad")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
