package com.zakeercareer.calculator.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.content.SharedPreferences
import com.zakeercareer.calculator.data.currency.CurrencyInfo
import com.zakeercareer.calculator.data.currency.CurrencyRepository
import com.zakeercareer.calculator.data.currency.ExchangeRatesState
import com.zakeercareer.calculator.data.currency.defaultCurrencies
import com.zakeercareer.calculator.data.db.AppDatabase
import com.zakeercareer.calculator.data.db.CalculationEntity
import com.zakeercareer.calculator.util.EvaluationResult
import com.zakeercareer.calculator.util.MathEvaluator
import com.zakeercareer.calculator.util.MatrixData
import com.zakeercareer.calculator.util.MatrixResult
import com.zakeercareer.calculator.util.MatrixUtils
import com.zakeercareer.calculator.util.UnitCategory
import com.zakeercareer.calculator.util.UnitConverter
import com.zakeercareer.calculator.util.UnitItem
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private var previewJob: Job? = null
    private val previewGeneration = java.util.concurrent.atomic.AtomicLong(0L)

    private val prefs: SharedPreferences = application.getSharedPreferences("calc_settings", Context.MODE_PRIVATE)
    private val dao = AppDatabase.getDatabase(application).calculationDao()

    // --- SECURITY & PRIVACY SETTINGS ---
    private val _incognitoMode = MutableStateFlow(prefs.getBoolean("incognito_mode", false))
    val incognitoMode: StateFlow<Boolean> = _incognitoMode.asStateFlow()

    companion object {
        private const val PBKDF2_ITERATIONS = 100_000
        private const val PBKDF2_KEY_LENGTH = 256
        private const val SALT_BYTE_LENGTH = 32
    }

    private fun getOrCreateSecurePinSalt(): String {
        var saltHex = prefs.getString("app_pin_salt_pbkdf2", null)
        if (saltHex == null) {
            val random = java.security.SecureRandom()
            val saltBytes = ByteArray(SALT_BYTE_LENGTH)
            random.nextBytes(saltBytes)
            saltHex = saltBytes.joinToString("") { "%02x".format(it) }
            prefs.edit().putString("app_pin_salt_pbkdf2", saltHex).apply()
        }
        return saltHex
    }

    private fun hashPinPbkdf2(pin: String, saltHex: String): String {
        val saltBytes = saltHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val spec = javax.crypto.spec.PBEKeySpec(pin.toCharArray(), saltBytes, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH)
        val factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hashBytes = factory.generateSecret(spec).encoded
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun getOrCreateLegacyPinSalt(): String {
        var salt = prefs.getString("app_pin_salt", null)
        if (salt == null) {
            salt = java.util.UUID.randomUUID().toString()
            prefs.edit().putString("app_pin_salt", salt).apply()
        }
        return salt
    }

    private fun hashPinLegacySha256(pin: String, salt: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((salt + pin).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun initPinSecurity(): Boolean {
        if (prefs.getString("app_pin_hash_pbkdf2", null)?.isNotEmpty() == true) {
            return true
        }
        val legacyPin = prefs.getString("app_pin", null)
        if (!legacyPin.isNullOrEmpty()) {
            val salt = getOrCreateSecurePinSalt()
            val hash = hashPinPbkdf2(legacyPin, salt)
            prefs.edit()
                .putString("app_pin_hash_pbkdf2", hash)
                .remove("app_pin")
                .remove("app_pin_hash")
                .remove("app_pin_salt")
                .apply()
            return true
        }
        return prefs.getString("app_pin_hash", null)?.isNotEmpty() == true
    }

    private val _hasAppPin = MutableStateFlow(initPinSecurity())
    val hasAppPin: StateFlow<Boolean> = _hasAppPin.asStateFlow()

    // Backward-compatible representation of appPin: non-empty if configured
    private val _appPin = MutableStateFlow(if (_hasAppPin.value) "SET" else "")
    val appPin: StateFlow<String> = _appPin.asStateFlow()

    private val _biometricLockEnabled = MutableStateFlow(prefs.getBoolean("biometric_lock_enabled", false))
    val biometricLockEnabled: StateFlow<Boolean> = _biometricLockEnabled.asStateFlow()

    private val _isAppLocked = MutableStateFlow(_hasAppPin.value || _biometricLockEnabled.value)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private var failedPinAttempts = prefs.getInt("app_pin_failed_attempts", 0)
    private var lockoutUntilMs = prefs.getLong("app_pin_lockout_until_ms", 0L)

    // --- DISPLAY & PREFERENCES SETTINGS ---
    private val _decimalPrecision = MutableStateFlow(prefs.getInt("decimal_precision", -1))
    val decimalPrecision: StateFlow<Int> = _decimalPrecision.asStateFlow()

    private val _numberFormatStyle = MutableStateFlow(prefs.getString("number_format_style", "STANDARD") ?: "STANDARD")
    val numberFormatStyle: StateFlow<String> = _numberFormatStyle.asStateFlow()

    fun setNumberFormatStyle(style: String) {
        _numberFormatStyle.value = style
        prefs.edit().putString("number_format_style", style).apply()
        updatePreview()
    }

    private val _themePreset = MutableStateFlow(prefs.getString("theme_preset", "MATERIAL_YOU") ?: "MATERIAL_YOU")
    val themePreset: StateFlow<String> = _themePreset.asStateFlow()

    private val _compactView = MutableStateFlow(prefs.getBoolean("compact_view", false))
    val compactView: StateFlow<Boolean> = _compactView.asStateFlow()

    private val _useDynamicColor = MutableStateFlow(prefs.getBoolean("dynamic_color", true))
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor.asStateFlow()

    private val _btnShape = MutableStateFlow(prefs.getString("btn_shape", "ROUNDED") ?: "ROUNDED")
    val btnShape: StateFlow<String> = _btnShape.asStateFlow()

    private val _displayFontSize = MutableStateFlow(prefs.getString("display_font_size", "NORMAL") ?: "NORMAL")
    val displayFontSize: StateFlow<String> = _displayFontSize.asStateFlow()

    private val _displayAlign = MutableStateFlow(prefs.getString("display_align", "RIGHT") ?: "RIGHT")
    val displayAlign: StateFlow<String> = _displayAlign.asStateFlow()

    private val _numberAnimationType = MutableStateFlow(prefs.getString("number_anim_type", "OFF") ?: "OFF")
    val numberAnimationType: StateFlow<String> = _numberAnimationType.asStateFlow()

    fun setNumberAnimationType(type: String) {
        _numberAnimationType.value = type
        prefs.edit().putString("number_anim_type", type).apply()
    }

    // --- GRANULAR DISPLAY & KEYPAD LAYOUT CUSTOMIZATION ---
    private val _displayHeightDp = MutableStateFlow(prefs.getInt("display_height_dp", 180))
    val displayHeightDp: StateFlow<Int> = _displayHeightDp.asStateFlow()

    private val _displayWidthPaddingDp = MutableStateFlow(prefs.getInt("display_width_padding_dp", 4))
    val displayWidthPaddingDp: StateFlow<Int> = _displayWidthPaddingDp.asStateFlow()

    private val _displayCornerRadiusDp = MutableStateFlow(prefs.getInt("display_corner_radius_dp", 32))
    val displayCornerRadiusDp: StateFlow<Int> = _displayCornerRadiusDp.asStateFlow()

    private val _displayMainFontSizeSp = MutableStateFlow(prefs.getInt("display_main_font_size_sp", 34))
    val displayMainFontSizeSp: StateFlow<Int> = _displayMainFontSizeSp.asStateFlow()

    private val _displayPreviewFontSizeSp = MutableStateFlow(prefs.getInt("display_preview_font_size_sp", 24))
    val displayPreviewFontSizeSp: StateFlow<Int> = _displayPreviewFontSizeSp.asStateFlow()

    private fun getInitialKeypadHeightPercent(): Float {
        val raw = prefs.getFloat("keypad_height_scale", 40f)
        return when {
            raw in 30f..70f -> raw
            raw in 0.3f..0.7f -> raw * 100f
            raw in 0.6f..1.6f -> 30f + (raw - 0.6f) * (40f / 1.0f)
            else -> 40f
        }.coerceIn(30f, 70f)
    }

    private val _keypadHeightScale = MutableStateFlow(getInitialKeypadHeightPercent())
    val keypadHeightScale: StateFlow<Float> = _keypadHeightScale.asStateFlow()

    private val _keypadWidthPaddingDp = MutableStateFlow(prefs.getInt("keypad_width_padding_dp", 6))
    val keypadWidthPaddingDp: StateFlow<Int> = _keypadWidthPaddingDp.asStateFlow()

    private val _keypadGridSpacingDp = MutableStateFlow(prefs.getInt("keypad_grid_spacing_dp", 6))
    val keypadGridSpacingDp: StateFlow<Int> = _keypadGridSpacingDp.asStateFlow()

    private val _keypadBtnCornerRadiusDp = MutableStateFlow(prefs.getInt("keypad_btn_corner_radius_dp", 50))
    val keypadBtnCornerRadiusDp: StateFlow<Int> = _keypadBtnCornerRadiusDp.asStateFlow()

    private val _keypadBtnFontSizeSp = MutableStateFlow(prefs.getInt("keypad_btn_font_size_sp", 22))
    val keypadBtnFontSizeSp: StateFlow<Int> = _keypadBtnFontSizeSp.asStateFlow()

    private val _lockKeypadHeight = MutableStateFlow(prefs.getBoolean("lock_keypad_height", true))
    val lockKeypadHeight: StateFlow<Boolean> = _lockKeypadHeight.asStateFlow()

    private val _hasUserCustomDefaults = MutableStateFlow(prefs.getBoolean("has_user_custom_defaults", false))
    val hasUserCustomDefaults: StateFlow<Boolean> = _hasUserCustomDefaults.asStateFlow()

    private val _showLivePreview = MutableStateFlow(prefs.getBoolean("show_live_preview", true))
    val showLivePreview: StateFlow<Boolean> = _showLivePreview.asStateFlow()

    // --- ULTRA PERFORMANCE MODE (Lag-Free / High FPS) ---
    private val _ultraPerformanceMode = MutableStateFlow(prefs.getBoolean("ultra_performance_mode", false))
    val ultraPerformanceMode: StateFlow<Boolean> = _ultraPerformanceMode.asStateFlow()

    // --- LIVE PREVIEW ANIMATION (DEFAULT OFF) ---
    private val _livePreviewAnimEnabled = MutableStateFlow(prefs.getBoolean("live_preview_anim_enabled", false))
    val livePreviewAnimEnabled: StateFlow<Boolean> = _livePreviewAnimEnabled.asStateFlow()

    private val _livePreviewAnimStyle = MutableStateFlow(prefs.getString("live_preview_anim_style", "SLIDE") ?: "SLIDE")
    val livePreviewAnimStyle: StateFlow<String> = _livePreviewAnimStyle.asStateFlow()

    // --- HISTORY GRIDLINES, SWIPE ACTIONS & DELETION LOCK ---
    private val _historyGridlinesEnabled = MutableStateFlow(prefs.getBoolean("history_gridlines_enabled", true))
    val historyGridlinesEnabled: StateFlow<Boolean> = _historyGridlinesEnabled.asStateFlow()

    private val _historySwipeLeftAction = MutableStateFlow(prefs.getString("history_swipe_left_action", "DELETE") ?: "DELETE")
    val historySwipeLeftAction: StateFlow<String> = _historySwipeLeftAction.asStateFlow()

    private val _historySwipeRightAction = MutableStateFlow(prefs.getString("history_swipe_right_action", "INSERT") ?: "INSERT")
    val historySwipeRightAction: StateFlow<String> = _historySwipeRightAction.asStateFlow()

    private val _historyDeletionLocked = MutableStateFlow(prefs.getBoolean("history_deletion_locked", false))
    val historyDeletionLocked: StateFlow<Boolean> = _historyDeletionLocked.asStateFlow()

    // --- CALC SECTION HISTORY BAR CUSTOMIZATION ---
    private val _calcHistoryGridlineStyle = MutableStateFlow(prefs.getString("calc_history_gridline_style", "DOTTED") ?: "DOTTED")
    val calcHistoryGridlineStyle: StateFlow<String> = _calcHistoryGridlineStyle.asStateFlow()

    private val _calcHistoryShowGridlines = MutableStateFlow(prefs.getBoolean("calc_history_show_gridlines", true))
    val calcHistoryShowGridlines: StateFlow<Boolean> = _calcHistoryShowGridlines.asStateFlow()

    private val _calcHistoryGridlineStrokeWidthDp = MutableStateFlow(prefs.getFloat("calc_history_gridline_stroke_width_dp", 1.5f))
    val calcHistoryGridlineStrokeWidthDp: StateFlow<Float> = _calcHistoryGridlineStrokeWidthDp.asStateFlow()

    private val _calcHistoryGridlineAlpha = MutableStateFlow(prefs.getFloat("calc_history_gridline_alpha", 0.85f))
    val calcHistoryGridlineAlpha: StateFlow<Float> = _calcHistoryGridlineAlpha.asStateFlow()

    private val _calcHistoryShowItemDividers = MutableStateFlow(prefs.getBoolean("calc_history_show_item_dividers", true))
    val calcHistoryShowItemDividers: StateFlow<Boolean> = _calcHistoryShowItemDividers.asStateFlow()

    private val _calcHistoryAutoScrollTop = MutableStateFlow(prefs.getBoolean("calc_history_auto_scroll_top", true))
    val calcHistoryAutoScrollTop: StateFlow<Boolean> = _calcHistoryAutoScrollTop.asStateFlow()

    private val _calcHistoryItemSpacingDp = MutableStateFlow(prefs.getInt("calc_history_item_spacing_dp", 6))
    val calcHistoryItemSpacingDp: StateFlow<Int> = _calcHistoryItemSpacingDp.asStateFlow()

    private val _calcHistoryMaxItemsCount = MutableStateFlow(prefs.getInt("calc_history_max_items_count", 5))
    val calcHistoryMaxItemsCount: StateFlow<Int> = _calcHistoryMaxItemsCount.asStateFlow()

    private val _calcHistoryIsAdaptive = MutableStateFlow(prefs.getBoolean("calc_history_is_adaptive", true))
    val calcHistoryIsAdaptive: StateFlow<Boolean> = _calcHistoryIsAdaptive.asStateFlow()

    private val _calcHistoryExpanded = MutableStateFlow(false)
    val calcHistoryExpanded: StateFlow<Boolean> = _calcHistoryExpanded.asStateFlow()

    // --- CALC SECTION CURRENCY BAR CUSTOMIZATION ---
    private val _calcCurrencyBarEnabled = MutableStateFlow(prefs.getBoolean("calc_currency_bar_enabled", true))
    val calcCurrencyBarEnabled: StateFlow<Boolean> = _calcCurrencyBarEnabled.asStateFlow()

    private val _calcCurrencyDecimals = MutableStateFlow(prefs.getInt("calc_currency_decimals", 2))
    val calcCurrencyDecimals: StateFlow<Int> = _calcCurrencyDecimals.asStateFlow()

    private val _calcCurrencyHeaderShowToggle = MutableStateFlow(prefs.getBoolean("calc_currency_header_show_toggle", true))
    val calcCurrencyHeaderShowToggle: StateFlow<Boolean> = _calcCurrencyHeaderShowToggle.asStateFlow()

    private val _calcCurrencyToggleActive = MutableStateFlow(prefs.getBoolean("calc_currency_toggle_active", true))
    val calcCurrencyToggleActive: StateFlow<Boolean> = _calcCurrencyToggleActive.asStateFlow()

    private val _calcCurrencyToggleAlignment = MutableStateFlow(prefs.getString("calc_currency_toggle_alignment", "LEFT") ?: "LEFT")
    val calcCurrencyToggleAlignment: StateFlow<String> = _calcCurrencyToggleAlignment.asStateFlow()

    private val _calcCurrencyShowGuide = MutableStateFlow(prefs.getBoolean("calc_currency_show_guide", true))
    val calcCurrencyShowGuide: StateFlow<Boolean> = _calcCurrencyShowGuide.asStateFlow()

    private val _calcCurrencyShowThemeStudio = MutableStateFlow(prefs.getBoolean("calc_currency_show_theme_studio", true))
    val calcCurrencyShowThemeStudio: StateFlow<Boolean> = _calcCurrencyShowThemeStudio.asStateFlow()

    private val _calcCurrencyToggleEnabledAction = MutableStateFlow(prefs.getString("calc_currency_toggle_enabled_action", "LIVE_CONVERT") ?: "LIVE_CONVERT")
    val calcCurrencyToggleEnabledAction: StateFlow<String> = _calcCurrencyToggleEnabledAction.asStateFlow()

    private val _calcCurrencyToggleDisabledAction = MutableStateFlow(prefs.getString("calc_currency_toggle_disabled_action", "HIDE_RATE_PILL") ?: "HIDE_RATE_PILL")
    val calcCurrencyToggleDisabledAction: StateFlow<String> = _calcCurrencyToggleDisabledAction.asStateFlow()

    // --- ADAPTIVE DISPLAY RESIZING ---
    private val _adaptiveDisplayResizing = MutableStateFlow(prefs.getBoolean("adaptive_display_resizing", true))
    val adaptiveDisplayResizing: StateFlow<Boolean> = _adaptiveDisplayResizing.asStateFlow()

    // --- ADAPTIVE THEMING ---
    private val _adaptiveThemeEnabled = MutableStateFlow(prefs.getBoolean("adaptive_theme_enabled", true))
    val adaptiveThemeEnabled: StateFlow<Boolean> = _adaptiveThemeEnabled.asStateFlow()

    private val _themeContrastMode = MutableStateFlow(prefs.getString("theme_contrast_mode", "AUTO") ?: "AUTO")
    val themeContrastMode: StateFlow<String> = _themeContrastMode.asStateFlow()

    private val _topHistoryBannerVisible = MutableStateFlow(prefs.getBoolean("top_history_banner", true))
    val topHistoryBannerVisible: StateFlow<Boolean> = _topHistoryBannerVisible.asStateFlow()

    private val _hapticFeedbackEnabled = MutableStateFlow(prefs.getBoolean("haptic_feedback", false))
    val hapticFeedbackEnabled: StateFlow<Boolean> = _hapticFeedbackEnabled.asStateFlow()

    // --- NAVIGATION CUSTOMIZATION SETTINGS ---
    private val defaultTabList = listOf("CALCULATOR", "SCIENTIFIC", "CURRENCY", "UNIT", "MATRIX", "HISTORY", "SETTINGS")
    private val _tabOrder = MutableStateFlow(getSavedTabOrder())
    val tabOrder: StateFlow<List<String>> = _tabOrder.asStateFlow()

    private val _showBottomBar = MutableStateFlow(prefs.getBoolean("show_bottom_bar", true))
    val showBottomBar: StateFlow<Boolean> = _showBottomBar.asStateFlow()

    private val _pillNavStyle = MutableStateFlow(prefs.getBoolean("pill_nav_style", true))
    val pillNavStyle: StateFlow<Boolean> = _pillNavStyle.asStateFlow()

    private val _navBarStyle = MutableStateFlow(prefs.getString("nav_bar_style", "M3DOCK") ?: "M3DOCK")
    val navBarStyle: StateFlow<String> = _navBarStyle.asStateFlow()

    private val _navBarBlurOpacity = MutableStateFlow(prefs.getFloat("nav_bar_blur_opacity", 1.0f))
    val navBarBlurOpacity: StateFlow<Float> = _navBarBlurOpacity.asStateFlow()

    private val _navAnimationSpeed = MutableStateFlow(prefs.getString("nav_anim_speed", "SMOOTH_GLIDE") ?: "SMOOTH_GLIDE")
    val navAnimationSpeed: StateFlow<String> = _navAnimationSpeed.asStateFlow()

    private val _iconOnlyNav = MutableStateFlow(prefs.getBoolean("icon_only_nav", true))
    val iconOnlyNav: StateFlow<Boolean> = _iconOnlyNav.asStateFlow()

    private val _navIndicatorSize = MutableStateFlow(prefs.getString("nav_indicator_size", "STANDARD") ?: "STANDARD")
    val navIndicatorSize: StateFlow<String> = _navIndicatorSize.asStateFlow()

    private val _navIndicatorScale = MutableStateFlow(prefs.getFloat("nav_indicator_scale", 1.0f))
    val navIndicatorScale: StateFlow<Float> = _navIndicatorScale.asStateFlow()

    private val _enabledTabs = MutableStateFlow(getSavedEnabledTabs())
    val enabledTabs: StateFlow<Set<String>> = _enabledTabs.asStateFlow()

    private fun getSavedTabOrder(): List<String> {
        val validSet = defaultTabList.toSet()
        val saved = prefs.getString("tab_order", null) ?: return defaultTabList
        val list = saved.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() && validSet.contains(it) }
            .distinct()
            .toMutableList()

        defaultTabList.forEach { tab ->
            if (!list.contains(tab)) {
                list.add(tab)
            }
        }
        return list
    }

    private fun getSavedEnabledTabs(): Set<String> {
        val validSet = defaultTabList.toSet()
        val saved = prefs.getStringSet("enabled_tabs", null)
        if (saved.isNullOrEmpty()) return defaultTabList.toSet()
        val filtered = saved.filter { validSet.contains(it) }.toMutableSet()
        if (filtered.isEmpty()) {
            filtered.addAll(defaultTabList)
        }
        filtered.add("CALCULATOR") // Always ensure primary calculator remains enabled
        return filtered
    }

    // --- STANDARD / SCIENTIFIC CALCULATOR STATE ---
    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _textFieldValue = MutableStateFlow(TextFieldValue(text = "", selection = TextRange.Zero))
    val textFieldValue: StateFlow<TextFieldValue> = _textFieldValue.asStateFlow()

    private var isResultFresh = false

    private val _previewResult = MutableStateFlow("0")
    val previewResult: StateFlow<String> = _previewResult.asStateFlow()

    private val _isDegreeMode = MutableStateFlow(true)
    val isDegreeMode: StateFlow<Boolean> = _isDegreeMode.asStateFlow()

    private val _isInvMode = MutableStateFlow(false)
    val isInvMode: StateFlow<Boolean> = _isInvMode.asStateFlow()

    private val _memoryValue = MutableStateFlow(0.0)
    val memoryValue: StateFlow<Double> = _memoryValue.asStateFlow()

    // --- MATRIX STATE ---
    private val _matrixARows = MutableStateFlow(3)
    val matrixARows = _matrixARows.asStateFlow()

    private val _matrixACols = MutableStateFlow(3)
    val matrixACols = _matrixACols.asStateFlow()

    private val _matrixA = MutableStateFlow(
        Array(3) { DoubleArray(3) { 0.0 } }
    )
    val matrixA = _matrixA.asStateFlow()

    private val _matrixBRows = MutableStateFlow(3)
    val matrixBRows = _matrixBRows.asStateFlow()

    private val _matrixBCols = MutableStateFlow(3)
    val matrixBCols = _matrixBCols.asStateFlow()

    private val _matrixB = MutableStateFlow(
        Array(3) { DoubleArray(3) { 0.0 } }
    )
    val matrixB = _matrixB.asStateFlow()

    private val _scalarK = MutableStateFlow(2.0)
    val scalarK = _scalarK.asStateFlow()

    private val _matrixResultText = MutableStateFlow("")
    val matrixResultText = _matrixResultText.asStateFlow()

    // --- UNIT CONVERTER STATE ---
    private val _unitCategory = MutableStateFlow(UnitCategory.LENGTH)
    val unitCategory = _unitCategory.asStateFlow()

    private val _unitInputValue = MutableStateFlow("1")
    val unitInputValue = _unitInputValue.asStateFlow()

    private val _unitFrom = MutableStateFlow(UnitConverter.getUnits(UnitCategory.LENGTH)[0])
    val unitFrom = _unitFrom.asStateFlow()

    private val _unitTo = MutableStateFlow(UnitConverter.getUnits(UnitCategory.LENGTH)[1])
    val unitTo = _unitTo.asStateFlow()

    private val _unitResultValue = MutableStateFlow("1000")
    val unitResultValue = _unitResultValue.asStateFlow()

    // --- CURRENCY CONVERTER STATE ---
    private val _currencyAmount = MutableStateFlow("100")
    val currencyAmount = _currencyAmount.asStateFlow()

    private val savedFromCode = prefs.getString("calc_from_currency_code", "USD") ?: "USD"
    private val savedToCode = prefs.getString("calc_to_currency_code", "EUR") ?: "EUR"

    private val _fromCurrency = MutableStateFlow(defaultCurrencies.find { it.code == savedFromCode } ?: defaultCurrencies[0])
    val fromCurrency = _fromCurrency.asStateFlow()

    private val _toCurrency = MutableStateFlow(defaultCurrencies.find { it.code == savedToCode } ?: defaultCurrencies[1])
    val toCurrency = _toCurrency.asStateFlow()

    private val _exchangeState = MutableStateFlow(ExchangeRatesState(isLoading = true))
    val exchangeState = _exchangeState.asStateFlow()

    private val _convertedCurrency = MutableStateFlow("0.00")
    val convertedCurrency = _convertedCurrency.asStateFlow()

    // --- HISTORY STATE & ROOM FLOW ---
    val filterCategory = MutableStateFlow("ALL")
    val searchQuery = MutableStateFlow("")

    private fun escapeSqlWildcards(input: String): String {
        return input.replace("^", "^^")
            .replace("%", "^%")
            .replace("_", "^_")
    }

    val historyList: StateFlow<List<CalculationEntity>> = combine(searchQuery, filterCategory) { query, cat ->
        query to cat
    }.flatMapLatest { (query, cat) ->
        if (query.isNotBlank()) {
            val sanitizedQuery = escapeSqlWildcards(query.trim())
            when (cat) {
                "ALL" -> dao.searchHistory(sanitizedQuery)
                "FAVORITES" -> dao.searchFavorites(sanitizedQuery)
                else -> dao.searchHistoryByCategory(sanitizedQuery, cat)
            }
        } else {
            when (cat) {
                "ALL" -> dao.getAllHistory()
                "FAVORITES" -> dao.getFavorites()
                else -> dao.getByCategory(cat)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecentHistory: StateFlow<List<CalculationEntity>> = dao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashList: StateFlow<List<CalculationEntity>> = dao.getTrashHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Set up initial matrix values
        initSampleMatrix()

        // Load persistent currency cache if available
        val cached = CurrencyRepository.loadCache(getApplication<Application>())
        if (cached != null) {
            _exchangeState.value = cached
        }

        // Fetch currency rates
        refreshCurrencyRates()
    }

    private fun initSampleMatrix() {
        val a = Array(3) { DoubleArray(3) }
        a[0] = doubleArrayOf(1.0, 2.0, 3.0)
        a[1] = doubleArrayOf(0.0, 1.0, 4.0)
        a[2] = doubleArrayOf(5.0, 6.0, 0.0)
        _matrixA.value = a

        val b = Array(3) { DoubleArray(3) }
        b[0] = doubleArrayOf(2.0, 0.0, 1.0)
        b[1] = doubleArrayOf(3.0, 1.0, 0.0)
        b[2] = doubleArrayOf(5.0, 1.0, 2.0)
        _matrixB.value = b
    }

    // --- CALCULATOR ACTIONS ---
    fun onExpressionValueChange(newValue: TextFieldValue) {
        isResultFresh = false
        _textFieldValue.value = newValue
        _expression.value = newValue.text
        updatePreview()
    }

    fun setExpression(expr: String) {
        isResultFresh = false
        _expression.value = expr
        _textFieldValue.value = TextFieldValue(text = expr, selection = TextRange(expr.length))
        updatePreview()
    }

    fun applyResult(result: String) {
        isResultFresh = false
        _expression.value = result
        _textFieldValue.value = TextFieldValue(text = result, selection = TextRange(result.length))
        updatePreview()
    }

    fun applyEquation(equation: String) {
        isResultFresh = false
        _expression.value = equation
        _textFieldValue.value = TextFieldValue(text = equation, selection = TextRange(equation.length))
        updatePreview()
    }

    fun onAppendInput(text: String) {
        val isOperator = text in listOf("+", "−", "-", "×", "*", "÷", "/", "^", "%")
        if (isResultFresh) {
            isResultFresh = false
            if (!isOperator) {
                _textFieldValue.value = TextFieldValue(text = "", selection = TextRange.Zero)
                _expression.value = ""
            }
        }

        val currentTfv = _textFieldValue.value
        val currentText = currentTfv.text
        val selection = currentTfv.selection

        val start = selection.min.coerceIn(0, currentText.length)
        val end = selection.max.coerceIn(0, currentText.length)

        val newText = currentText.replaceRange(start, end, text)
        val newCursorPos = start + text.length
        val newTfv = TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPos)
        )

        _textFieldValue.value = newTfv
        _expression.value = newText
        updatePreview()
    }

    fun onDeleteChar() {
        isResultFresh = false
        val currentTfv = _textFieldValue.value
        val currentText = currentTfv.text
        val selection = currentTfv.selection

        if (currentText.isEmpty()) return

        val start = selection.min.coerceIn(0, currentText.length)
        val end = selection.max.coerceIn(0, currentText.length)

        val (newText, newCursorPos) = if (start < end) {
            val textAfterDelete = currentText.removeRange(start, end)
            Pair(textAfterDelete, start)
        } else if (start > 0) {
            val textAfterDelete = currentText.removeRange(start - 1, start)
            Pair(textAfterDelete, start - 1)
        } else {
            Pair(currentText, 0)
        }

        val newTfv = TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPos)
        )
        _textFieldValue.value = newTfv
        _expression.value = newText
        updatePreview()
    }

    fun onClearAll() {
        isResultFresh = false
        _textFieldValue.value = TextFieldValue(text = "", selection = TextRange.Zero)
        _expression.value = ""
        _previewResult.value = "0"
    }

    fun toggleDegreeMode() {
        _isDegreeMode.value = !_isDegreeMode.value
        updatePreview()
    }

    fun toggleInvMode() {
        _isInvMode.value = !_isInvMode.value
    }

    fun memoryClear() {
        _memoryValue.value = 0.0
    }

    fun memoryRecall() {
        onAppendInput(MathEvaluator.formatNumber(_memoryValue.value))
    }

    fun memoryAdd() {
        val eval = MathEvaluator.evaluatePartial(_expression.value, _isDegreeMode.value)
        if (eval is EvaluationResult.Success) {
            _memoryValue.value += eval.rawValue
        }
    }

    fun memorySubtract() {
        val eval = MathEvaluator.evaluatePartial(_expression.value, _isDegreeMode.value)
        if (eval is EvaluationResult.Success) {
            _memoryValue.value -= eval.rawValue
        }
    }

    fun memoryStore() {
        val eval = MathEvaluator.evaluatePartial(_expression.value, _isDegreeMode.value)
        if (eval is EvaluationResult.Success) {
            _memoryValue.value = eval.rawValue
        }
    }

    fun toggleSign() {
        val expr = _expression.value
        if (expr.isEmpty()) return
        if (expr.startsWith("-")) {
            val newExpr = expr.substring(1)
            _expression.value = newExpr
            _textFieldValue.value = TextFieldValue(text = newExpr, selection = TextRange(newExpr.length))
        } else {
            val newExpr = "-($expr)"
            _expression.value = newExpr
            _textFieldValue.value = TextFieldValue(text = newExpr, selection = TextRange(newExpr.length))
        }
        updatePreview()
    }

    private fun updatePreview() {
        val expr = _expression.value
        if (expr.isBlank()) {
            _previewResult.value = "0"
            return
        }
        val gen = previewGeneration.incrementAndGet()
        previewJob?.cancel()
        previewJob = viewModelScope.launch(Dispatchers.Default) {
            delay(50)
            if (gen != previewGeneration.get()) return@launch
            val res = MathEvaluator.evaluatePartial(expr, _isDegreeMode.value, _decimalPrecision.value, _numberFormatStyle.value)
            if (gen != previewGeneration.get()) return@launch
            withContext(Dispatchers.Main) {
                if (gen == previewGeneration.get()) {
                    _previewResult.value = when (res) {
                        is EvaluationResult.Success -> res.formattedResult
                        is EvaluationResult.Error -> "..."
                    }
                }
            }
        }
    }

    fun onCalculate() {
        val expr = _expression.value
        if (expr.isBlank()) return
        val res = MathEvaluator.evaluateStrict(expr, _isDegreeMode.value, _decimalPrecision.value, _numberFormatStyle.value)
        if (res is EvaluationResult.Success) {
            val resultStr = res.formattedResult
            _expression.value = resultStr
            _textFieldValue.value = TextFieldValue(text = resultStr, selection = TextRange(resultStr.length))
            _previewResult.value = resultStr
            isResultFresh = true

            // Save to Room DB history
            saveHistory("STANDARD", expr, resultStr, details = if (_isDegreeMode.value) "DEG" else "RAD")
        } else if (res is EvaluationResult.Error) {
            _previewResult.value = res.message
        }
    }

    // --- MATRIX ACTIONS ---
    fun updateMatrixDimensionsA(rows: Int, cols: Int) {
        _matrixARows.value = rows
        _matrixACols.value = cols
        _matrixA.value = Array(rows) { r ->
            DoubleArray(cols) { c ->
                _matrixA.value.getOrNull(r)?.getOrNull(c) ?: 0.0
            }
        }
    }

    fun updateMatrixDimensionsB(rows: Int, cols: Int) {
        _matrixBRows.value = rows
        _matrixBCols.value = cols
        _matrixB.value = Array(rows) { r ->
            DoubleArray(cols) { c ->
                _matrixB.value.getOrNull(r)?.getOrNull(c) ?: 0.0
            }
        }
    }

    fun updateMatrixACell(r: Int, c: Int, value: Double) {
        val arr = _matrixA.value.map { it.clone() }.toTypedArray()
        if (r in arr.indices && c in arr[r].indices) {
            arr[r][c] = value
            _matrixA.value = arr
        }
    }

    fun updateMatrixBCell(r: Int, c: Int, value: Double) {
        val arr = _matrixB.value.map { it.clone() }.toTypedArray()
        if (r in arr.indices && c in arr[r].indices) {
            arr[r][c] = value
            _matrixB.value = arr
        }
    }

    fun updateScalarK(k: Double) {
        _scalarK.value = k
    }

    fun executeMatrixOperation(op: String) {
        val matA = MatrixData(_matrixARows.value, _matrixACols.value, _matrixA.value)
        val matB = MatrixData(_matrixBRows.value, _matrixBCols.value, _matrixB.value)

        val result = when (op) {
            "A + B" -> MatrixUtils.add(matA, matB)
            "A - B" -> MatrixUtils.subtract(matA, matB)
            "A × B" -> MatrixUtils.multiply(matA, matB)
            "k × A" -> MatrixUtils.scalarMultiply(matA, _scalarK.value)
            "Det(A)" -> MatrixUtils.determinant(matA)
            "Inv(A)" -> MatrixUtils.inverse(matA)
            "Transpose(A)" -> MatrixUtils.transpose(matA)
            "Trace(A)" -> MatrixUtils.trace(matA)
            "Rank(A)" -> MatrixUtils.rank(matA)
            else -> MatrixResult.Error("Unknown Matrix Operation")
        }

        val text = when (result) {
            is MatrixResult.SuccessMatrix -> result.matrix.formatted()
            is MatrixResult.SuccessScalar -> MathEvaluator.formatNumber(result.scalar)
            is MatrixResult.Error -> "Error: ${result.message}"
        }

        _matrixResultText.value = text

        if (result !is MatrixResult.Error) {
            val exprDesc = "Matrix Op: $op"
            saveHistory("MATRIX", exprDesc, text, details = "${matA.rows}x${matA.cols} Matrix")
        }
    }

    // --- UNIT CONVERTER ACTIONS ---
    fun selectUnitCategory(category: UnitCategory) {
        _unitCategory.value = category
        val units = UnitConverter.getUnits(category)
        _unitFrom.value = units[0]
        _unitTo.value = if (units.size > 1) units[1] else units[0]
        calculateUnitConversion()
    }

    fun updateUnitInputValue(value: String) {
        _unitInputValue.value = value
        calculateUnitConversion()
    }

    fun selectFromUnit(unit: UnitItem) {
        _unitFrom.value = unit
        calculateUnitConversion()
    }

    fun selectToUnit(unit: UnitItem) {
        _unitTo.value = unit
        calculateUnitConversion()
    }

    fun swapUnits() {
        val temp = _unitFrom.value
        _unitFrom.value = _unitTo.value
        _unitTo.value = temp
        calculateUnitConversion()
    }

    private fun calculateUnitConversion() {
        val raw = _unitInputValue.value.toDoubleOrNull() ?: 0.0
        val res = UnitConverter.convert(raw, _unitCategory.value, _unitFrom.value, _unitTo.value)
        val formatted = MathEvaluator.formatNumber(res)
        _unitResultValue.value = formatted
    }

    fun saveUnitConversionHistory() {
        val valStr = _unitInputValue.value
        val from = _unitFrom.value
        val to = _unitTo.value
        val expr = "$valStr ${from.symbol} → ${to.symbol}"
        val res = "${_unitResultValue.value} ${to.symbol}"
        saveHistory("UNIT", expr, res, details = _unitCategory.value.displayName)
    }

    // --- CURRENCY CONVERTER ACTIONS ---
    fun updateCurrencyAmount(amount: String) {
        _currencyAmount.value = amount
        calculateCurrencyConversion()
    }

    fun selectFromCurrency(currency: CurrencyInfo) {
        _fromCurrency.value = currency
        prefs.edit().putString("calc_from_currency_code", currency.code).apply()
        calculateCurrencyConversion()
    }

    fun selectToCurrency(currency: CurrencyInfo) {
        _toCurrency.value = currency
        prefs.edit().putString("calc_to_currency_code", currency.code).apply()
        calculateCurrencyConversion()
    }

    fun swapCurrencies() {
        val temp = _fromCurrency.value
        _fromCurrency.value = _toCurrency.value
        _toCurrency.value = temp
        prefs.edit().putString("calc_from_currency_code", _fromCurrency.value.code).apply()
        prefs.edit().putString("calc_to_currency_code", _toCurrency.value.code).apply()
        calculateCurrencyConversion()
    }

    fun refreshCurrencyRates() {
        viewModelScope.launch {
            _exchangeState.value = _exchangeState.value.copy(isLoading = true)
            val updatedState = CurrencyRepository.fetchRealtimeRates(getApplication<Application>())
            _exchangeState.value = updatedState
            calculateCurrencyConversion()
        }
    }

    private fun calculateCurrencyConversion() {
        val amount = _currencyAmount.value.toDoubleOrNull() ?: 0.0
        val from = _fromCurrency.value.code
        val to = _toCurrency.value.code
        val rates = _exchangeState.value.rates

        val res = CurrencyRepository.convertCurrency(amount, from, to, rates)
        _convertedCurrency.value = MathEvaluator.formatNumber(res)
    }

    fun saveCurrencyHistory() {
        val amount = _currencyAmount.value
        val from = _fromCurrency.value
        val to = _toCurrency.value
        val expr = "$amount ${from.code} (${from.flag}) → ${to.code} (${to.flag})"
        val res = "${_convertedCurrency.value} ${to.code}"
        saveHistory("CURRENCY", expr, res, details = _exchangeState.value.lastUpdated)
    }

    // --- HISTORY ACTIONS & ROOM DATABASE ---
    private fun saveHistory(category: String, expression: String, result: String, details: String? = null) {
        if (_incognitoMode.value) return // Incognito mode enabled: Do not record history
        viewModelScope.launch {
            dao.insert(
                CalculationEntity(
                    category = category,
                    expression = expression,
                    result = result,
                    details = details
                )
            )
        }
    }

    // --- SETTINGS & PREFERENCES MANAGEMENT ---
    fun toggleIncognitoMode(enabled: Boolean) {
        _incognitoMode.value = enabled
        prefs.edit().putBoolean("incognito_mode", enabled).apply()
    }

    fun toggleBiometricLock(enabled: Boolean) {
        _biometricLockEnabled.value = enabled
        prefs.edit().putBoolean("biometric_lock_enabled", enabled).apply()
    }

    fun unlockAppDirectly() {
        failedPinAttempts = 0
        lockoutUntilMs = 0L
        prefs.edit().remove("app_pin_failed_attempts").remove("app_pin_lockout_until_ms").apply()
        _isAppLocked.value = false
    }

    fun setAppPin(pin: String) {
        if (pin.length != 4 || !pin.all { it.isDigit() }) return
        val salt = getOrCreateSecurePinSalt()
        val hash = hashPinPbkdf2(pin, salt)
        prefs.edit()
            .putString("app_pin_hash_pbkdf2", hash)
            .remove("app_pin")
            .remove("app_pin_hash")
            .remove("app_pin_salt")
            .remove("app_pin_failed_attempts")
            .remove("app_pin_lockout_until_ms")
            .apply()
        _hasAppPin.value = true
        _appPin.value = "SET"
        _isAppLocked.value = false
        failedPinAttempts = 0
        lockoutUntilMs = 0L
    }

    fun removeAppPin() {
        prefs.edit()
            .remove("app_pin")
            .remove("app_pin_hash")
            .remove("app_pin_salt")
            .remove("app_pin_hash_pbkdf2")
            .remove("app_pin_salt_pbkdf2")
            .remove("app_pin_failed_attempts")
            .remove("app_pin_lockout_until_ms")
            .apply()
        _hasAppPin.value = false
        _appPin.value = ""
        _isAppLocked.value = false
        failedPinAttempts = 0
        lockoutUntilMs = 0L
    }

    /**
     * Verifies if the provided PIN matches without changing lock state.
     */
    fun verifyPin(pin: String): Boolean {
        val now = System.currentTimeMillis()
        if (now < lockoutUntilMs) return false

        val pbkdf2Hash = prefs.getString("app_pin_hash_pbkdf2", null)
        val legacySha256Hash = prefs.getString("app_pin_hash", null)
        if (pbkdf2Hash == null && legacySha256Hash == null) return true

        var isMatch = false
        if (pbkdf2Hash != null) {
            val salt = getOrCreateSecurePinSalt()
            val computedHash = hashPinPbkdf2(pin, salt)
            isMatch = java.security.MessageDigest.isEqual(
                computedHash.toByteArray(Charsets.UTF_8),
                pbkdf2Hash.toByteArray(Charsets.UTF_8)
            )
        } else if (legacySha256Hash != null) {
            val salt = getOrCreateLegacyPinSalt()
            val computedHash = hashPinLegacySha256(pin, salt)
            isMatch = java.security.MessageDigest.isEqual(
                computedHash.toByteArray(Charsets.UTF_8),
                legacySha256Hash.toByteArray(Charsets.UTF_8)
            )
        }

        if (isMatch) {
            failedPinAttempts = 0
            lockoutUntilMs = 0L
            prefs.edit().remove("app_pin_failed_attempts").remove("app_pin_lockout_until_ms").apply()
        } else {
            failedPinAttempts++
            prefs.edit().putInt("app_pin_failed_attempts", failedPinAttempts).apply()
            if (failedPinAttempts >= 5) {
                lockoutUntilMs = System.currentTimeMillis() + 30_000L
                prefs.edit().putLong("app_pin_lockout_until_ms", lockoutUntilMs).apply()
            }
        }
        return isMatch
    }

    fun unlockApp(pin: String): Boolean {
        val now = System.currentTimeMillis()
        if (now < lockoutUntilMs) {
            return false
        }
        val pbkdf2Hash = prefs.getString("app_pin_hash_pbkdf2", null)
        val legacySha256Hash = prefs.getString("app_pin_hash", null)

        if (pbkdf2Hash == null && legacySha256Hash == null) {
            _isAppLocked.value = false
            return true
        }

        var isMatch = false
        if (pbkdf2Hash != null) {
            val salt = getOrCreateSecurePinSalt()
            val computedHash = hashPinPbkdf2(pin, salt)
            isMatch = java.security.MessageDigest.isEqual(
                computedHash.toByteArray(Charsets.UTF_8),
                pbkdf2Hash.toByteArray(Charsets.UTF_8)
            )
        } else if (legacySha256Hash != null) {
            val salt = getOrCreateLegacyPinSalt()
            val computedHash = hashPinLegacySha256(pin, salt)
            isMatch = java.security.MessageDigest.isEqual(
                computedHash.toByteArray(Charsets.UTF_8),
                legacySha256Hash.toByteArray(Charsets.UTF_8)
            )
            // Upgrade legacy hash seamlessly to PBKDF2
            if (isMatch) {
                val newSalt = getOrCreateSecurePinSalt()
                val newHash = hashPinPbkdf2(pin, newSalt)
                prefs.edit()
                    .putString("app_pin_hash_pbkdf2", newHash)
                    .remove("app_pin_hash")
                    .remove("app_pin_salt")
                    .apply()
            }
        }

        if (isMatch) {
            failedPinAttempts = 0
            lockoutUntilMs = 0L
            prefs.edit().remove("app_pin_failed_attempts").remove("app_pin_lockout_until_ms").apply()
            _isAppLocked.value = false
            return true
        } else {
            failedPinAttempts++
            prefs.edit().putInt("app_pin_failed_attempts", failedPinAttempts).apply()
            if (failedPinAttempts >= 5) {
                lockoutUntilMs = System.currentTimeMillis() + 30_000L
                prefs.edit().putLong("app_pin_lockout_until_ms", lockoutUntilMs).apply()
            }
            return false
        }
    }

    fun getPinLockoutRemainingSeconds(): Long {
        val remaining = (lockoutUntilMs - System.currentTimeMillis()) / 1000L
        return if (remaining > 0) remaining else 0L
    }

    fun lockApp() {
        if (_hasAppPin.value || _biometricLockEnabled.value) {
            _isAppLocked.value = true
        }
    }

    fun setDecimalPrecision(precision: Int) {
        _decimalPrecision.value = precision
        prefs.edit().putInt("decimal_precision", precision).apply()
        updatePreview()
    }

    fun setThemePreset(preset: String) {
        _themePreset.value = preset
        prefs.edit().putString("theme_preset", preset).apply()
    }

    fun setCompactView(compact: Boolean) {
        _compactView.value = compact
        prefs.edit().putBoolean("compact_view", compact).apply()
    }

    fun setUseDynamicColor(useDynamic: Boolean) {
        _useDynamicColor.value = useDynamic
        prefs.edit().putBoolean("dynamic_color", useDynamic).apply()
    }

    fun setBtnShape(shape: String) {
        _btnShape.value = shape
        prefs.edit().putString("btn_shape", shape).apply()
    }

    fun setDisplayFontSize(size: String) {
        _displayFontSize.value = size
        prefs.edit().putString("display_font_size", size).apply()
    }

    fun setDisplayAlign(align: String) {
        _displayAlign.value = align
        prefs.edit().putString("display_align", align).apply()
    }

    fun setDisplayHeightDp(heightDp: Int) {
        _displayHeightDp.value = heightDp
        prefs.edit().putInt("display_height_dp", heightDp).apply()
    }

    fun setDisplayWidthPaddingDp(paddingDp: Int) {
        _displayWidthPaddingDp.value = paddingDp
        prefs.edit().putInt("display_width_padding_dp", paddingDp).apply()
    }

    fun setDisplayCornerRadiusDp(radiusDp: Int) {
        _displayCornerRadiusDp.value = radiusDp
        prefs.edit().putInt("display_corner_radius_dp", radiusDp).apply()
    }

    fun setDisplayMainFontSizeSp(sizeSp: Int) {
        _displayMainFontSizeSp.value = sizeSp
        prefs.edit().putInt("display_main_font_size_sp", sizeSp).apply()
    }

    fun setDisplayPreviewFontSizeSp(sizeSp: Int) {
        _displayPreviewFontSizeSp.value = sizeSp
        prefs.edit().putInt("display_preview_font_size_sp", sizeSp).apply()
    }

    fun setKeypadHeightScale(scaleOrPercent: Float) {
        val percent = when {
            scaleOrPercent in 30f..70f -> scaleOrPercent
            scaleOrPercent in 0.3f..0.7f -> scaleOrPercent * 100f
            scaleOrPercent in 0.6f..1.6f -> 30f + (scaleOrPercent - 0.6f) * (40f / 1.0f)
            else -> 52f
        }.coerceIn(30f, 70f)
        _keypadHeightScale.value = percent
        prefs.edit().putFloat("keypad_height_scale", percent).apply()
    }

    fun setKeypadWidthPaddingDp(paddingDp: Int) {
        _keypadWidthPaddingDp.value = paddingDp
        prefs.edit().putInt("keypad_width_padding_dp", paddingDp).apply()
    }

    fun setKeypadGridSpacingDp(spacingDp: Int) {
        _keypadGridSpacingDp.value = spacingDp
        prefs.edit().putInt("keypad_grid_spacing_dp", spacingDp).apply()
    }

    fun setKeypadBtnCornerRadiusDp(radiusDp: Int) {
        _keypadBtnCornerRadiusDp.value = radiusDp
        prefs.edit().putInt("keypad_btn_corner_radius_dp", radiusDp).apply()
    }

    fun setKeypadBtnFontSizeSp(sizeSp: Int) {
        _keypadBtnFontSizeSp.value = sizeSp
        prefs.edit().putInt("keypad_btn_font_size_sp", sizeSp).apply()
    }

    fun setLockKeypadHeight(lock: Boolean) {
        _lockKeypadHeight.value = lock
        prefs.edit().putBoolean("lock_keypad_height", lock).apply()
    }

    fun saveCurrentAsUserDefault() {
        prefs.edit()
            .putInt("user_def_display_height_dp", _displayHeightDp.value)
            .putInt("user_def_display_width_padding_dp", _displayWidthPaddingDp.value)
            .putInt("user_def_display_corner_radius_dp", _displayCornerRadiusDp.value)
            .putInt("user_def_display_main_font_size_sp", _displayMainFontSizeSp.value)
            .putInt("user_def_display_preview_font_size_sp", _displayPreviewFontSizeSp.value)
            .putFloat("user_def_keypad_height_scale", _keypadHeightScale.value)
            .putInt("user_def_keypad_width_padding_dp", _keypadWidthPaddingDp.value)
            .putInt("user_def_keypad_grid_spacing_dp", _keypadGridSpacingDp.value)
            .putInt("user_def_keypad_btn_corner_radius_dp", _keypadBtnCornerRadiusDp.value)
            .putInt("user_def_keypad_btn_font_size_sp", _keypadBtnFontSizeSp.value)
            .putBoolean("user_def_lock_keypad_height", _lockKeypadHeight.value)
            .putBoolean("has_user_custom_defaults", true)
            .apply()
        _hasUserCustomDefaults.value = true
    }

    fun resetToUserCustomDefault() {
        if (prefs.getBoolean("has_user_custom_defaults", false)) {
            setDisplayHeightDp(prefs.getInt("user_def_display_height_dp", 135))
            setDisplayWidthPaddingDp(prefs.getInt("user_def_display_width_padding_dp", 12))
            setDisplayCornerRadiusDp(prefs.getInt("user_def_display_corner_radius_dp", 28))
            setDisplayMainFontSizeSp(prefs.getInt("user_def_display_main_font_size_sp", 34))
            setDisplayPreviewFontSizeSp(prefs.getInt("user_def_display_preview_font_size_sp", 24))
            setKeypadHeightScale(prefs.getFloat("user_def_keypad_height_scale", 1.0f))
            setKeypadWidthPaddingDp(prefs.getInt("user_def_keypad_width_padding_dp", 6))
            setKeypadGridSpacingDp(prefs.getInt("user_def_keypad_grid_spacing_dp", 6))
            setKeypadBtnCornerRadiusDp(prefs.getInt("user_def_keypad_btn_corner_radius_dp", 18))
            setKeypadBtnFontSizeSp(prefs.getInt("user_def_keypad_btn_font_size_sp", 20))
            setLockKeypadHeight(prefs.getBoolean("user_def_lock_keypad_height", true))
        } else {
            resetToFactoryDefault()
        }
    }

    fun resetToFactoryDefault() {
        setThemePreset("MATERIAL_YOU")
        setDecimalPrecision(-1)
        setCompactView(false)
        setBtnShape("ROUNDED")
        setDisplayFontSize("NORMAL")
        setDisplayAlign("RIGHT")
        setNumberAnimationType("OFF")
        setDisplayHeightDp(80)
        setDisplayWidthPaddingDp(12)
        setDisplayCornerRadiusDp(28)
        setDisplayMainFontSizeSp(34)
        setDisplayPreviewFontSizeSp(24)
        setKeypadHeightScale(40f)
        setKeypadWidthPaddingDp(6)
        setKeypadGridSpacingDp(6)
        setKeypadBtnCornerRadiusDp(18)
        setKeypadBtnFontSizeSp(20)
        setLockKeypadHeight(true)
        setShowLivePreview(true)
        setLivePreviewAnimEnabled(false)
        setLivePreviewAnimStyle("SLIDE")
        setHistoryGridlinesEnabled(true)
        setHistorySwipeLeftAction("DELETE")
        setHistorySwipeRightAction("INSERT")
        setHistoryDeletionLocked(false)
        setCalcHistoryGridlineStyle("DASHED")
        setCalcHistoryItemSpacingDp(4)
        setCalcHistoryMaxItemsCount(5)
        setCalcHistoryIsAdaptive(true)
        setAdaptiveDisplayResizing(true)
        setAdaptiveThemeEnabled(true)
        setThemeContrastMode("AUTO")
        setTopHistoryBannerVisible(true)
        setHapticFeedbackEnabled(false)
        setShowBottomBar(true)
        setNavBarStyle("LIQUID_GLASS")
        setNavBarBlurOpacity(1.0f)
    }

    fun resetLayoutCustomization() {
        resetToFactoryDefault()
    }

    fun setShowLivePreview(show: Boolean) {
        _showLivePreview.value = show
        prefs.edit().putBoolean("show_live_preview", show).apply()
    }

    fun setTopHistoryBannerVisible(visible: Boolean) {
        _topHistoryBannerVisible.value = visible
        prefs.edit().putBoolean("top_history_banner", visible).apply()
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        _hapticFeedbackEnabled.value = enabled
        prefs.edit().putBoolean("haptic_feedback", enabled).apply()
    }

    fun setShowBottomBar(enabled: Boolean) {
        _showBottomBar.value = enabled
        prefs.edit().putBoolean("show_bottom_bar", enabled).apply()
    }

    fun setPillNavStyle(enabled: Boolean) {
        _pillNavStyle.value = enabled
        prefs.edit().putBoolean("pill_nav_style", enabled).apply()
    }

    fun setNavBarStyle(style: String) {
        _navBarStyle.value = style
        prefs.edit().putString("nav_bar_style", style).apply()
    }

    fun setNavBarBlurOpacity(opacity: Float) {
        _navBarBlurOpacity.value = opacity
        prefs.edit().putFloat("nav_bar_blur_opacity", opacity).apply()
    }

    fun setNavAnimationSpeed(speed: String) {
        _navAnimationSpeed.value = speed
        prefs.edit().putString("nav_anim_speed", speed).apply()
    }

    fun setIconOnlyNav(enabled: Boolean) {
        _iconOnlyNav.value = enabled
        prefs.edit().putBoolean("icon_only_nav", enabled).apply()
    }

    fun setNavIndicatorSize(size: String) {
        _navIndicatorSize.value = size
        prefs.edit().putString("nav_indicator_size", size).apply()
    }

    fun setNavIndicatorScale(scale: Float) {
        _navIndicatorScale.value = scale
        prefs.edit().putFloat("nav_indicator_scale", scale).apply()
    }

    fun toggleTabEnabled(tabKey: String) {
        val current = _enabledTabs.value.toMutableSet()
        if (current.contains(tabKey)) {
            if (current.size > 1) {
                current.remove(tabKey)
            }
        } else {
            current.add(tabKey)
        }
        _enabledTabs.value = current
        prefs.edit().putStringSet("enabled_tabs", current).apply()
    }

    fun moveTabUp(index: Int) {
        if (index <= 0) return
        val current = _tabOrder.value.toMutableList()
        val item = current.removeAt(index)
        current.add(index - 1, item)
        saveTabOrder(current)
    }

    fun moveTabDown(index: Int) {
        if (index >= _tabOrder.value.size - 1) return
        val current = _tabOrder.value.toMutableList()
        val item = current.removeAt(index)
        current.add(index + 1, item)
        saveTabOrder(current)
    }

    fun resetTabOrder() {
        saveTabOrder(defaultTabList)
    }

    private fun saveTabOrder(order: List<String>) {
        _tabOrder.value = order
        prefs.edit().putString("tab_order", order.joinToString(",")).apply()
    }

    fun toggleFavorite(entry: CalculationEntity) {
        viewModelScope.launch {
            dao.updateFavorite(entry.id, !entry.isFavorite)
        }
    }

    fun updateEntryNote(entry: CalculationEntity, note: String) {
        viewModelScope.launch {
            dao.updateNote(entry.id, if (note.isBlank()) null else note)
        }
    }

    sealed interface HistoryOpResult {
        data object Success : HistoryOpResult
        data object DeletionLocked : HistoryOpResult
        data class Failure(val cause: Throwable) : HistoryOpResult
    }

    suspend fun deleteHistoryEntryAwait(entry: CalculationEntity): HistoryOpResult {
        if (_historyDeletionLocked.value) {
            return HistoryOpResult.DeletionLocked
        }
        return try {
            dao.setTrashStatus(entry.id, true)
            HistoryOpResult.Success
        } catch (e: Throwable) {
            HistoryOpResult.Failure(e)
        }
    }

    fun deleteHistoryEntry(entry: CalculationEntity): Boolean {
        if (_historyDeletionLocked.value) {
            return false
        }
        viewModelScope.launch {
            deleteHistoryEntryAwait(entry)
        }
        return true
    }

    suspend fun clearHistoryAwait(): HistoryOpResult {
        if (_historyDeletionLocked.value) {
            return HistoryOpResult.DeletionLocked
        }
        return try {
            val query = searchQuery.value
            val cat = filterCategory.value
            if (query.isNotBlank()) {
                val sanitizedQuery = escapeSqlWildcards(query.trim())
                when (cat) {
                    "ALL" -> dao.clearBySearch(sanitizedQuery)
                    "FAVORITES" -> dao.clearFavoritesBySearch(sanitizedQuery)
                    else -> dao.clearBySearchAndCategory(sanitizedQuery, cat)
                }
            } else if (cat == "FAVORITES") {
                dao.clearFavorites()
            } else if (cat == "ALL") {
                dao.clearAll()
            } else {
                dao.clearByCategory(cat)
            }
            HistoryOpResult.Success
        } catch (e: Throwable) {
            HistoryOpResult.Failure(e)
        }
    }

    fun clearHistory(): Boolean {
        if (_historyDeletionLocked.value) {
            return false
        }
        viewModelScope.launch {
            clearHistoryAwait()
        }
        return true
    }

    fun clearAllDatabaseHistory() {
        viewModelScope.launch {
            dao.permanentlyDeleteAll()
        }
    }

    fun setHistoryDeletionLocked(locked: Boolean) {
        _historyDeletionLocked.value = locked
        prefs.edit().putBoolean("history_deletion_locked", locked).apply()
    }

    fun restoreFromTrash(entry: CalculationEntity) {
        viewModelScope.launch {
            dao.setTrashStatus(entry.id, false)
        }
    }

    fun permanentlyDeleteFromTrash(entry: CalculationEntity) {
        viewModelScope.launch {
            dao.permanentlyDelete(entry.id)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            dao.emptyTrash()
        }
    }

    fun setFilterCategory(cat: String) {
        filterCategory.value = cat
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    // --- NEW FEATURE SETTERS & UTILITIES ---

    fun setHistorySwipeLeftAction(action: String) {
        _historySwipeLeftAction.value = action
        prefs.edit().putString("history_swipe_left_action", action).apply()
    }

    fun setHistorySwipeRightAction(action: String) {
        _historySwipeRightAction.value = action
        prefs.edit().putString("history_swipe_right_action", action).apply()
    }

    fun setCalcHistoryGridlineStyle(style: String) {
        _calcHistoryGridlineStyle.value = style
        prefs.edit().putString("calc_history_gridline_style", style).apply()
    }

    fun setCalcHistoryShowGridlines(show: Boolean) {
        _calcHistoryShowGridlines.value = show
        prefs.edit().putBoolean("calc_history_show_gridlines", show).apply()
    }

    fun setCalcHistoryAutoScrollTop(scrollTop: Boolean) {
        _calcHistoryAutoScrollTop.value = scrollTop
        prefs.edit().putBoolean("calc_history_auto_scroll_top", scrollTop).apply()
    }

    fun setCalcHistoryItemSpacingDp(spacingDp: Int) {
        _calcHistoryItemSpacingDp.value = spacingDp
        prefs.edit().putInt("calc_history_item_spacing_dp", spacingDp).apply()
    }

    fun setCalcHistoryMaxItemsCount(count: Int) {
        _calcHistoryMaxItemsCount.value = count
        prefs.edit().putInt("calc_history_max_items_count", count).apply()
    }

    fun setCalcHistoryIsAdaptive(isAdaptive: Boolean) {
        _calcHistoryIsAdaptive.value = isAdaptive
        prefs.edit().putBoolean("calc_history_is_adaptive", isAdaptive).apply()
    }

    fun setCalcCurrencyBarEnabled(enabled: Boolean) {
        _calcCurrencyBarEnabled.value = enabled
        prefs.edit().putBoolean("calc_currency_bar_enabled", enabled).apply()
    }

    fun setCalcCurrencyDecimals(decimals: Int) {
        _calcCurrencyDecimals.value = decimals
        prefs.edit().putInt("calc_currency_decimals", decimals).apply()
    }

    fun setCalcCurrencyHeaderShowToggle(show: Boolean) {
        _calcCurrencyHeaderShowToggle.value = show
        prefs.edit().putBoolean("calc_currency_header_show_toggle", show).apply()
    }

    fun setCalcCurrencyToggleActive(active: Boolean) {
        _calcCurrencyToggleActive.value = active
        prefs.edit().putBoolean("calc_currency_toggle_active", active).apply()
    }

    fun setCalcCurrencyToggleEnabledAction(action: String) {
        _calcCurrencyToggleEnabledAction.value = action
        prefs.edit().putString("calc_currency_toggle_enabled_action", action).apply()
    }

    fun setCalcCurrencyToggleDisabledAction(action: String) {
        _calcCurrencyToggleDisabledAction.value = action
        prefs.edit().putString("calc_currency_toggle_disabled_action", action).apply()
    }

    fun setCalcCurrencyToggleAlignment(alignment: String) {
        _calcCurrencyToggleAlignment.value = alignment
        prefs.edit().putString("calc_currency_toggle_alignment", alignment).apply()
    }

    fun setCalcCurrencyShowGuide(show: Boolean) {
        _calcCurrencyShowGuide.value = show
        prefs.edit().putBoolean("calc_currency_show_guide", show).apply()
    }

    fun setCalcCurrencyShowThemeStudio(show: Boolean) {
        _calcCurrencyShowThemeStudio.value = show
        prefs.edit().putBoolean("calc_currency_show_theme_studio", show).apply()
    }

    fun setCalcHistoryGridlineStrokeWidthDp(width: Float) {
        _calcHistoryGridlineStrokeWidthDp.value = width
        prefs.edit().putFloat("calc_history_gridline_stroke_width_dp", width).apply()
    }

    fun setCalcHistoryGridlineAlpha(alpha: Float) {
        val clampedAlpha = alpha.coerceIn(0.05f, 1.0f)
        _calcHistoryGridlineAlpha.value = clampedAlpha
        prefs.edit().putFloat("calc_history_gridline_alpha", clampedAlpha).apply()
    }

    fun setCalcHistoryShowItemDividers(show: Boolean) {
        _calcHistoryShowItemDividers.value = show
        prefs.edit().putBoolean("calc_history_show_item_dividers", show).apply()
    }

    fun setCalcHistoryExpanded(expanded: Boolean) {
        _calcHistoryExpanded.value = expanded
    }

    fun toggleCalcHistoryExpanded() {
        _calcHistoryExpanded.value = !_calcHistoryExpanded.value
    }

    fun setShowCalcHistoryBar(show: Boolean) {
        _topHistoryBannerVisible.value = show
        prefs.edit().putBoolean("top_history_banner", show).apply()
    }

    fun setUltraPerformanceMode(enabled: Boolean) {
        _ultraPerformanceMode.value = enabled
        prefs.edit().putBoolean("ultra_performance_mode", enabled).apply()
    }

    fun setLivePreviewAnimEnabled(enabled: Boolean) {
        _livePreviewAnimEnabled.value = enabled
        prefs.edit().putBoolean("live_preview_anim_enabled", enabled).apply()
    }

    fun setLivePreviewAnimStyle(style: String) {
        _livePreviewAnimStyle.value = style
        prefs.edit().putString("live_preview_anim_style", style).apply()
    }

    fun setHistoryGridlinesEnabled(enabled: Boolean) {
        _historyGridlinesEnabled.value = enabled
        prefs.edit().putBoolean("history_gridlines_enabled", enabled).apply()
    }

    fun setAdaptiveDisplayResizing(enabled: Boolean) {
        _adaptiveDisplayResizing.value = enabled
        prefs.edit().putBoolean("adaptive_display_resizing", enabled).apply()
    }

    fun setAdaptiveThemeEnabled(enabled: Boolean) {
        _adaptiveThemeEnabled.value = enabled
        prefs.edit().putBoolean("adaptive_theme_enabled", enabled).apply()
    }

    fun setThemeContrastMode(mode: String) {
        _themeContrastMode.value = mode
        prefs.edit().putString("theme_contrast_mode", mode).apply()
    }

    fun increaseDisplayHeight() {
        val newHeight = (_displayHeightDp.value + 15).coerceIn(80, 320)
        setDisplayHeightDp(newHeight)
    }

    fun decreaseDisplayHeight() {
        val newHeight = (_displayHeightDp.value - 15).coerceIn(80, 320)
        setDisplayHeightDp(newHeight)
    }

    // --- EXPORT & IMPORT SETTINGS JSON ---
    fun exportSettingsJson(): String {
        val json = org.json.JSONObject()
        json.put("schemaVersion", 1)
        json.put("appVersion", "1.0")

        json.put("theme_preset", themePreset.value)
        json.put("decimal_precision", decimalPrecision.value)
        json.put("compact_view", compactView.value)
        json.put("btn_shape", btnShape.value)
        json.put("display_font_size", displayFontSize.value)
        json.put("display_align", displayAlign.value)
        json.put("number_anim_type", numberAnimationType.value)
        json.put("display_height_dp", displayHeightDp.value)
        json.put("display_width_padding_dp", displayWidthPaddingDp.value)
        json.put("display_corner_radius_dp", displayCornerRadiusDp.value)
        json.put("display_main_font_size_sp", displayMainFontSizeSp.value)
        json.put("display_preview_font_size_sp", displayPreviewFontSizeSp.value)
        json.put("keypad_height_scale", keypadHeightScale.value.toDouble())
        json.put("keypad_width_padding_dp", keypadWidthPaddingDp.value)
        json.put("keypad_grid_spacing_dp", keypadGridSpacingDp.value)
        json.put("keypad_btn_corner_radius_dp", keypadBtnCornerRadiusDp.value)
        json.put("keypad_btn_font_size_sp", keypadBtnFontSizeSp.value)
        json.put("lock_keypad_height", lockKeypadHeight.value)
        json.put("show_live_preview", showLivePreview.value)
        json.put("live_preview_anim_enabled", livePreviewAnimEnabled.value)
        json.put("live_preview_anim_style", livePreviewAnimStyle.value)
        json.put("history_gridlines_enabled", historyGridlinesEnabled.value)
        json.put("history_swipe_left_action", historySwipeLeftAction.value)
        json.put("history_swipe_right_action", historySwipeRightAction.value)
        json.put("history_deletion_locked", historyDeletionLocked.value)
        json.put("calc_history_gridline_style", calcHistoryGridlineStyle.value)
        json.put("calc_history_item_spacing_dp", calcHistoryItemSpacingDp.value)
        json.put("calc_history_max_items_count", calcHistoryMaxItemsCount.value)
        json.put("calc_history_is_adaptive", calcHistoryIsAdaptive.value)
        json.put("adaptive_display_resizing", adaptiveDisplayResizing.value)
        json.put("adaptive_theme_enabled", adaptiveThemeEnabled.value)
        json.put("theme_contrast_mode", themeContrastMode.value)
        json.put("top_history_banner", topHistoryBannerVisible.value)
        json.put("ultra_performance_mode", ultraPerformanceMode.value)
        json.put("haptic_feedback", hapticFeedbackEnabled.value)
        json.put("show_bottom_bar", showBottomBar.value)
        json.put("nav_bar_style", navBarStyle.value)
        json.put("nav_bar_blur_opacity", navBarBlurOpacity.value.toDouble())

        // Additional portable settings
        json.put("tab_order", prefs.getString("tab_order", null) ?: defaultTabList.joinToString(","))
        val enabledArr = org.json.JSONArray()
        enabledTabs.value.forEach { enabledArr.put(it) }
        json.put("enabled_tabs", enabledArr)
        json.put("pill_nav_enabled", pillNavStyle.value)
        json.put("icon_only_nav", iconOnlyNav.value)
        json.put("nav_indicator_size", navIndicatorSize.value)
        json.put("nav_indicator_scale", navIndicatorScale.value.toDouble())
        json.put("nav_animation_speed", navAnimationSpeed.value)
        json.put("calc_from_currency_code", fromCurrency.value.code)
        json.put("calc_to_currency_code", toCurrency.value.code)
        json.put("degree_mode", isDegreeMode.value)
        json.put("number_format_style", numberFormatStyle.value)

        return json.toString(4)
    }

    fun importSettingsFromJson(jsonStr: String): Boolean {
        return try {
            val json = org.json.JSONObject(jsonStr)
            val schemaVer = json.optInt("schemaVersion", 1)
            if (schemaVer > 1) {
                // Incompatible future schema
                return false
            }

            val editor = prefs.edit()
            val pendingFlowUpdates = mutableListOf<() -> Unit>()

            val validThemePresets = setOf(
                "MATERIAL_YOU", "AMOLED", "MIDNIGHT_CYAN", "CYBERPUNK",
                "SOLAR_GOLD", "EMERALD_MINT", "ROSE_GOLD", "NEON_PURPLE",
                "DARK_SLATE", "HIGH_CONTRAST", "WARM_RETRO", "OCEAN_DEEP"
            )
            val validBtnShapes = setOf("ROUNDED_RECT", "CIRCLE", "SQUIRCLE", "PILL", "SUBTLE_ROUNDED")
            val validDisplayFontSizes = setOf("AUTO", "LARGE", "MEDIUM", "COMPACT")
            val validDisplayAligns = setOf("RIGHT", "CENTER", "LEFT")
            val validNumberAnimTypes = setOf("SLIDE", "FADE", "SCALE", "POP", "NONE")
            val validPreviewAnimStyles = setOf("SLIDE", "FADE", "SCALE", "POP")
            val validSwipeActions = setOf("COPY", "RELOAD", "DELETE", "FAVORITE", "NONE")
            val validGridlineStyles = setOf("SOLID", "DASHED", "DOT", "GLOW", "NONE")
            val validThemeContrastModes = setOf("AUTO", "NORMAL", "HIGH", "MAXIMUM")
            val validNavBarStyles = setOf("MATERIAL3", "LIQUID_GLASS", "FLOATING_PILL", "COMPACT_DOCK", "MINIMAL_BAR")
            val validNavIndicatorSizes = setOf("SMALL", "MEDIUM", "LARGE", "CIRCLE")
            val validNavAnimationSpeeds = setOf("SLOW", "NORMAL", "FAST", "INSTANT")
            val validNumberFormatStyles = setOf("STANDARD", "SCIENTIFIC", "EUROPEAN", "INDIAN", "PLAIN")

            if (json.has("theme_preset")) {
                val v = json.getString("theme_preset")
                if (validThemePresets.contains(v)) {
                    editor.putString("theme_preset", v)
                    pendingFlowUpdates.add { _themePreset.value = v }
                }
            }
            if (json.has("decimal_precision")) {
                val v = json.getInt("decimal_precision").coerceIn(-1, 15)
                editor.putInt("decimal_precision", v)
                pendingFlowUpdates.add { _decimalPrecision.value = v }
            }
            if (json.has("compact_view")) {
                val v = json.getBoolean("compact_view")
                editor.putBoolean("compact_view", v)
                pendingFlowUpdates.add { _compactView.value = v }
            }
            if (json.has("btn_shape")) {
                val v = json.getString("btn_shape")
                if (validBtnShapes.contains(v)) {
                    editor.putString("btn_shape", v)
                    pendingFlowUpdates.add { _btnShape.value = v }
                }
            }
            if (json.has("display_font_size")) {
                val v = json.getString("display_font_size")
                if (validDisplayFontSizes.contains(v)) {
                    editor.putString("display_font_size", v)
                    pendingFlowUpdates.add { _displayFontSize.value = v }
                }
            }
            if (json.has("display_align")) {
                val v = json.getString("display_align")
                if (validDisplayAligns.contains(v)) {
                    editor.putString("display_align", v)
                    pendingFlowUpdates.add { _displayAlign.value = v }
                }
            }
            if (json.has("number_anim_type")) {
                val v = json.getString("number_anim_type")
                if (validNumberAnimTypes.contains(v)) {
                    editor.putString("number_anim_type", v)
                    pendingFlowUpdates.add { _numberAnimationType.value = v }
                }
            }
            if (json.has("display_height_dp")) {
                val v = json.getInt("display_height_dp").coerceIn(100, 400)
                editor.putInt("display_height_dp", v)
                pendingFlowUpdates.add { _displayHeightDp.value = v }
            }
            if (json.has("display_width_padding_dp")) {
                val v = json.getInt("display_width_padding_dp").coerceIn(0, 48)
                editor.putInt("display_width_padding_dp", v)
                pendingFlowUpdates.add { _displayWidthPaddingDp.value = v }
            }
            if (json.has("display_corner_radius_dp")) {
                val v = json.getInt("display_corner_radius_dp").coerceIn(0, 64)
                editor.putInt("display_corner_radius_dp", v)
                pendingFlowUpdates.add { _displayCornerRadiusDp.value = v }
            }
            if (json.has("display_main_font_size_sp")) {
                val v = json.getInt("display_main_font_size_sp").coerceIn(16, 72)
                editor.putInt("display_main_font_size_sp", v)
                pendingFlowUpdates.add { _displayMainFontSizeSp.value = v }
            }
            if (json.has("display_preview_font_size_sp")) {
                val v = json.getInt("display_preview_font_size_sp").coerceIn(12, 40)
                editor.putInt("display_preview_font_size_sp", v)
                pendingFlowUpdates.add { _displayPreviewFontSizeSp.value = v }
            }
            if (json.has("keypad_height_scale")) {
                val v = json.getDouble("keypad_height_scale").toFloat().coerceIn(0.5f, 2.0f)
                editor.putFloat("keypad_height_scale", v)
                pendingFlowUpdates.add { _keypadHeightScale.value = v }
            }
            if (json.has("keypad_width_padding_dp")) {
                val v = json.getInt("keypad_width_padding_dp").coerceIn(0, 48)
                editor.putInt("keypad_width_padding_dp", v)
                pendingFlowUpdates.add { _keypadWidthPaddingDp.value = v }
            }
            if (json.has("keypad_grid_spacing_dp")) {
                val v = json.getInt("keypad_grid_spacing_dp").coerceIn(0, 32)
                editor.putInt("keypad_grid_spacing_dp", v)
                pendingFlowUpdates.add { _keypadGridSpacingDp.value = v }
            }
            if (json.has("keypad_btn_corner_radius_dp")) {
                val v = json.getInt("keypad_btn_corner_radius_dp").coerceIn(0, 64)
                editor.putInt("keypad_btn_corner_radius_dp", v)
                pendingFlowUpdates.add { _keypadBtnCornerRadiusDp.value = v }
            }
            if (json.has("keypad_btn_font_size_sp")) {
                val v = json.getInt("keypad_btn_font_size_sp").coerceIn(12, 40)
                editor.putInt("keypad_btn_font_size_sp", v)
                pendingFlowUpdates.add { _keypadBtnFontSizeSp.value = v }
            }
            if (json.has("lock_keypad_height")) {
                val v = json.getBoolean("lock_keypad_height")
                editor.putBoolean("lock_keypad_height", v)
                pendingFlowUpdates.add { _lockKeypadHeight.value = v }
            }
            if (json.has("show_live_preview")) {
                val v = json.getBoolean("show_live_preview")
                editor.putBoolean("show_live_preview", v)
                pendingFlowUpdates.add { _showLivePreview.value = v }
            }
            if (json.has("live_preview_anim_enabled")) {
                val v = json.getBoolean("live_preview_anim_enabled")
                editor.putBoolean("live_preview_anim_enabled", v)
                pendingFlowUpdates.add { _livePreviewAnimEnabled.value = v }
            }
            if (json.has("live_preview_anim_style")) {
                val v = json.getString("live_preview_anim_style")
                if (validPreviewAnimStyles.contains(v)) {
                    editor.putString("live_preview_anim_style", v)
                    pendingFlowUpdates.add { _livePreviewAnimStyle.value = v }
                }
            }
            if (json.has("history_gridlines_enabled")) {
                val v = json.getBoolean("history_gridlines_enabled")
                editor.putBoolean("history_gridlines_enabled", v)
                pendingFlowUpdates.add { _historyGridlinesEnabled.value = v }
            }
            if (json.has("history_swipe_left_action")) {
                val v = json.getString("history_swipe_left_action")
                if (validSwipeActions.contains(v)) {
                    editor.putString("history_swipe_left_action", v)
                    pendingFlowUpdates.add { _historySwipeLeftAction.value = v }
                }
            }
            if (json.has("history_swipe_right_action")) {
                val v = json.getString("history_swipe_right_action")
                if (validSwipeActions.contains(v)) {
                    editor.putString("history_swipe_right_action", v)
                    pendingFlowUpdates.add { _historySwipeRightAction.value = v }
                }
            }
            if (json.has("history_deletion_locked")) {
                val v = json.getBoolean("history_deletion_locked")
                editor.putBoolean("history_deletion_locked", v)
                pendingFlowUpdates.add { _historyDeletionLocked.value = v }
            }
            if (json.has("calc_history_gridline_style")) {
                val v = json.getString("calc_history_gridline_style")
                if (validGridlineStyles.contains(v)) {
                    editor.putString("calc_history_gridline_style", v)
                    pendingFlowUpdates.add { _calcHistoryGridlineStyle.value = v }
                }
            }
            if (json.has("calc_history_item_spacing_dp")) {
                val v = json.getInt("calc_history_item_spacing_dp").coerceIn(2, 32)
                editor.putInt("calc_history_item_spacing_dp", v)
                pendingFlowUpdates.add { _calcHistoryItemSpacingDp.value = v }
            }
            if (json.has("calc_history_max_items_count")) {
                val v = json.getInt("calc_history_max_items_count").coerceIn(10, 1000)
                editor.putInt("calc_history_max_items_count", v)
                pendingFlowUpdates.add { _calcHistoryMaxItemsCount.value = v }
            }
            if (json.has("calc_history_is_adaptive")) {
                val v = json.getBoolean("calc_history_is_adaptive")
                editor.putBoolean("calc_history_is_adaptive", v)
                pendingFlowUpdates.add { _calcHistoryIsAdaptive.value = v }
            }
            if (json.has("adaptive_display_resizing")) {
                val v = json.getBoolean("adaptive_display_resizing")
                editor.putBoolean("adaptive_display_resizing", v)
                pendingFlowUpdates.add { _adaptiveDisplayResizing.value = v }
            }
            if (json.has("adaptive_theme_enabled")) {
                val v = json.getBoolean("adaptive_theme_enabled")
                editor.putBoolean("adaptive_theme_enabled", v)
                pendingFlowUpdates.add { _adaptiveThemeEnabled.value = v }
            }
            if (json.has("theme_contrast_mode")) {
                val v = json.getString("theme_contrast_mode")
                if (validThemeContrastModes.contains(v)) {
                    editor.putString("theme_contrast_mode", v)
                    pendingFlowUpdates.add { _themeContrastMode.value = v }
                }
            }
            if (json.has("top_history_banner")) {
                val v = json.getBoolean("top_history_banner")
                editor.putBoolean("top_history_banner", v)
                pendingFlowUpdates.add { _topHistoryBannerVisible.value = v }
            }
            if (json.has("haptic_feedback")) {
                val v = json.getBoolean("haptic_feedback")
                editor.putBoolean("haptic_feedback", v)
                pendingFlowUpdates.add { _hapticFeedbackEnabled.value = v }
            }
            if (json.has("show_bottom_bar")) {
                val v = json.getBoolean("show_bottom_bar")
                editor.putBoolean("show_bottom_bar", v)
                pendingFlowUpdates.add { _showBottomBar.value = v }
            }
            if (json.has("nav_bar_style")) {
                val v = json.getString("nav_bar_style")
                if (validNavBarStyles.contains(v)) {
                    editor.putString("nav_bar_style", v)
                    pendingFlowUpdates.add { _navBarStyle.value = v }
                }
            }
            if (json.has("nav_bar_blur_opacity")) {
                val v = json.getDouble("nav_bar_blur_opacity").toFloat().coerceIn(0.0f, 1.0f)
                editor.putFloat("nav_bar_blur_opacity", v)
                pendingFlowUpdates.add { _navBarBlurOpacity.value = v }
            }
            if (json.has("ultra_performance_mode")) {
                val v = json.getBoolean("ultra_performance_mode")
                editor.putBoolean("ultra_performance_mode", v)
                pendingFlowUpdates.add { _ultraPerformanceMode.value = v }
            }

            // Additional portable settings
            if (json.has("pill_nav_enabled")) {
                val v = json.getBoolean("pill_nav_enabled")
                editor.putBoolean("pill_nav_enabled", v)
                pendingFlowUpdates.add { _pillNavStyle.value = v }
            }
            if (json.has("icon_only_nav")) {
                val v = json.getBoolean("icon_only_nav")
                editor.putBoolean("icon_only_nav", v)
                pendingFlowUpdates.add { _iconOnlyNav.value = v }
            }
            if (json.has("nav_indicator_size")) {
                val v = json.getString("nav_indicator_size")
                if (validNavIndicatorSizes.contains(v)) {
                    editor.putString("nav_indicator_size", v)
                    pendingFlowUpdates.add { _navIndicatorSize.value = v }
                }
            }
            if (json.has("nav_indicator_scale")) {
                val v = json.getDouble("nav_indicator_scale").toFloat().coerceIn(0.5f, 2.0f)
                editor.putFloat("nav_indicator_scale", v)
                pendingFlowUpdates.add { _navIndicatorScale.value = v }
            }
            if (json.has("nav_animation_speed")) {
                val v = json.getString("nav_animation_speed")
                if (validNavAnimationSpeeds.contains(v)) {
                    editor.putString("nav_animation_speed", v)
                    pendingFlowUpdates.add { _navAnimationSpeed.value = v }
                }
            }
            if (json.has("number_format_style")) {
                val v = json.getString("number_format_style")
                if (validNumberFormatStyles.contains(v)) {
                    editor.putString("number_format_style", v)
                    pendingFlowUpdates.add { _numberFormatStyle.value = v }
                }
            }
            if (json.has("calc_from_currency_code")) {
                val code = json.getString("calc_from_currency_code").trim().uppercase(java.util.Locale.US)
                val curr = defaultCurrencies.find { it.code == code }
                if (curr != null) {
                    editor.putString("calc_from_currency_code", code)
                    pendingFlowUpdates.add { _fromCurrency.value = curr }
                }
            }
            if (json.has("calc_to_currency_code")) {
                val code = json.getString("calc_to_currency_code").trim().uppercase(java.util.Locale.US)
                val curr = defaultCurrencies.find { it.code == code }
                if (curr != null) {
                    editor.putString("calc_to_currency_code", code)
                    pendingFlowUpdates.add { _toCurrency.value = curr }
                }
            }
            if (json.has("degree_mode")) {
                val v = json.getBoolean("degree_mode")
                editor.putBoolean("degree_mode", v)
                pendingFlowUpdates.add { _isDegreeMode.value = v }
            }
            if (json.has("tab_order")) {
                val orderStr = json.getString("tab_order")
                val validSet = defaultTabList.toSet()
                val parsed = orderStr.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() && validSet.contains(it) }
                    .distinct()
                    .toMutableList()
                defaultTabList.forEach { if (!parsed.contains(it)) parsed.add(it) }
                val finalOrderStr = parsed.joinToString(",")
                editor.putString("tab_order", finalOrderStr)
                pendingFlowUpdates.add { _tabOrder.value = parsed }
            }
            if (json.has("enabled_tabs")) {
                val arr = json.getJSONArray("enabled_tabs")
                val validSet = defaultTabList.toSet()
                val set = mutableSetOf<String>()
                for (i in 0 until arr.length()) {
                    val tab = arr.getString(i).trim()
                    if (validSet.contains(tab)) {
                        set.add(tab)
                    }
                }
                if (set.isEmpty()) set.addAll(defaultTabList)
                set.add("CALCULATOR")
                editor.putStringSet("enabled_tabs", set)
                pendingFlowUpdates.add { _enabledTabs.value = set }
            }

            val committed = editor.commit()
            if (committed) {
                pendingFlowUpdates.forEach { it() }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun resetAllSettings() {
        prefs.edit().clear().apply()
        resetToFactoryDefault()
        setLivePreviewAnimEnabled(false)
        setLivePreviewAnimStyle("SLIDE")
        setHistoryGridlinesEnabled(true)
        setAdaptiveDisplayResizing(true)
        setAdaptiveThemeEnabled(true)
        setThemeContrastMode("AUTO")
    }
}
