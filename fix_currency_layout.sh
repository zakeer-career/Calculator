#!/bin/bash
FILE="app/src/main/java/com/zakeercareer/calculator/ui/screens/CurrencyConverterScreen.kt"

# We replace lines 146 to 195 with a new vertical layout
sed -i '146,195c\
        Column(\
            modifier = Modifier.fillMaxWidth(),\
            verticalArrangement = Arrangement.spacedBy(8.dp),\
            horizontalAlignment = Alignment.CenterHorizontally\
        ) {\
            // From Currency Selector\
            OutlinedCard(\
                onClick = { showCurrencyDialogForFrom = true },\
                modifier = Modifier\
                    .fillMaxWidth()\
                    .testTag("from_currency_card")\
            ) {\
                Column(modifier = Modifier.padding(16.dp)) {\
                    Text("From", style = MaterialTheme.typography.labelMedium)\
                    Text(\
                        text = "${fromCurrency.flag} ${fromCurrency.code}",\
                        fontWeight = FontWeight.Bold,\
                        fontSize = 18.sp\
                    )\
                    Text(fromCurrency.name, style = MaterialTheme.typography.bodySmall)\
                }\
            }\
\
            androidx.compose.material3.FilledTonalIconButton(\
                onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); viewModel.swapCurrencies() },\
                modifier = Modifier.testTag("currency_swap_btn")\
            ) {\
                Icon(Icons.Default.SwapVert, contentDescription = "Swap Currencies")\
            }\
\
            // To Currency Selector\
            OutlinedCard(\
                onClick = { showCurrencyDialogForTo = true },\
                modifier = Modifier\
                    .fillMaxWidth()\
                    .testTag("to_currency_card")\
            ) {\
                Column(modifier = Modifier.padding(16.dp)) {\
                    Text("To", style = MaterialTheme.typography.labelMedium)\
                    Text(\
                        text = "${toCurrency.flag} ${toCurrency.code}",\
                        fontWeight = FontWeight.Bold,\
                        fontSize = 18.sp\
                    )\
                    Text(toCurrency.name, style = MaterialTheme.typography.bodySmall)\
                }\
            }\
        }' "$FILE"

# Add spacer to end of scrollable content
sed -i '/^    }$/i \        Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp))' "$FILE"
