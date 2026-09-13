#!/bin/bash
FILE="app/src/main/java/com/zakeercareer/calculator/MainActivity.kt"
sed -i 's/val haptic = LocalHapticFeedback.current//g' "$FILE"
sed -i 's/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); //g' "$FILE"
sed -i 's/import androidx.compose.ui.hapticfeedback.HapticFeedbackType//g' "$FILE"
sed -i 's/import androidx.compose.ui.platform.LocalHapticFeedback//g' "$FILE"

sed -i '/import androidx.compose.runtime.getValue/a import androidx.compose.ui.platform.LocalHapticFeedback\nimport androidx.compose.ui.hapticfeedback.HapticFeedbackType' "$FILE"
sed -i '/fun MainCalculatorApp(viewModel: CalculatorViewModel = viewModel()) {/a \    val haptic = LocalHapticFeedback.current' "$FILE"
sed -i 's/selectedTab = index/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); selectedTab = index/g' "$FILE"
sed -i 's/onTabSelected = { selectedTab = it }/onTabSelected = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); selectedTab = it }/g' "$FILE"
