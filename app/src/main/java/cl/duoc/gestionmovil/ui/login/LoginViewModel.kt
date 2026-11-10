package cl.duoc.gestionmovil.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                usuarioRepository.asegurarUsuariosIniciales()
            } catch (_: Exception) {
                // Se volverá a intentar al iniciar sesión si aún no está lista
            }
        }
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        _uiState.value = LoginUiState(isLoading = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuario = usuarioRepository.login(correo, contrasena)

                if (usuario != null) {
                    _uiState.value = LoginUiState(usuarioLogueado = usuario)
                } else {
                    _uiState.value = LoginUiState(
                        error = "Credenciales incorrectas (prueba: supervisor@test.com / 1234)"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = LoginUiState(
                    error = "Error de base de datos: ${e.localizedMessage ?: "desconocido"}"
                )
            }
        }
    }

    fun cerrarSesion() {
        _uiState.value = LoginUiState()
    }
}

class LoginViewModelFactory(private val usuarioRepository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
