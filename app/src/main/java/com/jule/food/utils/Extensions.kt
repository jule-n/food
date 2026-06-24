package com.jule.food.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.ui.unit.IntOffset

//Int to Enum
inline fun <reified T : Enum<T>> Int.toEnum(): T? {
    return enumValues<T>().firstOrNull { it.ordinal == this }
}

//Enum to Int
inline fun <reified T : Enum<T>> T.toInt(): Int {
    return this.ordinal
}

// Add all elements of a list to a mutable list without duplicates
fun <T> MutableList<T>.addAllWithoutDuplicates(list: List<T>) {
    list.forEach {
        if (!this.contains(it)) {
            this.add(it)
        }
    }
}
// Custom animation for a complete slide in
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun completeSlideIn(left: Boolean, motionScheme: MotionScheme): EnterTransition {
    return slideIn(
        animationSpec = motionScheme.slowSpatialSpec(),
        initialOffset = { fullSize -> IntOffset((if (left) 1 else -1) * fullSize.width, 0) }
    )
}
// Custom animation for a complete slide out
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun completeSlideOut(left: Boolean, motionScheme: MotionScheme): ExitTransition {
    return slideOut(
        animationSpec = motionScheme.slowSpatialSpec(),
        targetOffset = { fullSize -> IntOffset((if (left) -1 else 1) * fullSize.width, 0) }
    )
}