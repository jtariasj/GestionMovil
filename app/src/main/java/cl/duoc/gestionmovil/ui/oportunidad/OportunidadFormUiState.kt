package cl.duoc.gestionmovil.ui.oportunidad

import cl.duoc.gestionmovil.model.Prioridad
import cl.duoc.gestionmovil.model.PotencialComercial
import cl.duoc.gestionmovil.model.TipoOportunidad

data class OportunidadFormUiState(
    // Valores originales
    val nombre: String = "",
    val tipo: TipoOportunidad? = null,
    val descripcion: String = "",
    val fuente: String = "",
    val fechaDeteccion: String = "",
    val responsable: String = "",
    val prioridad: Prioridad? = null,
    val potencial: PotencialComercial? = null,
    val observaciones: String = "",

    // NUEVOS VALORES (Punto 4 de la rúbrica)
    val costo: String = "",
    val fechaTermino: String = "",

    // Estados de error originales
    val errorNombre: Boolean = false,
    val errorTipo: Boolean = false,
    val errorDescripcion: Boolean = false,
    val errorFuente: Boolean = false,
    val errorFecha: Boolean = false,
    val errorResponsable: Boolean = false,
    val errorPrioridad: Boolean = false,
    val errorPotencial: Boolean = false,

    // NUEVOS ESTADOS DE ERROR (Usamos String para mandar mensajes específicos)
    val errorCosto: String? = null,
    val errorFechaTermino: String? = null,

    // Para avisar a la pantalla que se guardó correctamente
    val guardadoExitoso: Boolean = false
)