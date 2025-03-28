package br.com.wheatherApp.ui.theme

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
    primary = Color(0xFF5D9CEC),      // Azul claro para elementos principais
    secondary = Color(0xFF4A89DC),    // Azul um pouco mais escuro
    tertiary = Color(0xFF3BAFBF),     // Tons de azul-verde para contraste
    background = Color(0xFF121212),   // Fundo muito escuro, quase preto
    surface = Color(0xFF1E1E1E),      // Superfícies em tom de cinza escuro
    onPrimary = Color.White,          // Texto sobre elementos primários
    onSecondary = Color.White,        // Texto sobre elementos secundários
    onBackground = Color(0xFFE0E0E0), // Texto de fundo
    onSurface = Color.White           // Texto em superfícies
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun WheaterAppTheme(
    darkTheme: Boolean = true,  // Definindo como padrão o Dark Theme
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