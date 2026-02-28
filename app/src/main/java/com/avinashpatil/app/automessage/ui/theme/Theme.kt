package com.avinashpatil.app.automessage.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightBackground,
    surface = LightBackground
)

// Neumorphic color scheme for system bars
private val NeumorphicStatusBarColor = NeoSurface
private val NeumorphicNavigationBarColor = NeoSurface

@Composable
fun AutoMessageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val base = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            if (darkTheme) base else base.copy(background = LightBackground, surface = LightBackground)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Subtle blended colors to simulate depth on system bars
    fun blendColors(a: Color, b: Color, ratio: Float): Color {
        val r = a.red * (1f - ratio) + b.red * ratio
        val g = a.green * (1f - ratio) + b.green * ratio
        val bl = a.blue * (1f - ratio) + b.blue * ratio
        val alpha = a.alpha * (1f - ratio) + b.alpha * ratio
        return Color(r, g, bl, alpha)
    }
    val statusBarColor = blendColors(NeumorphicStatusBarColor, NeoLightBackground, 0.2f)
    val navigationBarColor = blendColors(NeumorphicNavigationBarColor, NeoLightBackground, 0.35f)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Apply neumorphic styling to system bars using subtle blended tones
            window.statusBarColor = statusBarColor.toArgb()
            window.navigationBarColor = navigationBarColor.toArgb()

            // Use dark icons for better legibility on light bars
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}