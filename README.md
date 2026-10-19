# Gestión Móvil - Oportunidades Comerciales

## Descripción del Proyecto
Aplicación móvil nativa para Android desarrollada con Kotlin y Jetpack Compose. Permite a los usuarios registrar y gestionar oportunidades comerciales en terreno, capturando datos estructurados (fechas, costos, responsables) y evidencia fotográfica nativa. Los datos se almacenan localmente utilizando la base de datos Room, garantizando persistencia y acceso sin conexión.

## Integrantes del Equipo
* Jaime Arias
* Francisca Torres

## Funcionalidades Implementadas
* **Gestión de Estado (UI State):** Implementación de flujos de datos unidireccionales con `ViewModel` y `StateFlow`.
* **Formularios Dinámicos:** Validación en tiempo real de campos de texto, fechas, menús desplegables y valores numéricos (ej. costos no negativos, coherencia de fechas).
* **Uso de Hardware Nativo:** Integración de la cámara del dispositivo para capturar evidencia fotográfica y guardarla en el almacenamiento interno.
* **Persistencia de Datos:** Uso de Room Database con soporte para corrutinas (inserción y lectura asíncrona de datos).
* **Interfaz de Usuario (UI):** Diseño moderno basado en Material Design 3 (Cards, OutlinedTextFields, Snackbar).

## Pasos para ejecutar el proyecto
1. Clonar este repositorio en tu máquina local.
2. Abrir el proyecto utilizando **Android Studio** (versión recomendada: Iguana o superior).
3. Esperar a que Gradle sincronice todas las dependencias.
4. Conectar un dispositivo físico Android (con depuración USB activada) o iniciar un Emulador de Android (AVD).
5. Presionar el botón **Run 'app'** (icono de play verde) en la barra de herramientas superior o usar el atajo `Shift + F10`.
