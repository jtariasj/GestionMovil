package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadDao
import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class OportunidadListViewModel(private val oportunidadDao: OportunidadDao) : ViewModel() {

    // Texto de búsqueda que el usuario escribirá
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Combinamos la lista de la base de datos con el texto de búsqueda
    val oportunidadesFiltradas: StateFlow<List<Oportunidad>> = combine(
        oportunidadDao.getAllOportunidades(), // Necesitas tener esta función en OportunidadDao
        _searchQuery
    ) { lista, query ->
        if (query.isBlank()) {
            lista
        } else {
            // Filtramos por el nombre del evento/oportunidad
            lista.filter { it.nombre.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}

// Factory para poder inicializarlo con el DAO
class OportunidadListViewModelFactory(private val oportunidadDao: OportunidadDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OportunidadListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OportunidadListViewModel(oportunidadDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}