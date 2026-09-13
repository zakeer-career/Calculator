package com.zakeercareer.calculator.ui.theme

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern Android Liquid Glassmorphism modifier extension.
 * Applies backdrop blur (API 31+), directional specular gradient highlight border,
 * and semi-transparent refraction tint overlay.
 */
fun Modifier.liquidGlass(
    shape: Shape,
    blurRadius: Float = 25f,
    tintColor: Color = Color.White.copy(alpha = 0.12f),
    borderTopColor: Color = Color.White.copy(alpha = 0.45f),
    borderBottomColor: Color = Color.White.copy(alpha = 0.08f),
    borderWidth: Dp = 1.dp,
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this

    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurRadius > 0f) {
        Modifier.graphicsLayer {
            renderEffect = RenderEffect.createBlurEffect(
                blurRadius,
                blurRadius,
                Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        }
    } else {
        Modifier
    }

    val specularBorder = Brush.verticalGradient(
        colors = listOf(
            borderTopColor,
            borderBottomColor
        )
    )

    return this
        .clip(shape)
        .then(blurModifier)
        .background(tintColor)
        .border(
            width = borderWidth,
            brush = specularBorder,
            shape = shape
        )
}
