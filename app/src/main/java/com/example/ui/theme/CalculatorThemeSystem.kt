package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Central Theme State model for Calculator UI handling all 10 visual styles.
 */
data class CalculatorThemeState(
    val id: String,
    val name: String,
    val subtitle: String,
    val tag: String,
    val colorScheme: ColorScheme,
    val buttonShapeStyle: String = "ROUNDED", // ROUNDED, SQUIRCLE, PILL, SQUARE, NEUMORPHIC, BEVELED
    val displayCornerRadiusDp: Int = 28,
    val keypadBtnCornerRadiusDp: Int = 22,
    val keypadGridSpacingDp: Int = 8,
    val isDark: Boolean = true,
    val hasGlassmorphism: Boolean = false,
    val hasNeumorphicStyle: Boolean = false,
    val hasRetroGlow: Boolean = false,
    val backgroundBrush: Brush? = null
) {
    companion object {
        val ALL_PRESETS: List<CalculatorThemeState>
            get() = AllCalculatorThemes

        fun getPreset(id: String): CalculatorThemeState {
            return AllCalculatorThemes.find { it.id == id } ?: AllCalculatorThemes[0]
        }
    }
}

typealias CalculatorThemeStyle = CalculatorThemeState

// ============================================================================
// COLOR SCHEMES FOR THE 10 DRIBBBLE-INSPIRED THEMES
// ============================================================================

// 1. Expressive Material You
val ExpressiveMaterialYouDark = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = Color(0xFF22323F),
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = Color(0xFF101418),
    surface = Color(0xFF101418),
    surfaceContainer = Color(0xFF1C2024),
    surfaceContainerHigh = Color(0xFF262A2E),
    surfaceContainerHighest = Color(0xFF313539),
    onSurface = Color(0xFFE0E2E6),
    outlineVariant = Color(0xFF43474E)
)

// 2. Liquid Glassmorphism
val LiquidGlassColorScheme = darkColorScheme(
    primary = LiquidGlassPrimary,
    onPrimary = LiquidGlassOnPrimary,
    primaryContainer = LiquidGlassContainer,
    onPrimaryContainer = Color(0xFFD6E8FF),
    secondary = LiquidGlassSecondary,
    onSecondary = Color.White,
    secondaryContainer = LiquidGlassSecondaryContainer,
    onSecondaryContainer = Color(0xFFECECFF),
    tertiary = LiquidGlassTertiary,
    tertiaryContainer = LiquidGlassTertiaryContainer,
    onTertiaryContainer = Color(0xFFFFE8D6),
    error = Color(0xFFFF453A),
    errorContainer = Color(0x3DFF453A),
    onErrorContainer = Color(0xFFFFD6D6),
    background = LiquidGlassBackground,
    surface = LiquidGlassBackground,
    surfaceContainer = LiquidGlassSurface,
    surfaceContainerHigh = LiquidGlassSurfaceHigh,
    surfaceContainerHighest = Color(0x38FFFFFF),
    onSurface = Color(0xFFF5F7FA),
    outlineVariant = LiquidGlassGlassBorder
)

// 3. Dark OLED & Cyberpunk Neon
val NeonCyberpunkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003D42),
    onPrimaryContainer = NeonCyan,
    secondary = NeonMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF52001B),
    onSecondaryContainer = Color(0xFFFFD1E1),
    tertiary = NeonPurple,
    tertiaryContainer = Color(0xFF380E62),
    onTertiaryContainer = Color(0xFFEADBFF),
    error = Color(0xFFFF3366),
    errorContainer = Color(0xFF4D0014),
    onErrorContainer = Color(0xFFFFD1DC),
    background = NeonBackground,
    surface = NeonSurface,
    surfaceContainer = Color(0xFF141726),
    surfaceContainerHigh = Color(0xFF1E2238),
    surfaceContainerHighest = Color(0xFF292E4A),
    onSurface = Color(0xFFE1F5FE),
    outlineVariant = Color(0xFF2E3554)
)

// 4. Neumorphic Soft UI
val NeumorphicColorScheme = lightColorScheme(
    primary = NeumorphicPrimary,
    onPrimary = NeumorphicOnPrimary,
    primaryContainer = Color(0xFFC5CAE9),
    onPrimaryContainer = Color(0xFF1A237E),
    secondary = Color(0xFF00897B),
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),
    tertiary = Color(0xFF8E24AA),
    tertiaryContainer = Color(0xFFE1BEE7),
    onTertiaryContainer = Color(0xFF4A148C),
    background = NeumorphicBackground,
    surface = NeumorphicSurface,
    surfaceContainer = NeumorphicSurface,
    surfaceContainerHigh = NeumorphicSurface,
    surfaceContainerHighest = NeumorphicSurface,
    onSurface = NeumorphicText,
    outlineVariant = Color(0xFFD1D9E6)
)

