package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.currency.CurrencyInfo
import com.example.data.currency.CurrencyRepository
import com.example.data.currency.ExchangeRatesState
import com.example.data.currency.defaultCurrencies
import com.example.ui.screens.CurrencyPickerDialog
import com.example.util.MathEvaluator

@Composable
fun CurrencyHeaderToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val targetPx = remember(density) { with(density) { 20.dp.toPx() } }

    val thumbOffsetPx by animateFloatAsState(
        targetValue = if (checked) targetPx else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "toggle_thumb_offset_px"
    )

    Surface(
        onClick = { onCheckedChange(!checked) },
        shape = RoundedCornerShape(50),
        color = if (checked) Color(0xFF00E676) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        border = BorderStroke(
            0.8.dp,
            if (checked) Color(0xFF00C853) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .width(44.dp)
            .height(24.dp)
            .testTag("currency_header_toggle_switch")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        translationX = thumbOffsetPx
                    }
            ) {}
        }
    }
}

@Composable
fun FuturisticCalcCurrencyBar(
    expression: String,
    previewResult: String,
    fromCurrency: CurrencyInfo,
    toCurrency: CurrencyInfo,
    exchangeState: ExchangeRatesState,
    decimals: Int = 2,
    showToggle: Boolean = true,
    isToggleActive: Boolean = true,
    toggleAlignment: String = "LEFT",
    showGuide: Boolean = true,
    showThemeStudio: Boolean = true,
    toggleEnabledAction: String = "LIVE_CONVERT",
    toggleDisabledAction: String = "HIDE_RATE_PILL",
    onToggleActiveChanged: (Boolean) -> Unit = {},
    onSelectFromCurrency: (CurrencyInfo) -> Unit,
    onSelectToCurrency: (CurrencyInfo) -> Unit,
    onSwapCurrencies: () -> Unit,
    onInsertValueToCalc: (String) -> Unit,
    onOpenCurrencySettings: () -> Unit,
    onOpenThemeStudio: () -> Unit,
    onOpenGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    // Parse active value from preview result or expression
    val numericValue = remember(previewResult, expression) {
        val target = if (previewResult.isNotBlank() && previewResult != "Error" && previewResult != "...") {
            previewResult.replace(",", "")
        } else {
            expression.replace(",", "")
        }
        target.toDoubleOrNull() ?: 1.0
    }

    val convertedVal = remember(numericValue, fromCurrency, toCurrency, exchangeState.rates) {
        CurrencyRepository.convertCurrency(numericValue, fromCurrency.code, toCurrency.code, exchangeState.rates)
    }

    val baseRateVal = remember(fromCurrency, toCurrency, exchangeState.rates) {
        CurrencyRepository.convertCurrency(1.0, fromCurrency.code, toCurrency.code, exchangeState.rates)
    }

    val formattedConverted = remember(convertedVal, decimals) {
        if (decimals == -1) {
            MathEvaluator.formatNumber(convertedVal)
        } else if (decimals == 0) {
            String.format(java.util.Locale.US, "%.0f", convertedVal)
        } else {
            String.format(java.util.Locale.US, "%.${decimals}f", convertedVal)
        }
    }

    val formattedBaseRate = remember(baseRateVal, decimals) {
        if (decimals == -1) MathEvaluator.formatNumber(baseRateVal) else String.format(java.util.Locale.US, "%.${decimals.coerceAtLeast(2)}f", baseRateVal)
    }

    // Glassmorphic futuristic bar container
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("futuristic_currency_bar"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f)
        ),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Quick Toggle Switch + Live Currency Selector Pills
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showToggle && toggleAlignment == "LEFT") {
                    CurrencyHeaderToggleSwitch(
                        checked = isToggleActive,
                        onCheckedChange = onToggleActiveChanged
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }

                // From Currency Chip
                Surface(
                    onClick = { showFromPicker = true },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("currency_from_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = fromCurrency.flag, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = fromCurrency.code,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Swap Button
                IconButton(
                    onClick = onSwapCurrencies,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("currency_swap_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap Currencies",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // To Currency Chip
                Surface(
                    onClick = { showToPicker = true },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("currency_to_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = toCurrency.flag, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = toCurrency.code,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // Middle: Live Converted Rate Pill / Mode Status Pill
            if (isToggleActive) {
                // ACTIVE CONVERSION PILL
                Surface(
                    onClick = {
                        if (toggleEnabledAction == "INSERT_TO_CALC") {
                            onInsertValueToCalc(formattedConverted)
                        } else if (toggleEnabledAction == "SWAP_CURRENCIES") {
                            onSwapCurrencies()
                        } else {
                            onInsertValueToCalc(formattedConverted)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(horizontal = 4.dp)
                        .testTag("currency_converted_result_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                        AnimatedContent(
                            targetState = "$formattedConverted ${toCurrency.code}",
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "converted_curr_anim"
                        ) { text ->
                            Text(
                                text = text,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            } else {
                // INACTIVE TOGGLE STATE (e.g. Pure Calc Mode / Static Pair)
                if (toggleDisabledAction == "STATIC_PAIR") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "1 = $formattedBaseRate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else if (toggleDisabledAction == "PAUSE_CONVERSION") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Paused",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                // HIDE_RATE_PILL: shows clean empty space so math mode is uninterrupted!
            }

            // Right: Settings Gear + Right Toggle Switch + Guide + Theme Studio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (showToggle && toggleAlignment == "RIGHT") {
                    CurrencyHeaderToggleSwitch(
                        checked = isToggleActive,
                        onCheckedChange = onToggleActiveChanged
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }

                IconButton(
                    onClick = onOpenCurrencySettings,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("currency_header_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Currency Header Settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(17.dp)
                    )
                }

                if (showGuide) {
                    IconButton(
                        onClick = onOpenGuide,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("calc_guide_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Calculation Guide",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                if (showThemeStudio) {
                    IconButton(
                        onClick = onOpenThemeStudio,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("quick_customize_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Expressive Theme Studio",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }

    // Currency Picker Dialogs
    if (showFromPicker) {
        CurrencyPickerDialog(
            currencies = exchangeState.availableCurrencies.ifEmpty { defaultCurrencies },
            onDismiss = { showFromPicker = false },
            onSelect = {
                onSelectFromCurrency(it)
                showFromPicker = false
            }
        )
    }

    if (showToPicker) {
        CurrencyPickerDialog(
            currencies = exchangeState.availableCurrencies.ifEmpty { defaultCurrencies },
            onDismiss = { showToPicker = false },
            onSelect = {
                onSelectToCurrency(it)
                showToPicker = false
            }
        )
    }
}
