package cl.duoc.gestionmovil.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "seguimientos",
    foreignKeys = [
        ForeignKey(
            entity = Oportunidad::class,
            parentColumns = ["id"],
            childColumns = ["oportunidadId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Seguimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: Long, // Usualmente guardamos timestamps
    val descripcion: String,
    val tipoGestion: String, // Llamada, Correo, Reunión
    val oportunidadId: Int // Clave foránea
)