package cl.duoc.gestionmovil.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "organizaciones")
data class Organizacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreEmpresa: String,
    val rut: String,
    val direccion: String
)


