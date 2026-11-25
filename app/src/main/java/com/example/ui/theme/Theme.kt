package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = RosewoodPrimary,
    onPrimary = OnRosewoodPrimary,
    primaryContainer = RosewoodContainer,
    onPrimaryContainer = OnRosewoodContainer,
    secondary = SageSecondary,
    onSecondary = OnSageSecondary,
    secondaryContainer = SageContainer,
    onSecondaryContainer = OnSageContainer,
    tertiary = BrassTertiary,
    onTertiary = OnBrassTertiary,
    tertiaryContainer = BrassContainer,
    onTertiaryContainer = OnBrassContainer,
    background = SilkIvoryBackground,
    onBackground = OnSilkIvoryBackground,
    surface = TravertineSurface,
    onSurface = OnSilkIvoryBackground,
    surfaceVariant = TravertineSurfaceVariant,
    onSurfaceVariant = OnTravertineSurfaceVariant,
    outline = WarmOutline,
    outlineVariant = WarmOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = RosewoodDarkPrimary,
    onPrimary = OnRosewoodDarkPrimary,
    primaryContainer = RosewoodDarkContainer,
    onPrimaryContainer = OnRosewoodDarkContainer,
    secondary = SageDarkSecondary,
    onSecondary = OnSageDarkSecondary,
    secondaryContainer = SageDarkContainer,
    onSecondaryContainer = OnSageDarkContainer,
    tertiary = BrassDarkTertiary,
    onTertiary = OnBrassDarkTertiary,
    tertiaryContainer = BrassDarkContainer,
    onTertiaryContainer = OnBrassDarkContainer,
    background = EspressoDarkBackground,
    onBackground = OnEspressoDarkBackground,
    surface = EspressoDarkSurface,
    onSurface = OnEspressoDarkBackground,
    surfaceVariant = EspressoDarkSurfaceVariant,
    onSurfaceVariant = OnEspressoDarkSurfaceVariant,
    outline = DarkWarmOutline
)

val AtelierShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun ModestTailorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AtelierShapes,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    ModestTailorTheme(darkTheme = darkTheme, content = content)
}
