package cl.duoc.gestionmovil.ui.organizacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OrganizacionDao
import cl.duoc.gestionmovil.model.Organizacion
import cl.duoc.gestionmovil.util.validarRut // Importamos la utilidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// CREAMOS EL ESTADO PARA LA UI
data class OrganizacionUiState(
    val nombre: String = "",
    val rut: String = "",
    val direccion: String = "",
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val guardadoExitoso: Boolean = false
)

class OrganizacionViewModel(private val organizacionDao: OrganizacionDao) : ViewModel() {

    private val _organizaciones = MutableStateFlow<List<Organizacion>>(emptyList())
    val organizaciones: StateFlow<List<Organizacion>> = _organizaciones.asStateFlow()

    private val _uiState = MutableStateFlow(OrganizacionUiState())
    val uiState: StateFlow<OrganizacionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            organizacionDao.getAllOrganizaciones().collect { lista ->
                _organizaciones.value = lista
            }
        }
    }

    // Actualizadores de estado
    fun cambiarNombre(valor: String) = _uiState.update { it.copy(nombre = valor, errorNombre = null) }
    fun cambiarRut(valor: String) = _uiState.update { it.copy(rut = valor, errorRut = null) }
    fun cambiarDireccion(valor: String) = _uiState.update { it.copy(direccion = valor) }
    fun resetExito() = _uiState.update { it.copy(guardadoExitoso = false, nombre = "", rut = "", direccion = "") }

    fun validarYGuardar() {
        val estado = _uiState.value
        var isValid = true

        var errNombre: String? = null
        var errRut: String? = null

        if (estado.nombre.isBlank()) {
            errNombre = "El nombre de la empresa es obligatorio"
            isValid = false
        }

        // VALIDACIÓN DEL RUT
        if (estado.rut.isBlank() || !validarRut(estado.rut)) {
            errRut = "Ingrese un RUT válido (8 a 9 caracteres)"
            isValid = false
        }

        val nombreTrim = estado.nombre.trim()
        val rutTrim = estado.rut.trim()
        val duplicado = _organizaciones.value.any {
            it.nombreEmpresa.equals(nombreTrim, ignoreCase = true) || it.rut.equals(rutTrim, ignoreCase = true)
        }
        if (duplicado) {
            errNombre = "Ya existe una organización con este nombre o RUT"
            isValid = false
        }

        _uiState.update { it.copy(errorNombre = errNombre, errorRut = errRut) }

        if (isValid) {
            viewModelScope.launch {
                val nuevaOrg = Organizacion(
                    nombreEmpresa = nombreTrim,
                    rut = rutTrim,
                    direccion = estado.direccion.trim()
                )
                organizacionDao.insert(nuevaOrg)
                _uiState.value = OrganizacionUiState(guardadoExitoso = true)
            }
        }
    }
}

class OrganizacionViewModelFactory(private val organizacionDao: OrganizacionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrganizacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrganizacionViewModel(organizacionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}