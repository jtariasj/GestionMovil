package cl.duoc.gestionmovil.network

import com.google.gson.annotations.SerializedName

data class IndicadoresResponse(
    val version: String? = null,
    val autor: String? = null,
    val fecha: String? = null,
    val uf: IndicadorDetalle? = null,
    val dolar: IndicadorDetalle? = null,
    val utm: IndicadorDetalle? = null
)

data class IndicadorDetalle(
    val codigo: String,
    val nombre: String,
    @SerializedName("unidad_medida")
    val unidadMedida: String,
    val fecha: String,
    val valor: Double
)