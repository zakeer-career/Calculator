package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// EXTENDED CALCULATOR COLOR MAPPINGS
// ============================================================================
val LightExtendedCalcColors = ExtendedCalcColors(
    operatorContainer = Color(0xFFD0E1F9),
    onOperatorContainer = Color(0xFF001E36),
    scientificFunctionContainer = Color(0xFFE2E2E9),
    onScientificFunctionContainer = Color(0xFF1B1B21),
    glassBorder = Color(0x40FFFFFF),
    glassBackground = Color(0xCCF3F4F8),
    historyGridline = Color(0x20000000),
    displaySecondaryText = Color(0xFF535F70)
)

val DarkExtendedCalcColors = ExtendedCalcColors(
    operatorContainer = Color(0xFF004B75),
    onOperatorContainer = Color(0xFFCDE5FF),
    scientificFunctionContainer = Color(0xFF2E3038),
    onScientificFunctionContainer = Color(0xFFE2E2E9),
    glassBorder = Color(0x38FFFFFF),
    glassBackground = Color(0xCC111318),
    historyGridline = Color(0x30FFFFFF),
    displaySecondaryText = Color(0xFF8C9BAE)
)

// ============================================================================
// 1. MATERIAL YOU / EXPRESSIVE PALETTE
// ============================================================================
val PrimaryLight = Color(0xFF006399)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFCDE5FF)
val OnPrimaryContainerLight = Color(0xFF001D32)

val SecondaryLight = Color(0xFF51606F)
val SecondaryContainerLight = Color(0xFFD4E4F6)
val OnSecondaryContainerLight = Color(0xFF0D1D2A)

val TertiaryLight = Color(0xFF67587A)
val TertiaryContainerLight = Color(0xFFEDDCFF)
val OnTertiaryContainerLight = Color(0xFF221533)

val PrimaryDark = Color(0xFF95CCFF)
val OnPrimaryDark = Color(0xFF003353)
val PrimaryContainerDark = Color(0xFF004B75)
val OnPrimaryContainerDark = Color(0xFFCDE5FF)

val SecondaryDark = Color(0xFFB8C8DA)
val SecondaryContainerDark = Color(0xFF394956)
val OnSecondaryContainerDark = Color(0xFFD4E4F6)

val TertiaryDark = Color(0xFFD2C1E7)
val TertiaryContainerDark = Color(0xFF4F4061)
val OnTertiaryContainerDark = Color(0xFFEDDCFF)

// ============================================================================
// 2. GLASSMORPHISM / FROSTED GLASS PALETTE
// ============================================================================
val LiquidGlassBackground = Color(0xFF0A0E1A)
val LiquidGlassSurface = Color(0x1AFFFFFF)
val LiquidGlassSurfaceHigh = Color(0x28FFFFFF)
val LiquidGlassPrimary = Color(0xFF0A84FF) // iOS Electric Blue
val LiquidGlassOnPrimary = Color(0xFFFFFFFF)
val LiquidGlassContainer = Color(0x330A84FF)
val LiquidGlassSecondary = Color(0xFF5E5CE6) // iOS Indigo Glass
val LiquidGlassSecondaryContainer = Color(0x305E5CE6)
val LiquidGlassTertiary = Color(0xFFFF9F0A) // iOS Liquid Orange
val LiquidGlassTertiaryContainer = Color(0x38FF9F0A)
val LiquidGlassGlassBorder = Color(0x38FFFFFF) // Frosted glass sheen line

// ============================================================================
// 3. DARK OLED HIGH-CONTRAST & CYBERPUNK NEON PALETTE
// ============================================================================
val OledBackground = Color(0xFF000000)
val OledSurface = Color(0xFF0D0D0D)
val OledPrimary = Color(0xFF00E676)
val OledContainer = Color(0xFF1F1F1F)

val NeonCyan = Color(0xFF00F0FF)
val NeonMagenta = Color(0xFFFF0055)
val NeonBackground = Color(0xFF090A10)
val NeonSurface = Color(0xFF121420)
val NeonPurple = Color(0xFF7B2CBF)

// ============================================================================
// 4. NEUMORPHIC (SOFT UI) PALETTE
// ============================================================================
val NeumorphicBackground = Color(0xFFE0E5EC)
val NeumorphicSurface = Color(0xFFE0E5EC)
val NeumorphicPrimary = Color(0xFF3D5AFE)
val NeumorphicOnPrimary = Color(0xFFFFFFFF)
val NeumorphicLightShadow = Color(0xFFFFFFFF)
val NeumorphicDarkShadow = Color(0xFFA3B1C6)
val NeumorphicText = Color(0xFF2D3748)

