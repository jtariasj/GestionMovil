package cl.duoc.gestionmovil.ui.organizacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OrganizacionRepository
import cl.duoc.gestionmovil.model.Organizacion
import cl.duoc.gestionmovil.util.validarRut
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface OrganizacionListUiState {
    data object Cargando : OrganizacionListUiState
    data class Exito(val organizaciones: List<Organizacion>) : OrganizacionListUiState
    data class Error(val mensaje: String) : OrganizacionListUiState
}

class OrganizacionViewModel(
    private val organizacionRepository: OrganizacionRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow<OrganizacionListUiState>(OrganizacionListUiState.Cargando)
    val listUiState: StateFlow<OrganizacionListUiState> = _listUiState.asStateFlow()

    // Mantiene compatibilidad directa con OrganizacionScreen.kt
    private val _organizaciones = MutableStateFlow<List<Organizacion>>(emptyList())
    val organizaciones: StateFlow<List<Organizacion>> = _organizaciones.asStateFlow()

    private val _uiState = MutableStateFlow(OrganizacionUiState())
    val uiState: StateFlow<OrganizacionUiState> = _uiState.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private var cargaJob: Job? = null

    init {
        cargarOrganizaciones()
    }

    fun cargarOrganizaciones() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            _listUiState.value = OrganizacionListUiState.Cargando
            try {
                organizacionRepository.getAllOrganizaciones().collect { lista ->
                    _organizaciones.value = lista
                    _listUiState.value = OrganizacionListUiState.Exito(lista)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _listUiState.value = OrganizacionListUiState.Error(
                    "No se pudieron cargar las organizaciones."
                )
            }
        }
    }

    fun cambiarNombre(valor: String) = _uiState.update { it.copy(nombre = valor, errorNombre = null) }
    fun cambiarRut(valor: String) = _uiState.update { it.copy(rut = valor, errorRut = null) }
    fun cambiarDireccion(valor: String) = _uiState.update { it.copy(direccion = valor) }
    fun resetExito() = _uiState.update { it.copy(guardadoExitoso = false, nombre = "", rut = "", direccion = "") }

    fun validarYGuardar() {
        if (_guardando.value) return

        val estado = _uiState.value
        var isValid = true

        var errNombre: String? = null
        var errRut: String? = null

        if (estado.nombre.isBlank()) {
            errNombre = "El nombre de la empresa es obligatorio"
            isValid = false
        }

        if (estado.rut.isBlank() || !validarRut(estado.rut)) {
            errRut = "Ingrese un RUT válido (8 a 9 caracteres)"
            isValid = false
        }

        val nombreTrim = estado.nombre.trim()
        val rutTrim = estado.rut.trim()
        val duplicado = _organizaciones.value.any {
            it.nombreEmpresa.equals(nombreTrim, ignoreCase = true) ||
                    it.rut.equals(rutTrim, ignoreCase = true)
        }
        if (duplicado) {
            errNombre = "Ya existe una organización con este nombre o RUT"
            isValid = false
        }

        _uiState.update { it.copy(errorNombre = errNombre, errorRut = errRut) }

        if (isValid) {
            val nuevaOrganizacion = Organizacion(
                nombreEmpresa = nombreTrim,
                rut = rutTrim,
                direccion = estado.direccion.trim()
            )

            viewModelScope.launch {
                _guardando.value = true
                try {
                    organizacionRepository.guardar(nuevaOrganizacion)
                    _uiState.value = OrganizacionUiState(guardadoExitoso = true)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(errorNombre = "No se pudo guardar la organización: ${e.message}")
                    }
                } finally {
                    _guardando.value = false
                }
            }
        }
    }

    fun eliminarOrganizacion(id: Int) {
        viewModelScope.launch {
            try {
                organizacionRepository.eliminar(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _listUiState.value = OrganizacionListUiState.Error(
                    "No se pudo eliminar la organización: ${e.message}"
                )
            }
        }
    }
}

class OrganizacionViewModelFactory(
    private val organizacionRepository: OrganizacionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrganizacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrganizacionViewModel(organizacionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}