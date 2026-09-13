#!/bin/bash
FILE="app/src/main/java/com/zakeercareer/calculator/MainActivity.kt"

# Add imports
sed -i '/import androidx.compose.runtime.getValue/a import androidx.compose.ui.platform.LocalHapticFeedback\nimport androidx.compose.ui.hapticfeedback.HapticFeedbackType' "$FILE"

# Add haptic variable
sed -i '/fun MainCalculatorApp/a \    val haptic = LocalHapticFeedback.current' "$FILE"

# Add haptics to the BottomBar selected tab (LiquidGlassBottomBar or similar)
# Let's see how LiquidGlassBottomBar is used
