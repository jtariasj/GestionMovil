package cl.duoc.gestionmovil.ui.oportunidad

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.AppDatabase
import cl.duoc.gestionmovil.data.OportunidadRepository
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.TipoOportunidad
import cl.duoc.gestionmovil.util.formatearFecha
import cl.duoc.gestionmovil.util.parsearFecha
import cl.duoc.gestionmovil.util.validarDescripcion
import cl.duoc.gestionmovil.util.validarFechaDeteccion
import cl.duoc.gestionmovil.util.validarFuente
import cl.duoc.gestionmovil.util.validarNombreOportunidad
import cl.duoc.gestionmovil.util.validarResponsable
import cl.duoc.gestionmovil.util.validarSeleccion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadDao

class OportunidadFormViewModel(private val oportunidadDao: OportunidadDao) : ViewModel() {

    private val _uiState = MutableStateFlow(estadoInicial())
    val uiState: StateFlow<OportunidadFormUiState> = _uiState.asStateFlow()

    // La fecha de detección parte con el día de hoy
    private fun estadoInicial() = OportunidadFormUiState(
        fechaDeteccion = formatearFecha(System.currentTimeMillis())
    )

    fun cambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor) }
    }

    fun cambiarTipo(valor: TipoOportunidad) {
        _uiState.update { it.copy(tipo = valor) }
    }

    fun cambiarDescripcion(valor: String) {
        _uiState.update { it.copy(descripcion = valor) }
    }

    fun cambiarFuente(valor: String) {
        _uiState.update { it.copy(fuente = valor) }
    }

    fun cambiarFechaDeteccion(valor: String) {
        _uiState.update { it.copy(fechaDeteccion = valor) }
    }

    fun cambiarResponsable(valor: String) {
        _uiState.update { it.copy(responsable = valor) }
    }

    fun cambiarPrioridad(valor: Prioridad) {
        _uiState.update { it.copy(prioridad = valor) }
    }

    fun cambiarPotencial(valor: PotencialComercial) {
        _uiState.update { it.copy(potencial = valor) }
    }

    fun cambiarObservaciones(valor: String) {
        _uiState.update { it.copy(observaciones = valor) }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(guardadoExitoso = false) }
    }

    // Acción del formulario
    fun guardar() {
        val estado = _uiState.value

        val errorNombre = !validarNombreOportunidad(estado.nombre)
        val errorTipo = !validarSeleccion(estado.tipo)
        val errorDescripcion = !validarDescripcion(estado.descripcion)
        val errorFuente = !validarFuente(estado.fuente)
        val errorFecha = !validarFechaDeteccion(estado.fechaDeteccion)
        val errorResponsable = !validarResponsable(estado.responsable)
        val errorPrioridad = !validarSeleccion(estado.prioridad)
        val errorPotencial = !validarSeleccion(estado.potencial)

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorTipo = errorTipo,
                errorDescripcion = errorDescripcion,
                errorFuente = errorFuente,
                errorFecha = errorFecha,
                errorResponsable = errorResponsable,
                errorPrioridad = errorPrioridad,
                errorPotencial = errorPotencial
            )
        }

        val formularioValido =
            !errorNombre && !errorTipo && !errorDescripcion && !errorFuente &&
                    !errorFecha && !errorResponsable && !errorPrioridad && !errorPotencial

        if (formularioValido) {
            val ahora = System.currentTimeMillis()
            val oportunidad = Oportunidad(
                nombre = estado.nombre.trim(),
                tipo = estado.tipo!!,
                descripcion = estado.descripcion.trim(),
                fuenteInformacion = estado.fuente.trim(),
                fechaDeteccion = parsearFecha(estado.fechaDeteccion)!!,
                responsable = estado.responsable.trim(),
                prioridad = estado.prioridad!!,
                potencialComercial = estado.potencial!!,
                fechaActualizacion = ahora,
                observaciones = estado.observaciones.trim()
            )
            viewModelScope.launch {
                oportunidadDao.insertar(oportunidad)
                _uiState.value = estadoInicial().copy(guardadoExitoso = true)
            }
        }

    }
}

class OportunidadFormViewModelFactory(private val oportunidadDao: OportunidadDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OportunidadFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OportunidadFormViewModel(oportunidadDao) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
