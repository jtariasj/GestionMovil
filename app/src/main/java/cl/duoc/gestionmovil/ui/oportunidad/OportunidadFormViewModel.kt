package cl.duoc.gestionmovil.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.OportunidadDao
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.TipoOportunidad
import cl.duoc.gestionmovil.util.formatearFecha
import cl.duoc.gestionmovil.util.parsearFecha
import cl.duoc.gestionmovil.util.validarContieneLetras
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

class OportunidadFormViewModel(private val oportunidadDao: OportunidadDao) : ViewModel() {

    private val _uiState = MutableStateFlow(estadoInicial())
    val uiState: StateFlow<OportunidadFormUiState> = _uiState.asStateFlow()

    // La fecha de detección parte con el día de hoy
    private fun estadoInicial() = OportunidadFormUiState(
        fechaDeteccion = formatearFecha(System.currentTimeMillis())
    )

    fun cambiarNombre(valor: String) { _uiState.update { it.copy(nombre = valor) } }
    fun cambiarTipo(valor: TipoOportunidad) { _uiState.update { it.copy(tipo = valor) } }
    fun cambiarDescripcion(valor: String) { _uiState.update { it.copy(descripcion = valor) } }
    fun cambiarFuente(valor: String) { _uiState.update { it.copy(fuente = valor) } }
    fun cambiarFechaDeteccion(valor: String) { _uiState.update { it.copy(fechaDeteccion = valor) } }
    fun cambiarResponsable(valor: String) { _uiState.update { it.copy(responsable = valor) } }
    fun cambiarPrioridad(valor: Prioridad) { _uiState.update { it.copy(prioridad = valor) } }
    fun cambiarPotencial(valor: PotencialComercial) { _uiState.update { it.copy(potencial = valor) } }
    fun cambiarObservaciones(valor: String) { _uiState.update { it.copy(observaciones = valor) } }
    fun cambiarCosto(valor: String) { _uiState.update { it.copy(costo = valor, errorCosto = null) } }
    fun cambiarFechaTermino(valor: String) { _uiState.update { it.copy(fechaTermino = valor, errorFechaTermino = null) } }
    fun mensajeMostrado() { _uiState.update { it.copy(guardadoExitoso = false) } }
    fun cambiarFotoPath(path: String?) { _uiState.update { it.copy(fotoPath = path) } }

    // Acción del formulario
    fun guardar() {
        val estado = _uiState.value

        val errorNombre = !validarNombreOportunidad(estado.nombre)
        val errorTipo = !validarSeleccion(estado.tipo)
        val errorDescripcion = !validarContieneLetras(estado.descripcion)
        val errorFuente = !validarContieneLetras(estado.fuente)
        val errorFecha = !validarFechaDeteccion(estado.fechaDeteccion)
        val errorResponsable = !validarResponsable(estado.responsable)
        val errorPrioridad = !validarSeleccion(estado.prioridad)
        val errorPotencial = !validarSeleccion(estado.potencial)

        // REGLA DE NEGOCIO 1: Costos no negativos
        var msgErrorCosto: String? = null
        if (estado.costo.isBlank()) {
            msgErrorCosto = "El costo referencial es obligatorio."
        } else {
            val costoNum = estado.costo.toDoubleOrNull()
            if (costoNum == null) {
                msgErrorCosto = "Debe ser un valor numérico válido."
            } else if (costoNum < 0) {
                msgErrorCosto = "El costo no puede ser un valor negativo."
            }
        }

        // REGLA DE NEGOCIO 2: Fechas coherentes (Término no puede ser antes de inicio/detección)
        var msgErrorFechaTermino: String? = null
        if (estado.fechaTermino.isBlank()) {
            msgErrorFechaTermino = "La fecha de término es obligatoria."
        } else {
            // Intentamos validar que la fecha de término sea posterior a la de inicio
            val fechaInicio = parsearFecha(estado.fechaDeteccion)
            val fechaFin = parsearFecha(estado.fechaTermino)
            if (fechaInicio != null && fechaFin != null) {
                if (fechaFin < fechaInicio) { // Comparamos los milisegundos (Long)
                    msgErrorFechaTermino = "La fecha de término no puede ser anterior al inicio."
                }
            }
        }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorTipo = errorTipo,
                errorDescripcion = errorDescripcion,
                errorFuente = errorFuente,
                errorFecha = errorFecha,
                errorResponsable = errorResponsable,
                errorPrioridad = errorPrioridad,
                errorPotencial = errorPotencial,
                errorCosto = msgErrorCosto,
                errorFechaTermino = msgErrorFechaTermino
            )
        }

        val formularioValido =
            !errorNombre && !errorTipo && !errorDescripcion && !errorFuente &&
                    !errorFecha && !errorResponsable && !errorPrioridad && !errorPotencial &&
                    msgErrorCosto == null && msgErrorFechaTermino == null

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
                observaciones = estado.observaciones.trim(),
                fotoPath = estado.fotoPath
                // Si agregaste costoReferencial o fechaTermino a la data class Oportunidad, agrégalos aquí también.
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