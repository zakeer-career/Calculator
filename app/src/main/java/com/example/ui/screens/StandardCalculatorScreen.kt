package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import com.example.data.db.CalculationEntity
import com.example.ui.components.HistoryItemActionSheet
import com.example.ui.components.LiveLayoutPreviewCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CalcButton
import com.example.ui.components.CalcButtonType
import com.example.ui.components.CalcSectionHistoryBar
import com.example.ui.components.CalculationGuideSheet
import com.example.ui.components.DisplayPanel
import com.example.ui.components.CalcHistoryBarOptionsSheet
import com.example.ui.components.CalcCurrencyHeaderOptionsSheet
import com.example.ui.components.FuturisticCalcCurrencyBar
import com.example.ui.components.ThemeSelectionBottomSheet
import com.example.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardCalculatorScreen(
    viewModel: CalculatorViewModel
) {
    var showCustomizeSheet by remember { mutableStateOf(false) }
    var showThemeStudioSheet by remember { mutableStateOf(false) }
    var showGuideSheet by remember { mutableStateOf(false) }
    var showHistoryBarCustomizationSheet by remember { mutableStateOf(false) }
    var showCurrencyHeaderCustomizationSheet by remember { mutableStateOf(false) }
    var selectedHistoryItemForAction by remember { mutableStateOf<CalculationEntity?>(null) }

    val calcHistoryGridlineStyle by viewModel.calcHistoryGridlineStyle.collectAsStateWithLifecycle()
    val calcHistoryItemSpacingDp by viewModel.calcHistoryItemSpacingDp.collectAsStateWithLifecycle()
    val calcHistoryMaxItemsCount by viewModel.calcHistoryMaxItemsCount.collectAsStateWithLifecycle()
    val calcHistoryIsAdaptive by viewModel.calcHistoryIsAdaptive.collectAsStateWithLifecycle()
    val calcHistoryExpanded by viewModel.calcHistoryExpanded.collectAsStateWithLifecycle()
    val swipeLeftAction by viewModel.historySwipeLeftAction.collectAsStateWithLifecycle()
    val swipeRightAction by viewModel.historySwipeRightAction.collectAsStateWithLifecycle()

    val expression by viewModel.expression.collectAsStateWithLifecycle()
    val textFieldValue by viewModel.textFieldValue.collectAsStateWithLifecycle()
    val previewResult by viewModel.previewResult.collectAsStateWithLifecycle()
    val isDegreeMode by viewModel.isDegreeMode.collectAsStateWithLifecycle()
    val memoryValue by viewModel.memoryValue.collectAsStateWithLifecycle()
    val isInvMode by viewModel.isInvMode.collectAsStateWithLifecycle()

    val topHistoryBannerVisible by viewModel.topHistoryBannerVisible.collectAsStateWithLifecycle()
    val historyList by viewModel.allRecentHistory.collectAsStateWithLifecycle()
    val compactView by viewModel.compactView.collectAsStateWithLifecycle()
    val btnShape by viewModel.btnShape.collectAsStateWithLifecycle()
    val displayFontSize by viewModel.displayFontSize.collectAsStateWithLifecycle()
    val displayAlign by viewModel.displayAlign.collectAsStateWithLifecycle()
    val showLivePreview by viewModel.showLivePreview.collectAsStateWithLifecycle()
    val numberAnimationType by viewModel.numberAnimationType.collectAsStateWithLifecycle()
    val themePreset by viewModel.themePreset.collectAsStateWithLifecycle()

    val livePreviewAnimEnabled by viewModel.livePreviewAnimEnabled.collectAsStateWithLifecycle()
    val adaptiveDisplayResizing by viewModel.adaptiveDisplayResizing.collectAsStateWithLifecycle()

    val fromCurrency by viewModel.fromCurrency.collectAsStateWithLifecycle()
    val toCurrency by viewModel.toCurrency.collectAsStateWithLifecycle()
    val exchangeState by viewModel.exchangeState.collectAsStateWithLifecycle()

    val calcCurrencyBarEnabled by viewModel.calcCurrencyBarEnabled.collectAsStateWithLifecycle()
    val calcCurrencyDecimals by viewModel.calcCurrencyDecimals.collectAsStateWithLifecycle()
    val calcCurrencyHeaderShowToggle by viewModel.calcCurrencyHeaderShowToggle.collectAsStateWithLifecycle()
    val calcCurrencyToggleActive by viewModel.calcCurrencyToggleActive.collectAsStateWithLifecycle()
    val calcCurrencyToggleAlignment by viewModel.calcCurrencyToggleAlignment.collectAsStateWithLifecycle()
    val calcCurrencyShowGuide by viewModel.calcCurrencyShowGuide.collectAsStateWithLifecycle()
    val calcCurrencyShowThemeStudio by viewModel.calcCurrencyShowThemeStudio.collectAsStateWithLifecycle()
    val calcCurrencyToggleEnabledAction by viewModel.calcCurrencyToggleEnabledAction.collectAsStateWithLifecycle()
    val calcCurrencyToggleDisabledAction by viewModel.calcCurrencyToggleDisabledAction.collectAsStateWithLifecycle()
    val calcHistoryAutoScrollTop by viewModel.calcHistoryAutoScrollTop.collectAsStateWithLifecycle()
    val calcHistoryShowGridlines by viewModel.calcHistoryShowGridlines.collectAsStateWithLifecycle()
    val calcHistoryGridlineStrokeWidthDp by viewModel.calcHistoryGridlineStrokeWidthDp.collectAsStateWithLifecycle()
    val calcHistoryGridlineAlpha by viewModel.calcHistoryGridlineAlpha.collectAsStateWithLifecycle()
    val calcHistoryShowItemDividers by viewModel.calcHistoryShowItemDividers.collectAsStateWithLifecycle()

    // Granular layout customization states
    val displayHeightDp by viewModel.displayHeightDp.collectAsStateWithLifecycle()
    val displayWidthPaddingDp by viewModel.displayWidthPaddingDp.collectAsStateWithLifecycle()
    val displayCornerRadiusDp by viewModel.displayCornerRadiusDp.collectAsStateWithLifecycle()
    val displayMainFontSizeSp by viewModel.displayMainFontSizeSp.collectAsStateWithLifecycle()
    val displayPreviewFontSizeSp by viewModel.displayPreviewFontSizeSp.collectAsStateWithLifecycle()

    val keypadHeightScale by viewModel.keypadHeightScale.collectAsStateWithLifecycle()
    val keypadWidthPaddingDp by viewModel.keypadWidthPaddingDp.collectAsStateWithLifecycle()
    val keypadGridSpacingDp by viewModel.keypadGridSpacingDp.collectAsStateWithLifecycle()
    val keypadBtnCornerRadiusDp by viewModel.keypadBtnCornerRadiusDp.collectAsStateWithLifecycle()
    val keypadBtnFontSizeSp by viewModel.keypadBtnFontSizeSp.collectAsStateWithLifecycle()
    val lockKeypadHeight by viewModel.lockKeypadHeight.collectAsStateWithLifecycle()
    val hasUserCustomDefaults by viewModel.hasUserCustomDefaults.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (compactView) 4.dp else 8.dp)
    ) {
        val isWide = maxWidth >= 600.dp || (maxWidth > maxHeight && maxHeight < 500.dp)
        val keypadHeightPercent = if (keypadHeightScale in 30f..70f) keypadHeightScale else 52f
        val keypadWeight = (keypadHeightPercent / 100f).coerceIn(0.30f, 0.70f)
        val topWeight = (1.0f - keypadWeight).coerceAtLeast(0.30f)

        // Proportional button font size & grid spacing scaling based on height percentage
        val heightScaleRatio = keypadHeightPercent / 50f
        val effectiveBtnFontSizeSp = (keypadBtnFontSizeSp * (0.75f + 0.35f * heightScaleRatio)).toInt().coerceIn(12, 34)
        val effectiveGridSpacing = (keypadGridSpacingDp * (0.8f + 0.3f * heightScaleRatio)).dp.coerceIn(2.dp, 12.dp)
        val gridSpacing = effectiveGridSpacing

        if (isWide) {
            // --- WIDE SCREEN / LANDSCAPE SPLIT LAYOUT ---
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left Column: History + Display + Scientific controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
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
                        livePreviewAnimEnabled = livePreviewAnimEnabled,
                        adaptiveDisplayResizing = adaptiveDisplayResizing,
                        onIncreaseHeight = { viewModel.increaseDisplayHeight() },
                        onDecreaseHeight = { viewModel.decreaseDisplayHeight() },
                        customHeightDp = displayHeightDp,
                        customWidthPaddingDp = displayWidthPaddingDp,
                        customCornerRadiusDp = displayCornerRadiusDp,
                        customMainFontSizeSp = displayMainFontSizeSp,
                        customPreviewFontSizeSp = displayPreviewFontSizeSp,
                        lockKeypadHeight = lockKeypadHeight
                    )

                    // Futuristic Live Currency Bar
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
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { showGuideSheet = true },
                                modifier = Modifier.testTag("calc_guide_btn")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Guide", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(
                                onClick = { showThemeStudioSheet = true },
                                modifier = Modifier.testTag("quick_customize_btn")
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = "Expressive Theme Studio", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Right Column: Standard Keypad Grid filling full height
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .padding(gridSpacing),
                    verticalArrangement = Arrangement.spacedBy(gridSpacing)
                ) {
                    // Row 1: AC, (, ), ÷
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("AC", CalcButtonType.ACTION, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onClearAll() }
                        CalcButton("(", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("(") }
                        CalcButton(")", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput(")") }
                        CalcButton("÷", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("÷") }
                    }

                    // Row 2: 7, 8, 9, ×
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("7", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("7") }
                        CalcButton("8", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("8") }
                        CalcButton("9", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("9") }
                        CalcButton("×", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("×") }
                    }

                    // Row 3: 4, 5, 6, −
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("4", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("4") }
                        CalcButton("5", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("5") }
                        CalcButton("6", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("6") }
                        CalcButton("−", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("−") }
                    }

                    // Row 4: 1, 2, 3, +
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("1", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("1") }
                        CalcButton("2", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("2") }
                        CalcButton("3", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("3") }
                        CalcButton("+", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("+") }
                    }

                    // Row 5: 0, %, ., =
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("0", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("0") }
                        CalcButton("%", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput("%") }
                        CalcButton(".", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onAppendInput(".") }
                        CalcButton("=", CalcButtonType.EQUALS, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape) { viewModel.onCalculate() }
                    }
                }
            }
        } else {
            // --- PORTRAIT / HANDHELD LAYOUT ---
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Display and Controls Column weighted proportionally
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(topWeight),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top History Banner (Adaptive, Gridlines, Custom Spacing, Expandable)
                    if (topHistoryBannerVisible && historyList.isNotEmpty()) {
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

                // Display
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
                    livePreviewAnimEnabled = livePreviewAnimEnabled,
                    adaptiveDisplayResizing = adaptiveDisplayResizing,
                    onIncreaseHeight = { viewModel.increaseDisplayHeight() },
                    onDecreaseHeight = { viewModel.decreaseDisplayHeight() },
                    customHeightDp = displayHeightDp,
                    customWidthPaddingDp = displayWidthPaddingDp,
                    customCornerRadiusDp = displayCornerRadiusDp,
                    customMainFontSizeSp = displayMainFontSizeSp,
                    customPreviewFontSizeSp = displayPreviewFontSizeSp,
                    lockKeypadHeight = lockKeypadHeight,
                    modifier = Modifier.weight(1f, fill = false)
                )

                // Futuristic Live Currency Bar
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
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showGuideSheet = true },
                            modifier = Modifier.testTag("calc_guide_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Guide", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(
                            onClick = { showThemeStudioSheet = true },
                            modifier = Modifier.testTag("quick_customize_btn")
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "Expressive Theme Studio", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

                // Standard Keypad Column - auto-resizing via weight & horizontal padding
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(keypadWeight)
                        .padding(horizontal = keypadWidthPaddingDp.dp, vertical = gridSpacing),
                    verticalArrangement = Arrangement.spacedBy(gridSpacing)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("AC", CalcButtonType.ACTION, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onClearAll() }
                        CalcButton("⌫", CalcButtonType.ACTION, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onDeleteChar() }
                        CalcButton("(", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("(") }
                        CalcButton("÷", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("÷") }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("7", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("7") }
                        CalcButton("8", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("8") }
                        CalcButton("9", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("9") }
                        CalcButton("×", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("×") }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("4", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("4") }
                        CalcButton("5", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("5") }
                        CalcButton("6", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("6") }
                        CalcButton("−", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("−") }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("1", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("1") }
                        CalcButton("2", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("2") }
                        CalcButton("3", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("3") }
                        CalcButton("+", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("+") }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        CalcButton("0", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("0") }
                        CalcButton("%", CalcButtonType.OPERATOR, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput("%") }
                        CalcButton(".", CalcButtonType.NUMBER, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onAppendInput(".") }
                        CalcButton("=", CalcButtonType.EQUALS, Modifier.weight(1f), isCompact = compactView, btnShape = btnShape, customCornerRadiusDp = keypadBtnCornerRadiusDp, customFontSizeSp = effectiveBtnFontSizeSp) { viewModel.onCalculate() }
                    }
                }
            }
        }
    }

    // --- QUICK CUSTOMIZATION BOTTOM SHEET ---
    if (showCustomizeSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showCustomizeSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Customize Calculator Layout", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider()

                // Button Corners Shape Selection
                Text("Button Shape", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val shapes = listOf("ROUNDED" to "Standard", "EXTRA_ROUNDED" to "Round", "SQUARE" to "Square", "PILL" to "Pill")
                    shapes.forEach { (key, label) ->
                        FilterChip(
                            selected = btnShape == key,
                            onClick = { viewModel.setBtnShape(key) },
                            label = { Text(label) }
                        )
                    }
                }

                // Display Font Size Selection
                Text("Display Size", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val sizes = listOf("NORMAL" to "Normal", "LARGE" to "Large", "XLARGE" to "Extra Large")
                    sizes.forEach { (key, label) ->
                        FilterChip(
                            selected = displayFontSize == key,
                            onClick = { viewModel.setDisplayFontSize(key) },
                            label = { Text(label) }
                        )
                    }
                }

                // Display Alignment
                Text("Display Alignment", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = displayAlign == "RIGHT",
                        onClick = { viewModel.setDisplayAlign("RIGHT") },
                        label = { Text("Right Aligned") }
                    )
                    FilterChip(
                        selected = displayAlign == "LEFT",
                        onClick = { viewModel.setDisplayAlign("LEFT") },
                        label = { Text("Left Aligned") }
                    )
                }

                // Number Display Animation Selection
                Text("Number Display Animation", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    val animOptions = listOf(
                        "OFF" to "Off (Default)",
                        "SPRING_BOUNCE" to "Spring Bounce",
                        "VERTICAL_SLIDE" to "Vertical Slide",
                        "SCALE_POP" to "Scale Pop",
                        "FADE_PULSE" to "Fade Pulse"
                    )
                    animOptions.forEach { (typeKey, typeLabel) ->
                        FilterChip(
                            selected = numberAnimationType == typeKey,
                            onClick = { viewModel.setNumberAnimationType(typeKey) },
                            label = { Text(typeLabel) }
                        )
                    }
                }

                HorizontalDivider()

                // Live Preview Card updating live as sliders move
                LiveLayoutPreviewCard(
                    displayHeightDp = displayHeightDp,
                    displayWidthPaddingDp = displayWidthPaddingDp,
                    displayCornerRadiusDp = displayCornerRadiusDp,
                    displayMainFontSizeSp = displayMainFontSizeSp,
                    displayPreviewFontSizeSp = displayPreviewFontSizeSp,
                    keypadHeightScale = keypadHeightScale,
                    keypadWidthPaddingDp = keypadWidthPaddingDp,
                    keypadGridSpacingDp = keypadGridSpacingDp,
                    keypadBtnCornerRadiusDp = keypadBtnCornerRadiusDp,
                    keypadBtnFontSizeSp = keypadBtnFontSizeSp,
                    lockKeypadHeight = lockKeypadHeight
                )

                // Lock Keypad Height Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Lock Keypad Height", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Prevents keypad from jumping or expanding when entering numbers",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = lockKeypadHeight,
                        onCheckedChange = { viewModel.setLockKeypadHeight(it) }
                    )
                }

                HorizontalDivider()

                Text("Display Dimensions & Text", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                // Display Height Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Display Height", fontSize = 14.sp)
                        Text("${displayHeightDp} dp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = displayHeightDp.toFloat(),
                        onValueChange = { viewModel.setDisplayHeightDp(it.toInt()) },
                        valueRange = 80f..260f,
                        steps = 17
                    )
                }

                // Display Horizontal Padding Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Display Width Padding", fontSize = 14.sp)
                        Text("${displayWidthPaddingDp} dp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = displayWidthPaddingDp.toFloat(),
                        onValueChange = { viewModel.setDisplayWidthPaddingDp(it.toInt()) },
                        valueRange = 0f..32f,
                        steps = 15
                    )
                }

                // Display Corner Radius Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Display Corner Softness", fontSize = 14.sp)
                        Text("${displayCornerRadiusDp} dp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = displayCornerRadiusDp.toFloat(),
                        onValueChange = { viewModel.setDisplayCornerRadiusDp(it.toInt()) },
                        valueRange = 0f..40f,
                        steps = 19
                    )
                }

                // Display Main Text Size Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Main Expression Font Size", fontSize = 14.sp)
                        Text("${displayMainFontSizeSp} sp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = displayMainFontSizeSp.toFloat(),
                        onValueChange = { viewModel.setDisplayMainFontSizeSp(it.toInt()) },
                        valueRange = 20f..60f,
                        steps = 19
                    )
                }

                // Display Preview Text Size Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Live Preview Font Size", fontSize = 14.sp)
                        Text("${displayPreviewFontSizeSp} sp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = displayPreviewFontSizeSp.toFloat(),
                        onValueChange = { viewModel.setDisplayPreviewFontSizeSp(it.toInt()) },
                        valueRange = 14f..38f,
                        steps = 11
                    )
                }

                HorizontalDivider()

                Text("Keypad Dimensions & Buttons", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                // Keypad Height Coverage Slider
                val bsKeypadHeightPercent = if (keypadHeightScale in 30f..70f) keypadHeightScale else 52f
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Keypad Height Coverage", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("${kotlin.math.round(bsKeypadHeightPercent).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("Adjust keypad screen height from 30% (compact) up to 70% (large)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = bsKeypadHeightPercent,
                        onValueChange = { viewModel.setKeypadHeightScale(it) },
                        valueRange = 30f..70f,
                        steps = 39,
                        modifier = Modifier.testTag("bs_keypad_height_slider")
                    )
                }

                // Keypad Horizontal Margin Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Keypad Width Side Padding", fontSize = 14.sp)
                        Text("${keypadWidthPaddingDp} dp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = keypadWidthPaddingDp.toFloat(),
                        onValueChange = { viewModel.setKeypadWidthPaddingDp(it.toInt()) },
                        valueRange = 0f..40f,
                        steps = 19
                    )
                }

                // Keypad Grid Gap Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Button Grid Gap", fontSize = 14.sp)
                        Text("${keypadGridSpacingDp} dp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = keypadGridSpacingDp.toFloat(),
                        onValueChange = { viewModel.setKeypadGridSpacingDp(it.toInt()) },
                        valueRange = 1f..16f,
                        steps = 14
                    )
                }

                // Button Corner Softness Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Button Corner Softness", fontSize = 14.sp)
                        Text("${keypadBtnCornerRadiusDp} dp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = keypadBtnCornerRadiusDp.toFloat(),
                        onValueChange = { viewModel.setKeypadBtnCornerRadiusDp(it.toInt()) },
                        valueRange = 0f..50f,
                        steps = 24
                    )
                }

                // Button Font Size Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Button Font Size", fontSize = 14.sp)
                        Text("${keypadBtnFontSizeSp} sp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Slider(
                        value = keypadBtnFontSizeSp.toFloat(),
                        onValueChange = { viewModel.setKeypadBtnFontSizeSp(it.toInt()) },
                        valueRange = 12f..32f,
                        steps = 19
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.material3.Button(
                        onClick = { viewModel.saveCurrentAsUserDefault() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Current Layout as My Custom Default")
                    }

                    if (hasUserCustomDefaults) {
                        OutlinedButton(
                            onClick = { viewModel.resetToUserCustomDefault() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Restore My Saved Custom Default")
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetToFactoryDefault() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset to Factory Initial Defaults")
                    }
                }

                HorizontalDivider()

                // Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Compact View Mode", fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = compactView,
                        onCheckedChange = { viewModel.setCompactView(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Live Result Preview", fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = showLivePreview,
                        onCheckedChange = { viewModel.setShowLivePreview(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Top History Stream Banner", fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = topHistoryBannerVisible,
                        onCheckedChange = { viewModel.setTopHistoryBannerVisible(it) }
                    )
                }

                HorizontalDivider()

                // Color Themes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Expressive Theme Studio", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showThemeStudioSheet = true }) {
                        Text("Browse Cards")
                    }
                }
                val themes = listOf(
                    "MATERIAL_YOU" to "Expressive Material You",
                    "LIQUID_GLASS" to "💧 Liquid Glassmorphism",
                    "NEON_CYBERPUNK" to "⚡ OLED Cyberpunk Neon",
                    "NEUMORPHIC" to "🔘 Neumorphic Soft UI",
                    "MINIMALIST_MONO" to "🏁 Minimalist Monochromatic",
                    "RETRO_80S" to "📟 Retro 80s Digital LED",
                    "SOFT_PASTEL" to "🎨 Soft Pastel Card Layout",
                    "SKEUOMORPHIC" to "🎛️ Skeuomorphic Tactile Analog",
                    "COMPACT_WIDGET" to "📱 Compact Floating Widget",
                    "DUAL_TONE" to "🌓 Modern Dual-Tone Accent"
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    themes.forEach { (key, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setThemePreset(key) }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = themePreset == key,
                                onClick = { viewModel.setThemePreset(key) }
                            )
                            Text(label, fontWeight = if (themePreset == key) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showThemeStudioSheet) {
        ThemeSelectionBottomSheet(
            selectedThemeId = themePreset,
            onSelectTheme = { themeId ->
                viewModel.setThemePreset(themeId)
            },
            onDismissRequest = { showThemeStudioSheet = false }
        )
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
