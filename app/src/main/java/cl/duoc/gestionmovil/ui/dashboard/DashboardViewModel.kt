package cl.duoc.gestionmovil.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.network.ClimaApiService
import cl.duoc.gestionmovil.network.IndicadoresApiService
import cl.duoc.gestionmovil.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface IndicadoresUiState {
    data object Cargando : IndicadoresUiState
    data class Exito(
        val valorDolar: String,
        val temperatura: String,
        val viento: String,
        val descripcionClima: String
    ) : IndicadoresUiState
    data class Error(val mensaje: String) : IndicadoresUiState
}

class DashboardViewModel(
    private val indicadoresApi: IndicadoresApiService = RetrofitClient.indicadoresApi,
    private val climaApi: ClimaApiService = RetrofitClient.climaApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<IndicadoresUiState>(IndicadoresUiState.Cargando)
    val uiState: StateFlow<IndicadoresUiState> = _uiState.asStateFlow()

    init {
        cargarIndicadores()
    }

    fun cargarIndicadores() {
        _uiState.value = IndicadoresUiState.Cargando
        viewModelScope.launch {
            try {
                val indicadoresDeferred = async { indicadoresApi.obtenerIndicadores() }
                val climaDeferred = async { climaApi.obtenerClimaActual() }

                val indicadores = indicadoresDeferred.await()
                val clima = climaDeferred.await().currentWeather

                val dolar = indicadores.dolar?.valor?.toString() ?: "N/D"
                val temp = clima?.temperature?.toString() ?: "N/D"
                val viento = clima?.windspeed?.toString() ?: "N/D"
                val desc = interpretarCodigoClima(clima?.weathercode)

                _uiState.value = IndicadoresUiState.Exito(
                    valorDolar = dolar,
                    temperatura = temp,
                    viento = viento,
                    descripcionClima = desc
                )
            } catch (e: Exception) {
                _uiState.value = IndicadoresUiState.Error(
                    "No se pudo conectar con los servicios externos (${e.localizedMessage ?: "sin red"})"
                )
            }
        }
    }

    private fun interpretarCodigoClima(code: Int?): String = when (code) {
        0 -> "Despejado"
        1, 2, 3 -> "Parcialmente nublado"
        45, 48 -> "Neblina"
        51, 53, 55, 61, 63, 65 -> "Lluvia"
        else -> "Estable"
    }
}