package cl.duoc.gestionmovil.ui.seguimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.SeguimientoDao
import cl.duoc.gestionmovil.model.Seguimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SeguimientoViewModel(
    private val seguimientoDao: SeguimientoDao,
    private val oportunidadId: Int
) : ViewModel() {

    private val _historial = MutableStateFlow<List<Seguimiento>>(emptyList())
    val historial: StateFlow<List<Seguimiento>> = _historial.asStateFlow()

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            // Asume que tu SeguimientoDao tiene un método:
            // @Query("SELECT * FROM seguimientos WHERE idOportunidad = :id ORDER BY fecha DESC")
            // fun obtenerPorOportunidad(id: Int): Flow<List<Seguimiento>>
            seguimientoDao.obtenerPorOportunidad(oportunidadId).collect { lista ->
                _historial.value = lista
            }
        }
    }

    fun agregarSeguimiento(descripcion: String) {
        viewModelScope.launch {
            val nuevoSeguimiento = Seguimiento(
                idOportunidad = oportunidadId,
                descripcion = descripcion,
                fecha = System.currentTimeMillis()
            )
            seguimientoDao.insertar(nuevoSeguimiento)
        }
    }
}

class SeguimientoViewModelFactory(
    private val seguimientoDao: SeguimientoDao,
    private val oportunidadId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeguimientoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeguimientoViewModel(seguimientoDao, oportunidadId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

