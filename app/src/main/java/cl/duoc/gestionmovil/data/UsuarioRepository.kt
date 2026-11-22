package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.model.RolUsuario
import cl.duoc.gestionmovil.model.Usuario
import cl.duoc.gestionmovil.network.GestionMovilApiService
import cl.duoc.gestionmovil.network.RetrofitClient
import cl.duoc.gestionmovil.network.UsuarioRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import retrofit2.Response

class UsuarioRepository(
    private val usuarioDao: UsuarioDao? = null,
    private val api: GestionMovilApiService = RetrofitClient.api
) {

    private val usuariosBase = listOf(
        UsuarioRequest("Supervisor Uno", "supervisor@test.com", "1234", RolUsuario.SUPERVISOR.name),
        UsuarioRequest("Ejecutivo Uno", "ejecutivo1@test.com", "1234", RolUsuario.EJECUTIVO.name),
        UsuarioRequest("Ejecutivo Dos", "ejecutivo2@test.com", "1234", RolUsuario.EJECUTIVO.name),
        UsuarioRequest("Ejecutivo Tres", "ejecutivo3@test.com", "1234", RolUsuario.EJECUTIVO.name)
    )

    // =========================================================================
    // 1. OPERACIONES REMOTAS (RETROFIT -> SPRING BOOT) - Patrón EA3.2
    // =========================================================================

    suspend fun obtenerUsuariosApi(): List<Usuario> {
        var listaRemota = api.obtenerUsuarios()
        // Si la BD de Spring Boot inicia vacía, registra los 4 usuarios ficticios iniciales
        if (listaRemota.isEmpty()) {
            usuariosBase.forEach { crearUsuarioApi(it) }
            listaRemota = api.obtenerUsuarios()
        }
        // Respaldo local en Room evitando conflicto si el ID ya existe
        usuarioDao?.let { dao ->
            listaRemota.forEach { usuario ->
                runCatching { dao.insert(usuario) }
            }
        }
        return listaRemota
    }

    suspend fun crearUsuarioApi(usuario: UsuarioRequest) {
        comprobarRespuesta(api.crearUsuario(usuario))
    }

    private fun comprobarRespuesta(respuesta: Response<Void>) {
        if (!respuesta.isSuccessful) {
            throw HttpException(respuesta)
        }
    }

    // =========================================================================
    // 2. AUTENTICACIÓN Y RESPALDO LOCAL (ROOM)
    // =========================================================================

    suspend fun login(correo: String, contrasena: String): Usuario? {
        val correoTrim = correo.trim()
        val passTrim = contrasena.trim()

        return try {
            val usuarios = obtenerUsuariosApi()
            usuarios.find {
                it.correo.equals(correoTrim, ignoreCase = true) && it.contrasena == passTrim
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Respaldo offline en Room ante conexiones inestables (Sección 3.3 del caso)
            asegurarUsuariosIniciales()
            usuarioDao?.login(correoTrim, passTrim)
        }
    }

    suspend fun contarUsuarios(): Int {
        return usuarioDao?.contarUsuarios() ?: 0
    }

    suspend fun asegurarUsuariosIniciales() {
        val dao = usuarioDao ?: return
        if (dao.contarUsuarios() == 0) {
            usuariosBase.forEach { req ->
                dao.insert(
                    Usuario(
                        nombre = req.nombre,
                        correo = req.correo,
                        contrasena = req.contrasena,
                        rol = RolUsuario.valueOf(req.rol)
                    )
                )
            }
        }
    }

    fun getAllUsuarios(): Flow<List<Usuario>> {
        return requireNotNull(usuarioDao).getAllUsuarios()
    }
}