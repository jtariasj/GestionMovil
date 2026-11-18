package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class OportunidadListViewModel(
    private val oportunidadRepository: OportunidadRepository,
    private val rolUsuario: String,
    private val nombreUsuario: String
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // 1. Elegimos qué lista traer según el rol
    private val flujoBaseDatos = if (rolUsuario == "SUPERVISOR") {
        oportunidadRepository.getAllOportunidades()
    } else {
        oportunidadRepository.obtenerPorResponsable(nombreUsuario)
    }

    // 2. Combinamos esa lista con el buscador
    val oportunidadesFiltradas: StateFlow<List<Oportunidad>> = combine(
        flujoBaseDatos,
        _searchQuery
    ) { lista, query ->
        if (query.isBlank()) {
            lista
        } else {
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

class OportunidadListViewModelFactory(
    private val oportunidadRepository: OportunidadRepository,
    private val rolUsuario: String,
    private val nombreUsuario: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OportunidadListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OportunidadListViewModel(oportunidadRepository, rolUsuario, nombreUsuario) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
