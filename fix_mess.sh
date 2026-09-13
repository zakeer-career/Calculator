sed -i 's/val haptic = LocalHapticFeedback.current//g' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
sed -i 's/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); //g' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
sed -i '/fun MainCalculatorApp(viewModel: CalculatorViewModel = viewModel()) {/a \    val haptic = LocalHapticFeedback.current' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
sed -i 's/selectedTab = index/haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); selectedTab = index/g' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
sed -i 's/onTabSelected = { selectedTab = it }/onTabSelected = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); selectedTab = it }/g' app/src/main/java/com/zakeercareer/calculator/MainActivity.kt
