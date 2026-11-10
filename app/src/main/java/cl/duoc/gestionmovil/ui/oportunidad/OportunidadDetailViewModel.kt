package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.EstadoOportunidad
import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OportunidadDetailViewModel(private val oportunidadRepository: OportunidadRepository) : ViewModel() {

    private val _oportunidad = MutableStateFlow<Oportunidad?>(null)
    val oportunidad: StateFlow<Oportunidad?> = _oportunidad.asStateFlow()

    fun cargarOportunidad(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val op = oportunidadRepository.obtenerPorId(id)
            _oportunidad.value = op
        }
    }

    fun eliminarOportunidad(onEliminado: () -> Unit) {
        val op = _oportunidad.value
        if (op != null) {
            viewModelScope.launch(Dispatchers.IO) {
                oportunidadRepository.eliminar(op)
                viewModelScope.launch(Dispatchers.Main) {
                    onEliminado()
                }
            }
        }
    }

    fun reasignarOportunidad(nuevoResponsable: String) {
        val oportunidadActual = oportunidad.value ?: return

        val oportunidadActualizada = oportunidadActual.copy(
            responsable = nuevoResponsable,
            fechaActualizacion = System.currentTimeMillis()
        )

        viewModelScope.launch {
            oportunidadRepository.actualizar(oportunidadActualizada)
            _oportunidad.value = oportunidadActualizada
        }
    }

    fun actualizarEstado(nuevoEstado: EstadoOportunidad) {
        val oportunidadActual = oportunidad.value ?: return

        val oportunidadActualizada = oportunidadActual.copy(
            estado = nuevoEstado,
            fechaActualizacion = System.currentTimeMillis()
        )

        viewModelScope.launch {
            oportunidadRepository.actualizar(oportunidadActualizada)
            _oportunidad.value = oportunidadActualizada
        }
    }
}

class OportunidadDetailViewModelFactory(private val oportunidadRepository: OportunidadRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OportunidadDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OportunidadDetailViewModel(oportunidadRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
