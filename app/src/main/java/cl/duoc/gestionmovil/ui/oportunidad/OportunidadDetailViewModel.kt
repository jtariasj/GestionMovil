package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadDao
import cl.duoc.gestionmovil.model.Oportunidad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OportunidadDetailViewModel(private val oportunidadDao: OportunidadDao) : ViewModel() {

    private val _oportunidad = MutableStateFlow<Oportunidad?>(null)
    val oportunidad: StateFlow<Oportunidad?> = _oportunidad.asStateFlow()

    // Cargar la oportunidad desde Room
    fun cargarOportunidad(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val op = oportunidadDao.obtenerPorId(id) // Asegúrate de tener esta función en tu DAO
            _oportunidad.value = op
        }
    }

    // Borrar la oportunidad
    fun eliminarOportunidad(onEliminado: () -> Unit) {
        val op = _oportunidad.value
        if (op != null) {
            viewModelScope.launch(Dispatchers.IO) {
                oportunidadDao.delete(op)
                viewModelScope.launch(Dispatchers.Main) {
                    onEliminado()
                }
            }
        }
    }

    fun reasignarOportunidad(nuevoResponsable: String) {
        val oportunidadActual = oportunidad.value ?: return

        // Creamos una copia de la oportunidad con el nuevo responsable
        val oportunidadActualizada = oportunidadActual.copy(
            responsable = nuevoResponsable,
            fechaActualizacion = System.currentTimeMillis()
        )

        viewModelScope.launch {
            // Actualizamos en la Base de Datos
            oportunidadDao.actualizar(oportunidadActualizada)
            // Actualizamos el estado de la UI para que se refleje de inmediato
            _oportunidad.value = oportunidadActualizada
        }
    }

    fun actualizarEstado(nuevoEstado: cl.duoc.gestionmovil.model.EstadoOportunidad) {
        val oportunidadActual = oportunidad.value ?: return

        // Creamos una copia con el nuevo estado
        val oportunidadActualizada = oportunidadActual.copy(
            estado = nuevoEstado,
            fechaActualizacion = System.currentTimeMillis()
        )

        viewModelScope.launch {
            oportunidadDao.actualizar(oportunidadActualizada)
            // Actualizamos la UI inmediatamente
            _oportunidad.value = oportunidadActualizada
        }
    }
}

// Factory para inyectar el DAO
    class OportunidadDetailViewModelFactory(private val oportunidadDao: OportunidadDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OportunidadDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OportunidadDetailViewModel(oportunidadDao) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }