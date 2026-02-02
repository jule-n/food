package com.jule.food

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

enum class SelectedOption { Yes, No, Half }

@Composable
fun CustomCheckbox(
    modifier: Modifier = Modifier,
    selectionOption: SelectedOption,
    topStartRadius: Int = 20,
    selectedTopStartRadius: Int = 20,
    selectedColor: Color = Color.Black.copy(alpha = 0.3f),
    backgroundColor: Color = Color.Transparent,
    iconTint: Color = Color.White
) {
    val selected = selectionOption == SelectedOption.Yes || selectionOption == SelectedOption.Half
    val icon = if (selectionOption == SelectedOption.Yes) R.drawable.done else R.drawable.minus
    val currentTopStartRadius by animateIntAsState(if (selected) selectedTopStartRadius else topStartRadius)
    Box(modifier = modifier.size(20.dp)) {
        Surface(
            color = if (selected) selectedColor else backgroundColor,
            shape = RoundedCornerShape(currentTopStartRadius, 20, 20, 20),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f)),
            modifier = Modifier.size(20.dp)
        ) {
            if (selected)
                Icon(painter = painterResource(icon), tint = iconTint, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}