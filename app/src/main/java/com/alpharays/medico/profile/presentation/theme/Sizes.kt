package com.alpharays.medico.profile.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Sizes(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val lessMedium : Dp = 12.dp,
    val medium: Dp = 16.dp,
    val defaultIconSize: Dp = 24.dp,
    val large: Dp = 32.dp,
    val extraLarge: Dp = 64.dp
)

val LocalSize = compositionLocalOf { Sizes() }

val MaterialTheme.size: Sizes
    @Composable
    @ReadOnlyComposable
    get() = LocalSize.current
