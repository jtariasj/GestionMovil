package cl.duoc.gestionmovil.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val contrasena: String, // Contraseña ficticia
    val rol: RolUsuario // Enum: EJECUTIVO o SUPERVISOR
)


