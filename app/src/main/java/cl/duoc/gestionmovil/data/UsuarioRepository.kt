package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.RolUsuario
import cl.duoc.gestionmovil.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun contarUsuarios(): Int {
        return usuarioDao.contarUsuarios()
    }

    suspend fun asegurarUsuariosIniciales() {
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

    suspend fun login(correo: String, contrasena: String): Usuario? {
        asegurarUsuariosIniciales()
        return usuarioDao.login(correo.trim(), contrasena.trim())
    }

    fun getAllUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.getAllUsuarios()
    }
}
