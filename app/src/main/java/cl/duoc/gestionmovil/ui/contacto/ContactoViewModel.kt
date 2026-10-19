package cl.duoc.gestionmovil.ui.contacto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.ContactoDao
import cl.duoc.gestionmovil.model.Contacto
import cl.duoc.gestionmovil.util.validarCorreo // Asegúrate de tener estas funciones en tu archivo de utilidades
import cl.duoc.gestionmovil.util.validarTelefono
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactoUiState(
    val nombre: String = "",
    val cargo: String = "",
    val telefono: String = "",
    val correo: String = "",
    val errorNombre: String? = null,
    val errorCargo: String? = null,
    val errorTelefono: String? = null,
    val errorCorreo: String? = null,
    val guardadoExitoso: Boolean = false
)

class ContactoViewModel(
    private val contactoDao: ContactoDao,
    private val organizacionId: Int
) : ViewModel() {

    private val _contactos = MutableStateFlow<List<Contacto>>(emptyList())
    val contactos: StateFlow<List<Contacto>> = _contactos.asStateFlow()

    private val _uiState = MutableStateFlow(ContactoUiState())
    val uiState: StateFlow<ContactoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            contactoDao.getContactosPorOrganizacion(organizacionId).collect { lista ->
                _contactos.value = lista
            }
        }
    }

    fun cambiarNombre(valor: String) = _uiState.update { it.copy(nombre = valor, errorNombre = null) }
    fun cambiarCargo(valor: String) = _uiState.update { it.copy(cargo = valor, errorCargo = null) }
    fun cambiarTelefono(valor: String) = _uiState.update { it.copy(telefono = valor, errorTelefono = null) }
    fun cambiarCorreo(valor: String) = _uiState.update { it.copy(correo = valor, errorCorreo = null) }
    fun resetExito() = _uiState.update { it.copy(guardadoExitoso = false) }

    fun validarYGuardar() {
        val estado = _uiState.value
        var isValid = true

        var errNombre: String? = null
        var errCargo: String? = null
        var errTelefono: String? = null
        var errCorreo: String? = null

        if (estado.nombre.isBlank()) { errNombre = "El nombre es obligatorio"; isValid = false }
        if (estado.cargo.isBlank()) { errCargo = "El cargo es obligatorio"; isValid = false }

        // APLICANDO NUEVA VALIDACIÓN DE TELÉFONO
        if (estado.telefono.isBlank() || !validarTelefono(estado.telefono)) {
            errTelefono = "Ingrese un teléfono válido (máx 20 caracteres)"
            isValid = false
        }

        // APLICANDO NUEVA VALIDACIÓN DE CORREO
        if (estado.correo.isBlank() || !validarCorreo(estado.correo)) {
            errCorreo = "El correo debe terminar en @gmail.com o @test.com"
            isValid = false
        }

        _uiState.update {
            it.copy(errorNombre = errNombre, errorCargo = errCargo, errorTelefono = errTelefono, errorCorreo = errCorreo)
        }

        if (isValid) {
            viewModelScope.launch {
                contactoDao.insert(Contacto(
                    nombre = estado.nombre.trim(),
                    cargo = estado.cargo.trim(),
                    telefono = estado.telefono.trim(),
                    correo = estado.correo.trim(),
                    organizacionId = organizacionId
                ))
                _uiState.value = ContactoUiState(guardadoExitoso = true)
            }
        }
    }
}

class ContactoViewModelFactory(
    private val contactoDao: ContactoDao,
    private val organizacionId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactoViewModel(contactoDao, organizacionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}