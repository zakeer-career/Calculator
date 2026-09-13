package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalculatorTheme

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.IconButton
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun DisplayPanel(
    expression: String,
    previewResult: String,
    isDegreeMode: Boolean,
    memoryValue: Double,
    onToggleDegree: () -> Unit,
    textFieldValue: TextFieldValue? = null,
    onExpressionValueChange: ((TextFieldValue) -> Unit)? = null,
    onCalculate: (() -> Unit)? = null,
    onDeleteChar: (() -> Unit)? = null,
    numberAnimationType: String = "OFF",
    showLivePreview: Boolean = true,
    livePreviewAnimEnabled: Boolean = false,
    adaptiveDisplayResizing: Boolean = true,
    onIncreaseHeight: (() -> Unit)? = null,
    onDecreaseHeight: (() -> Unit)? = null,
    displayFontSize: String = "NORMAL",
    displayAlign: String = "RIGHT",
    customHeightDp: Int = 135,
    customWidthPaddingDp: Int = 12,
    customCornerRadiusDp: Int = 28,
    customMainFontSizeSp: Int = 34,
    customPreviewFontSizeSp: Int = 24,
    lockKeypadHeight: Boolean = true,
    modifier: Modifier = Modifier
) {
    val activeTheme = CalculatorTheme.current
    val currentTfv = textFieldValue ?: remember(expression) {
        TextFieldValue(text = expression, selection = TextRange(expression.length))
    }
    val scrollState = rememberScrollState()
    LaunchedEffect(currentTfv.text) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val mainTextSize = if (customMainFontSizeSp > 0) {
        customMainFontSizeSp.sp
    } else {
        when (displayFontSize) {
            "SMALL" -> 28.sp
            "LARGE" -> 42.sp
            else -> 34.sp
        }
    }

    val previewTextSize = if (customPreviewFontSizeSp > 0) {
        customPreviewFontSizeSp.sp
    } else {
        when (displayFontSize) {
            "SMALL" -> 18.sp
            "LARGE" -> 28.sp
            else -> 24.sp
        }
    }

    val textAlignment = when (displayAlign) {
        "LEFT" -> TextAlign.Left
        "CENTER" -> TextAlign.Center
        else -> TextAlign.Right
    }

    val shapeRadius = if (customCornerRadiusDp > 0) {
        customCornerRadiusDp.dp
    } else {
        activeTheme.displayCornerRadiusDp.dp
    }

    // Card background brush & border adaptiveness
    val cardBackground = if (activeTheme.hasRetroGlow) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D2115),
                Color(0xFF07140C)
            )
        )
    } else if (activeTheme.hasGlassmorphism) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x2EFFFFFF),
                Color(0x12FFFFFF)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceContainerHighest,
                MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f)
            )
        )
    }

    val borderStroke = when {
        activeTheme.hasRetroGlow -> BorderStroke(1.5.dp, Color(0xFF39FF14).copy(alpha = 0.4f))
        activeTheme.hasGlassmorphism -> BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.5f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
        )
        else -> BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                )
            )
        )
    }

    val mainTextColor = if (activeTheme.hasRetroGlow) {
        Color(0xFF39FF14) // Digital Green Phosphor
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val resultTextColor = if (activeTheme.hasRetroGlow) {
        Color(0xFFFF9900) // 80s Amber
    } else {
        MaterialTheme.colorScheme.primary
    }

    val extraAdaptiveHeight = if (adaptiveDisplayResizing && currentTfv.text.length > 18) {
        ((currentTfv.text.length - 18) / 8 * 18).coerceAtMost(140)
    } else 0
    val effectiveMinHeightDp = (customHeightDp + extraAdaptiveHeight).coerceIn(80, 320)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = customWidthPaddingDp.dp)
            .heightIn(min = if (lockKeypadHeight) 80.dp else effectiveMinHeightDp.dp)
            .testTag("display_panel_card"),
        shape = RoundedCornerShape(shapeRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = cardBackground)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status bar row (RAD/DEG, Size controls, Memory indicator, Backspace button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleDegree()
                            },
                            label = {
                                Text(
                                    text = if (isDegreeMode) "DEG" else "RAD",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Functions,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.testTag("deg_rad_chip")
                        )

                        // Top Display Size Adjustment Controls (- / + height)
                        if (onIncreaseHeight != null || onDecreaseHeight != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    if (onDecreaseHeight != null) {
                                        Text(
                                            text = "-",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onDecreaseHeight()
                                                }
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                .testTag("display_size_decrease_btn")
                                        )
                                    }
                                    Text(
                                        text = "${effectiveMinHeightDp}dp",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (onIncreaseHeight != null) {
                                        Text(
                                            text = "+",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onIncreaseHeight()
                                                }
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                .testTag("display_size_increase_btn")
                                        )
                                    }
                                }
                            }
                        }

                        if (memoryValue != 0.0) {
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = "M = $memoryValue",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                                modifier = Modifier.testTag("memory_chip")
                            )
                        }
                    }

                    if (currentTfv.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDeleteChar?.invoke()
                            },
                            modifier = Modifier.testTag("display_backspace_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Editable Expression Display with direct cursor placement, selection toolbar, and soft keyboard suppressed
                val textSelectionColors = TextSelectionColors(
                    handleColor = if (activeTheme.hasRetroGlow) Color(0xFF39FF14) else MaterialTheme.colorScheme.primary,
                    backgroundColor = (if (activeTheme.hasRetroGlow) Color(0xFF39FF14) else MaterialTheme.colorScheme.primary).copy(alpha = 0.35f)
                )

                val softwareKeyboardController = LocalSoftwareKeyboardController.current

                var numberPulseTrigger by remember { mutableStateOf(false) }
                LaunchedEffect(currentTfv.text) {
                    if (currentTfv.text.isNotEmpty() && numberAnimationType != "OFF") {
                        numberPulseTrigger = true
                        kotlinx.coroutines.delay(120)
                        numberPulseTrigger = false
                    }
                }

                val isAnimEnabled = numberAnimationType != "OFF"
                val numberEntryScale by animateFloatAsState(
                    targetValue = if (isAnimEnabled && numberPulseTrigger) 1.06f else 1.0f,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMedium,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    ),
                    label = "number_entry_scale"
                )

                CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                            .graphicsLayer {
                                if (numberAnimationType == "SPRING_BOUNCE" || numberAnimationType == "SCALE_POP") {
                                    scaleX = numberEntryScale
                                    scaleY = numberEntryScale
                                }
                            }
                            .testTag("expression_field_container"),
                        contentAlignment = when (displayAlign) {
                            "LEFT" -> Alignment.CenterStart
                            "CENTER" -> Alignment.Center
                            else -> Alignment.CenterEnd
                        }
                    ) {
                        if (numberAnimationType == "VERTICAL_SLIDE") {
                            AnimatedContent(
                                targetState = currentTfv.text,
                                transitionSpec = {
                                    (slideInVertically { height -> height / 2 } + fadeIn()) togetherWith
                                            (slideOutVertically { height -> -height / 2 } + fadeOut())
                                },
                                label = "slide_anim"
                            ) { _ ->
                                BasicTextField(
                                    value = currentTfv,
                                    onValueChange = { newTfv ->
                                        softwareKeyboardController?.hide()
                                        onExpressionValueChange?.invoke(newTfv)
                                    },
                                    readOnly = false,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused) {
                                                softwareKeyboardController?.hide()
                                            }
                                        }
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = {
                                                    softwareKeyboardController?.hide()
                                                }
                                            )
                                        }
                                        .testTag("expression_text"),
                                    textStyle = TextStyle(
                                        fontSize = mainTextSize,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = mainTextColor,
                                        textAlign = textAlignment
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(if (activeTheme.hasRetroGlow) Color(0xFF39FF14) else MaterialTheme.colorScheme.primary),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { onCalculate?.invoke() }
                                    )
                                )
                            }
                        } else if (numberAnimationType == "FADE_PULSE") {
                            AnimatedContent(
                                targetState = currentTfv.text,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "fade_anim"
                            ) { _ ->
                                BasicTextField(
                                    value = currentTfv,
                                    onValueChange = { newTfv ->
                                        softwareKeyboardController?.hide()
                                        onExpressionValueChange?.invoke(newTfv)
                                    },
                                    readOnly = false,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused) {
                                                softwareKeyboardController?.hide()
                                            }
                                        }
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = {
                                                    softwareKeyboardController?.hide()
                                                }
                                            )
                                        }
                                        .testTag("expression_text"),
                                    textStyle = TextStyle(
                                        fontSize = mainTextSize,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = mainTextColor,
                                        textAlign = textAlignment
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(if (activeTheme.hasRetroGlow) Color(0xFF39FF14) else MaterialTheme.colorScheme.primary),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { onCalculate?.invoke() }
                                    )
                                )
                            }
                        } else {
                            BasicTextField(
                                value = currentTfv,
                                onValueChange = { newTfv ->
                                    softwareKeyboardController?.hide()
                                    onExpressionValueChange?.invoke(newTfv)
                                },
                                readOnly = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        if (focusState.isFocused) {
                                            softwareKeyboardController?.hide()
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                softwareKeyboardController?.hide()
                                            }
                                        )
                                    }
                                    .testTag("expression_text"),
                                textStyle = TextStyle(
                                    fontSize = mainTextSize,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = mainTextColor,
                                    textAlign = textAlignment
                                ),
                                singleLine = true,
                                cursorBrush = SolidColor(if (activeTheme.hasRetroGlow) Color(0xFF39FF14) else MaterialTheme.colorScheme.primary),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { onCalculate?.invoke() }
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = when (displayAlign) {
                                            "LEFT" -> Alignment.CenterStart
                                            "CENTER" -> Alignment.Center
                                            else -> Alignment.CenterEnd
                                        }
                                    ) {
                                        if (currentTfv.text.isEmpty()) {
                                            Text(
                                                text = "0",
                                                fontSize = mainTextSize,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = FontFamily.Monospace,
                                                color = mainTextColor.copy(alpha = 0.4f),
                                                textAlign = textAlignment,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }
                }

                // Result Preview (Tap to copy) with Spring Scale & Slide Number Animation
                if (showLivePreview) {
                    val displayResult = if (previewResult == expression) "" else previewResult
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = displayResult.isNotBlank()) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                clipboardManager.setText(AnnotatedString(displayResult))
                                Toast.makeText(context, "Copied result: $displayResult", Toast.LENGTH_SHORT).show()
                            },
                        horizontalArrangement = if (displayAlign == "LEFT") Arrangement.Start else Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (displayResult.isNotBlank()) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy result",
                                tint = resultTextColor.copy(alpha = 0.7f),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                        if (livePreviewAnimEnabled) {
                            AnimatedContent(
                                targetState = displayResult,
                                transitionSpec = {
                                    (scaleIn(
                                        initialScale = 0.8f,
                                        animationSpec = spring(
                                            stiffness = Spring.StiffnessMedium,
                                            dampingRatio = Spring.DampingRatioMediumBouncy
                                        )
                                    ) + slideInVertically { height -> height / 2 } + fadeIn()) togetherWith
                                            (scaleOut(targetScale = 1.1f) + slideOutVertically { height -> -height / 2 } + fadeOut())
                                },
                                label = "preview_result_animation"
                            ) { animatedResult ->
                                Text(
                                    text = animatedResult,
                                    fontSize = previewTextSize,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = resultTextColor,
                                    textAlign = textAlignment,
                                    modifier = Modifier.testTag("preview_result_text")
                                )
                            }
                        } else {
                            Text(
                                text = displayResult,
                                fontSize = previewTextSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = resultTextColor,
                                textAlign = textAlignment,
                                modifier = Modifier.testTag("preview_result_text")
                            )
                        }
                    }
                }
            }
        }
    }
}
