package cl.duoc.gestionmovil.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApiService {
    @GET("v1/forecast")
    suspend fun obtenerClimaActual(
        @Query("latitude") latitud: Double = -33.4489, // Santiago, Chile
        @Query("longitude") longitud: Double = -70.6693,
        @Query("current_weather") actual: Boolean = true
    ): ClimaResponse
}

data class ClimaResponse(
    @SerializedName("current_weather")
    val currentWeather: ClimaActual? = null
)

data class ClimaActual(
    val temperature: Double,
    val windspeed: Double,
    val weathercode: Int
)