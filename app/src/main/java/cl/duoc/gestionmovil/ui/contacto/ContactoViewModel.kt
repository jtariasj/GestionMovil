package cl.duoc.gestionmovil.ui.contacto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.ContactoRepository
import cl.duoc.gestionmovil.model.Contacto
import cl.duoc.gestionmovil.network.ContactoRequest
import cl.duoc.gestionmovil.util.validarCorreo
import cl.duoc.gestionmovil.util.validarTelefono
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ContactoListUiState {
    data object Cargando : ContactoListUiState
    data class Exito(val contactos: List<Contacto>) : ContactoListUiState
    data class Error(val mensaje: String) : ContactoListUiState
}

class ContactoViewModel(
    private val contactoRepository: ContactoRepository,
    private val organizacionId: Int
) : ViewModel() {

    private val _listUiState = MutableStateFlow<ContactoListUiState>(ContactoListUiState.Cargando)
    val listUiState: StateFlow<ContactoListUiState> = _listUiState.asStateFlow()

    // Mantiene compatibilidad directa con ContactoScreen.kt
    private val _contactos = MutableStateFlow<List<Contacto>>(emptyList())
    val contactos: StateFlow<List<Contacto>> = _contactos.asStateFlow()

    private val _uiState = MutableStateFlow(ContactoUiState())
    val uiState: StateFlow<ContactoUiState> = _uiState.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    init {
        cargarContactos()
    }

    fun cargarContactos() {
        viewModelScope.launch {
            _listUiState.value = ContactoListUiState.Cargando
            try {
                val lista = contactoRepository.obtenerContactosPorOrganizacionApi(organizacionId)
                _contactos.value = lista
                _listUiState.value = ContactoListUiState.Exito(lista)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _listUiState.value = ContactoListUiState.Error(
                    "No se pudieron cargar los contactos desde el servidor."
                )
            }
        }
    }

    fun cambiarNombre(valor: String) = _uiState.update { it.copy(nombre = valor, errorNombre = null) }
    fun cambiarCargo(valor: String) = _uiState.update { it.copy(cargo = valor, errorCargo = null) }
    fun cambiarTelefono(valor: String) = _uiState.update { it.copy(telefono = valor, errorTelefono = null) }
    fun cambiarCorreo(valor: String) = _uiState.update { it.copy(correo = valor, errorCorreo = null) }
    fun resetExito() = _uiState.update { it.copy(guardadoExitoso = false) }

    fun validarYGuardar() {
        if (_guardando.value) return

        val estado = _uiState.value
        var isValid = true

        var errNombre: String? = null
        var errCargo: String? = null
        var errTelefono: String? = null
        var errCorreo: String? = null

        if (estado.nombre.isBlank()) { errNombre = "El nombre es obligatorio"; isValid = false }
        if (estado.cargo.isBlank()) { errCargo = "El cargo es obligatorio"; isValid = false }

        if (estado.telefono.isBlank() || !validarTelefono(estado.telefono)) {
            errTelefono = "Ingrese un teléfono válido (máx 20 caracteres)"
            isValid = false
        }

        if (estado.correo.isBlank() || !validarCorreo(estado.correo)) {
            errCorreo = "El correo debe terminar en @gmail.com o @test.com"
            isValid = false
        }

        val correoTrim = estado.correo.trim()
        val nombreTrim = estado.nombre.trim()
        val cargoTrim = estado.cargo.trim()
        val duplicado = _contactos.value.any {
            it.correo.equals(correoTrim, ignoreCase = true) ||
                    (it.nombre.equals(nombreTrim, ignoreCase = true) && it.cargo.equals(cargoTrim, ignoreCase = true))
        }
        if (duplicado) {
            errCorreo = "Ya existe un contacto con este correo o nombre y cargo"
            isValid = false
        }

        _uiState.update {
            it.copy(errorNombre = errNombre, errorCargo = errCargo, errorTelefono = errTelefono, errorCorreo = errCorreo)
        }

        if (isValid) {
            val solicitud = ContactoRequest(
                nombre = nombreTrim,
                cargo = cargoTrim,
                telefono = estado.telefono.trim(),
                correo = correoTrim,
                organizacionId = organizacionId
            )

            viewModelScope.launch {
                _guardando.value = true
                try {
                    contactoRepository.crearContactoApi(solicitud)
                    _uiState.value = ContactoUiState(guardadoExitoso = true)
                    // Refresca la lista desde Spring Boot tras guardar el nuevo contacto
                    cargarContactos()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(errorCorreo = "No se pudo guardar en el servidor: ${e.message}")
                    }
                } finally {
                    _guardando.value = false
                }
            }
        }
    }

    fun eliminarContacto(id: Int) {
        viewModelScope.launch {
            try {
                contactoRepository.eliminarContactoApi(id)
                cargarContactos()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _listUiState.value = ContactoListUiState.Error(
                    "No se pudo eliminar el contacto: ${e.message}"
                )
            }
        }
    }
}

class ContactoViewModelFactory(
    private val contactoRepository: ContactoRepository,
    private val organizacionId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactoViewModel(contactoRepository, organizacionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}