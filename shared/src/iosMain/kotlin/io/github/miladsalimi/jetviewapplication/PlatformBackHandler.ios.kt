package io.github.miladsalimi.jetviewapplication

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS handles back navigation via native swipe gesture or UI buttons
}