// 5. Minimalist Monochromatic
val MinimalistMonoColorScheme = lightColorScheme(
    primary = MonoPrimary,
    onPrimary = MonoOnPrimary,
    primaryContainer = Color(0xFF333333),
    onPrimaryContainer = Color.White,
    secondary = MonoSecondary,
    secondaryContainer = Color(0xFFE0E0E0),
    onSecondaryContainer = Color(0xFF111111),
    tertiary = Color(0xFF444444),
    tertiaryContainer = Color(0xFFEEEEEE),
    onTertiaryContainer = Color(0xFF111111),
    background = MonoBackground,
    surface = MonoSurface,
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFE5E5E5),
    surfaceContainerHighest = Color(0xFFD9D9D9),
    onSurface = Color(0xFF111111),
    outlineVariant = MonoBorder
)

// 6. Retro 80s / Digital LED
val Retro80sColorScheme = darkColorScheme(
    primary = RetroPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF4A2B00),
    onPrimaryContainer = Color(0xFFFFD199),
    secondary = RetroSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A0B12),
    onSecondaryContainer = Color(0xFFFFB8C0),
    tertiary = RetroLedText,
    tertiaryContainer = Color(0xFF0A3A16),
    onTertiaryContainer = RetroLedText,
    background = RetroBackground,
    surface = RetroBackground,
    surfaceContainer = RetroButtonSurface,
    surfaceContainerHigh = Color(0xFF34343C),
    surfaceContainerHighest = RetroScreenBackground,
    onSurface = RetroOnSurface,
    outlineVariant = Color(0xFF42424D)
)

// 7. Soft Pastel Card
val SoftPastelColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = PastelLavender,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF006A60),
    secondaryContainer = PastelMint,
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFF7D5260),
    tertiaryContainer = PastelPeach,
    onTertiaryContainer = Color(0xFF31111D),
    errorContainer = PastelRose,
    onErrorContainer = Color(0xFF410002),
    background = PastelBackground,
    surface = PastelSurface,
    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerHigh = Color(0xFFECE6F0),
    surfaceContainerHighest = Color(0xFFE6E0E9),
    onSurface = PastelText,
    outlineVariant = Color(0xFFCAC4D0)
)

// 8. Skeuomorphic Tactile Analog
val SkeuomorphicColorScheme = darkColorScheme(
    primary = SkeuoPrimary,
    onPrimary = SkeuoOnPrimary,
    primaryContainer = Color(0xFF5A2000),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFF546E7A),
    secondaryContainer = Color(0xFF263238),
    onSecondaryContainer = Color(0xFFECEFF1),
    tertiary = Color(0xFF78909C),
    tertiaryContainer = Color(0xFF37474F),
    onTertiaryContainer = Color(0xFFECEFF1),
    background = SkeuoBackground,
    surface = SkeuoSurface,
    surfaceContainer = Color(0xFF343842),
    surfaceContainerHigh = Color(0xFF3E434F),
    surfaceContainerHighest = Color(0xFF484E5C),
    onSurface = SkeuoText,
    outlineVariant = Color(0xFF545B6B)
)

// 9. Compact Floating Widget
val FloatingWidgetColorScheme = darkColorScheme(
    primary = WidgetPrimary,
    onPrimary = WidgetOnPrimary,
    primaryContainer = Color(0xFF322880),
    onPrimaryContainer = Color(0xFFE0DFFF),
    secondary = WidgetSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004F4D),
    onSecondaryContainer = Color(0xFFB2FAF7),
    tertiary = Color(0xFFFF7675),
    tertiaryContainer = Color(0xFF5C1818),
    onTertiaryContainer = Color(0xFFFFD8D8),
    background = WidgetBackground,
    surface = WidgetSurface,
    surfaceContainer = Color(0xFF2D2D42),
    surfaceContainerHigh = Color(0xFF383852),
    surfaceContainerHighest = Color(0xFF444463),
    onSurface = WidgetText,
    outlineVariant = Color(0xFF4B4B6E)
)

// 10. Modern Dual-Tone Geometric Accent
val DualToneColorScheme = darkColorScheme(
    primary = DualToneAccent1,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5C1E06),
    onPrimaryContainer = Color(0xFFFFDBD0),
    secondary = DualToneAccent2,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF002B4D),
    onSecondaryContainer = Color(0xFFCBE6FF),
    tertiary = Color(0xFF00B4D8),
    tertiaryContainer = Color(0xFF003543),
    onTertiaryContainer = Color(0xFFB8F2FF),
    background = DualToneBackground,
    surface = DualToneSurface,
    surfaceContainer = Color(0xFF212845),
    surfaceContainerHigh = Color(0xFF2B3357),
    surfaceContainerHighest = Color(0xFF353E69),
    onSurface = DualToneText,
    outlineVariant = Color(0xFF3A4575)
)

