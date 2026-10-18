package cl.duoc.gestionmovil.ui.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.UsuarioDao
import cl.duoc.gestionmovil.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Esta clase representa el estado de nuestra pantalla (MVVM)
data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val usuarioLogueado: Usuario? = null
)

class LoginViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    // Manejo de estado con StateFlow como pide la rúbrica
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun iniciarSesion(correo: String, contrasena: String) {
        // Mostramos estado de carga (opcional, pero buena práctica)
        _uiState.value = LoginUiState(isLoading = true)

        viewModelScope.launch {
            try {
                // Consultamos a Room si el usuario existe
                val usuario = usuarioDao.login(correo, contrasena)

                if (usuario != null) {
                    // ¡Login exitoso!
                    _uiState.value = LoginUiState(usuarioLogueado = usuario)
                } else {
                    // Credenciales incorrectas
                    _uiState.value = LoginUiState(error = "Correo o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = "Error de conexión a la base de datos")
            }
        }
    }
}

// Como nuestro ViewModel necesita el DAO, necesitamos un "Factory" para crearlo
class LoginViewModelFactory(private val usuarioDao: UsuarioDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}