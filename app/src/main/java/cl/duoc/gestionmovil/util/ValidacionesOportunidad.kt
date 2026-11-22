package cl.duoc.gestionmovil.util

// Reglas de validación del formulario de oportunidad.

fun validarNombreOportunidad(nombre: String): Boolean {
    return nombre.trim().length >= 3
}

fun validarDescripcion(descripcion: String): Boolean {
    return descripcion.isNotBlank()
}

fun validarFuente(fuente: String): Boolean {
    return fuente.isNotBlank()
}

fun validarFechaDeteccion(fecha: String): Boolean {
    val millis = parsearFecha(fecha) ?: return false
    return millis <= System.currentTimeMillis()
}

fun validarResponsable(responsable: String): Boolean {
    return responsable.isNotBlank()
}

fun validarSeleccion(valor: Any?): Boolean {
    return valor != null
}

fun validarRut(rut: String): Boolean {
    val rutLimpio = rut.replace(".", "").replace("-", "").trim()
    return rutLimpio.length in 8..9
}

fun validarTelefono(telefono: String): Boolean {
    val telLimpio = telefono.replace(" ", "").trim()
    return telLimpio.isNotEmpty() && telLimpio.length <= 20 && telLimpio.all { it.isDigit() || it == '+' }
}

fun validarCorreo(correo: String): Boolean {
    val email = correo.trim().lowercase()
    return email.endsWith("@gmail.com") || email.endsWith("@test.com")
}

fun validarContieneLetras(texto: String): Boolean {
    return texto.trim().any { it.isLetter() }
}
fun validarPublicoEsperado(publico: String): Boolean { //Preferi utilizar string para su multiuso y flexibilidad
    val valor = publico.trim().toIntOrNull() ?: return false
    return valor > 0
}

fun validarCostoNoNegativo(costo: String): Boolean {
    val valor = costo.trim().toDoubleOrNull() ?: return false
    return valor >= 0.0
}