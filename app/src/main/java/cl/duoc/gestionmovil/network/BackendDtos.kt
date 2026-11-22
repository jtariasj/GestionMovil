package cl.duoc.gestionmovil.network

data class OportunidadRequest(
    val nombre: String,
    val tipo: String,
    val descripcion: String,
    val fuenteInformacion: String,
    val fechaDeteccion: Long,
    val responsable: String,
    val prioridad: String,
    val potencialComercial: String,
    val estado: String,
    val fechaActualizacion: Long,
    val observaciones: String = "",
    val fotoPath: String? = null
    // Nota: Si debajo de la línea 49 de Oportunidad.java existe 'organizacionId: Int', agrégalo aquí también.
)

data class OrganizacionRequest(
    val nombreEmpresa: String,
    val rut: String,
    val direccion: String
)

data class ContactoRequest(
    val nombre: String,
    val cargo: String,
    val telefono: String,
    val correo: String,
    val organizacionId: Int
)

data class SeguimientoRequest(
    val fecha: Long,
    val descripcion: String,
    val tipoGestion: String,
    val oportunidadId: Int
)

data class UsuarioRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String
)