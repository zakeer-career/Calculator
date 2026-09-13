#!/bin/bash
FILE="app/src/main/java/com/zakeercareer/calculator/MainActivity.kt"
# We just want one copy of imports and declarations
sed -i 's/val haptic = LocalHapticFeedback.current//g' "$FILE"
sed -i 's/haptic.performHapticFeedback.*selectedTab = index/selectedTab = index/g' "$FILE"
sed -i 's/haptic.performHapticFeedback.*selectedTab = it/selectedTab = it/g' "$FILE"

# Make sure we don't have dupes of hapticFeedback imports
sed -i '/import androidx.compose.ui.hapticfeedback.HapticFeedbackType/d' "$FILE"

sed -i '/import androidx.compose.runtime.getValue/a import androidx.compose.ui.platform.LocalHapticFeedback\nimport androidx.compose.ui.hapticfeedback.HapticFeedbackType' "$FILE"

sed -i '/fun MainCalculatorApp(viewModel: CalculatorViewModel = viewModel()) {/a \    val haptic = LocalHapticFeedback.current' "$FILE"
sed -i 's/selectedTab = index/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); selectedTab = index/g' "$FILE"
sed -i 's/onTabSelected = { selectedTab = it }/onTabSelected = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); selectedTab = it }/g' "$FILE"
