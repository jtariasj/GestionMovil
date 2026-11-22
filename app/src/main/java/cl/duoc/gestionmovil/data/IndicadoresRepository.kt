package cl.duoc.gestionmovil.data

import cl.duoc.gestionmovil.network.IndicadoresApiService
import cl.duoc.gestionmovil.network.IndicadoresResponse
import cl.duoc.gestionmovil.network.RetrofitClient

class IndicadoresRepository(
    private val api: IndicadoresApiService = RetrofitClient.indicadoresApi
) {
    suspend fun obtenerIndicadores(): IndicadoresResponse {
        return api.obtenerIndicadores()
    }
}