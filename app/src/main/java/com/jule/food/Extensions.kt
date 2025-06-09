package com.jule.food

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

//Int to Enum
inline fun <reified T : Enum<T>> Int.toEnum(): T? {
    return enumValues<T>().firstOrNull { it.ordinal == this }
}

//Enum to Int
inline fun <reified T : Enum<T>> T.toInt(): Int {
    return this.ordinal
}

fun <T> MutableList<T>.addAllWithoutDuplicates(list: List<T>) {
    list.forEach {
        if (!this.contains(it)) {
            this.add(it)
        }
    }
}

fun completeSlideIn(left: Boolean): EnterTransition {
    return slideIn(initialOffset = { fullSize -> IntOffset((if (left) 1 else -1) * fullSize.width, 0) })
}
fun completeSlideOut(left: Boolean): ExitTransition {
    return slideOut(targetOffset = { fullSize -> IntOffset((if (left) -1 else 1) * fullSize.width, 0) })
}

class NoRippleInteractionSource : MutableInteractionSource {

    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}

    override fun tryEmit(interaction: Interaction) = true

}
