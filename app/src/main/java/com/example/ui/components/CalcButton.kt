package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalcTheme
import com.example.ui.theme.CalculatorTheme
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

fun getAccessibleCalcLabel(text: String): String {
    return when (text) {
        "×", "*" -> "Multiply"
        "÷", "/" -> "Divide"
        "−", "-" -> "Minus"
        "+" -> "Plus"
        "=" -> "Equals"
        "%" -> "Percent"
        "√" -> "Square root"
        "^" -> "Power of"
        "!" -> "Factorial"
        "π" -> "Pi constant"
        "e" -> "Euler constant"
        "C" -> "Clear"
        "AC" -> "All clear"
        "⌫" -> "Backspace"
        "±" -> "Plus minus toggle"
        "DEG" -> "Degree mode"
        "RAD" -> "Radian mode"
        "sin" -> "Sine"
        "cos" -> "Cosine"
        "tan" -> "Tangent"
        "asin" -> "Arc sine"
        "acos" -> "Arc cosine"
        "atan" -> "Arc tangent"
        "sinh" -> "Hyperbolic sine"
        "cosh" -> "Hyperbolic cosine"
        "tanh" -> "Hyperbolic tangent"
        "log" -> "Base 10 logarithm"
        "ln" -> "Natural logarithm"
        "MC" -> "Memory clear"
        "MR" -> "Memory recall"
        "M+" -> "Memory add"
        "M-" -> "Memory subtract"
        "MS" -> "Memory store"
        "(" -> "Open parenthesis"
        ")" -> "Close parenthesis"
        "." -> "Decimal point"
        "," -> "Comma"
        else -> text
    }
}

@Composable
fun ExpressiveCalcButton(
    text: String,
    type: CalcButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = CalcTheme.extendedColors
    val shapes = MaterialTheme.shapes
    val typography = MaterialTheme.typography

    val containerColor = when (type) {
        CalcButtonType.NUMBER -> MaterialTheme.colorScheme.surfaceContainerHigh
        CalcButtonType.OPERATOR -> extendedColors.operatorContainer
        CalcButtonType.SCIENTIFIC -> extendedColors.scientificFunctionContainer
        CalcButtonType.ACTION -> MaterialTheme.colorScheme.errorContainer
        CalcButtonType.EQUALS -> MaterialTheme.colorScheme.primary
    }

    val contentColor = when (type) {
        CalcButtonType.NUMBER -> MaterialTheme.colorScheme.onSurface
        CalcButtonType.OPERATOR -> extendedColors.onOperatorContainer
        CalcButtonType.SCIENTIFIC -> extendedColors.onScientificFunctionContainer
        CalcButtonType.ACTION -> MaterialTheme.colorScheme.onErrorContainer
        CalcButtonType.EQUALS -> MaterialTheme.colorScheme.onPrimary
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = getAccessibleCalcLabel(text) },
        shape = shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = typography.titleLarge
        )
    }
}

enum class CalcButtonType {
    NUMBER,
    OPERATOR,
    SCIENTIFIC,
    ACTION,
    EQUALS
}

@Composable
fun CalcButton(
    text: String,
    type: CalcButtonType,
    modifier: Modifier = Modifier,
    testTag: String = "",
    isCompact: Boolean = false,
    customCornerRadiusDp: Int? = null,
    customFontSizeSp: Int? = null,
    btnShape: String? = null,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val activeTheme = CalculatorTheme.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile M3 Expressive press spring scaling
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.91f else 1.0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "calc_button_press_scale"
    )

    // Vibrant Material 3 Expressive Color Mappings
    val containerColor = when (type) {
        CalcButtonType.NUMBER -> MaterialTheme.colorScheme.surfaceContainerHigh
        CalcButtonType.OPERATOR -> MaterialTheme.colorScheme.primaryContainer
        CalcButtonType.SCIENTIFIC -> MaterialTheme.colorScheme.tertiaryContainer
        CalcButtonType.ACTION -> MaterialTheme.colorScheme.errorContainer
        CalcButtonType.EQUALS -> MaterialTheme.colorScheme.primary
    }

    val contentColor = when (type) {
        CalcButtonType.NUMBER -> MaterialTheme.colorScheme.onSurface
        CalcButtonType.OPERATOR -> MaterialTheme.colorScheme.onPrimaryContainer
        CalcButtonType.SCIENTIFIC -> MaterialTheme.colorScheme.onTertiaryContainer
        CalcButtonType.ACTION -> MaterialTheme.colorScheme.onErrorContainer
        CalcButtonType.EQUALS -> MaterialTheme.colorScheme.onPrimary
    }

    // Border & Bevel Styling
    val borderStroke = when {
        activeTheme.hasNeumorphicStyle -> BorderStroke(
            1.2.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
        activeTheme.hasGlassmorphism -> BorderStroke(
            1.dp,
            androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(
                    if (isPressed) Color.White.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.45f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
        )
        type == CalcButtonType.EQUALS -> BorderStroke(
            1.2.dp,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        )
        type == CalcButtonType.OPERATOR -> BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
        )
        type == CalcButtonType.ACTION -> BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
        )
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
    }

    // Shape customization based on active theme or override
    val shapeStyle = btnShape ?: activeTheme.buttonShapeStyle
    val shape = when {
        shapeStyle == "PILL" || shapeStyle == "CIRCLE" -> CircleShape
        shapeStyle == "SQUARE" -> RoundedCornerShape(8.dp)
        shapeStyle == "BEVELED" -> RoundedCornerShape(14.dp)
        shapeStyle == "NEUMORPHIC" || activeTheme.hasGlassmorphism -> RoundedCornerShape(20.dp)
        customCornerRadiusDp != null && customCornerRadiusDp >= 0 -> RoundedCornerShape(customCornerRadiusDp.dp)
        else -> RoundedCornerShape(activeTheme.keypadBtnCornerRadiusDp.dp)
    }

    // Font Sizing
    val fontSize = if (customFontSizeSp != null && customFontSizeSp > 0) {
        customFontSizeSp.sp
    } else if (isCompact) {
        if (text.length > 3) 12.sp else 16.sp
    } else {
        if (text.length > 3) 15.sp else 21.sp
    }

    // Dynamic Elevation
    val defaultElevation = when {
        type == CalcButtonType.EQUALS -> 6.dp
        activeTheme.hasNeumorphicStyle -> 4.dp
        activeTheme.hasGlassmorphism -> 1.dp
        else -> 2.dp
    }

    val elevation = ButtonDefaults.buttonElevation(
        defaultElevation = defaultElevation,
        pressedElevation = 0.dp
    )

    val shadowModifier = if (activeTheme.hasNeumorphicStyle && !isPressed) {
        Modifier.shadow(elevation = 4.dp, shape = shape, clip = false)
    } else {
        Modifier
    }

    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxSize()
            .scale(pressScale)
            .then(shadowModifier)
            .semantics { contentDescription = getAccessibleCalcLabel(text) }
            .testTag(testTag),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = elevation,
        border = borderStroke,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = when (type) {
                CalcButtonType.EQUALS, CalcButtonType.OPERATOR -> FontWeight.Bold
                CalcButtonType.ACTION -> FontWeight.SemiBold
                else -> FontWeight.Medium
            }
        )
    }
}
