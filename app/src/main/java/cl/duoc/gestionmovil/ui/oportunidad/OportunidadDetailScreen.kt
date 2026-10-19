package cl.duoc.gestionmovil.ui.oportunidad

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.gestionmovil.model.EstadoOportunidad
import cl.duoc.gestionmovil.util.formatearFecha
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadDetailScreen(
    oportunidadId: Int,
    rolUsuario: String,
    viewModel: OportunidadDetailViewModel,
    onNavigateBack: () -> Unit,
    onIrASeguimiento: (Int) -> Unit
) {
    val context = LocalContext.current

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
                    IconButton(onClick = { viewModel.eliminarOportunidad(onNavigateBack) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (oportunidad == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val item = oportunidad!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta Principal de Información
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(item.tipo.etiqueta) }
                            )
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Prioridad: ${item.prioridad.etiqueta}") }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Fecha de Detección",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = formatearFecha(item.fechaDeteccion),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Responsable",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.responsable,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Estado Actual",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.estado.etiqueta,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Potencial Comercial",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.potencialComercial.etiqueta,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Fuente de Información",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.fuenteInformacion,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (item.observaciones.isNotBlank()) {
                            Text(
                                text = "Observaciones",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = item.observaciones,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Recurso Nativo 1: Evidencia Fotográfica (Cámara)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Evidencia Fotográfica",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val path = item.fotoPath
                        if (!path.isNullOrBlank() && File(path).exists()) {
                            val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Foto de la oportunidad",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = "No fue posible decodificar la imagen.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } else {
                            Text(
                                text = "Esta oportunidad no tiene evidencia fotográfica adjunta.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Recurso Nativo 2: Calendario del Dispositivo
                Button(
                    onClick = {
                        val inicioMillis = if (item.fechaDeteccion > 0) item.fechaDeteccion else System.currentTimeMillis()
                        val finMillis = inicioMillis + (2 * 60 * 60 * 1000)

                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, "Activación: ${item.nombre}")
                            putExtra(CalendarContract.Events.DESCRIPTION, item.descripcion)
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicioMillis)
                            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, finMillis)
                            putExtra(CalendarContract.Events.ALL_DAY, false)
                        }

                        try {
                            context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            try {
                                val fallbackIntent = Intent(Intent.ACTION_EDIT).apply {
                                    type = "vnd.android.cursor.item/event"
                                    putExtra(CalendarContract.Events.TITLE, "Activación: ${item.nombre}")
                                    putExtra(CalendarContract.Events.DESCRIPTION, item.descripcion)
                                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicioMillis)
                                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, finMillis)
                                }
                                context.startActivity(fallbackIntent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Abre la app de Calendario en tu dispositivo para configurarla.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendario",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Agendar en Calendario Nativo")
                }

                // Botón para ir a la Bitácora de Seguimientos
                Button(
                    onClick = { onIrASeguimiento(oportunidadId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Ver Historial de Seguimientos (Bitácora)")
                }

                // SECCIÓN EXCLUSIVA PARA EL SUPERVISOR
                if (rolUsuario.equals("Supervisor", ignoreCase = true)) {
                    var expandido by remember { mutableStateOf(false) }
                    val ejecutivos = listOf("Carlos Ruiz", "María Soto", "Pedro Gómez")
                    var ejecutivoSeleccionado by remember(item.responsable) {
                        mutableStateOf(item.responsable)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Panel de Supervisor: Reasignar",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )

                            ExposedDropdownMenuBox(
                                expanded = expandido,
                                onExpandedChange = { expandido = !expandido }
                            ) {
                                OutlinedTextField(
                                    value = ejecutivoSeleccionado,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Ejecutivo asignado") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                                    },
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = expandido,
                                    onDismissRequest = { expandido = false }
                                ) {
                                    ejecutivos.forEach { nombre ->
                                        DropdownMenuItem(
                                            text = { Text(nombre) },
                                            onClick = {
                                                ejecutivoSeleccionado = nombre
                                                expandido = false
                                                viewModel.reasignarOportunidad(nombre)
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Resolución de la Oportunidad:",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.actualizarEstado(EstadoOportunidad.PARTICIPACION_CONFIRMADA)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Confirmar")
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.actualizarEstado(EstadoOportunidad.DESCARTADA)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Descartar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}