// ============================================================================
// 5. MINIMALIST MONOCHROMATIC PALETTE
// ============================================================================
val MonoBackground = Color(0xFFF7F7F7)
val MonoSurface = Color(0xFFFFFFFF)
val MonoPrimary = Color(0xFF111111)
val MonoOnPrimary = Color(0xFFFFFFFF)
val MonoSecondary = Color(0xFF666666)
val MonoBorder = Color(0xFFE2E2E2)

// ============================================================================
// 6. RETRO 80s / DIGITAL LED DISPLAY PALETTE
// ============================================================================
val RetroBackground = Color(0xFF18181C)
val RetroScreenBackground = Color(0xFF0F2518)
val RetroLedText = Color(0xFF39FF14) // Phosphor Green
val RetroPrimary = Color(0xFFFF9900) // 80s Amber
val RetroSecondary = Color(0xFFCC2233) // Cherry Red
val RetroButtonSurface = Color(0xFF2A2A30)
val RetroOnSurface = Color(0xFFE0E0E0)

// ============================================================================
// 7. SOFT PASTEL CARD PALETTE
// ============================================================================
val PastelBackground = Color(0xFFF9F6FF)
val PastelSurface = Color(0xFFFFFFFF)
val PastelLavender = Color(0xFFE8DEF8)
val PastelMint = Color(0xFFE0F2F1)
val PastelPeach = Color(0xFFFFECB3)
val PastelSky = Color(0xFFE1F5FE)
val PastelRose = Color(0xFFFFCDD2)
val PastelText = Color(0xFF322F37)

// ============================================================================
// 8. SKEUOMORPHIC TACTILE ANALOG PALETTE
// ============================================================================
val SkeuoBackground = Color(0xFF222428)
val SkeuoSurface = Color(0xFF2C2F36)
val SkeuoPrimary = Color(0xFFE65100)
val SkeuoOnPrimary = Color(0xFFFFFFFF)
val SkeuoButtonGradStart = Color(0xFF3D414B)
val SkeuoButtonGradEnd = Color(0xFF25282E)
val SkeuoText = Color(0xFFE1E4EA)

// ============================================================================
// 9. COMPACT FLOATING WIDGET PALETTE
// ============================================================================
val WidgetBackground = Color(0xFF191924)
val WidgetSurface = Color(0xFF232333)
val WidgetPrimary = Color(0xFF6C5CE7)
val WidgetOnPrimary = Color(0xFFFFFFFF)
val WidgetSecondary = Color(0xFF00CEC9)
val WidgetText = Color(0xFFF1F2F6)

// ============================================================================
// 10. DUAL-TONE GEOMETRIC ACCENT PALETTE
// ============================================================================
val DualToneBackground = Color(0xFF101424)
val DualToneSurface = Color(0xFF1A1F36)
val DualToneAccent1 = Color(0xFFFF6B35) // Vibrant Tangerine
val DualToneAccent2 = Color(0xFF004E89) // Deep Marine Blue
val DualToneText = Color(0xFFF7F7FF)
val SunsetAmber = Color(0xFFFF9100)
val SunsetCoral = Color(0xFFFF3D00)
val SunsetBackground = Color(0xFF1C1310)
val SunsetSurface = Color(0xFF2A1C18)

// ============================================================================
// 11. AURORA VIVID (VIBRANT EMERALD & VIOLET) PALETTE
// ============================================================================
val AuroraBackground = Color(0xFF08141B)
val AuroraSurface = Color(0xFF0F232E)
val AuroraPrimary = Color(0xFF00E6A8) // Electric Emerald
val AuroraSecondary = Color(0xFF9D4EDD) // Vivid Violet Glow
val AuroraTertiary = Color(0xFFFF007F) // Electric Magenta
val AuroraText = Color(0xFFE0F7FA)

// ============================================================================
// 12. SUNSET GOLD (AMBER & GOLD LUXE) PALETTE
// ============================================================================
val SunsetGoldBackground = Color(0xFF18120B)
val SunsetGoldSurface = Color(0xFF281E15)
val SunsetGoldPrimary = Color(0xFFFFB703) // Gold Amber
val SunsetGoldSecondary = Color(0xFFFB8500) // Vivid Orange
val SunsetGoldTertiary = Color(0xFFE63946) // Ruby
val SunsetGoldText = Color(0xFFFFF3E0)
