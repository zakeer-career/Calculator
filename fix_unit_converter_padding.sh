#!/bin/bash
FILE="app/src/main/java/com/zakeercareer/calculator/ui/screens/UnitConverterScreen.kt"

# Find the last closing brace of the Column and insert a Spacer
sed -i '/import androidx.compose.foundation.layout.navigationBarsPadding/!b;n' "$FILE"
grep -q "import androidx.compose.foundation.layout.navigationBarsPadding" "$FILE" || sed -i '/import androidx.compose.foundation.layout.padding/i import androidx.compose.foundation.layout.navigationBarsPadding' "$FILE"

# Add spacer before the final brace (assuming Column is the root composable block)
# Actually, it's easier to just append it before the very last brace
sed -i '/^    }$/i \        Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp))' "$FILE"
