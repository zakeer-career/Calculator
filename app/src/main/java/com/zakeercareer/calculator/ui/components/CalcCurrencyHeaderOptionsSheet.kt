package com.zakeercareer.calculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zakeercareer.calculator.data.currency.defaultCurrencies
import com.zakeercareer.calculator.ui.screens.CurrencyPickerDialog
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcCurrencyHeaderOptionsSheet(
    viewModel: CalculatorViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val calcCurrencyBarEnabled by viewModel.calcCurrencyBarEnabled.collectAsStateWithLifecycle()
    val calcCurrencyDecimals by viewModel.calcCurrencyDecimals.collectAsStateWithLifecycle()
    val calcCurrencyHeaderShowToggle by viewModel.calcCurrencyHeaderShowToggle.collectAsStateWithLifecycle()
    val calcCurrencyToggleActive by viewModel.calcCurrencyToggleActive.collectAsStateWithLifecycle()
    val calcCurrencyToggleAlignment by viewModel.calcCurrencyToggleAlignment.collectAsStateWithLifecycle()
    val calcCurrencyShowGuide by viewModel.calcCurrencyShowGuide.collectAsStateWithLifecycle()
    val calcCurrencyShowThemeStudio by viewModel.calcCurrencyShowThemeStudio.collectAsStateWithLifecycle()
    val calcCurrencyToggleEnabledAction by viewModel.calcCurrencyToggleEnabledAction.collectAsStateWithLifecycle()
    val calcCurrencyToggleDisabledAction by viewModel.calcCurrencyToggleDisabledAction.collectAsStateWithLifecycle()

    val fromCurrency by viewModel.fromCurrency.collectAsStateWithLifecycle()
    val toCurrency by viewModel.toCurrency.collectAsStateWithLifecycle()
    val exchangeState by viewModel.exchangeState.collectAsStateWithLifecycle()

    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CurrencyExchange,
                    contentDescription = "Currency Header Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Live Currency Conversion Header Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            // CARD 1: Master Enable / Disable Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Currency Bar", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Show live conversion header bar above calculator keypad", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = calcCurrencyBarEnabled,
                            onCheckedChange = { viewModel.setCalcCurrencyBarEnabled(it) },
                            modifier = Modifier.testTag("calc_currency_bar_master_switch")
                        )
                    }
                }
            }

            if (calcCurrencyBarEnabled) {
                // CARD 2: Persistent Currency Pair Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Default Currency Pair (Reopen Memory)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your selected currencies stay saved even when you reopen the app.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // From Currency
                            Surface(
                                onClick = { showFromPicker = true },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(fromCurrency.flag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(fromCurrency.code, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            IconButton(onClick = { viewModel.swapCurrencies() }) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Swap Currencies", tint = MaterialTheme.colorScheme.primary)
                            }

                            // To Currency
                            Surface(
                                onClick = { showToPicker = true },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(toCurrency.flag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(toCurrency.code, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // CARD 3: Decimal Customization
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Conversion Decimal Precision", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Select how many decimal places to show for converted values", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(-1 to "Auto", 0 to "0", 1 to "1", 2 to "2", 3 to "3", 4 to "4").forEach { (decimals, label) ->
                                ElevatedFilterChip(
                                    selected = calcCurrencyDecimals == decimals,
                                    onClick = { viewModel.setCalcCurrencyDecimals(decimals) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f).testTag("currency_decimals_$decimals")
                                )
                            }
                        }
                    }
                }

                // CARD 4: Live Header Quick Toggle Switch Customization
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Show Header Quick Toggle", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("Display a quick pill switch directly on the conversion bar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = calcCurrencyHeaderShowToggle,
                                onCheckedChange = { viewModel.setCalcCurrencyHeaderShowToggle(it) },
                                modifier = Modifier.testTag("show_header_toggle_switch")
                            )
                        }

                        if (calcCurrencyHeaderShowToggle) {
                            HorizontalDivider()

                            // Toggle Switch Alignment Option
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Toggle Switch Alignment", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ElevatedFilterChip(
                                        selected = calcCurrencyToggleAlignment == "LEFT",
                                        onClick = { viewModel.setCalcCurrencyToggleAlignment("LEFT") },
                                        label = { Text("Left Align", fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f).testTag("toggle_align_left")
                                    )
                                    ElevatedFilterChip(
                                        selected = calcCurrencyToggleAlignment == "RIGHT",
                                        onClick = { viewModel.setCalcCurrencyToggleAlignment("RIGHT") },
                                        label = { Text("Right Align", fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f).testTag("toggle_align_right")
                                    )
                                }
                            }

                            HorizontalDivider()

                            // Toggle ENABLED Action
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("When Toggle is ENABLED (Active):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                listOf(
                                    "LIVE_CONVERT" to "Live Calculation Conversion",
                                    "INSERT_TO_CALC" to "Auto-Insert Converted Result",
                                    "AUTO_COPY" to "Auto-Copy Converted Value",
                                    "SWAP_CURRENCIES" to "Quick Swap Currencies"
                                ).forEach { (actionKey, label) ->
                                    ElevatedFilterChip(
                                        selected = calcCurrencyToggleEnabledAction == actionKey,
                                        onClick = { viewModel.setCalcCurrencyToggleEnabledAction(actionKey) },
                                        label = { Text(label, fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth().testTag("toggle_enabled_action_$actionKey")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Toggle DISABLED Action
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("When Toggle is DISABLED (Off):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
                                listOf(
                                    "HIDE_RATE_PILL" to "Hide Rate Pill (Plain Calculations)",
                                    "PAUSE_CONVERSION" to "Pause Conversion (Static 1.0 Rate)",
                                    "STATIC_PAIR" to "Show Static Base Pair Rate"
                                ).forEach { (actionKey, label) ->
                                    ElevatedFilterChip(
                                        selected = calcCurrencyToggleDisabledAction == actionKey,
                                        onClick = { viewModel.setCalcCurrencyToggleDisabledAction(actionKey) },
                                        label = { Text(label, fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth().testTag("toggle_disabled_action_$actionKey")
                                    )
                                }
                            }
                        }
                    }
                }

                // CARD 5: Header Action Buttons Visibility (Guide & Theme Studio)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Header Quick Action Icons Visibility",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enable or disable individual quick action buttons shown on the right side of the currency header",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Show Calculation Guide Icon (?)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text("Quick access to calculation guide and formula tips", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = calcCurrencyShowGuide,
                                onCheckedChange = { viewModel.setCalcCurrencyShowGuide(it) },
                                modifier = Modifier.testTag("show_currency_guide_switch")
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Show Theme Studio Icon (Palette)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text("Quick access to color theme and style customization", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = calcCurrencyShowThemeStudio,
                                onCheckedChange = { viewModel.setCalcCurrencyShowThemeStudio(it) },
                                modifier = Modifier.testTag("show_currency_theme_studio_switch")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Currency Picker Dialogs
    if (showFromPicker) {
        CurrencyPickerDialog(
            currencies = exchangeState.availableCurrencies.ifEmpty { defaultCurrencies },
            onDismiss = { showFromPicker = false },
            onSelect = {
                viewModel.selectFromCurrency(it)
                showFromPicker = false
            }
        )
    }

    if (showToPicker) {
        CurrencyPickerDialog(
            currencies = exchangeState.availableCurrencies.ifEmpty { defaultCurrencies },
            onDismiss = { showToPicker = false },
            onSelect = {
                viewModel.selectToCurrency(it)
                showToPicker = false
            }
        )
    }
}
