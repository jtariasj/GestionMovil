package cl.duoc.gestionmovil.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

// Mensaje bajo el campo, con animación de entrada/salida
@Composable
private fun MensajeError(visible: Boolean, mensaje: String) {
    AnimatedVisibility(visible = visible) {
        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
    }
}

// Campo de texto con ícono, retroalimentación visual de error y mensaje
@Composable
fun CampoFormulario(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    hayError: Boolean,
    mensajeError: String,
    modifier: Modifier = Modifier,
    lineasMinimas: Int = 1,
    opcional: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        label = { Text(if (opcional) "$etiqueta (opcional)" else etiqueta) },
        leadingIcon = { Icon(imageVector = icono, contentDescription = null) },
        trailingIcon = {
            if (hayError) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        isError = hayError,
        supportingText = { MensajeError(hayError, mensajeError) },
        minLines = lineasMinimas,
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

// Lista desplegable genérica (sirve para cualquier enum del modelo)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CampoDesplegable(
    etiqueta: String,
    icono: ImageVector,
    opciones: List<T>,
    seleccion: T?,
    textoOpcion: (T) -> String,
    onSeleccionar: (T) -> Unit,
    hayError: Boolean,
    mensajeError: String,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = seleccion?.let(textoOpcion) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            leadingIcon = { Icon(imageVector = icono, contentDescription = null) },
            trailingIcon = {
                if (hayError) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Campo con error",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                }
            },
            isError = hayError,
            supportingText = { MensajeError(hayError, mensajeError) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoOpcion(opcion)) },
                    onClick = {
                        onSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
