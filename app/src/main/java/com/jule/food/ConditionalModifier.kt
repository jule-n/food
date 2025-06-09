package com.jule.food

import androidx.compose.ui.Modifier

inline fun Modifier.conditional(
    condition: Boolean,
    ifFalse: Modifier.() -> Modifier = { this },
    ifTrue: Modifier.() -> Modifier,
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}