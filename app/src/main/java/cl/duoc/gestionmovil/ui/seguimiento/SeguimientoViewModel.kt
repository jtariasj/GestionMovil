package cl.duoc.gestionmovil.ui.seguimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.SeguimientoRepository
import cl.duoc.gestionmovil.model.Seguimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SeguimientoViewModel(
    private val seguimientoRepository: SeguimientoRepository,
    private val oportunidadId: Int
) : ViewModel() {

    private val _historial = MutableStateFlow<List<Seguimiento>>(emptyList())
    val historial: StateFlow<List<Seguimiento>> = _historial.asStateFlow()

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            seguimientoRepository.getSeguimientosPorOportunidad(oportunidadId).collect { lista ->
                _historial.value = lista
            }
        }
    }

    fun agregarSeguimiento(descripcion: String, tipoGestion: String = "General", onDuplicado: () -> Unit = {}) {
        val descTrim = descripcion.trim()
        val existe = _historial.value.any { it.descripcion.equals(descTrim, ignoreCase = true) && it.tipoGestion.equals(tipoGestion, ignoreCase = true) }
        if (existe) {
            onDuplicado()
            return
        }
        viewModelScope.launch {
            val nuevoSeguimiento = Seguimiento(
                fecha = System.currentTimeMillis(),
                descripcion = descTrim,
                tipoGestion = tipoGestion,
                oportunidadId = oportunidadId
            )
            seguimientoRepository.guardar(nuevoSeguimiento)
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
