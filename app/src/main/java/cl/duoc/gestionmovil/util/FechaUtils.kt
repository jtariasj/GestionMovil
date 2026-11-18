package cl.duoc.gestionmovil.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FORMATO_FECHA = "dd/MM/yyyy"
private val REGEX_FECHA = Regex("""^\d{2}/\d{2}/\d{4}$""")

// Convierte "dd/MM/yyyy" a milisegundos. Retorna null si el texto no es una fecha real.
// (Se usa SimpleDateFormat y no java.time porque minSdk = 24)
fun parsearFecha(texto: String): Long? {
    val limpio = texto.trim()
    if (!REGEX_FECHA.matches(limpio)) return null
    return try {
        val formato = SimpleDateFormat(FORMATO_FECHA, Locale.getDefault())
        formato.isLenient = false
        formato.parse(limpio)?.time
    } catch (e: ParseException) {
        null
    }
}

fun formatearFecha(millis: Long): String {
    return SimpleDateFormat(FORMATO_FECHA, Locale.getDefault()).format(Date(millis))
}
