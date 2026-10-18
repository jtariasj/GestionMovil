package cl.duoc.gestionmovil.ui.contacto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.ContactoDao
import cl.duoc.gestionmovil.model.Contacto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactoViewModel(
    private val contactoDao: ContactoDao,
    private val organizacionId: Int
) : ViewModel() {

    private val _contactos = MutableStateFlow<List<Contacto>>(emptyList())
    val contactos: StateFlow<List<Contacto>> = _contactos.asStateFlow()

    init {
        // Cargamos solo los contactos que pertenecen a la organización seleccionada
        viewModelScope.launch {
            contactoDao.getContactosPorOrganizacion(organizacionId).collect { lista ->
                _contactos.value = lista
            }
        }
    }

    fun agregarContacto(nombre: String, cargo: String, telefono: String, correo: String) {
        viewModelScope.launch {
            val nuevoContacto = Contacto(
                nombre = nombre,
                cargo = cargo,
                telefono = telefono,
                correo = correo,
                organizacionId = organizacionId
            )
            contactoDao.insert(nuevoContacto)
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