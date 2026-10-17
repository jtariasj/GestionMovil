package cl.duoc.gestionmovil.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Entity = representación Kotlin de la tabla "oportunidades".
// Solo contiene datos de la oportunidad: los errores del formulario
// pertenecen al UiState, no a este modelo.
@Entity(tableName = "oportunidades")
data class Oportunidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: TipoOportunidad,
    val descripcion: String,
    val fuenteInformacion: String,
    val fechaDeteccion: Long,          // epoch millis
    val responsable: String,           // responsable interno (ficticio)
    val prioridad: Prioridad,
    val potencialComercial: PotencialComercial,
    val estado: EstadoOportunidad = EstadoOportunidad.EVENTO_IDENTIFICADO,
    val fechaActualizacion: Long,      // epoch millis
    val observaciones: String = ""
)
