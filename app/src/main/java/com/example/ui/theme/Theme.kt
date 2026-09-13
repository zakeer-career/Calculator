package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

object CalcTheme {
    val extendedColors: ExtendedCalcColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedCalcColors.current
}

@Composable
fun RemixCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themePreset: String = "MATERIAL_YOU",
    adaptiveThemeEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val presetState = CalculatorThemeState.getPreset(themePreset)

    val (colorScheme, extendedColors) = if (themePreset == "MATERIAL_YOU" && dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val scheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        val ext = if (darkTheme) DarkExtendedCalcColors else LightExtendedCalcColors
        scheme to ext
    } else {
        val scheme = presetState.colorScheme
        val ext = if (darkTheme || presetState.isDark) DarkExtendedCalcColors else LightExtendedCalcColors
        scheme to ext
    }

    val updatedPresetState = presetState.copy(colorScheme = colorScheme)

    CompositionLocalProvider(
        LocalCalculatorThemeState provides updatedPresetState,
        LocalExtendedCalcColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ExpressiveTypography,
            shapes = ExpressiveShapes,
            content = content
        )
    }
}

@Composable
fun AdvancedCalculatorTheme(
    themePreset: String = "MATERIAL_YOU",
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    adaptiveThemeEnabled: Boolean = false,
    content: @Composable () -> Unit
) = RemixCalculatorTheme(
    darkTheme = darkTheme,
    dynamicColor = dynamicColor,
    themePreset = themePreset,
    adaptiveThemeEnabled = adaptiveThemeEnabled,
    content = content
)
