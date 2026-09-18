package com.zakeercareer.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.size
import com.zakeercareer.calculator.R
import com.zakeercareer.calculator.ui.components.LiquidGlassBottomBar
import com.zakeercareer.calculator.ui.components.NavigationTabItem
import com.zakeercareer.calculator.ui.theme.CalculatorTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.ui.draw.clip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zakeercareer.calculator.ui.screens.CurrencyConverterScreen
import com.zakeercareer.calculator.ui.screens.HistoryScreen
import com.zakeercareer.calculator.ui.screens.MatrixScreen
import com.zakeercareer.calculator.ui.screens.ScientificCalculatorScreen
import com.zakeercareer.calculator.ui.screens.SettingsScreen
import com.zakeercareer.calculator.ui.screens.StandardCalculatorScreen
import com.zakeercareer.calculator.ui.screens.UnitConverterScreen
import com.zakeercareer.calculator.ui.theme.AdvancedCalculatorTheme
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel

import com.zakeercareer.calculator.ui.components.CalculationGuideSheet
import com.zakeercareer.calculator.ui.components.ThemeSelectionBottomSheet

import android.view.WindowManager
import androidx.compose.runtime.LaunchedEffect

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    private val calculatorViewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = calculatorViewModel

            val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
            val incognitoMode by viewModel.incognitoMode.collectAsStateWithLifecycle()

            // Requirement 3: Dynamically manage FLAG_SECURE to prevent OS Recents Preview data leakage
            LaunchedEffect(isAppLocked, incognitoMode) {
                if (isAppLocked || incognitoMode) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            val themePreset by viewModel.themePreset.collectAsStateWithLifecycle()
            val useDynamicColor by viewModel.useDynamicColor.collectAsStateWithLifecycle()
            val adaptiveThemeEnabled by viewModel.adaptiveThemeEnabled.collectAsStateWithLifecycle()

            AdvancedCalculatorTheme(
                themePreset = themePreset,
                dynamicColor = useDynamicColor,
                adaptiveThemeEnabled = adaptiveThemeEnabled
            ) {
                MainCalculatorApp(viewModel = viewModel)
            }
        }
    }

    fun canAuthenticateBiometrics(): Pair<Boolean, String?> {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true to null
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false to "No biometric sensor available on this device"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false to "Biometric sensor is currently unavailable"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false to "No biometric credentials enrolled. Please use your PIN."
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> false to "Security update required for biometric authentication"
            else -> false to "Biometric authentication is not supported"
        }
    }

    fun triggerBiometricAuthentication(onSuccess: () -> Unit, onFallbackToPin: (() -> Unit)? = null) {
        val (canAuth, errorReason) = canAuthenticateBiometrics()
        if (!canAuth) {
            if (errorReason != null) {
                Toast.makeText(this, errorReason, Toast.LENGTH_SHORT).show()
            }
            onFallbackToPin?.invoke()
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // If user cancels or clicks negative button to use PIN, fall back cleanly without toast
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_CANCELED
                    ) {
                        onFallbackToPin?.invoke()
                    } else {
                        Toast.makeText(this@MainActivity, "$errString", Toast.LENGTH_SHORT).show()
                        onFallbackToPin?.invoke()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@MainActivity, "Biometric auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Calculator")
            .setSubtitle("Authenticate using fingerprint or face ID")
            .setNegativeButtonText("Use PIN Passcode")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            calculatorViewModel.lockApp()
        }
    }
}

sealed class NavItem(val key: String, val title: String, val icon: ImageVector, val tag: String) {
    object Calculator : NavItem("CALCULATOR", "Standard", Icons.Default.Calculate, "nav_calc")
    object Scientific : NavItem("SCIENTIFIC", "Scientific", Icons.Default.Functions, "nav_scientific")
    object Currency : NavItem("CURRENCY", "Currency", Icons.Default.CurrencyExchange, "nav_currency")
    object Unit : NavItem("UNIT", "Unit", Icons.Default.Straighten, "nav_unit")
    object Matrix : NavItem("MATRIX", "Matrix", Icons.Default.GridOn, "nav_matrix")
    object History : NavItem("HISTORY", "History", Icons.Default.History, "nav_history")
    object Settings : NavItem("SETTINGS", "Settings", Icons.Default.Settings, "nav_settings")

