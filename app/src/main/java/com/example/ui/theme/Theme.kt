package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val NainaDarkColorScheme = darkColorScheme(
    primary = NeonCrimson,
    onPrimary = CrispWhite,
    primaryContainer = DeepRuby,
    onPrimaryContainer = CrispWhite,
    secondary = RoyalGold,
    onSecondary = ObsidianVoid,
    secondaryContainer = VelvetSurface,
    onSecondaryContainer = RoyalGold,
    tertiary = GlowingRuby,
    onTertiary = CrispWhite,
    background = ObsidianVoid,
    onBackground = CrispWhite,
    surface = DeepCrimsonNight,
    onSurface = CrispWhite,
    surfaceVariant = VelvetSurface,
    onSurfaceVariant = SoftRoseText,
    outline = CrimsonBorder,
    outlineVariant = SoftCrimsonBorder,
    error = ErrorCoral,
    onError = ObsidianVoid
)

private val NainaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun NainaAiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NainaDarkColorScheme,
        typography = NainaTypography,
        shapes = NainaShapes,
        content = content
    )
}
