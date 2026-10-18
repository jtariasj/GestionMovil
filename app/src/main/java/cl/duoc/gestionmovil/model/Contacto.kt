package cl.duoc.gestionmovil.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey


@Entity(
    tableName = "contactos",
    foreignKeys = [
        ForeignKey(
            entity = Organizacion::class,
            parentColumns = ["id"],
            childColumns = ["organizacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Contacto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val cargo: String,
    val telefono: String,
    val correo: String,
    val organizacionId: Int // Clave foránea
)