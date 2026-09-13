package com.zakeercareer.calculator.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Material 3 Expressive Extended Theme Tokens for Calculator Specific Components
 */
@Immutable
data class ExtendedCalcColors(
    val operatorContainer: Color,
    val onOperatorContainer: Color,
    val scientificFunctionContainer: Color,
    val onScientificFunctionContainer: Color,
    val glassBorder: Color,
    val glassBackground: Color,
    val historyGridline: Color,
    val displaySecondaryText: Color
)

val LocalExtendedCalcColors = staticCompositionLocalOf {
    ExtendedCalcColors(
        operatorContainer = Color.Unspecified,
        onOperatorContainer = Color.Unspecified,
        scientificFunctionContainer = Color.Unspecified,
        onScientificFunctionContainer = Color.Unspecified,
        glassBorder = Color.Unspecified,
        glassBackground = Color.Unspecified,
        historyGridline = Color.Unspecified,
        displaySecondaryText = Color.Unspecified
    )
}
