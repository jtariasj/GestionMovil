package cl.duoc.gestionmovil.ui.oportunidad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadListScreen(
    viewModel: OportunidadListViewModel,
    onItemClick: (Int) -> Unit // Para navegar al detalle
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val oportunidades by viewModel.oportunidadesFiltradas.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "Oportunidades",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barra de Búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar oportunidad...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Oportunidades
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (oportunidades.isEmpty()) {
                item {
                    Text("No hay oportunidades para mostrar.", modifier = Modifier.padding(16.dp))
                }
            } else {
                items(oportunidades) { oportunidad ->
                    OportunidadCard(oportunidad, onItemClick)
                }
            }
        }
    }
}

@Composable
fun OportunidadCard(
    oportunidad: cl.duoc.gestionmovil.model.Oportunidad,
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
            // Usamos colores semánticos como pide el checklist (IE: MaterialTheme.colorScheme.primary)
            Text(text = "Estado: ${oportunidad.estado}", color = MaterialTheme.colorScheme.primary)
        }
    }
}