package com.jule.food.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

data object CustomColors {
    val deleted: Color
        @Composable
        get() = getDeleted()

    @Composable
    fun getDeleted(): Color {
        val onBg = MaterialTheme.colorScheme.onBackground
        val bg = MaterialTheme.colorScheme.background
        return lerp(bg, onBg, 0.2f)
    }
}