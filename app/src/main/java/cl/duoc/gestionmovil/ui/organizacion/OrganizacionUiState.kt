package cl.duoc.gestionmovil.ui.organizacion

data class OrganizacionUiState(
    val nombre: String = "",
    val rut: String = "",
    val direccion: String = "",
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val guardadoExitoso: Boolean = false
)
