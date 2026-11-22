package cl.duoc.gestionmovil.data

import android.util.Log
import cl.duoc.gestionmovil.model.RolUsuario
import cl.duoc.gestionmovil.model.Usuario
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.RetrofitClient
import cl.duoc.gestionmovil.network.UsuarioRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val api: GestionMovilApiService = RetrofitClient.api
) {
    private val usuariosIniciales = listOf(
        Usuario(nombre = "Supervisor Uno", correo = "supervisor@test.com", contrasena = "1234", rol = RolUsuario.SUPERVISOR),
        Usuario(nombre = "Ejecutivo Uno", correo = "ejecutivo1@test.com", contrasena = "1234", rol = RolUsuario.EJECUTIVO),
        Usuario(nombre = "Ejecutivo Dos", correo = "ejecutivo2@test.com", contrasena = "1234", rol = RolUsuario.EJECUTIVO),
        Usuario(nombre = "Ejecutivo Tres", correo = "ejecutivo3@test.com", contrasena = "1234", rol = RolUsuario.EJECUTIVO)
    )

    suspend fun contarUsuarios(): Int {
        return try {
            obtenerListaSeguraApi().size
        } catch (e: Exception) {
            usuarioDao.contarUsuarios()
        }
    }

    suspend fun asegurarUsuariosIniciales() {
        try {
            if (obtenerListaSeguraApi().isEmpty()) {
                usuariosIniciales.forEach {
                    api.crearUsuario(UsuarioRequest(it.nombre, it.correo, it.contrasena, it.rol.name))
                }
            }
        } catch (e: Exception) {
            runCatching { Log.w("UsuarioRepo", "Fallo API en seed inicial, usando Room: ${e.message}") }
            if (usuarioDao.contarUsuarios() == 0) {
                usuariosIniciales.forEach { usuarioDao.insert(it) }
            }
        }
    }

    suspend fun login(correo: String, contrasena: String): Usuario? {
        return try {
            asegurarUsuariosIniciales()
            obtenerListaSeguraApi().find {
                it.correo.equals(correo.trim(), ignoreCase = true) && it.contrasena == contrasena.trim()
            }
        } catch (e: Exception) {
            runCatching { Log.w("UsuarioRepo", "Fallo API en login, usando Plan B (Room): ${e.message}") }
            if (usuarioDao.contarUsuarios() == 0) {
                usuariosIniciales.forEach { usuarioDao.insert(it) }
            }
            usuarioDao.login(correo.trim(), contrasena.trim())
        }
    }

    fun getAllUsuarios(): Flow<List<Usuario>> = flow {
        try {
            emit(obtenerListaSeguraApi())
        } catch (e: Exception) {
            usuarioDao.getAllUsuarios().collect { emit(it) }
        }
    }

    private suspend fun obtenerListaSeguraApi(): List<Usuario> {
        return try {
            api.obtenerUsuarios()
        } catch (e: HttpException) {
            if (e.code() == 404) emptyList() else throw e
        }
    }
}