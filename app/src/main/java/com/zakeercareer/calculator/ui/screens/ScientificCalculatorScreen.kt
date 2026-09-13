package com.zakeercareer.calculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zakeercareer.calculator.data.db.CalculationEntity
import com.zakeercareer.calculator.ui.components.CalcButton
import com.zakeercareer.calculator.ui.components.CalcButtonType
import com.zakeercareer.calculator.ui.components.CalcHistoryBarOptionsSheet
import com.zakeercareer.calculator.ui.components.CalcCurrencyHeaderOptionsSheet
import com.zakeercareer.calculator.ui.components.CalcSectionHistoryBar
import com.zakeercareer.calculator.ui.components.CalculationGuideSheet
import com.zakeercareer.calculator.ui.components.DisplayPanel
import com.zakeercareer.calculator.ui.components.FuturisticCalcCurrencyBar
import com.zakeercareer.calculator.ui.components.HistoryItemActionSheet
import com.zakeercareer.calculator.ui.components.ThemeSelectionBottomSheet
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val expression by viewModel.expression.collectAsStateWithLifecycle()
    val textFieldValue by viewModel.textFieldValue.collectAsStateWithLifecycle()
    val previewResult by viewModel.previewResult.collectAsStateWithLifecycle()
    val isDegreeMode by viewModel.isDegreeMode.collectAsStateWithLifecycle()
    val memoryValue by viewModel.memoryValue.collectAsStateWithLifecycle()
    val isInvMode by viewModel.isInvMode.collectAsStateWithLifecycle()

    val fromCurrency by viewModel.fromCurrency.collectAsStateWithLifecycle()
    val toCurrency by viewModel.toCurrency.collectAsStateWithLifecycle()
    val exchangeState by viewModel.exchangeState.collectAsStateWithLifecycle()

    val topHistoryBannerVisible by viewModel.topHistoryBannerVisible.collectAsStateWithLifecycle()
    val historyList by viewModel.allRecentHistory.collectAsStateWithLifecycle()
    val compactView by viewModel.compactView.collectAsStateWithLifecycle()
    val btnShape by viewModel.btnShape.collectAsStateWithLifecycle()
    val displayFontSize by viewModel.displayFontSize.collectAsStateWithLifecycle()
    val displayAlign by viewModel.displayAlign.collectAsStateWithLifecycle()
    val showLivePreview by viewModel.showLivePreview.collectAsStateWithLifecycle()
    val numberAnimationType by viewModel.numberAnimationType.collectAsStateWithLifecycle()
    val themePreset by viewModel.themePreset.collectAsStateWithLifecycle()

    val calcHistoryGridlineStyle by viewModel.calcHistoryGridlineStyle.collectAsStateWithLifecycle()
    val calcHistoryShowGridlines by viewModel.calcHistoryShowGridlines.collectAsStateWithLifecycle()
    val calcHistoryGridlineStrokeWidthDp by viewModel.calcHistoryGridlineStrokeWidthDp.collectAsStateWithLifecycle()
    val calcHistoryGridlineAlpha by viewModel.calcHistoryGridlineAlpha.collectAsStateWithLifecycle()
    val calcHistoryShowItemDividers by viewModel.calcHistoryShowItemDividers.collectAsStateWithLifecycle()
    val calcHistoryAutoScrollTop by viewModel.calcHistoryAutoScrollTop.collectAsStateWithLifecycle()
    val calcHistoryItemSpacingDp by viewModel.calcHistoryItemSpacingDp.collectAsStateWithLifecycle()
    val calcHistoryMaxItemsCount by viewModel.calcHistoryMaxItemsCount.collectAsStateWithLifecycle()
    val calcHistoryIsAdaptive by viewModel.calcHistoryIsAdaptive.collectAsStateWithLifecycle()
    val calcHistoryExpanded by viewModel.calcHistoryExpanded.collectAsStateWithLifecycle()

    val calcCurrencyBarEnabled by viewModel.calcCurrencyBarEnabled.collectAsStateWithLifecycle()
    val calcCurrencyDecimals by viewModel.calcCurrencyDecimals.collectAsStateWithLifecycle()
    val calcCurrencyHeaderShowToggle by viewModel.calcCurrencyHeaderShowToggle.collectAsStateWithLifecycle()
    val calcCurrencyToggleActive by viewModel.calcCurrencyToggleActive.collectAsStateWithLifecycle()
    val calcCurrencyToggleAlignment by viewModel.calcCurrencyToggleAlignment.collectAsStateWithLifecycle()
    val calcCurrencyShowGuide by viewModel.calcCurrencyShowGuide.collectAsStateWithLifecycle()
    val calcCurrencyShowThemeStudio by viewModel.calcCurrencyShowThemeStudio.collectAsStateWithLifecycle()
    val calcCurrencyToggleEnabledAction by viewModel.calcCurrencyToggleEnabledAction.collectAsStateWithLifecycle()
    val calcCurrencyToggleDisabledAction by viewModel.calcCurrencyToggleDisabledAction.collectAsStateWithLifecycle()

    val displayHeightDp by viewModel.displayHeightDp.collectAsStateWithLifecycle()
    val displayWidthPaddingDp by viewModel.displayWidthPaddingDp.collectAsStateWithLifecycle()
    val displayCornerRadiusDp by viewModel.displayCornerRadiusDp.collectAsStateWithLifecycle()
    val displayMainFontSizeSp by viewModel.displayMainFontSizeSp.collectAsStateWithLifecycle()
    val displayPreviewFontSizeSp by viewModel.displayPreviewFontSizeSp.collectAsStateWithLifecycle()
    val lockKeypadHeight by viewModel.lockKeypadHeight.collectAsStateWithLifecycle()
    val ultraPerformanceMode by viewModel.ultraPerformanceMode.collectAsStateWithLifecycle()

    var showHistoryBarCustomizationSheet by remember { mutableStateOf(false) }
    var showCurrencyHeaderCustomizationSheet by remember { mutableStateOf(false) }
    var showThemeStudioSheet by remember { mutableStateOf(false) }
    var showGuideSheet by remember { mutableStateOf(false) }
    var selectedHistoryItemForAction by remember { mutableStateOf<CalculationEntity?>(null) }
    var isHypMode by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(if (compactView) 4.dp else 8.dp)
            .testTag("scientific_calculator_screen")
    ) {
        val rowHeight = if (compactView) 38.dp else 46.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // History Bar
            if (topHistoryBannerVisible) {
                CalcSectionHistoryBar(
                    historyList = historyList,
                    maxItemsCount = calcHistoryMaxItemsCount,
                    gridlineStyle = calcHistoryGridlineStyle,
                    showGridlines = calcHistoryShowGridlines,
                    strokeWidthDp = calcHistoryGridlineStrokeWidthDp,
                    gridlineAlpha = calcHistoryGridlineAlpha,
                    showItemDividers = calcHistoryShowItemDividers,
                    autoScrollTop = calcHistoryAutoScrollTop,
                    itemSpacingDp = calcHistoryItemSpacingDp,
                    isAdaptive = calcHistoryIsAdaptive,
                    isExpanded = calcHistoryExpanded,
                    compactView = compactView,
                    onToggleExpand = { viewModel.toggleCalcHistoryExpanded() },
                    onSelectHistoryItem = { selectedHistoryItemForAction = it },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onOpenBarSettings = { showHistoryBarCustomizationSheet = true },
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // Display Panel
            DisplayPanel(
                expression = expression,
                textFieldValue = textFieldValue,
                onExpressionValueChange = { viewModel.onExpressionValueChange(it) },
                onCalculate = { viewModel.onCalculate() },
                onDeleteChar = { viewModel.onDeleteChar() },
                numberAnimationType = numberAnimationType,
                previewResult = previewResult,
                isDegreeMode = isDegreeMode,
                memoryValue = memoryValue,
                onToggleDegree = { viewModel.toggleDegreeMode() },
                displayFontSize = displayFontSize,
                displayAlign = displayAlign,
                showLivePreview = showLivePreview,
                customHeightDp = displayHeightDp,
                customWidthPaddingDp = displayWidthPaddingDp,
                customCornerRadiusDp = displayCornerRadiusDp,
                customMainFontSizeSp = displayMainFontSizeSp,
                customPreviewFontSizeSp = displayPreviewFontSizeSp,
                lockKeypadHeight = lockKeypadHeight,
                ultraPerformanceMode = ultraPerformanceMode
            )

            // Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AssistChip(
                        onClick = { viewModel.toggleDegreeMode() },
                        label = { Text(if (isDegreeMode) "DEG" else "RAD", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    AssistChip(
                        onClick = { viewModel.toggleInvMode() },
                        label = { Text(if (isInvMode) "INV ON" else "INV", fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isInvMode) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            labelColor = if (isInvMode) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    AssistChip(
                        onClick = { isHypMode = !isHypMode },
                        label = { Text(if (isHypMode) "HYP ON" else "HYP", fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isHypMode) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                            labelColor = if (isHypMode) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // Futuristic Live Currency Conversion Bar
            if (calcCurrencyBarEnabled) {
                FuturisticCalcCurrencyBar(
                    expression = expression,
                    previewResult = previewResult,
                    fromCurrency = fromCurrency,
                    toCurrency = toCurrency,
                    exchangeState = exchangeState,
                    decimals = calcCurrencyDecimals,
                    showToggle = calcCurrencyHeaderShowToggle,
                    isToggleActive = calcCurrencyToggleActive,
                    toggleAlignment = calcCurrencyToggleAlignment,
                    showGuide = calcCurrencyShowGuide,
                    showThemeStudio = calcCurrencyShowThemeStudio,
                    toggleEnabledAction = calcCurrencyToggleEnabledAction,
                    toggleDisabledAction = calcCurrencyToggleDisabledAction,
                    onToggleActiveChanged = { viewModel.setCalcCurrencyToggleActive(it) },
                    onSelectFromCurrency = { viewModel.selectFromCurrency(it) },
                    onSelectToCurrency = { viewModel.selectToCurrency(it) },
                    onSwapCurrencies = { viewModel.swapCurrencies() },
                    onInsertValueToCalc = { viewModel.onAppendInput(it) },
                    onOpenCurrencySettings = { showCurrencyHeaderCustomizationSheet = true },
                    onOpenThemeStudio = { showThemeStudioSheet = true },
                    onOpenGuide = { showGuideSheet = true },
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            // Scientific Keypad Matrix
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Row 1: Memory Functions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalcButton("MC", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.memoryClear() }
                        CalcButton("MR", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.memoryRecall() }
                        CalcButton("M+", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.memoryAdd() }
                        CalcButton("M-", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.memorySubtract() }
                        CalcButton("MS", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.memoryStore() }
                    }

                    // Row 2: Trig Functions (Adapts with INV and HYP)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val sinName = when {
                            isHypMode && isInvMode -> "asinh"
                            isHypMode -> "sinh"
                            isInvMode -> "asin"
                            else -> "sin"
                        }
                        val cosName = when {
                            isHypMode && isInvMode -> "acosh"
                            isHypMode -> "cosh"
                            isInvMode -> "acos"
                            else -> "cos"
                        }
                        val tanName = when {
                            isHypMode && isInvMode -> "atanh"
                            isHypMode -> "tanh"
                            isInvMode -> "atan"
                            else -> "tan"
                        }

                        CalcButton(sinName, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("$sinName(") }
                        CalcButton(cosName, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("$cosName(") }
                        CalcButton(tanName, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("$tanName(") }
                        CalcButton("π", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("π") }
                        CalcButton("e", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("e") }
                    }

                    // Row 3: Logarithms & Exponentials
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val logLabel = if (isInvMode) "10^x" else "log"
                        val lnLabel = if (isInvMode) "e^x" else "ln"

                        CalcButton(logLabel, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) {
                            if (isInvMode) viewModel.onAppendInput("10^") else viewModel.onAppendInput("log(")
                        }
                        CalcButton(lnLabel, CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) {
                            if (isInvMode) viewModel.onAppendInput("e^") else viewModel.onAppendInput("ln(")
                        }
                        CalcButton("x^y", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("^") }
                        CalcButton("√", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("sqrt(") }
                        CalcButton("x²", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("^2") }
                    }

                    // Row 4: Factorials, Parentheses & Math ops
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalcButton("!", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("!") }
                        CalcButton("(", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("(") }
                        CalcButton(")", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput(")") }
                        CalcButton("1/x", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("1/") }
                        CalcButton("%", CalcButtonType.SCIENTIFIC, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("%") }
                    }

                    // Row 5: Numeric Keypad Row 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalcButton("AC", CalcButtonType.ACTION, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onClearAll() }
                        CalcButton("7", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("7") }
                        CalcButton("8", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("8") }
                        CalcButton("9", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("9") }
                        CalcButton("÷", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("÷") }
                    }

                    // Row 6: Numeric Keypad Row 2
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalcButton("⌫", CalcButtonType.ACTION, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onDeleteChar() }
                        CalcButton("4", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("4") }
                        CalcButton("5", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("5") }
                        CalcButton("6", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("6") }
                        CalcButton("×", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("×") }
                    }

                    // Row 7: Numeric Keypad Row 3
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalcButton("±", CalcButtonType.ACTION, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.toggleSign() }
                        CalcButton("1", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("1") }
                        CalcButton("2", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("2") }
                        CalcButton("3", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("3") }
                        CalcButton("-", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("-") }
                    }

                    // Row 8: Numeric Keypad Row 4
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalcButton("0", CalcButtonType.NUMBER, Modifier.weight(2f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("0") }
                        CalcButton(".", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput(".") }
                        CalcButton("=", CalcButtonType.EQUALS, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onCalculate() }
                        CalcButton("+", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("+") }
                    }
                }
            }
        }
    }

    if (showHistoryBarCustomizationSheet) {
        CalcHistoryBarOptionsSheet(
            viewModel = viewModel,
            gridlineStyle = calcHistoryGridlineStyle,
            itemSpacingDp = calcHistoryItemSpacingDp,
            maxItemsCount = calcHistoryMaxItemsCount,
            isAdaptive = calcHistoryIsAdaptive,
            showHistoryBanner = topHistoryBannerVisible,
            onDismiss = { showHistoryBarCustomizationSheet = false }
        )
    }

    if (showCurrencyHeaderCustomizationSheet) {
        CalcCurrencyHeaderOptionsSheet(
            viewModel = viewModel,
            onDismiss = { showCurrencyHeaderCustomizationSheet = false }
        )
    }

    if (showThemeStudioSheet) {
        ThemeSelectionBottomSheet(
            selectedThemeId = themePreset,
            onSelectTheme = { themeId -> viewModel.setThemePreset(themeId) },
            onDismissRequest = { showThemeStudioSheet = false }
        )
    }

    if (showGuideSheet) {
        CalculationGuideSheet(
            onDismiss = { showGuideSheet = false },
            onTryExample = { expr ->
                viewModel.onClearAll()
                viewModel.onAppendInput(expr)
            }
        )
    }

    selectedHistoryItemForAction?.let { item ->
        HistoryItemActionSheet(
            item = item,
            onDismiss = { selectedHistoryItemForAction = null },
            onApplyResult = { res -> viewModel.applyResult(res) },
            onApplyEquation = { eq -> viewModel.applyEquation(eq) }
        )
    }
}