// 11. Aurora Vivid (Emerald & Violet Glow)
val AuroraVividColorScheme = darkColorScheme(
    primary = AuroraPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D38),
    onPrimaryContainer = Color(0xFFA6FFDF),
    secondary = AuroraSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A127A),
    onSecondaryContainer = Color(0xFFE8C2FF),
    tertiary = AuroraTertiary,
    tertiaryContainer = Color(0xFF5E002C),
    onTertiaryContainer = Color(0xFFFFC2DC),
    background = AuroraBackground,
    surface = AuroraSurface,
    surfaceContainer = Color(0xFF142E3C),
    surfaceContainerHigh = Color(0xFF1C3A4A),
    surfaceContainerHighest = Color(0xFF26485A),
    onSurface = AuroraText,
    outlineVariant = Color(0xFF2A556B)
)

// 12. Sunset Gold (Amber & Gold Luxe)
val SunsetGoldColorScheme = darkColorScheme(
    primary = SunsetGoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF593E00),
    onPrimaryContainer = Color(0xFFFFE08B),
    secondary = SunsetGoldSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF612E00),
    onSecondaryContainer = Color(0xFFFFD4B0),
    tertiary = SunsetGoldTertiary,
    tertiaryContainer = Color(0xFF570F16),
    onTertiaryContainer = Color(0xFFFFC2C7),
    background = SunsetGoldBackground,
    surface = SunsetGoldSurface,
    surfaceContainer = Color(0xFF35281C),
    surfaceContainerHigh = Color(0xFF423223),
    surfaceContainerHighest = Color(0xFF4F3D2B),
    onSurface = SunsetGoldText,
    outlineVariant = Color(0xFF5E4935)
)

