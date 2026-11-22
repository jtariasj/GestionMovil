package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface OportunidadListUiState {
    data object Cargando : OportunidadListUiState
    data class Exito(val oportunidades: List<Oportunidad>) : OportunidadListUiState
    data class Error(val mensaje: String) : OportunidadListUiState
}

class OportunidadListViewModel(
    private val oportunidadRepository: OportunidadRepository,
    private val rolUsuario: String,
    private val nombreUsuario: String
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _estadoBase = MutableStateFlow<OportunidadListUiState>(OportunidadListUiState.Cargando)
    private var cargaJob: Job? = null

    // Combina el estado de red (Cargando/Exito/Error) con el buscador en tiempo real
    val uiState: StateFlow<OportunidadListUiState> = combine(
        _estadoBase,
        _searchQuery
    ) { estado, query ->
        if (estado is OportunidadListUiState.Exito && query.isNotBlank()) {
            estado.copy(
                oportunidades = estado.oportunidades.filter {
                    it.nombre.contains(query, ignoreCase = true)
                }
            )
        } else {
            estado
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OportunidadListUiState.Cargando
    )

    // Mantiene compatibilidad si tu OportunidadListScreen aún observa oportunidadesFiltradas
    val oportunidadesFiltradas: StateFlow<List<Oportunidad>> = combine(
        _estadoBase,
        _searchQuery
    ) { estado, query ->
        val lista = (estado as? OportunidadListUiState.Exito)?.oportunidades ?: emptyList()
        if (query.isBlank()) lista else lista.filter { it.nombre.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        cargarOportunidades()
    }

    fun cargarOportunidades() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            _estadoBase.value = OportunidadListUiState.Cargando
            try {
                val flujo = if (rolUsuario == "SUPERVISOR") {
                    oportunidadRepository.getAllOportunidades()
                } else {
                    oportunidadRepository.obtenerPorResponsable(nombreUsuario)
                }
                flujo.collect { lista ->
                    _estadoBase.value = OportunidadListUiState.Exito(lista)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _estadoBase.value = OportunidadListUiState.Error(
                    "No se pudieron cargar las oportunidades."
                )
            }
        }
    }

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