package cl.duoc.gestionmovil.ui.login

import cl.duoc.gestionmovil.model.Usuario

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val usuarioLogueado: Usuario? = null
)
