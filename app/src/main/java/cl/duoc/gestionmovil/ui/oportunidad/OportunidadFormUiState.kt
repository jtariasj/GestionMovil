package cl.duoc.gestionmovil.ui.oportunidad

import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.TipoOportunidad

// "Fotografía" del estado actual del formulario de oportunidad
data class OportunidadFormUiState(
    // Valores
    val nombre: String = "",
    val tipo: TipoOportunidad? = null,
    val descripcion: String = "",
    val fuente: String = "",
    val fechaDeteccion: String = "",
    val responsable: String = "",
    val prioridad: Prioridad? = null,
    val potencial: PotencialComercial? = null,
    val observaciones: String = "",

    // Estados de error
    val errorNombre: Boolean = false,
    val errorTipo: Boolean = false,
    val errorDescripcion: Boolean = false,
    val errorFuente: Boolean = false,
    val errorFecha: Boolean = false,
    val errorResponsable: Boolean = false,
    val errorPrioridad: Boolean = false,
    val errorPotencial: Boolean = false,

    // Para avisar a la pantalla que se guardó correctamente
    val guardadoExitoso: Boolean = false
)
