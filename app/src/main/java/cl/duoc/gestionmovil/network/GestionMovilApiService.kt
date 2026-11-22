package cl.duoc.gestionmovil.network

import cl.duoc.gestionmovil.model.Contacto
import cl.duoc.gestionmovil.model.Oportunidad
import cl.duoc.gestionmovil.model.Organizacion
import cl.duoc.gestionmovil.model.Seguimiento
import cl.duoc.gestionmovil.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GestionMovilApiService {

    // --- OPORTUNIDADES ---
    @GET("api/v1/oportunidad")
    suspend fun obtenerOportunidades(): List<Oportunidad>

    @GET("api/v1/oportunidad/{id}")
    suspend fun obtenerOportunidad(@Path("id") id: Int): Oportunidad

    @POST("api/v1/oportunidad")
    suspend fun crearOportunidad(@Body oportunidad: OportunidadRequest): Response<Void>

    @PUT("api/v1/oportunidad/{id}")
    suspend fun actualizarOportunidad(
        @Path("id") id: Int,
        @Body oportunidad: OportunidadRequest
    ): Response<Void>

    @DELETE("api/v1/oportunidad/{id}")
    suspend fun eliminarOportunidad(@Path("id") id: Int): Response<Void>

    // --- ORGANIZACIONES ---
    @GET("api/v1/organizacion")
    suspend fun obtenerOrganizaciones(): List<Organizacion>

    @GET("api/v1/organizacion/{id}")
    suspend fun obtenerOrganizacion(@Path("id") id: Int): Organizacion

    @POST("api/v1/organizacion")
    suspend fun crearOrganizacion(@Body organizacion: OrganizacionRequest): Response<Void>

    @PUT("api/v1/organizacion/{id}")
    suspend fun actualizarOrganizacion(
        @Path("id") id: Int,
        @Body organizacion: OrganizacionRequest
    ): Response<Void>

    @DELETE("api/v1/organizacion/{id}")
    suspend fun eliminarOrganizacion(@Path("id") id: Int): Response<Void>

    // --- CONTACTOS ---
    @GET("api/v1/contacto")
    suspend fun obtenerContactos(): List<Contacto>

    @GET("api/v1/contacto/{id}")
    suspend fun obtenerContacto(@Path("id") id: Int): Contacto

    @POST("api/v1/contacto")
    suspend fun crearContacto(@Body contacto: ContactoRequest): Response<Void>

    @PUT("api/v1/contacto/{id}")
    suspend fun actualizarContacto(
        @Path("id") id: Int,
        @Body contacto: ContactoRequest
    ): Response<Void>

    @DELETE("api/v1/contacto/{id}")
    suspend fun eliminarContacto(@Path("id") id: Int): Response<Void>

    // --- SEGUIMIENTOS ---
    @GET("api/v1/seguimiento")
    suspend fun obtenerSeguimientos(): List<Seguimiento>

    @POST("api/v1/seguimiento")
    suspend fun crearSeguimiento(@Body seguimiento: SeguimientoRequest): Response<Void>

    @PUT("api/v1/seguimiento/{id}")
    suspend fun actualizarSeguimiento(
        @Path("id") id: Int,
        @Body seguimiento: SeguimientoRequest
    ): Response<Void>

    @DELETE("api/v1/seguimiento/{id}")
    suspend fun eliminarSeguimiento(@Path("id") id: Int): Response<Void>

    // --- USUARIOS ---
    @GET("api/v1/usuario")
    suspend fun obtenerUsuarios(): List<Usuario>

    @POST("api/v1/usuario")
    suspend fun crearUsuario(@Body usuario: UsuarioRequest): Response<Void>
}