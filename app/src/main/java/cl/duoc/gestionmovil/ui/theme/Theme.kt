package cl.duoc.gestionmovil.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MoradoClaro,
    onPrimary = Color(0xFF2A004E),
    primaryContainer = MoradoPrincipal,
    onPrimaryContainer = MoradoContenedor,
    secondary = NaranjaClaro,
    onSecondary = Color(0xFF3E1500),
    secondaryContainer = NaranjaOscuro,
    onSecondaryContainer = NaranjaContenedor,
    tertiary = NaranjaOscuro,
    onTertiary = Color.White,
    background = FondoAppOscuro,
    surface = SuperficieOscura,
    onBackground = Color(0xFFEDE7F6),
    onSurface = Color(0xFFEDE7F6)
)

private val LightColorScheme = lightColorScheme(
    primary = MoradoPrincipal,
    onPrimary = Color.White,
    primaryContainer = MoradoContenedor,
    onPrimaryContainer = MoradoPrincipal,
    secondary = NaranjaOscuro,
    onSecondary = Color.White,
    secondaryContainer = NaranjaContenedor,
    onSecondaryContainer = NaranjaQuemado,
    tertiary = NaranjaQuemado,
    onTertiary = Color.White,
    tertiaryContainer = NaranjaContenedor,
    onTertiaryContainer = NaranjaQuemado,
    background = FondoAppClaro,
    surface = SuperficieClara,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro
)

@Composable
fun GestionMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // En false para que siempre se muestren el morado y naranjo oscuro en Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}