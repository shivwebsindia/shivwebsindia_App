package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandOrange,
    onPrimary = Color.White,
    primaryContainer = BrandOrangeLight,
    onPrimaryContainer = BrandOrangeDark,
    secondary = BrandDarkBlue,
    onSecondary = Color.White,
    secondaryContainer = BrandDarkBlueLight,
    onSecondaryContainer = BrandDarkBlue,
    tertiary = IncomeGreen,
    onTertiary = Color.White,
    tertiaryContainer = IncomeGreenLight,
    onTertiaryContainer = IncomeGreen,
    error = BrandDarkRed,
    onError = Color.White,
    errorContainer = BrandDarkRedLight,
    onErrorContainer = BrandDarkRed,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFEEF2F6),
    onSurfaceVariant = TextSecondary,
    outline = CardBorderColor
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandOrange,
    onPrimary = Color.White,
    primaryContainer = BrandOrangeDark,
    onPrimaryContainer = BrandOrangeLight,
    secondary = Color(0xFF64B5F6),
    onSecondary = BrandDarkBlue,
    secondaryContainer = BrandDarkBlueSurface,
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF065F46),
    onTertiaryContainer = Color(0xFFD1FAE5),
    error = Color(0xFFF87171),
    onError = BrandDarkRed,
    errorContainer = BrandDarkRed,
    onErrorContainer = BrandDarkRedLight,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF16375B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
