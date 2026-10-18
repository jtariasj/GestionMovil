package cl.duoc.gestionmovil.ui.organizacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OrganizacionDao
import cl.duoc.gestionmovil.model.Organizacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrganizacionViewModel(private val organizacionDao: OrganizacionDao) : ViewModel() {

    private val _organizaciones = MutableStateFlow<List<Organizacion>>(emptyList())
    val organizaciones: StateFlow<List<Organizacion>> = _organizaciones.asStateFlow()

    init {
        // Escuchamos los cambios en la tabla de organizaciones
        viewModelScope.launch {
            organizacionDao.getAllOrganizaciones().collect { lista ->
                _organizaciones.value = lista
            }
        }
    }

    fun agregarOrganizacion(nombre: String, rut: String, direccion: String) {
        viewModelScope.launch {
            val nuevaOrg = Organizacion(
                nombreEmpresa = nombre,
                rut = rut,
                direccion = direccion
            )
            organizacionDao.insert(nuevaOrg)
        }
    }
}

class OrganizacionViewModelFactory(
    private val organizacionDao: OrganizacionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrganizacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrganizacionViewModel(organizacionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}