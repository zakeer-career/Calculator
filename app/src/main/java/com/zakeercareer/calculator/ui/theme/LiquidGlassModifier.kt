package com.zakeercareer.calculator.ui.theme

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern Android Liquid Glassmorphism modifier extension.
 * Provides authentic optical physics:
 * 1. Dual-pass internal specular refraction gradient (simulating light bouncing inside frosted acrylic/glass).
 * 2. Directional specular prism highlight rim border.
 * 3. Top-edge inner glow reflection.
 * 4. Hardware RenderEffect blur for Android 12+ (API 31+).
 */
fun Modifier.liquidGlass(
    shape: Shape,
    blurRadius: Float = 24f,
    tintTopColor: Color = Color.White.copy(alpha = 0.22f),
    tintBottomColor: Color = Color.White.copy(alpha = 0.08f),
    borderTopColor: Color = Color.White.copy(alpha = 0.55f),
    borderBottomColor: Color = Color.White.copy(alpha = 0.12f),
    borderWidth: Dp = 1.2.dp,
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this

    // Hardware RenderEffect blur for API 31+ (Android 12+)
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

    // Specular highlight rim: bright light reflection at top angled edge, fading toward bottom
    val specularBorderBrush = Brush.linearGradient(
        colors = listOf(
            borderTopColor,
            borderTopColor.copy(alpha = (borderTopColor.alpha * 0.7f)),
            borderBottomColor
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Internal refraction gradient simulating physical glass thickness
    val internalGlassBrush = Brush.verticalGradient(
        colors = listOf(
            tintTopColor,
            tintBottomColor
        )
    )

    return this
        .clip(shape)
        .background(brush = internalGlassBrush)
        .drawBehind {
            // Optical prism dispersion highlight: an elliptical radial sheen near upper left corner
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.28f),
                        Color.White.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.28f, size.height * 0.15f),
                    radius = size.width * 0.75f
                )
            )
            // Secondary specular rim glow along the bottom edge
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.7f, size.height * 0.95f),
                    radius = size.width * 0.45f
                )
            )
        }
        .then(blurModifier)
        .border(
            width = borderWidth,
            brush = specularBorderBrush,
            shape = shape
        )
}
