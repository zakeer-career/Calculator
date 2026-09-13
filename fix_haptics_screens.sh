#!/bin/bash
for FILE in app/src/main/java/com/zakeercareer/calculator/ui/screens/CurrencyConverterScreen.kt app/src/main/java/com/zakeercareer/calculator/ui/screens/UnitConverterScreen.kt app/src/main/java/com/zakeercareer/calculator/ui/screens/HistoryScreen.kt; do
    sed -i '/import androidx.compose.runtime.getValue/a import androidx.compose.ui.platform.LocalHapticFeedback\nimport androidx.compose.ui.hapticfeedback.HapticFeedbackType' "$FILE"
done

# Currency
sed -i '/fun CurrencyConverterScreen/a \    val haptic = LocalHapticFeedback.current' app/src/main/java/com/zakeercareer/calculator/ui/screens/CurrencyConverterScreen.kt
sed -i 's/viewModel.swapCurrencies()/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); viewModel.swapCurrencies()/g' app/src/main/java/com/zakeercareer/calculator/ui/screens/CurrencyConverterScreen.kt
sed -i 's/viewModel.saveCurrencyConversionHistory()/haptic.performHapticFeedback(HapticFeedbackType.LongPress); viewModel.saveCurrencyConversionHistory()/g' app/src/main/java/com/zakeercareer/calculator/ui/screens/CurrencyConverterScreen.kt

# Unit
sed -i '/fun UnitConverterScreen/a \    val haptic = LocalHapticFeedback.current' app/src/main/java/com/zakeercareer/calculator/ui/screens/UnitConverterScreen.kt
sed -i 's/viewModel.swapUnits()/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); viewModel.swapUnits()/g' app/src/main/java/com/zakeercareer/calculator/ui/screens/UnitConverterScreen.kt
sed -i 's/viewModel.saveUnitConversionHistory()/haptic.performHapticFeedback(HapticFeedbackType.LongPress); viewModel.saveUnitConversionHistory()/g' app/src/main/java/com/zakeercareer/calculator/ui/screens/UnitConverterScreen.kt

# History
sed -i '/fun HistoryScreen/a \    val haptic = LocalHapticFeedback.current' app/src/main/java/com/zakeercareer/calculator/ui/screens/HistoryScreen.kt
sed -i 's/viewModel.deleteCalculation(calc)/haptic.performHapticFeedback(HapticFeedbackType.LongPress); viewModel.deleteCalculation(calc)/g' app/src/main/java/com/zakeercareer/calculator/ui/screens/HistoryScreen.kt