    companion object {
        val allItems = listOf(Calculator, Scientific, Currency, Unit, Matrix, History, Settings)
        fun fromKey(key: String): NavItem = allItems.find { it.key == key } ?: Calculator
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCalculatorApp(viewModel: CalculatorViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showCalcGuideSheet by remember { mutableStateOf(false) }
    var showThemeStudioSheet by remember { mutableStateOf(false) }
    var showTopScreenMenu by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    // PIN lock input state
    var pinVerificationInput by remember { mutableStateOf("") }
    var pinErrorText by remember { mutableStateOf<String?>(null) }

    // Collect Top-Level Layout States
    val context = LocalContext.current
    val activity = context as? MainActivity
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
    val biometricLockEnabled by viewModel.biometricLockEnabled.collectAsStateWithLifecycle()
    val tabOrder by viewModel.tabOrder.collectAsStateWithLifecycle()
    val incognitoMode by viewModel.incognitoMode.collectAsStateWithLifecycle()

    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isAppLocked, biometricLockEnabled) {
        if (isAppLocked && biometricLockEnabled && activity != null) {
            val (canAuth, _) = activity.canAuthenticateBiometrics()
            if (canAuth) {
                activity.triggerBiometricAuthentication(
                    onSuccess = { viewModel.unlockAppDirectly() }
                )
            }
        }
    }

    val showBottomBar by viewModel.showBottomBar.collectAsStateWithLifecycle()
    val pillNavStyle by viewModel.pillNavStyle.collectAsStateWithLifecycle()
    val navBarStyle by viewModel.navBarStyle.collectAsStateWithLifecycle()
    val navBarBlurOpacity by viewModel.navBarBlurOpacity.collectAsStateWithLifecycle()
    val navAnimationSpeed by viewModel.navAnimationSpeed.collectAsStateWithLifecycle()
    val iconOnlyNav by viewModel.iconOnlyNav.collectAsStateWithLifecycle()
    val navIndicatorSize by viewModel.navIndicatorSize.collectAsStateWithLifecycle()
    val navIndicatorScale by viewModel.navIndicatorScale.collectAsStateWithLifecycle()
    val enabledTabs by viewModel.enabledTabs.collectAsStateWithLifecycle()

    val (baseWidth, baseHeight, baseCorner) = when (navIndicatorSize) {
        "SMALL" -> Triple(36, 24, 12)
        "LARGE" -> Triple(60, 32, 16)
        "CIRCLE" -> Triple(28, 28, 14)
        else -> Triple(48, 28, 14)
    }

    val indicatorWidth = (baseWidth * navIndicatorScale).dp
    val indicatorHeight = (baseHeight * navIndicatorScale).dp
    val indicatorShape = if (navIndicatorSize == "CIRCLE") CircleShape else RoundedCornerShape((baseCorner * navIndicatorScale).dp)

    // Dynamically ordered nav items based on user preferences in Settings
    val currentNavItems = remember(tabOrder, enabledTabs) {
        tabOrder.filter { enabledTabs.contains(it) }.map { NavItem.fromKey(it) }
    }

    if (selectedTab >= currentNavItems.size) {
        selectedTab = 0
    }

    val currentScreen = currentNavItems.getOrNull(selectedTab) ?: NavItem.Calculator

    // System Back Button Navigation Handling
    BackHandler {
        if (selectedTab != 0) {
            val calcIdx = currentNavItems.indexOf(NavItem.Calculator)
            selectedTab = if (calcIdx >= 0) calcIdx else 0
        } else {
            val currentExpr = viewModel.expression.value
            if (currentExpr.isNotEmpty() && currentExpr != "0") {
                viewModel.onClearAll()
            } else {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackPressTime < 2000) {
                    activity?.finish()
                } else {
                    lastBackPressTime = currentTime
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // App Lock PIN Overlay Screen
    if (isAppLocked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "App Locked",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Calculator Locked",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Enter your 4-digit Passcode PIN to continue.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                OutlinedTextField(
                    value = pinVerificationInput,
                    onValueChange = {
                        if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                            pinVerificationInput = it
                        }
                    },
                    label = { Text("Enter PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = pinErrorText != null,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                pinErrorText?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
                ElevatedButton(
                    onClick = {
                        val remainingSec = viewModel.getPinLockoutRemainingSeconds()
                        if (remainingSec > 0) {
                            pinErrorText = "Too many failed attempts. Try again in $remainingSec seconds."
                            return@ElevatedButton
                        }
                        val success = viewModel.unlockApp(pinVerificationInput)
                        if (!success) {
                            val newRemainingSec = viewModel.getPinLockoutRemainingSeconds()
                            if (newRemainingSec > 0) {
                                pinErrorText = "Too many attempts. Locked for $newRemainingSec seconds."
                            } else {
                                pinErrorText = "Incorrect PIN. Try again."
                            }
                        } else {
                            pinErrorText = null
                            pinVerificationInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Unlock App", fontWeight = FontWeight.Bold)
                }

                if (activity != null && activity.canAuthenticateBiometrics().first) {
                    OutlinedButton(
                        onClick = {
                            activity.triggerBiometricAuthentication(
                                onSuccess = { viewModel.unlockAppDirectly() }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use Fingerprint / Face Unlock", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        return
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calculator_vector_logo),
                        contentDescription = "Calculator Luxury Emblem",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(64.dp)
                    )
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Calculator",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Engineered Precision • World-Class UI/UX",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Architect & Developer",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "zakeercareer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        "Installation & Sideload Guide:\n\n" +
                        "1. Tap 'Install' or 'Download APK' at the top-right in AI Studio.\n" +
                        "2. Open your Android phone's Downloads directory.\n" +
                        "3. Tap the downloaded .apk file.\n" +
                        "4. Allow 'Install from unknown sources' if prompted.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showTopScreenMenu = true }
                                .padding(vertical = 4.dp, horizontal = 6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calculator_vector_logo),
                                contentDescription = "Vector Logo",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = when (currentScreen) {
                                        NavItem.Calculator -> "Standard Calculator"
                                        NavItem.Scientific -> "Scientific Calculator"
                                        NavItem.Currency -> "Realtime Currency (150+)"
                                        NavItem.Unit -> "Unit Converter"
                                        NavItem.Matrix -> "Matrix Operations"
                                        NavItem.History -> "Calculation History"
                                        NavItem.Settings -> "App Settings & Privacy"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (incognitoMode) {
                                    Text(
                                        "🕵️ Incognito Active",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Select Screen",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = showTopScreenMenu,
                            onDismissRequest = { showTopScreenMenu = false }
                        ) {
                            currentNavItems.forEachIndexed { index, navItem ->
                                DropdownMenuItem(
                                    text = { Text(navItem.title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                                    leadingIcon = { Icon(navItem.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        selectedTab = index
                                        showTopScreenMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showThemeStudioSheet = true },
                        modifier = Modifier.testTag("top_theme_studio_btn")
                    ) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = "Expressive Theme Studio",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            modifier = Modifier.testTag("top_more_options_btn")
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Calculation Guide") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    showOverflowMenu = false
                                    showCalcGuideSheet = true
                                },
                                modifier = Modifier.testTag("top_guide_btn")
                            )
                            DropdownMenuItem(
                                text = { Text("App Settings") },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    showOverflowMenu = false
                                    val settingsIdx = currentNavItems.indexOf(NavItem.Settings)
                                    if (settingsIdx >= 0) selectedTab = settingsIdx
                                },
                                modifier = Modifier.testTag("top_settings_btn")
                            )
                            DropdownMenuItem(
                                text = { Text("Help & About") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    showOverflowMenu = false
                                    showHelpDialog = true
                                },
                                modifier = Modifier.testTag("help_dialog_btn")
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.82f)
                )
            )
        },
        containerColor = Color.Transparent,
    bottomBar = {
        if (showBottomBar) {
            val effectiveStyle = if (pillNavStyle && navBarStyle != "PILL" && navBarStyle != "LIQUID_GLASS" && navBarStyle != "MINIMAL_BUBBLE" && navBarStyle != "M3DOCK" && navBarStyle != "M3_EXPRESSIVE_DOCK") "PILL" else navBarStyle
            LiquidGlassBottomBar(
                items = currentNavItems.map { NavigationTabItem(title = it.title, icon = it.icon, tag = it.tag) },
                selectedIndex = selectedTab.coerceIn(0, currentNavItems.size - 1),
                onTabSelected = { selectedTab = it },
                iconOnly = iconOnlyNav,
                indicatorSize = navIndicatorSize,
                indicatorScale = navIndicatorScale,
                navBarStyle = effectiveStyle,
                blurOpacity = navBarBlurOpacity,
                animSpeed = navAnimationSpeed
            )
        }
    }
) { innerPadding ->
    val activeTheme = CalculatorTheme.current
    val bgModifier = if (activeTheme.backgroundBrush != null) {
        Modifier.background(activeTheme.backgroundBrush!!)
    } else {
        Modifier.background(MaterialTheme.colorScheme.background)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(bgModifier)
            .padding(innerPadding)
    ) {
            when (currentScreen) {
                NavItem.Calculator -> StandardCalculatorScreen(
                    viewModel = viewModel
                )
                NavItem.Scientific -> ScientificCalculatorScreen(
                    viewModel = viewModel
                )
                NavItem.Currency -> {
                    val currencyAmount by viewModel.currencyAmount.collectAsStateWithLifecycle()
                    val fromCurrency by viewModel.fromCurrency.collectAsStateWithLifecycle()
                    val toCurrency by viewModel.toCurrency.collectAsStateWithLifecycle()
                    val exchangeState by viewModel.exchangeState.collectAsStateWithLifecycle()
                    val convertedCurrency by viewModel.convertedCurrency.collectAsStateWithLifecycle()
                    CurrencyConverterScreen(
                        viewModel = viewModel,
                        amount = currencyAmount,
                        fromCurrency = fromCurrency,
                        toCurrency = toCurrency,
                        exchangeState = exchangeState,
                        convertedValue = convertedCurrency
                    )
                }
                NavItem.Unit -> {
                    val unitCategory by viewModel.unitCategory.collectAsStateWithLifecycle()
                    val unitInputValue by viewModel.unitInputValue.collectAsStateWithLifecycle()
                    val unitFrom by viewModel.unitFrom.collectAsStateWithLifecycle()
                    val unitTo by viewModel.unitTo.collectAsStateWithLifecycle()
                    val unitResultValue by viewModel.unitResultValue.collectAsStateWithLifecycle()
                    UnitConverterScreen(
                        viewModel = viewModel,
                        selectedCategory = unitCategory,
                        inputValue = unitInputValue,
                        fromUnit = unitFrom,
                        toUnit = unitTo,
                        resultValue = unitResultValue
                    )
                }
                NavItem.Matrix -> MatrixScreen(viewModel = viewModel)
                NavItem.History -> {
                    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
                    val filterCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
                    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                    HistoryScreen(
                        viewModel = viewModel,
                        historyList = historyList,
                        selectedCategoryFilter = filterCategory,
                        searchQuery = searchQuery,
                        onInsertToCalc = { result ->
                            viewModel.onAppendInput(result)
                            val calcIdx = currentNavItems.indexOf(NavItem.Calculator)
                            if (calcIdx >= 0) selectedTab = calcIdx
                        }
                    )
                }
                NavItem.Settings -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    if (showThemeStudioSheet) {
        val themePreset by viewModel.themePreset.collectAsStateWithLifecycle()
        ThemeSelectionBottomSheet(
            selectedThemeId = themePreset,
            onSelectTheme = { themeId ->
                viewModel.setThemePreset(themeId)
            },
            onDismissRequest = { showThemeStudioSheet = false }
        )
    }

    if (showCalcGuideSheet) {
        CalculationGuideSheet(
            onDismiss = { showCalcGuideSheet = false },
            onTryExample = { expr ->
                viewModel.onClearAll()
                viewModel.onAppendInput(expr)
                val calcIdx = currentNavItems.indexOf(NavItem.Calculator)
                if (calcIdx >= 0) selectedTab = calcIdx
            }
        )
    }
}
