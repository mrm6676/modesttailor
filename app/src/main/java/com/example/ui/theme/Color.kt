package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Haute-Atelier Light Palette (Rosewood, Silk Ivory, Sage Olive & Antique Brass)
val RosewoodPrimary = Color(0xFF6E3B49)
val OnRosewoodPrimary = Color(0xFFFFFFFF)
val RosewoodContainer = Color(0xFFFADAE2)
val OnRosewoodContainer = Color(0xFF280B14)

val SageSecondary = Color(0xFF4C6455)
val OnSageSecondary = Color(0xFFFFFFFF)
val SageContainer = Color(0xFFCEE9D6)
val OnSageContainer = Color(0xFF092014)

val BrassTertiary = Color(0xFF845926)
val OnBrassTertiary = Color(0xFFFFFFFF)
val BrassContainer = Color(0xFFFFDDB8)
val OnBrassContainer = Color(0xFF2B1700)

val SilkIvoryBackground = Color(0xFFFAF7F2)
val OnSilkIvoryBackground = Color(0xFF1F1A1B)
val TravertineSurface = Color(0xFFFDFBF8)
val TravertineSurfaceVariant = Color(0xFFF1EAE4)
val OnTravertineSurfaceVariant = Color(0xFF514346)
val WarmOutline = Color(0xFF837376)
val WarmOutlineVariant = Color(0xFFD5C2C5)

// Haute-Atelier Dark Palette (Espresso Silk, Soft Rose & Mint Sage)
val RosewoodDarkPrimary = Color(0xFFE6B5C1)
val OnRosewoodDarkPrimary = Color(0xFF42202B)
val RosewoodDarkContainer = Color(0xFF5A2E3B)
val OnRosewoodDarkContainer = Color(0xFFFADAE2)

val SageDarkSecondary = Color(0xFFB3CDBB)
val OnSageDarkSecondary = Color(0xFF1F3528)
val SageDarkContainer = Color(0xFF354C3E)
val OnSageDarkContainer = Color(0xFFCEE9D6)

val BrassDarkTertiary = Color(0xFFFAC083)
val OnBrassDarkTertiary = Color(0xFF482A00)
val BrassDarkContainer = Color(0xFF67400F)
val OnBrassDarkContainer = Color(0xFFFFDDB8)

val EspressoDarkBackground = Color(0xFF181314)
val OnEspressoDarkBackground = Color(0xFFECE0E1)
val EspressoDarkSurface = Color(0xFF201A1B)
val EspressoDarkSurfaceVariant = Color(0xFF382E30)
val OnEspressoDarkSurfaceVariant = Color(0xFFD5C2C5)
val DarkWarmOutline = Color(0xFF9E8C90)

val EditorialScrimTop = Color(0x261E1216)
val EditorialScrimBottom = Color(0xDE1E1216)

fun atelierSwatchColor(colorName: String, fallback: Color): Color = when (colorName) {
    "Rosewood" -> RosewoodPrimary
    "Silk Ivory" -> SilkIvoryBackground
    "Sage Olive" -> SageSecondary
    "Warm Camel" -> Color(0xFFC29B6F)
    "Dusty Mauve" -> Color(0xFFA87E8B)
    "Chambray Blue" -> Color(0xFF698CB2)
    "Espresso" -> Color(0xFF42312C)
    "Champagne Gold" -> Color(0xFFD8BC82)
    else -> fallback
}


