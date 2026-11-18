package cl.duoc.gestionmovil.ui.oportunidad

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.TipoOportunidad
import cl.duoc.gestionmovil.ui.components.CampoDesplegable
import cl.duoc.gestionmovil.ui.components.CampoFormulario
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

/**
 * Stateful Composable:
 * Maneja la recolección de estado, toasts y navegación.
 */
@Composable
fun OportunidadFormScreen(
    modifier: Modifier = Modifier,
    viewModel: OportunidadFormViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            Toast.makeText(context, "Creado exitosamente", Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
            onNavigateBack()
        }
    }

    OportunidadFormContent(
        modifier = modifier,
        uiState = uiState,
        onCambiarNombre = viewModel::cambiarNombre,
        onCambiarTipo = viewModel::cambiarTipo,
        onCambiarDescripcion = viewModel::cambiarDescripcion,
        onCambiarFuente = viewModel::cambiarFuente,
        onCambiarFechaDeteccion = viewModel::cambiarFechaDeteccion,
        onCambiarFechaTermino = viewModel::cambiarFechaTermino,
        onCambiarCosto = viewModel::cambiarCosto,
        onCambiarResponsable = viewModel::cambiarResponsable,
        onCambiarPrioridad = viewModel::cambiarPrioridad,
        onCambiarPotencial = viewModel::cambiarPotencial,
        onCambiarObservaciones = viewModel::cambiarObservaciones,
        onCambiarFotoPath = viewModel::cambiarFotoPath,
        onGuardar = viewModel::guardar,
        onDismissDuplicado = viewModel::mensajeMostrado
    )
}

/**
 * Stateless Composable:
 * Recibe únicamente el estado del formulario y los callbacks de modificación/guardado.
 */
@Composable
fun OportunidadFormContent(
    uiState: OportunidadFormUiState,
    onCambiarNombre: (String) -> Unit,
    onCambiarTipo: (TipoOportunidad) -> Unit,
    onCambiarDescripcion: (String) -> Unit,
    onCambiarFuente: (String) -> Unit,
    onCambiarFechaDeteccion: (String) -> Unit,
    onCambiarFechaTermino: (String) -> Unit,
    onCambiarCosto: (String) -> Unit,
    onCambiarResponsable: (String) -> Unit,
    onCambiarPrioridad: (Prioridad) -> Unit,
    onCambiarPotencial: (PotencialComercial) -> Unit,
    onCambiarObservaciones: (String) -> Unit,
    onCambiarFotoPath: (String?) -> Unit,
    onGuardar: () -> Unit,
    onDismissDuplicado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendario = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, anio, mes, dia ->
            val fechaFormateada = String.format("%02d/%02d/%04d", dia, mes + 1, anio)
            onCambiarFechaDeteccion(fechaFormateada)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    val datePickerTermino = DatePickerDialog(
        context,
        { _, anio, mes, dia ->
            val fechaFormateada = String.format("%02d/%02d/%04d", dia, mes + 1, anio)
            onCambiarFechaTermino(fechaFormateada)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try {
                val archivo = File(context.filesDir, "evidencia_${System.currentTimeMillis()}.jpg")
                FileOutputStream(archivo).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                onCambiarFotoPath(archivo.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Nueva oportunidad",
                style = MaterialTheme.typography.headlineMedium
            )

            CampoFormulario(
                valor = uiState.nombre,
                onValorCambia = onCambiarNombre,
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
                onSeleccionar = onCambiarTipo,
                hayError = uiState.errorTipo,
                mensajeError = "Seleccione un tipo de oportunidad",
                modifier = Modifier.fillMaxWidth()
            )

            CampoFormulario(
                valor = uiState.descripcion,
                onValorCambia = onCambiarDescripcion,
                etiqueta = "Descripción",
                icono = Icons.Default.Description,
                hayError = uiState.errorDescripcion,
                mensajeError = "La descripción es obligatoria",
                lineasMinimas = 2,
                modifier = Modifier.fillMaxWidth()
            )

            CampoFormulario(
                valor = uiState.fuente,
                onValorCambia = onCambiarFuente,
                etiqueta = "Fuente de información",
                icono = Icons.Default.Public,
                hayError = uiState.errorFuente,
                mensajeError = "Indique la fuente (ej: sitio web, red social)",
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.fechaDeteccion,
                onValueChange = { },
                label = { Text("Fecha de detección (dd/mm/aaaa)") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                readOnly = true,
                isError = uiState.errorFecha,
                supportingText = {
                    if (uiState.errorFecha) {
                        Text(
                            text = "Ingrese una fecha válida (dd/mm/aaaa), no futura",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (uiState.errorFecha) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            OutlinedTextField(
                value = uiState.fechaTermino,
                onValueChange = { },
                label = { Text("Fecha de término (dd/mm/aaaa)") },
                leadingIcon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null) },
                readOnly = true,
                isError = uiState.errorFechaTermino != null,
                supportingText = {
                    if (uiState.errorFechaTermino != null) {
                        Text(text = uiState.errorFechaTermino!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerTermino.show() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (uiState.errorFechaTermino != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            OutlinedTextField(
                value = uiState.costo,
                onValueChange = onCambiarCosto,
                label = { Text("Costo referencial (CLP)") },
                leadingIcon = { Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.errorCosto != null,
                supportingText = {
                    if (uiState.errorCosto != null) {
                        Text(text = uiState.errorCosto!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            CampoFormulario(
                valor = uiState.responsable,
                onValorCambia = onCambiarResponsable,
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
                onSeleccionar = onCambiarPrioridad,
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
                onSeleccionar = onCambiarPotencial,
                hayError = uiState.errorPotencial,
                mensajeError = "Seleccione el potencial comercial",
                modifier = Modifier.fillMaxWidth()
            )

            CampoFormulario(
                valor = uiState.observaciones,
                onValorCambia = onCambiarObservaciones,
                etiqueta = "Observaciones",
                icono = Icons.AutoMirrored.Filled.Notes,
                hayError = false,
                mensajeError = "",
                lineasMinimas = 2,
                opcional = true,
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .animateContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Evidencia fotográfica (Cámara nativa)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(if (uiState.fotoPath == null) "Tomar Foto" else "Cambiar Foto")
                    }

                    uiState.fotoPath?.let { path ->
                        val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
                        if (bitmap != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Evidencia de la oportunidad",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar oportunidad")
            }
        }

        if (uiState.errorDuplicado != null) {
            AlertDialog(
                onDismissRequest = onDismissDuplicado,
                title = { Text("Oportunidad duplicada") },
                text = { Text(uiState.errorDuplicado ?: "La oportunidad ya esta creada.") },
                confirmButton = {
                    Button(onClick = onDismissDuplicado) {
                        Text("Aceptar")
                    }
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
