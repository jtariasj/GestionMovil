package cl.duoc.gestionmovil.model

// Tipos de oportunidad definidos en el caso (sección 3.2)
enum class TipoOportunidad(val etiqueta: String) {
    EVENTO_CORPORATIVO("Evento corporativo"),
    FERIA_EMPRESARIAL("Feria empresarial"),
    ACTIVIDAD_MUNICIPAL("Actividad municipal"),
    JORNADA_BENEFICIOS("Jornada de beneficios"),
    FERIA_INSTITUCIONAL("Feria institucional"),
    ACTIVIDAD_TRABAJADORES("Actividad de trabajadores"),
    ACTIVACION_COMERCIAL("Activación comercial"),
    OTRA("Otra instancia relevante")
}

// Estados principales de la gestión identificados en el anexo del caso
enum class EstadoOportunidad(val etiqueta: String) {
    EVENTO_IDENTIFICADO("Evento identificado"),
    CONTACTO_PENDIENTE("Contacto pendiente"),
    CONTACTO_REALIZADO("Contacto realizado"),
    PROPUESTA_ENVIADA("Propuesta enviada"),
    EN_ESPERA_RESPUESTA("En espera de respuesta"),
    PARTICIPACION_CONFIRMADA("Participación confirmada"),
    DESCARTADA("Oportunidad descartada")
}

enum class Prioridad(val etiqueta: String) {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja")
}

enum class PotencialComercial(val etiqueta: String) {
    ALTO("Alto"),
    MEDIO("Medio"),
    BAJO("Bajo")
}