// ============================================================================
// ALL 10 THEME PRESETS LIST
// ============================================================================
val AllCalculatorThemes = listOf(
    CalculatorThemeStyle(
        id = "MATERIAL_YOU",
        name = "Expressive Material You",
        subtitle = "Dynamic M3 Monet color roles & responsive shapes",
        tag = "Material 3",
        colorScheme = ExpressiveMaterialYouDark,
        buttonShapeStyle = "ROUNDED",
        displayCornerRadiusDp = 28,
        keypadBtnCornerRadiusDp = 22,
        keypadGridSpacingDp = 8,
        isDark = true
    ),
    CalculatorThemeStyle(
        id = "LIQUID_GLASS",
        name = "Liquid Glassmorphism",
        subtitle = "Translucent frosted surfaces & electric vibrant glow",
        tag = "Glass",
        colorScheme = LiquidGlassColorScheme,
        buttonShapeStyle = "SQUIRCLE",
        displayCornerRadiusDp = 30,
        keypadBtnCornerRadiusDp = 24,
        keypadGridSpacingDp = 8,
        isDark = true,
        hasGlassmorphism = true,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0D1326), Color(0xFF050810))
        )
    ),
    CalculatorThemeStyle(
        id = "NEON_CYBERPUNK",
        name = "OLED Cyberpunk Neon",
        subtitle = "Pitch black backdrop with glowing cyan & magenta accents",
        tag = "Neon",
        colorScheme = NeonCyberpunkColorScheme,
        buttonShapeStyle = "PILL",
        displayCornerRadiusDp = 24,
        keypadBtnCornerRadiusDp = 50,
        keypadGridSpacingDp = 10,
        isDark = true,
        hasRetroGlow = true,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF090A10), Color(0xFF020204))
        )
    ),
    CalculatorThemeStyle(
        id = "NEUMORPHIC",
        name = "Neumorphic Soft UI",
        subtitle = "Tactile extruding shapes & soft dual-shadow depth",
        tag = "Soft UI",
        colorScheme = NeumorphicColorScheme,
        buttonShapeStyle = "NEUMORPHIC",
        displayCornerRadiusDp = 24,
        keypadBtnCornerRadiusDp = 20,
        keypadGridSpacingDp = 12,
        isDark = false,
        hasNeumorphicStyle = true
    ),
    CalculatorThemeStyle(
        id = "MINIMALIST_MONO",
        name = "Minimalist Monochromatic",
        subtitle = "High contrast stark typography & precision grid lines",
        tag = "Mono",
        colorScheme = MinimalistMonoColorScheme,
        buttonShapeStyle = "SQUARE",
        displayCornerRadiusDp = 16,
        keypadBtnCornerRadiusDp = 12,
        keypadGridSpacingDp = 6,
        isDark = false
    ),
    CalculatorThemeStyle(
        id = "RETRO_80S",
        name = "Retro 80s Digital LED",
        subtitle = "Vintage green phosphor display & amber action keys",
        tag = "Retro",
        colorScheme = Retro80sColorScheme,
        buttonShapeStyle = "SQUARE",
        displayCornerRadiusDp = 12,
        keypadBtnCornerRadiusDp = 10,
        keypadGridSpacingDp = 8,
        isDark = true,
        hasRetroGlow = true
    ),
    CalculatorThemeStyle(
        id = "SOFT_PASTEL",
        name = "Soft Pastel Card Layout",
        subtitle = "Calming pastel palette with floating elevated cards",
        tag = "Pastel",
        colorScheme = SoftPastelColorScheme,
        buttonShapeStyle = "ROUNDED",
        displayCornerRadiusDp = 32,
        keypadBtnCornerRadiusDp = 26,
        keypadGridSpacingDp = 10,
        isDark = false
    ),
    CalculatorThemeStyle(
        id = "SKEUOMORPHIC",
        name = "Skeuomorphic Tactile Analog",
        subtitle = "Analog bevel textures & physical key depression depth",
        tag = "Analog",
        colorScheme = SkeuomorphicColorScheme,
        buttonShapeStyle = "BEVELED",
        displayCornerRadiusDp = 20,
        keypadBtnCornerRadiusDp = 16,
        keypadGridSpacingDp = 8,
        isDark = true
    ),
    CalculatorThemeStyle(
        id = "COMPACT_WIDGET",
        name = "Compact Floating Widget",
        subtitle = "Floating pill layout optimized for quick one-hand use",
        tag = "Widget",
        colorScheme = FloatingWidgetColorScheme,
        buttonShapeStyle = "PILL",
        displayCornerRadiusDp = 28,
        keypadBtnCornerRadiusDp = 50,
        keypadGridSpacingDp = 8,
        isDark = true
    ),
    CalculatorThemeStyle(
        id = "DUAL_TONE",
        name = "Modern Dual-Tone Accent",
        subtitle = "Asymmetric deep navy & vibrant tangerine split tones",
        tag = "Dual Tone",
        colorScheme = DualToneColorScheme,
        buttonShapeStyle = "SQUIRCLE",
        displayCornerRadiusDp = 28,
        keypadBtnCornerRadiusDp = 22,
        keypadGridSpacingDp = 8,
        isDark = true,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF14192E), Color(0xFF0B0E1A))
        )
    ),
    CalculatorThemeStyle(
        id = "AURORA_VIVID",
        name = "Aurora Emerald & Violet",
        subtitle = "Luminous emerald green & vibrant violet aurora glow",
        tag = "Aurora",
        colorScheme = AuroraVividColorScheme,
        buttonShapeStyle = "SQUIRCLE",
        displayCornerRadiusDp = 28,
        keypadBtnCornerRadiusDp = 22,
        keypadGridSpacingDp = 8,
        isDark = true,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0A1D27), Color(0xFF040B0F))
        )
    ),
    CalculatorThemeStyle(
        id = "SUNSET_GOLD",
        name = "Sunset Amber & Gold Luxe",
        subtitle = "Warm rich amber gold with high contrast deep background",
        tag = "Luxe Gold",
        colorScheme = SunsetGoldColorScheme,
        buttonShapeStyle = "ROUNDED",
        displayCornerRadiusDp = 28,
        keypadBtnCornerRadiusDp = 20,
        keypadGridSpacingDp = 8,
        isDark = true,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF22180E), Color(0xFF0E0904))
        )
    )
)

/**
 * CompositionLocal for active CalculatorThemeState
 */
val LocalCalculatorThemeState = staticCompositionLocalOf {
    AllCalculatorThemes[0]
}

val LocalCalculatorTheme = LocalCalculatorThemeState

/**
 * Custom Calculator Theme Provider Composable
 */
@Composable
fun CalculatorTheme(
    presetId: String = "MATERIAL_YOU",
    content: @Composable () -> Unit
) {
    val activeState = CalculatorThemeState.getPreset(presetId)

    CompositionLocalProvider(LocalCalculatorThemeState provides activeState) {
        MaterialTheme(
            colorScheme = activeState.colorScheme,
            content = content
        )
    }
}

/**
 * Helper object to retrieve active CalculatorThemeState from composition
 */
object CalculatorTheme {
    val current: CalculatorThemeState
        @Composable
        @ReadOnlyComposable
        get() = LocalCalculatorThemeState.current
}
