package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.data.UsuarioRepository
import cl.duoc.gestionmovil.model.EstadoOportunidad
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.RolUsuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OportunidadDetailViewModel(
    private val oportunidadRepository: OportunidadRepository,
    private val usuarioRepository: UsuarioRepository? = null
) : ViewModel() {

    private val _oportunidad = MutableStateFlow<Oportunidad?>(null)
    val oportunidad: StateFlow<Oportunidad?> = _oportunidad.asStateFlow()

    private val _ejecutivos = MutableStateFlow(
        listOf("Ejecutivo Uno", "Ejecutivo Dos", "Ejecutivo Tres")
    )
    val ejecutivos: StateFlow<List<String>> = _ejecutivos.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarEjecutivos()
    }

    private fun cargarEjecutivos() {
        val repo = usuarioRepository ?: return
        viewModelScope.launch {
            try {
                repo.getAllUsuarios().collect { usuarios ->
                    val listaEjecutivos = usuarios
                        .filter { it.rol == RolUsuario.EJECUTIVO }
                        .map { it.nombre }
                        .distinct()
                    if (listaEjecutivos.isNotEmpty()) {
                        _ejecutivos.value = listaEjecutivos
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Mantiene los ejecutivos por defecto si falla la consulta
            }
        }
    }

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
    private val oportunidadRepository: OportunidadRepository,
    private val usuarioRepository: UsuarioRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OportunidadDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OportunidadDetailViewModel(oportunidadRepository, usuarioRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}