package cl.duoc.gestionmovil.ui.oportunidad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    viewModel: OportunidadFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Confirmación visual al guardar
    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            snackbarHostState.showSnackbar("Oportunidad registrada correctamente")
            viewModel.mensajeMostrado()
        }
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

            CampoFormulario(
                valor = uiState.fechaDeteccion,
                onValorCambia = viewModel::cambiarFechaDeteccion,
                etiqueta = "Fecha de detección (dd/mm/aaaa)",
                icono = Icons.Default.CalendarMonth,
                hayError = uiState.errorFecha,
                mensajeError = "Ingrese una fecha válida (dd/mm/aaaa), no futura",
                modifier = Modifier.fillMaxWidth()
            )

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
