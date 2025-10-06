package com.example.pillbestie.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GirlyPopColorScheme = darkColorScheme(
    primary = GirlyPopPink,
    onPrimary = OnGirlyPopPink,
    secondary = SoftLavender,
    onSecondary = OnSoftLavender,
    tertiary = SunnyYellow,
    onTertiary = OnSunnyYellow,
    background = BackgroundDark,
    onBackground = OnBackground,
    surface = SurfaceDark,
    onSurface = OnSurface,
    error = ErrorRed,
    onError = OnError
)

@Composable
fun PillBestieTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GirlyPopColorScheme,
        typography = AppTypography,
        shapes = PillBestieShapes,
        content = content
    )
}
