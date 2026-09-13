package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.currency.CurrencyInfo
import com.example.data.currency.CurrencyRepository
import com.example.data.currency.ExchangeRatesState
import com.example.data.currency.defaultCurrencies
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.util.MathEvaluator

@Composable
fun CurrencyConverterScreen(
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // From Currency Selector
            OutlinedCard(
                onClick = { showCurrencyDialogForFrom = true },
                modifier = Modifier
                    .weight(1f)
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

            IconButton(
                onClick = { viewModel.swapCurrencies() },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .testTag("currency_swap_btn")
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = "Swap Currencies")
            }

            // To Currency Selector
            OutlinedCard(
                onClick = { showCurrencyDialogForTo = true },
                modifier = Modifier
                    .weight(1f)
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
    }

    // Currency Picker Modal Dialog for FROM
    if (showCurrencyDialogForFrom) {
        CurrencyPickerDialog(
            currencies = exchangeState.availableCurrencies,
            onDismiss = { showCurrencyDialogForFrom = false },
            onSelect = {
                viewModel.selectFromCurrency(it)
                showCurrencyDialogForFrom = false
            }
        )
    }

    // Currency Picker Modal Dialog for TO
    if (showCurrencyDialogForTo) {
        CurrencyPickerDialog(
            currencies = exchangeState.availableCurrencies,
            onDismiss = { showCurrencyDialogForTo = false },
            onSelect = {
                viewModel.selectToCurrency(it)
                showCurrencyDialogForTo = false
            }
        )
    }
}

@Composable
fun CurrencyPickerDialog(
    currencies: List<CurrencyInfo>,
    onDismiss: () -> Unit,
    onSelect: (CurrencyInfo) -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = currencies.filter {
        it.code.contains(search, ignoreCase = true) || it.name.contains(search, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search USD, EUR, INR...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(filtered) { curr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(curr) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(curr.flag, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("${curr.code} - ${curr.name}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
