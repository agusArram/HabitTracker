package com.example.habittracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ArrambiTechColorScheme = darkColorScheme(
    primary = ArrambiCyan,
    onPrimary = ArrambiBlack,
    primaryContainer = ArrambiCyan,
    onPrimaryContainer = ArrambiDarkBlue,

    secondary = ArrambiCyan,
    onSecondary = ArrambiDarkBlue,

    background = ArrambiDarkBlue,
    onBackground = ArrambiWhite,

    surface = ArrambiDarkGray,
    onSurface = ArrambiWhite,
    surfaceVariant = ArrambiGray,
    onSurfaceVariant = ArrambiLightGray,

    outline = ArrambiGray,
    outlineVariant = ArrambiLightGray,

    error = Color(0xFFef4444),
    onError = ArrambiWhite
)

@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = true, // Siempre usar tema oscuro de ArrambiTech
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArrambiTechColorScheme,
        typography = Typography,
        content = content
    )
}