package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.EstadoOportunidad
import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OportunidadDetailViewModel(
    private val oportunidadRepository: OportunidadRepository
) : ViewModel() {

    private val _oportunidad = MutableStateFlow<Oportunidad?>(null)
    val oportunidad: StateFlow<Oportunidad?> = _oportunidad.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarOportunidad(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _error.value = null
            try {
                _oportunidad.value = oportunidadRepository.obtenerPorId(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo cargar la oportunidad: ${e.message}"
            }
        }
    }

    fun eliminarOportunidad(onEliminado: () -> Unit) {
        val op = _oportunidad.value ?: return
        viewModelScope.launch {
            _error.value = null
            try {
                oportunidadRepository.eliminar(op)
                onEliminado()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo eliminar la oportunidad: ${e.message}"
            }
        }
    }

    fun reasignarOportunidad(nuevoResponsable: String) {
        val oportunidadActual = _oportunidad.value ?: return
        val oportunidadActualizada = oportunidadActual.copy(
            responsable = nuevoResponsable,
            fechaActualizacion = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _error.value = null
            try {
                oportunidadRepository.actualizar(oportunidadActualizada)
                _oportunidad.value = oportunidadActualizada
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo reasignar el responsable: ${e.message}"
            }
        }
    }

    fun actualizarEstado(nuevoEstado: EstadoOportunidad) {
        val oportunidadActual = _oportunidad.value ?: return
        val oportunidadActualizada = oportunidadActual.copy(
            estado = nuevoEstado,
            fechaActualizacion = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _error.value = null
            try {
                oportunidadRepository.actualizar(oportunidadActualizada)
                _oportunidad.value = oportunidadActualizada
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar el estado: ${e.message}"
            }
        }
    }
}

class OportunidadDetailViewModelFactory(
    private val oportunidadRepository: OportunidadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OportunidadDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OportunidadDetailViewModel(oportunidadRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}