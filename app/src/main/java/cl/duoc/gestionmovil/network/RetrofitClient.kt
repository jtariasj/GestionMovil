package cl.duoc.gestionmovil.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val BASE_URL_EXTERNA = "https://mindicador.cl/"
    private const val BASE_URL_CLIMA = "https://api.open-meteo.com/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    val api: GestionMovilApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GestionMovilApiService::class.java)
    }

    val indicadoresApi: IndicadoresApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_EXTERNA)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IndicadoresApiService::class.java)
    }

    val climaApi: ClimaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CLIMA)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClimaApiService::class.java)
    }
}