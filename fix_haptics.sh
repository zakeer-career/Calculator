#!/bin/bash
FILE="app/src/main/java/com/zakeercareer/calculator/ui/screens/MatrixScreen.kt"

# Add imports
sed -i '/import androidx.compose.runtime.getValue/a import androidx.compose.ui.platform.LocalHapticFeedback\nimport androidx.compose.ui.hapticfeedback.HapticFeedbackType' "$FILE"

# Find MatrixScreen composable and inject haptic
sed -i '/fun MatrixScreen(viewModel: CalculatorViewModel) {/a \    val haptic = LocalHapticFeedback.current' "$FILE"

# Add haptic to button clicks
sed -i 's/viewModel.calculateMatrix("ADD")/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)\n                            viewModel.calculateMatrix("ADD")/g' "$FILE"
sed -i 's/viewModel.calculateMatrix("SUBTRACT")/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)\n                            viewModel.calculateMatrix("SUBTRACT")/g' "$FILE"
sed -i 's/viewModel.calculateMatrix("MULTIPLY")/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)\n                            viewModel.calculateMatrix("MULTIPLY")/g' "$FILE"
sed -i 's/viewModel.calculateMatrix("DETERMINANT")/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)\n                            viewModel.calculateMatrix("DETERMINANT")/g' "$FILE"
sed -i 's/viewModel.calculateMatrix("INVERSE")/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)\n                            viewModel.calculateMatrix("INVERSE")/g' "$FILE"
sed -i 's/viewModel.calculateMatrix("TRANSPOSE")/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)\n                            viewModel.calculateMatrix("TRANSPOSE")/g' "$FILE"
