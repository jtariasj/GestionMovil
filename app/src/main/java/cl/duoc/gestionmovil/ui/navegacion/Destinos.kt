package cl.duoc.gestionmovil.ui.navegacion

object Destinos {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val OPORTUNIDADES = "oportunidades"
    const val NUEVA_OPORTUNIDAD = "nueva_oportunidad"
    const val DETALLE_OPORTUNIDAD = "detalle_oportunidad/{id}"
    const val SEGUIMIENTO = "seguimiento/{oportunidadId}"
    const val DIRECTORIO = "directorio"

    // Funciones auxiliares para rutas con argumentos
    fun detalleOportunidad(id: Int) = "detalle_oportunidad/$id"
    fun seguimiento(oportunidadId: Int) = "seguimiento/$oportunidadId"
}