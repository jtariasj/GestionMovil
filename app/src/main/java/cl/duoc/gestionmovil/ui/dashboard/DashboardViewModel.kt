package cl.duoc.gestionmovil.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.IndicadoresRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface IndicadoresUiState {
    data object Cargando : IndicadoresUiState
    data class Exito(
        val valorUf: Double,
        val valorDolar: Double
    ) : IndicadoresUiState
    data class Error(val mensaje: String) : IndicadoresUiState
}

class DashboardViewModel(
    private val repository: IndicadoresRepository = IndicadoresRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<IndicadoresUiState>(IndicadoresUiState.Cargando)
    val uiState: StateFlow<IndicadoresUiState> = _uiState.asStateFlow()

    init {
        cargarIndicadores()
    }

    fun cargarIndicadores() {
        viewModelScope.launch {
            _uiState.value = IndicadoresUiState.Cargando
            try {
                val respuesta = repository.obtenerIndicadores()
                val uf = respuesta.uf?.valor ?: 0.0
                val dolar = respuesta.dolar?.valor ?: 0.0
                _uiState.value = IndicadoresUiState.Exito(
                    valorUf = uf,
                    valorDolar = dolar
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = IndicadoresUiState.Error(
                    "No se pudieron cargar los indicadores económicos."
                )
            }
        }
    }
}