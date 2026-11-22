package cl.duoc.gestionmovil.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val BASE_URL_EXTERNA = "https://mindicador.cl/"

    val api: GestionMovilApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GestionMovilApiService::class.java)
    }

    val indicadoresApi: IndicadoresApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_EXTERNA)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IndicadoresApiService::class.java)
    }
}