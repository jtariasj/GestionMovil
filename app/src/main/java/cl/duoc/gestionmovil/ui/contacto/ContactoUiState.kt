package cl.duoc.gestionmovil.ui.contacto

data class ContactoUiState(
    val nombre: String = "",
    val cargo: String = "",
    val telefono: String = "",
    val correo: String = "",
    val errorNombre: String? = null,
    val errorCargo: String? = null,
    val errorTelefono: String? = null,
    val errorCorreo: String? = null,
    val guardadoExitoso: Boolean = false
)
