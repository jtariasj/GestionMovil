package cl.duoc.gestionmovil.network

import retrofit2.http.GET

interface IndicadoresApiService {
    // Consulta los indicadores económicos del día (UF, Dólar, UTM)
    @GET("api")
    suspend fun obtenerIndicadores(): IndicadoresResponse
}