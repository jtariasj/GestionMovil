package cl.duoc.gestionmovil.ui.seguimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.SeguimientoRepository
import cl.duoc.gestionmovil.model.Seguimiento
import cl.duoc.gestionmovil.network.SeguimientoRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SeguimientoUiState {
    data object Cargando : SeguimientoUiState
    data class Exito(val seguimientos: List<Seguimiento>) : SeguimientoUiState
    data class Error(val mensaje: String) : SeguimientoUiState
}

class SeguimientoViewModel(
    private val seguimientoRepository: SeguimientoRepository,
    private val oportunidadId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeguimientoUiState>(SeguimientoUiState.Cargando)
    val uiState: StateFlow<SeguimientoUiState> = _uiState.asStateFlow()

    // Mantiene compatibilidad directa con SeguimientoScreen.kt
    private val _historial = MutableStateFlow<List<Seguimiento>>(emptyList())
    val historial: StateFlow<List<Seguimiento>> = _historial.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    init {
        cargarHistorial()
    }

    fun cargarHistorial() {
        viewModelScope.launch {
            _uiState.value = SeguimientoUiState.Cargando
            try {
                val lista = seguimientoRepository.obtenerSeguimientosPorOportunidadApi(oportunidadId)
                _historial.value = lista
                _uiState.value = SeguimientoUiState.Exito(lista)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = SeguimientoUiState.Error(
                    "No se pudo cargar el historial de seguimiento desde el servidor."
                )
            }
        }
    }

    fun agregarSeguimiento(
        descripcion: String,
        tipoGestion: String = "General",
        onDuplicado: () -> Unit = {}
    ) {
        if (_guardando.value) return

        val descTrim = descripcion.trim()
        if (descTrim.isBlank()) return

        val existe = _historial.value.any {
            it.descripcion.equals(descTrim, ignoreCase = true) &&
                    it.tipoGestion.equals(tipoGestion, ignoreCase = true)
        }
        if (existe) {
            onDuplicado()
            return
        }

        val solicitud = SeguimientoRequest(
            fecha = System.currentTimeMillis(),
            descripcion = descTrim,
            tipoGestion = tipoGestion,
            oportunidadId = oportunidadId
        )

        viewModelScope.launch {
            _guardando.value = true
            try {
                seguimientoRepository.crearSeguimientoApi(solicitud)
                // Recarga el historial desde Spring Boot tras registrar la gestión
                cargarHistorial()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = SeguimientoUiState.Error(
                    "No se pudo registrar el seguimiento: ${e.message}"
                )
            } finally {
                _guardando.value = false
            }
        }
    }
}

class SeguimientoViewModelFactory(
    private val seguimientoRepository: SeguimientoRepository,
    private val oportunidadId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeguimientoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeguimientoViewModel(seguimientoRepository, oportunidadId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}