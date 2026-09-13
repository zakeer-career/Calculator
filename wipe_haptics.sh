sed -i 's/val haptic = LocalHapticFeedback.current//g' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
sed -i 's/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); //g' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
sed -i '/import androidx.compose.ui.hapticfeedback.HapticFeedbackType/d' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
sed -i '/import androidx.compose.ui.platform.LocalHapticFeedback/d' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
