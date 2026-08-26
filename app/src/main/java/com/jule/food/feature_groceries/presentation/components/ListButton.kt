package com.jule.food.feature_groceries.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jule.food.utils.CustomCheckbox
import com.jule.food.utils.SelectionOption


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListButton(
    modifier: Modifier = Modifier,
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color = MaterialTheme.colorScheme.tertiary,
    selectCheckbox: Boolean = false
) {
    val color by animateColorAsState(
        targetValue = if (selected) selectedColor else MaterialTheme.colorScheme.surfaceVariant
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.contentColorFor(selectedColor) else MaterialTheme.colorScheme.onSurfaceVariant
    )
    val shapeCornerRadius by animateIntAsState(
        targetValue = if (!selected) 50 else 20
    )
    Box (modifier = modifier) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = textColor
            ),
            shapes = ButtonDefaults.shapes(
                shape = RoundedCornerShape(shapeCornerRadius),
                pressedShape = RoundedCornerShape(10)
            ),
            modifier = Modifier.height(40.dp)
        ) {
            Text(name)
        }

        if (selectCheckbox) {
            CustomCheckbox(
                selectionOption = if (selected) SelectionOption.Yes else SelectionOption.No,
                topStartRadius = 50,
                selectedTopStartRadius = 100
            )
        }
    }
}