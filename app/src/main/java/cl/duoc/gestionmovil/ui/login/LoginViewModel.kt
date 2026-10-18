package cl.duoc.gestionmovil.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.gestionmovil.data.UsuarioDao
import cl.duoc.gestionmovil.model.RolUsuario
import cl.duoc.gestionmovil.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val usuarioLogueado: Usuario? = null
)

class LoginViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                asegurarUsuariosIniciales()
            } catch (_: Exception) {
                // Se volverá a intentar al iniciar sesión si aún no está lista
            }
        }
    }

    private suspend fun asegurarUsuariosIniciales() {
        if (usuarioDao.contarUsuarios() == 0) {
            usuarioDao.insert(
                Usuario(
                    nombre = "Supervisor Uno",
                    correo = "supervisor@test.com",
                    contrasena = "1234",
                    rol = RolUsuario.SUPERVISOR
                )
            )
            usuarioDao.insert(
                Usuario(
                    nombre = "Ejecutivo Uno",
                    correo = "ejecutivo1@test.com",
                    contrasena = "1234",
                    rol = RolUsuario.EJECUTIVO
                )
            )
            usuarioDao.insert(
                Usuario(
                    nombre = "Ejecutivo Dos",
                    correo = "ejecutivo2@test.com",
                    contrasena = "1234",
                    rol = RolUsuario.EJECUTIVO
                )
            )
            usuarioDao.insert(
                Usuario(
                    nombre = "Ejecutivo Tres",
                    correo = "ejecutivo3@test.com",
                    contrasena = "1234",
                    rol = RolUsuario.EJECUTIVO
                )
            )
        }
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        _uiState.value = LoginUiState(isLoading = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                asegurarUsuariosIniciales()
                val usuario = usuarioDao.login(correo.trim(), contrasena.trim())

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

class LoginViewModelFactory(private val usuarioDao: UsuarioDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

