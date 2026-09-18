package com.zakeercareer.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import com.zakeercareer.calculator.ui.components.LiveLayoutPreviewCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToInt
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel

import androidx.compose.material.icons.filled.HelpOutline
import com.zakeercareer.calculator.ui.components.CalculationGuideSheet

enum class SettingsSubPage {
    INTERFACE,
    NAVIGATIONS,
    TAB_SECTIONS,
    SECURITY
}

@Composable
fun SettingsScreen(viewModel: CalculatorViewModel) {
    var showGuideSheet by remember { mutableStateOf(false) }
    val incognitoMode by viewModel.incognitoMode.collectAsStateWithLifecycle()
    val appPin by viewModel.appPin.collectAsStateWithLifecycle()
    val biometricLockEnabled by viewModel.biometricLockEnabled.collectAsStateWithLifecycle()
    val decimalPrecision by viewModel.decimalPrecision.collectAsStateWithLifecycle()
    val numberFormatStyle by viewModel.numberFormatStyle.collectAsStateWithLifecycle()
    val topHistoryBannerVisible by viewModel.topHistoryBannerVisible.collectAsStateWithLifecycle()
    val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
    val tabOrder by viewModel.tabOrder.collectAsStateWithLifecycle()
    val pillNavStyle by viewModel.pillNavStyle.collectAsStateWithLifecycle()
    val showBottomBar by viewModel.showBottomBar.collectAsStateWithLifecycle()
    val navBarStyle by viewModel.navBarStyle.collectAsStateWithLifecycle()
    val navBarBlurOpacity by viewModel.navBarBlurOpacity.collectAsStateWithLifecycle()
    val navAnimationSpeed by viewModel.navAnimationSpeed.collectAsStateWithLifecycle()
    val iconOnlyNav by viewModel.iconOnlyNav.collectAsStateWithLifecycle()
    val navIndicatorSize by viewModel.navIndicatorSize.collectAsStateWithLifecycle()
    val navIndicatorScale by viewModel.navIndicatorScale.collectAsStateWithLifecycle()
    val enabledTabs by viewModel.enabledTabs.collectAsStateWithLifecycle()
    val isDegreeMode by viewModel.isDegreeMode.collectAsStateWithLifecycle()
    val themePreset by viewModel.themePreset.collectAsStateWithLifecycle()
    val compactView by viewModel.compactView.collectAsStateWithLifecycle()
    val btnShape by viewModel.btnShape.collectAsStateWithLifecycle()
    val displayFontSize by viewModel.displayFontSize.collectAsStateWithLifecycle()
    val displayAlign by viewModel.displayAlign.collectAsStateWithLifecycle()
    val showLivePreview by viewModel.showLivePreview.collectAsStateWithLifecycle()
    val numberAnimationType by viewModel.numberAnimationType.collectAsStateWithLifecycle()
    val ultraPerformanceMode by viewModel.ultraPerformanceMode.collectAsStateWithLifecycle()

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

    val livePreviewAnimEnabled by viewModel.livePreviewAnimEnabled.collectAsStateWithLifecycle()
    val livePreviewAnimStyle by viewModel.livePreviewAnimStyle.collectAsStateWithLifecycle()
    val historyGridlinesEnabled by viewModel.historyGridlinesEnabled.collectAsStateWithLifecycle()
    val adaptiveDisplayResizing by viewModel.adaptiveDisplayResizing.collectAsStateWithLifecycle()
    val adaptiveThemeEnabled by viewModel.adaptiveThemeEnabled.collectAsStateWithLifecycle()
    val themeContrastMode by viewModel.themeContrastMode.collectAsStateWithLifecycle()

    var activeSubPage by remember { mutableStateOf<SettingsSubPage?>(null) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var exportJsonText by remember { mutableStateOf("") }
    var importJsonInput by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (activeSubPage) {
            // --- SUB-PAGE: INTERFACE (Themes & Visual Layout) ---
            SettingsSubPage.INTERFACE -> {
                SubPageHeader(title = "Appearance", subtitle = "Interface Settings") {
                    activeSubPage = null
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("App Theme Presets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        val themes = listOf(
                            "LIQUID_GLASS" to "💧 Liquid Glass",
                            "MATERIAL_YOU" to "Material You (Dynamic)",
                            "OLED_BLACK" to "🌑 Pitch Black OLED",
                            "NEON_CYBERPUNK" to "⚡ Neon Cyberpunk",
                            "WARM_SUNSET" to "🌅 Warm Sunset",
                            "DARK" to "🌙 Dark Mode",
                            "LIGHT" to "☀️ Light Mode"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            themes.forEach { (key, label) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.setThemePreset(key) }
                                        .padding(vertical = 6.dp)
                                ) {
                                    RadioButton(
                                        selected = themePreset == key,
                                        onClick = { viewModel.setThemePreset(key) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        label,
                                        fontWeight = if (themePreset == key) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        Text("Keypad Button Shape", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val shapes = listOf("ROUNDED" to "Standard", "EXTRA_ROUNDED" to "Round", "SQUARE" to "Square", "PILL" to "Pill")
                            shapes.forEach { (key, label) ->
                                FilterChip(
                                    selected = btnShape == key,
                                    onClick = { viewModel.setBtnShape(key) },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag("btn_shape_$key")
                                )
                            }
                        }

                        HorizontalDivider()

                        Text("Display Text Size", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val sizes = listOf("NORMAL" to "Normal", "LARGE" to "Large", "XLARGE" to "Extra Large")
                            sizes.forEach { (key, label) ->
                                FilterChip(
                                    selected = displayFontSize == key,
                                    onClick = { viewModel.setDisplayFontSize(key) },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag("display_size_$key")
                                )
                            }
                        }

                        HorizontalDivider()

                        Text("Display Text Alignment", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = displayAlign == "RIGHT",
                                onClick = { viewModel.setDisplayAlign("RIGHT") },
                                label = { Text("Right Aligned") },
                                modifier = Modifier.testTag("display_align_right")
                            )
                            FilterChip(
                                selected = displayAlign == "LEFT",
                                onClick = { viewModel.setDisplayAlign("LEFT") },
                                label = { Text("Left Aligned") },
                                modifier = Modifier.testTag("display_align_left")
                            )
                        }

                        HorizontalDivider()

                        // Ultra Performance Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Ultra Performance Mode (Max FPS)", fontWeight = FontWeight.Bold)
                                Text(
                                    "Disables complex slide transitions & blur effects for zero-lag, instant keystroke response",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = ultraPerformanceMode,
                                onCheckedChange = { viewModel.setUltraPerformanceMode(it) },
                                modifier = Modifier.testTag("ultra_performance_mode_switch")
                            )
                        }

                        HorizontalDivider()

                        Text("Number Entry Animation", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
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
                                    label = { Text(typeLabel) },
                                    modifier = Modifier.testTag("anim_chip_$typeKey")
                                )
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Real-time Live Calculation Preview", fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Evaluates expression as you type.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = showLivePreview,
                                onCheckedChange = { viewModel.setShowLivePreview(it) },
                                modifier = Modifier.testTag("live_preview_switch")
                            )
                        }

                        // Customize Live Preview Animation (Default OFF)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Live Preview Animation", fontWeight = FontWeight.Bold)
                                Text(
                                    "Animate result numbers in live preview (Default OFF for instant response)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = livePreviewAnimEnabled,
                                onCheckedChange = { viewModel.setLivePreviewAnimEnabled(it) },
                                modifier = Modifier.testTag("live_preview_anim_switch")
                            )
                        }

                        // Adaptive Display Resizing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Adaptive Display Resizing", fontWeight = FontWeight.Bold)
                                Text(
                                    "Automatically expands display panel height for long expressions",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = adaptiveDisplayResizing,
                                onCheckedChange = { viewModel.setAdaptiveDisplayResizing(it) },
                                modifier = Modifier.testTag("adaptive_display_resizing_switch")
                            )
                        }

                        // Adaptive Color System
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Adaptive Color System", fontWeight = FontWeight.Bold)
                                Text(
                                    "Dynamically balances background contrast & dynamic primary accents",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = adaptiveThemeEnabled,
                                onCheckedChange = { viewModel.setAdaptiveThemeEnabled(it) },
                                modifier = Modifier.testTag("adaptive_theme_switch")
                            )
                        }

                        // History Ledger Gridlines
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("History Ledger Gridlines", fontWeight = FontWeight.Bold)
                                Text(
                                    "Display horizontal gridlines & card borders in calculation history",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = historyGridlinesEnabled,
                                onCheckedChange = { viewModel.setHistoryGridlinesEnabled(it) },
                                modifier = Modifier.testTag("history_gridlines_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Compact View Mode", fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Smaller keypad buttons & reduced padding.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = compactView,
                                onCheckedChange = { viewModel.setCompactView(it) },
                                modifier = Modifier.testTag("compact_view_switch")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Granular Layout Customization Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Display & Keypad Custom Dimensions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

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
                                    "Prevents keypad height from resizing when entering numbers",
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

                        // Keypad Height Coverage Slider (30% to 70%)
                        val keypadHeightPercent = if (keypadHeightScale in 30f..70f) keypadHeightScale else 52f
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Keypad Height Coverage", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("${kotlin.math.round(keypadHeightPercent).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Text("Adjust keypad screen height from 30% (compact) up to 70% (large)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Slider(
                                value = keypadHeightPercent,
                                onValueChange = { viewModel.setKeypadHeightScale(it) },
                                valueRange = 30f..70f,
                                steps = 39,
                                modifier = Modifier.testTag("keypad_height_slider")
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
                    }
                }
            }

            // --- SUB-PAGE: NAVIGATIONS (Tab Visibility & Nav Options) ---
            SettingsSubPage.NAVIGATIONS -> {
                SubPageHeader(title = "Appearance", subtitle = "Navigations Settings") {
                    activeSubPage = null
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        // Enable Bottom Navigation Bar Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Enable Bottom Navigation Bar", fontWeight = FontWeight.Bold)
                                Text(
                                    "Show or hide the bottom navigation bar completely. When hidden, navigate via top screen menu.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = showBottomBar,
                                onCheckedChange = { viewModel.setShowBottomBar(it) },
                                modifier = Modifier.testTag("enable_bottom_bar_switch")
                            )
                        }

                        if (showBottomBar) {
                            HorizontalDivider()

                            // Tab Sections option item
                            SettingsItemRow(
                                icon = Icons.Default.Reorder,
                                title = "Tab Sections & Order",
                                subtitle = "Choose visible tabs and drag/reorder items",
                                showChevron = true
                            ) {
                                activeSubPage = SettingsSubPage.TAB_SECTIONS
                            }

                            HorizontalDivider()

                            // Expressive Navigation Design Style
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Expressive Navigation Style", fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val styles = listOf(
                                        "M3DOCK" to "⚡ M3 Dock",
                                        "INVISIBLE" to "👻 Invisible / No Bg",
                                        "LIQUID_GLASS" to "💧 Liquid Glass",
                                        "MINIMAL_BUBBLE" to "🫧 Bubble",
                                        "M3_STANDARD" to "📱 Classic M3"
                                    )
                                    styles.forEach { (key, label) ->
                                        FilterChip(
                                            selected = navBarStyle == key || (key == "M3DOCK" && navBarStyle == "M3_EXPRESSIVE_DOCK"),
                                            onClick = { viewModel.setNavBarStyle(key) },
                                            label = { Text(label, fontSize = 11.sp) },
                                            modifier = Modifier.testTag("nav_style_$key")
                                        )
                                    }
                                }
                            }

                            HorizontalDivider()

                            // Animation Physics Speed
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Animation Physics & Smoothness", fontWeight = FontWeight.Bold)
                                Text(
                                    "GPU-accelerated sliding spring curves",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val physics = listOf(
                                        "SMOOTH_GLIDE" to "🌊 Smooth Glide",
                                        "BOUNCY" to "🔮 Bouncy",
                                        "FAST" to "⚡ Fast Physics",
                                        "SNAPPY" to "🎯 Snappy"
                                    )
                                    physics.forEach { (key, label) ->
                                        FilterChip(
                                            selected = navAnimationSpeed == key || (key == "SMOOTH_GLIDE" && navAnimationSpeed == "SMOOTH"),
                                            onClick = { viewModel.setNavAnimationSpeed(key) },
                                            label = { Text(label, fontSize = 11.sp) },
                                            modifier = Modifier.testTag("anim_speed_$key")
                                        )
                                    }
                                }
                            }

                            HorizontalDivider()

                            // Glass Opacity Slider
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Glass Blur Surface Opacity", fontWeight = FontWeight.Bold)
                                    Text(
                                        "${(navBarBlurOpacity * 100).roundToInt()}%",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Slider(
                                    value = navBarBlurOpacity,
                                    onValueChange = { viewModel.setNavBarBlurOpacity(it) },
                                    valueRange = 0.2f..1.0f,
                                    modifier = Modifier.testTag("nav_blur_opacity_slider")
                                )
                            }

                            HorizontalDivider()

                            // Icon-Only Bottom Bar Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Icon-Only Navigation", fontWeight = FontWeight.Bold)
                                    Text(
                                        "Hide text labels for a minimalist look",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = iconOnlyNav,
                                    onCheckedChange = { viewModel.setIconOnlyNav(it) },
                                    modifier = Modifier.testTag("icon_only_nav_switch")
                                )
                            }

                            HorizontalDivider()

                            // Selection Indicator Size
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Active Indicator Size & Scale", fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(
                                        "SMALL" to "Compact",
                                        "STANDARD" to "Standard",
                                        "LARGE" to "Wide",
                                        "CIRCLE" to "Circle"
                                    ).forEach { (sizeKey, label) ->
                                        FilterChip(
                                            selected = navIndicatorSize == sizeKey,
                                            onClick = { viewModel.setNavIndicatorSize(sizeKey) },
                                            label = { Text(label, fontSize = 11.sp) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("indicator_size_$sizeKey")
                                        )
                                    }
                                }

                                Slider(
                                    value = navIndicatorScale,
                                    onValueChange = { viewModel.setNavIndicatorScale(it) },
                                    valueRange = 0.4f..1.6f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("indicator_scale_slider")
                                )
                            }
                        }
                    }
                }
            }

            // --- SUB-PAGE: TAB SECTIONS (Reorder & Visibility Checkboxes) ---
            SettingsSubPage.TAB_SECTIONS -> {
                SubPageHeader(title = "Navigations", subtitle = "Tab Sections") {
                    activeSubPage = SettingsSubPage.NAVIGATIONS
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tab Visibility & Order", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            TextButton(onClick = { viewModel.resetTabOrder() }) {
                                Text("Reset Default", fontSize = 12.sp)
                            }
                        }

                        Text(
                            "Choose which tabs are visible, and drag/use the handle buttons to reorder them in the navigation bar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider()

                        tabOrder.forEachIndexed { index, tabKey ->
                            val isEnabled = enabledTabs.contains(tabKey)
                            val tabName = tabKey.lowercase().replaceFirstChar { it.uppercase() }
                            val icon = when (tabKey) {
                                "CALCULATOR" -> Icons.Default.Calculate
                                "CURRENCY" -> Icons.Default.CurrencyExchange
                                "UNIT" -> Icons.Default.Straighten
                                "MATRIX" -> Icons.Default.GridOn
                                "HISTORY" -> Icons.Default.History
                                else -> Icons.Default.Settings
                            }

                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isEnabled,
                                            onCheckedChange = { viewModel.toggleTabEnabled(tabKey) }
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(tabName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.moveTabUp(index) },
                                            enabled = index > 0
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up")
                                        }
                                        IconButton(
                                            onClick = { viewModel.moveTabDown(index) },
                                            enabled = index < tabOrder.size - 1
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down")
                                        }
                                        Icon(
                                            Icons.Default.DragHandle,
                                            contentDescription = "Reorder",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        ElevatedButton(
                            onClick = { activeSubPage = SettingsSubPage.NAVIGATIONS },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Done")
                        }
                    }
                }
            }

            // --- SUB-PAGE: SECURITY & PRIVACY ---
            SettingsSubPage.SECURITY -> {
                SubPageHeader(title = "Security", subtitle = "Authentication & Passcode PIN") {
                    activeSubPage = null
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Incognito Mode", fontWeight = FontWeight.Bold)
                                Text(
                                    "Do not record calculations into history database while active.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = incognitoMode,
                                onCheckedChange = { viewModel.toggleIncognitoMode(it) },
                                modifier = Modifier.testTag("incognito_switch")
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Passcode PIN Lock", fontWeight = FontWeight.Bold)
                                Text(
                                    if (appPin.isEmpty()) "Disabled. Protect your app with a 4-digit PIN." else "Enabled (PIN set)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (appPin.isEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        pinInput = ""
                                        pinError = null
                                        showPinDialog = true
                                    },
                                    modifier = Modifier.testTag("set_pin_btn")
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Set PIN")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.removeAppPin() },
                                    modifier = Modifier.testTag("remove_pin_btn")
                                ) {
                                    Icon(Icons.Default.LockOpen, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove PIN")
                                }
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Biometric Authentication", fontWeight = FontWeight.Bold)
                                Text(
                                    "Unlock using Fingerprint or Face ID when PIN lock is active.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = biometricLockEnabled,
                                onCheckedChange = { viewModel.toggleBiometricLock(it) },
                                modifier = Modifier.testTag("biometric_lock_switch")
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Clear All Data", fontWeight = FontWeight.Bold)
                                Text(
                                    "Permanently delete all calculation history and saved favorites.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(
                                onClick = { showClearConfirm = true },
                                modifier = Modifier.testTag("clear_all_data_btn")
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // --- MAIN CATEGORIZED SETTINGS SCREEN (Default) ---
            null -> {
                // Section 1: Appearance
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SettingsItemRow(
                            icon = Icons.Default.Palette,
                            title = "Interface",
                            subtitle = "Themes, colors, and layout",
                            showChevron = true
                        ) {
                            activeSubPage = SettingsSubPage.INTERFACE
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                        SettingsItemRow(
                            icon = Icons.Default.Tune,
                            title = "Navigations",
                            subtitle = "Tab visibility and tab style",
                            showChevron = true
                        ) {
                            activeSubPage = SettingsSubPage.NAVIGATIONS
                        }
                    }
                }

                // Section 2: Calls & System / Calculator Preferences
                Text(
                    text = "Calculator & System",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Top History Stream Banner Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                CircularIconBadge(icon = Icons.Default.History)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Top History Banner", fontWeight = FontWeight.Bold)
                                    Text(
                                        "Recent history stream on keypad",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = topHistoryBannerVisible,
                                onCheckedChange = { viewModel.setTopHistoryBannerVisible(it) }
                            )
                        }

                        HorizontalDivider()

                        // Decimal Precision Selection
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularIconBadge(icon = Icons.Default.Functions)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Decimal Precision", fontWeight = FontWeight.Bold)
                                    Text("Floating point rounding output", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(-1 to "Auto", 2 to "2 Dec", 4 to "4 Dec", 6 to "6 Dec").forEach { (precision, label) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { viewModel.setDecimalPrecision(precision) }
                                    ) {
                                        RadioButton(
                                            selected = decimalPrecision == precision,
                                            onClick = { viewModel.setDecimalPrecision(precision) }
                                        )
                                        Text(label, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        HorizontalDivider()

                        // Number Formatting Style Selection
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularIconBadge(icon = Icons.Default.Calculate)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Number Formatting Preference", fontWeight = FontWeight.Bold)
                                    Text("Select separator style for live and main display", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "STANDARD" to "1,234.56",
                                    "EUROPEAN" to "1.234,56",
                                    "INDIAN" to "12,34,567",
                                    "SCIENTIFIC" to "1.23e4",
                                    "PLAIN" to "1234.56"
                                ).forEach { (fmtKey, label) ->
                                    FilterChip(
                                        selected = numberFormatStyle.equals(fmtKey, ignoreCase = true),
                                        onClick = { viewModel.setNumberFormatStyle(fmtKey) },
                                        label = { Text(label, fontSize = 11.sp) },
                                        modifier = Modifier.testTag("num_format_chip_$fmtKey")
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        // Haptic Touch Feedback Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                CircularIconBadge(icon = Icons.Default.Vibration)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Sound & Vibration", fontWeight = FontWeight.Bold)
                                    Text(
                                        "Ringtones, dialpad tones, and haptics",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = hapticFeedbackEnabled,
                                onCheckedChange = { viewModel.setHapticFeedbackEnabled(it) }
                            )
                        }

                        HorizontalDivider()

                        // Calculation & Features Guide Item
                        SettingsItemRow(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            title = "Calculation & Features Guide",
                            subtitle = "Learn percentage syntax, scientific functions & modes",
                            showChevron = true
                        ) {
                            showGuideSheet = true
                        }
                    }
                }

                // Section 3: Security & Privacy
                Text(
                    text = "Security & Privacy",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SettingsItemRow(
                            icon = Icons.Default.Shield,
                            title = "Authentication & Passcode",
                            subtitle = if (appPin.isEmpty()) "Not configured" else "Passcode PIN Active",
                            showChevron = true
                        ) {
                            activeSubPage = SettingsSubPage.SECURITY
                        }
                    }
                }

                // Section 4: Backup & Configuration Export
                Text(
                    text = "Backup & Export",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SettingsItemRow(
                            icon = Icons.Default.FileDownload,
                            title = "Export Settings (JSON)",
                            subtitle = "Export current configuration & layout preferences to JSON",
                            showChevron = true
                        ) {
                            exportJsonText = viewModel.exportSettingsJson()
                            showExportDialog = true
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                        SettingsItemRow(
                            icon = Icons.Default.FileUpload,
                            title = "Import Settings (JSON)",
                            subtitle = "Restore layout, theme, and animation preferences from JSON",
                            showChevron = true
                        ) {
                            importJsonInput = ""
                            showImportDialog = true
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                        SettingsItemRow(
                            icon = Icons.Default.RestartAlt,
                            title = "Reset All Settings",
                            subtitle = "Restore factory default themes and layout configurations",
                            showChevron = false
                        ) {
                            viewModel.resetAllSettings()
                            Toast.makeText(context, "Settings reset to factory defaults", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // App Info Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Advanced Liquid Glass Calculator v2.5", fontWeight = FontWeight.Bold)
                            Text("Pill Navigation | 100% Offline Database | Material 3 UI", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    // PIN Setup Modal Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set 4-Digit Passcode PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter a secret 4-digit PIN to lock your calculator app and history.")
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                pinInput = it
                            }
                        },
                        label = { Text("4-Digit PIN") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = pinError != null
                    )
                    pinError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        if (pinInput.length == 4) {
                            viewModel.setAppPin(pinInput)
                            showPinDialog = false
                        } else {
                            pinError = "PIN must be exactly 4 digits."
                        }
                    }
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation Dialog for Clear All Data
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All Data?") },
            text = { Text("Are you sure you want to delete all calculation history and saved favorites? This action cannot be undone.") },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        viewModel.clearAllDatabaseHistory()
                        showClearConfirm = false
                        Toast.makeText(context, "All calculation history permanently deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete Everything", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Export Settings Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Settings (JSON)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your current application configuration JSON:")
                    OutlinedTextField(
                        value = exportJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportJsonText))
                        Toast.makeText(context, "Copied JSON settings to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Import Settings Dialog
    if (showImportDialog) {
        var importError by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Settings (JSON)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste custom configuration JSON below to apply settings:")
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = {
                            importJsonInput = it
                            importError = null
                        },
                        placeholder = { Text("{\n  \"theme_preset\": \"LIQUID_GLASS\",\n  ...\n}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        singleLine = false,
                        isError = importError != null
                    )
                    importError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        if (importJsonInput.isBlank()) {
                            importError = "Please paste a valid JSON string."
                        } else {
                            val success = viewModel.importSettingsFromJson(importJsonInput)
                            if (success) {
                                Toast.makeText(context, "Settings imported & applied successfully!", Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                            } else {
                                importError = "Invalid JSON structure. Please check syntax."
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showGuideSheet) {
        CalculationGuideSheet(
            onDismiss = { showGuideSheet = false }
        )
    }
}

@Composable
fun SubPageHeader(title: String, subtitle: String, onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(subtitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CircularIconBadge(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showChevron: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            CircularIconBadge(icon = icon)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (showChevron) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
