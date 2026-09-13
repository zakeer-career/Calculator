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
 * Expressive Glassmorphism Modifier using Android 12+ RenderEffect.createBlurEffect
 * with fallback border/background clipping for legacy Android versions.
 */
fun Modifier.expressiveGlassSurface(
    shape: Shape = ExpressiveShapes.large,
    backgroundColor: Color = Color(0xCC111318),
    borderColor: Color = Color(0x38FFFFFF),
    borderWidth: Dp = 1.2.dp,
    blurRadius: Float = 25f
): Modifier {
    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

    return this
        .clip(shape)
        .then(blurModifier)
        .background(backgroundColor)
        .border(
            width = borderWidth,
            brush = Brush.verticalGradient(
                colors = listOf(borderColor, borderColor.copy(alpha = 0.1f))
            ),
            shape = shape
        )
}
