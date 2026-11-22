package cl.duoc.gestionmovil.ui.oportunidad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.gestionmovil.model.Oportunidad

@Composable
fun OportunidadCard(
    oportunidad: Oportunidad,
    onClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(oportunidad.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = oportunidad.nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Estado: ${oportunidad.estado.etiqueta}", color = MaterialTheme.colorScheme.primary)
        }
    }
}

/**
 * Stateful Composable (Screen Route):
 * Conecta con el ViewModel y recolecta el estado reactivo.
 */
@Composable
fun OportunidadListScreen(
    viewModel: OportunidadListViewModel,
    onItemClick: (Int) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // Recarga la lista desde Spring Boot al volver desde el formulario (EA3.2)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarOportunidades()
    }

    OportunidadListContent(
        searchQuery = searchQuery,
        estado = estado,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onReintentar = viewModel::cargarOportunidades,
        onItemClick = onItemClick
    )
}

/**
 * Stateless Composable (Content View):
 * Muestra el contenido desacoplado de dependencias de ViewModel.
 */
@Composable
fun OportunidadListContent(
    searchQuery: String,
    estado: OportunidadListUiState,
    onSearchQueryChange: (String) -> Unit,
    onReintentar: () -> Unit,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Oportunidades",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barra de Búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar oportunidad...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Manejo de estados de red (EA3.1)
        when (val actual = estado) {
            is OportunidadListUiState.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is OportunidadListUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = actual.mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onReintentar) {
                        Text("Reintentar")
                    }
                }
            }

            is OportunidadListUiState.Exito -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (actual.oportunidades.isEmpty()) {
                        item {
                            Text(
                                text = "No hay oportunidades para mostrar.",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(
                            items = actual.oportunidades,
                            key = { it.id }
                        ) { oportunidad ->
                            Box(modifier = Modifier.animateItem()) {
                                OportunidadCard(
                                    oportunidad = oportunidad,
                                    onClick = onItemClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}