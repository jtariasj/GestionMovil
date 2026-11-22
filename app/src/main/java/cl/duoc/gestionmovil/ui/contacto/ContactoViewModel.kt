package cl.duoc.gestionmovil.ui.contacto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.ContactoRepository
import cl.duoc.gestionmovil.model.Contacto
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
                contactoRepository.getContactosPorOrganizacion(organizacionId).collect { lista ->
                    _contactos.value = lista
                    _listUiState.value = ContactoListUiState.Exito(lista)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _listUiState.value = ContactoListUiState.Error(
                    "No se pudieron cargar los contactos."
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
        val nombreTrim = estado.nombre.trim()
        val cargoTrim = estado.cargo.trim()
        val telefonoTrim = estado.telefono.trim()
        val correoTrim = estado.correo.trim()

        val errNombre = if (nombreTrim.isBlank()) "El nombre es obligatorio" else null
        val errCargo = if (cargoTrim.isBlank()) "El cargo es obligatorio" else null
        val errTelefono = if (telefonoTrim.isBlank() || !validarTelefono(telefonoTrim)) {
            "Ingrese un teléfono válido (máx 20 caracteres)"
        } else null
        val errCorreo = if (correoTrim.isBlank() || !validarCorreo(correoTrim)) {
            "El correo debe terminar en @gmail.com o @test.com"
        } else null

        val listaActual = (_listUiState.value as? ContactoListUiState.Exito)?.contactos ?: _contactos.value
        val duplicado = listaActual.any {
            it.correo.equals(correoTrim, ignoreCase = true) ||
                    (it.nombre.equals(nombreTrim, ignoreCase = true) && it.cargo.equals(cargoTrim, ignoreCase = true))
        }

        val finalErrCorreo = errCorreo ?: if (duplicado) "Ya existe un contacto con este correo o nombre y cargo" else null
        val isValid = errNombre == null && errCargo == null && errTelefono == null && finalErrCorreo == null

        _uiState.update {
            it.copy(
                errorNombre = errNombre,
                errorCargo = errCargo,
                errorTelefono = errTelefono,
                errorCorreo = finalErrCorreo
            )
        }

        if (isValid) {
            _guardando.value = true
            val nuevoContacto = Contacto(
                nombre = nombreTrim,
                cargo = cargoTrim,
                telefono = telefonoTrim,
                correo = correoTrim,
                organizacionId = organizacionId
            )

            viewModelScope.launch {
                try {
                    contactoRepository.guardar(nuevoContacto)
                    _uiState.value = ContactoUiState(guardadoExitoso = true)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _uiState.update {
                        it.copy(errorCorreo = "No se pudo guardar el contacto.")
                    }
                } finally {
                    _guardando.value = false
                }
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
