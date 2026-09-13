package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NavigationTabItem(
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun LiquidGlassBottomBar(
    items: List<NavigationTabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    iconOnly: Boolean = false,
    indicatorSize: String = "STANDARD",
    indicatorScale: Float = 1.0f,
    navBarStyle: String = "LIQUID_GLASS",
    blurOpacity: Float = 0.85f,
    animSpeed: String = "BOUNCY"
) {
    if (items.isEmpty()) return

    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    val barHeight = when {
        iconOnly -> 58.dp
        navBarStyle == "MINIMAL_BUBBLE" -> 62.dp
        navBarStyle == "PILL" -> 64.dp
        else -> 70.dp
    }

    val isBarInvisible = navBarStyle == "INVISIBLE" || navBarStyle == "TRANSPARENT" || blurOpacity == 0f

    val outerShape = when {
        isBarInvisible -> RoundedCornerShape(32.dp)
        navBarStyle == "MINIMAL_BUBBLE" || navBarStyle == "PILL" -> CircleShape
        navBarStyle == "M3_EXPRESSIVE_DOCK" || navBarStyle == "M3DOCK" -> RoundedCornerShape(28.dp)
        navBarStyle == "M3_STANDARD" -> RoundedCornerShape(18.dp)
        else -> RoundedCornerShape(32.dp) // LIQUID_GLASS
    }

    val springSpec = remember(animSpeed) {
        when (animSpeed.uppercase()) {
            "SMOOTH_GLIDE", "SMOOTH" -> spring<Float>(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy)
            "FAST" -> spring<Float>(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioNoBouncy)
            "SNAPPY" -> spring<Float>(stiffness = Spring.StiffnessHigh, dampingRatio = Spring.DampingRatioLowBouncy)
            else -> spring<Float>(stiffness = Spring.StiffnessLow, dampingRatio = 0.52f) // BOUNCY
        }
    }

    val colorSpringSpec = remember(animSpeed) {
        when (animSpeed.uppercase()) {
            "FAST", "SNAPPY" -> spring<Color>(stiffness = Spring.StiffnessMedium)
            else -> spring<Color>(stiffness = Spring.StiffnessLow)
        }
    }

    val containerAlpha = blurOpacity.coerceIn(0.0f, 1.0f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = when (navBarStyle) {
                    "MINIMAL_BUBBLE" -> 24.dp
                    "PILL" -> 20.dp
                    "M3_STANDARD" -> 8.dp
                    else -> 12.dp
                },
                vertical = 6.dp
            )
            .height(barHeight)
            .clip(outerShape),
        shape = outerShape,
        color = if (isBarInvisible) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = containerAlpha.coerceIn(0.2f, 0.95f)),
        border = if (isBarInvisible) null else BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.42f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
        ),
        shadowElevation = if (isBarInvisible) 0.dp else 8.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(4.dp)
        ) {
            val totalWidth = maxWidth
            val itemCount = items.size
            val tabWidth = totalWidth / itemCount

            // Indicator dimensions calculation matching user preferences
            val normalizedIndicatorSize = indicatorSize.uppercase()
            val rawIndicatorWidth = when (normalizedIndicatorSize) {
                "COMPACT", "SMALL" -> (tabWidth * 0.52f * indicatorScale)
                "WIDE", "LARGE" -> (tabWidth * 0.90f * indicatorScale)
                "CIRCLE" -> 44.dp * indicatorScale
                else -> (tabWidth * 0.74f * indicatorScale) // STANDARD
            }
            val indicatorWidth = rawIndicatorWidth.coerceAtMost(tabWidth - 4.dp)
            val indicatorHeight = if (iconOnly) {
                42.dp * indicatorScale.coerceAtMost(1.1f)
            } else if (normalizedIndicatorSize == "CIRCLE") {
                44.dp * indicatorScale
            } else {
                50.dp * indicatorScale.coerceAtMost(1.1f)
            }

            val indicatorCorner = if (normalizedIndicatorSize == "CIRCLE" || navBarStyle == "PILL") CircleShape else RoundedCornerShape(22.dp)

            val indicatorWidthPx = with(density) { indicatorWidth.toPx() }
            val tabWidthPx = with(density) { tabWidth.toPx() }

            // Target X position in Px for GPU translationX
            val validIndex = selectedIndex.coerceIn(0, itemCount - 1)
            val targetOffsetPx = (tabWidthPx * validIndex) + ((tabWidthPx - indicatorWidthPx) / 2f)

            val animatedOffsetPx by animateFloatAsState(
                targetValue = targetOffsetPx,
                animationSpec = springSpec,
                label = "nav_indicator_offset_px"
            )

            // GPU-Accelerated Hardware Layer Sliding Selected Indicator (RPhone style primaryContainer background)
            Box(
                modifier = Modifier
                    .width(indicatorWidth)
                    .height(indicatorHeight)
                    .align(Alignment.CenterStart)
                    .graphicsLayer {
                        translationX = animatedOffsetPx
                    }
                    .clip(indicatorCorner)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.82f)
                            )
                        )
                    )
            )

            // Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    // RPhone-style Press scale (~0.85f / ~0.82f) and spring return on release
                    val targetScale = when {
                        isPressed -> if (navBarStyle == "PILL" || navBarStyle == "MINIMAL_BUBBLE") 0.82f else 0.85f
                        isSelected -> 1.12f
                        else -> 1.0f
                    }

                    val itemScale by animateFloatAsState(
                        targetValue = targetScale,
                        animationSpec = springSpec,
                        label = "tab_item_scale"
                    )

                    // RPhone-style smooth color transitions
                    val animatedIconColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = colorSpringSpec,
                        label = "tab_icon_color"
                    )

                    val animatedTextColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = colorSpringSpec,
                        label = "tab_text_color"
                    )

                    val labelAlpha by animateFloatAsState(
                        targetValue = if (iconOnly) 0f else 1f,
                        animationSpec = tween(durationMillis = 180),
                        label = "label_alpha"
                    )

                    Box(
                        modifier = Modifier
                            .width(tabWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(index)
                            }
                            .testTag(item.tag),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.graphicsLayer {
                                scaleX = itemScale
                                scaleY = itemScale
                            }
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = animatedIconColor,
                                modifier = Modifier.size(24.dp)
                            )

                            if (!iconOnly && navBarStyle != "MINIMAL_BUBBLE") {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = animatedTextColor,
                                    modifier = Modifier.alpha(labelAlpha)
                                )
                            } else if (isSelected && navBarStyle == "MINIMAL_BUBBLE") {
                                Spacer(modifier = Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(animatedIconColor)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
