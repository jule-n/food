package com.jule.food.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.jule.food.R

enum class SelectionOption { Yes, No, Half }

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun CustomCheckbox(
    modifier: Modifier = Modifier,
    selectionOption: SelectionOption,
    topStartRadius: Int = 20,
    selectedTopStartRadius: Int = topStartRadius,
    selectedBackgroundColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
    borderColor: Color = selectedBackgroundColor,
    backgroundColor: Color = Color.Transparent,
    iconTint: Color = Color.White
) {
    val selected = selectionOption == SelectionOption.Yes || selectionOption == SelectionOption.Half
    val color by animateColorAsState(if (selected) selectedBackgroundColor else backgroundColor)
    val currentTopStartRadius by animateIntAsState(if (selected) selectedTopStartRadius else topStartRadius)


    var lastSelectionOption by remember { mutableStateOf(SelectionOption.Yes) }

    val animationsAtEnd = remember { mutableStateListOf(false, false, false, false, false, false) }
    fun resetAnimations() {
        animationsAtEnd.clear()
        animationsAtEnd.addAll(listOf(false, false, false, false, false, false))
    }
    val painters: List<Painter> = listOf(
        rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.nothing_to_check), animationsAtEnd[0]),
        rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.minus_to_check), animationsAtEnd[1]),
        rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.nothing_to_minus), animationsAtEnd[2]),
        rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.check_to_minus), animationsAtEnd[3]),
        rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.check_to_nothing), animationsAtEnd[4]),
        rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.minus_to_nothing), animationsAtEnd[5])
    )

    var currentPainterIndex by remember { mutableIntStateOf (0) }


    LaunchedEffect(selectionOption) {
        resetAnimations()

        when (selectionOption) {
            SelectionOption.Yes ->
                if (lastSelectionOption == SelectionOption.No) {
                    currentPainterIndex = 0
                    animationsAtEnd[0] = true
                } else {
                    currentPainterIndex = 1
                    animationsAtEnd[1] = true
                }
            SelectionOption.No ->
                if (lastSelectionOption == SelectionOption.Yes) {
                    currentPainterIndex = 4
                    animationsAtEnd[4] = true
                } else {
                    currentPainterIndex = 5
                    animationsAtEnd[5] = true
                }
            SelectionOption.Half ->
                if (lastSelectionOption == SelectionOption.Yes) {
                    currentPainterIndex = 3
                    animationsAtEnd[3] = true
                } else {
                    currentPainterIndex = 2
                    animationsAtEnd[2] = true
                }
        }

        lastSelectionOption = selectionOption
    }


    Box(modifier = modifier.size(20.dp)) {
        Surface(
            color = color,
            shape = RoundedCornerShape(currentTopStartRadius, 20, 20, 20),
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.size(20.dp)
        ) {
            Image(
                painter = painters[currentPainterIndex],
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(iconTint, blendMode = BlendMode.SrcIn)
            )
        }
    }
//    Text("Ic: $icontext, atEnd: $checkAnimationAtEnd")
}