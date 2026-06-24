package com.jule.food.utils

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun TextButtonWithIcon(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconTint: Color? = null,
) {
    TextButton(modifier = modifier, onClick = onClick, enabled = enabled) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint ?: LocalContentColor.current
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        text()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectedButtonGroup(
    options: List<String>,
    selectedOptionIndex: Int,
    onSelectedOptionChange: (Int) -> Unit,
    checkedContainerColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = index == selectedOptionIndex,
                onCheckedChange = { if (it) onSelectedOptionChange(index) },
                shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
//                shapes = when (index) {
//                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
//                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
//                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
//                },
                colors = ToggleButtonDefaults.toggleButtonColors().copy(
                    checkedContainerColor = checkedContainerColor,
                    checkedContentColor = MaterialTheme.colorScheme.contentColorFor(
                        checkedContainerColor
                    )
                ),
            ) {
                Text(label)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectedIconGroup(
    @DrawableRes options: List<Int>,
    optionLabels: List<String>,
    selectedOptionIndex: Int,
    onSelectedOptionChange: (Int) -> Unit,
    checkedContainerColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        options.forEachIndexed { index, icon ->
            val label = optionLabels[index]
            ToggleButtonWithTooltip(
                checked = index == selectedOptionIndex,
                onCheckedChange = { if (it) onSelectedOptionChange(index) },
//                shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                colors = ToggleButtonDefaults.toggleButtonColors()
                    .copy(checkedContainerColor = checkedContainerColor),
                tooltipText = label
            ) {
                Icon(painterResource(icon), contentDescription = label)
            }
        }
    }
}