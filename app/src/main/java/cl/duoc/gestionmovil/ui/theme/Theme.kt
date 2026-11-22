package cl.duoc.gestionmovil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulIndigo,
    onPrimary = BlancoPuro,
    primaryContainer = AzulSuave,
    onPrimaryContainer = AzulIndigoOscuro,

    secondary = NaranjaAmbar,
    onSecondary = BlancoPuro,
    secondaryContainer = AmarilloClaro,
    onSecondaryContainer = TextoOscuro,

    tertiary = AmarilloDorado,
    onTertiary = TextoOscuro,
    tertiaryContainer = AmarilloClaro,
    onTertiaryContainer = TextoOscuro,

    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = BlancoPuro,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFFECEFF6),
    onSurfaceVariant = TextoSecundario,

    error = RojoAlerta,
    onError = BlancoPuro,
    errorContainer = RojoAlertaFondo,
    onErrorContainer = Color(0xFF8B1D1F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8C9EFF),
    onPrimary = Color(0xFF141B4D),
    primaryContainer = AzulIndigoOscuro,
    onPrimaryContainer = AzulSuave,

    secondary = AmarilloDorado,
    onSecondary = TextoOscuro,
    secondaryContainer = Color(0xFF4A3B10),
    onSecondaryContainer = AmarilloClaro,

    background = Color(0xFF12151F),
    onBackground = Color(0xFFE8ECF4),
    surface = Color(0xFF1C2030),
    onSurface = Color(0xFFE8ECF4),
    surfaceVariant = Color(0xFF262B3D),
    onSurfaceVariant = Color(0xFFB3BAC8)
)

@Composable
fun GestionMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}