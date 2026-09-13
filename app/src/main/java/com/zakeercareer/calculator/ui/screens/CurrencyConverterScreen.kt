package com.zakeercareer.calculator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zakeercareer.calculator.data.currency.CurrencyInfo
import com.zakeercareer.calculator.data.currency.CurrencyRepository
import com.zakeercareer.calculator.data.currency.ExchangeRatesState
import com.zakeercareer.calculator.data.currency.defaultCurrencies
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel
import com.zakeercareer.calculator.util.MathEvaluator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    val haptic = LocalHapticFeedback.current
    viewModel: CalculatorViewModel,
    amount: String,
    fromCurrency: CurrencyInfo,
    toCurrency: CurrencyInfo,
    exchangeState: ExchangeRatesState,
    convertedValue: String
) {
    var showCurrencyDialogForFrom by remember { mutableStateOf(false) }
    var showCurrencyDialogForTo by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Exchange Rate Banner (Live vs Offline indicator)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (exchangeState.isRealtime) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (exchangeState.isRealtime) "⚡ Real-time Rates Active" else "🌐 Offline Exchange Rates",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Status: ${exchangeState.lastUpdated}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (exchangeState.error != null) {
                        Text(
                            text = exchangeState.error,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (exchangeState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                } else {
                    IconButton(
                        onClick = { viewModel.refreshCurrencyRates() },
                        modifier = Modifier.testTag("refresh_currency_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Rates")
                    }
                }
            }
        }

        // Amount Input Field
        OutlinedTextField(
            value = amount,
            onValueChange = { viewModel.updateCurrencyAmount(it) },
            label = { Text("Currency Amount") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("currency_amount_field"),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // From Currency Card -> Swap Button -> To Currency Card
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // From Currency Selector
            OutlinedCard(
                onClick = { showCurrencyDialogForFrom = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("from_currency_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("From", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${fromCurrency.flag} ${fromCurrency.code}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(fromCurrency.name, style = MaterialTheme.typography.bodySmall)
                }
            }

            androidx.compose.material3.FilledTonalIconButton(
                onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); viewModel.swapCurrencies() },
                modifier = Modifier.testTag("currency_swap_btn")
            ) {
                Icon(Icons.Default.SwapVert, contentDescription = "Swap Currencies")
            }

            // To Currency Selector
            OutlinedCard(
                onClick = { showCurrencyDialogForTo = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("to_currency_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("To", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${toCurrency.flag} ${toCurrency.code}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(toCurrency.name, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Result Display Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("currency_result_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Converted Value",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$convertedValue ${toCurrency.code}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Text(
                    text = "$amount ${fromCurrency.code} = $convertedValue ${toCurrency.code}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        // Save Currency History Button
        ElevatedButton(
            onClick = { viewModel.saveCurrencyHistory() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_currency_history_btn")
        ) {
            Icon(Icons.Default.Bookmark, contentDescription = "Save")
            Text("  Save Conversion to History", fontWeight = FontWeight.Bold)
        }

        // Quick Conversion Table Card (Top Currencies)
        Text("Popular Global Rates for 1 ${fromCurrency.code}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("EUR", "GBP", "JPY", "INR", "CAD", "AUD").forEach { code ->
                    if (code != fromCurrency.code) {
                        val rate = CurrencyRepository.convertCurrency(1.0, fromCurrency.code, code, exchangeState.rates)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val info = defaultCurrencies.find { it.code == code }
                            Text("${info?.flag ?: ""} 1 ${fromCurrency.code} → $code", fontWeight = FontWeight.Medium)
                            Text(MathEvaluator.formatNumber(rate), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(88.dp))
        Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp))
    }

    // Currency Picker Modal Sheet for FROM
    if (showCurrencyDialogForFrom) {
        CurrencyPickerDialog(
            currencies = exchangeState.availableCurrencies,
            onDismiss = { showCurrencyDialogForFrom = false },
            onSelect = {
                viewModel.selectFromCurrency(it)
                showCurrencyDialogForFrom = false
            }
        )
        Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp))
    }

    // Currency Picker Modal Sheet for TO
    if (showCurrencyDialogForTo) {
        CurrencyPickerDialog(
            currencies = exchangeState.availableCurrencies,
            onDismiss = { showCurrencyDialogForTo = false },
            onSelect = {
                viewModel.selectToCurrency(it)
                showCurrencyDialogForTo = false
            }
        )
        Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerDialog(
    currencies: List<CurrencyInfo>,
    onDismiss: () -> Unit,
    onSelect: (CurrencyInfo) -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = currencies.filter {
        it.code.contains(search, ignoreCase = true) || it.name.contains(search, ignoreCase = true)
        Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp))
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Select Currency",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search USD, EUR, INR...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filtered) { curr ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(curr) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(curr.flag, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(curr.code, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(curr.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp))
    }
}
