package com.jule.food

import androidx.annotation.DrawableRes
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun TextButtonWithIcon(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    iconTint: Color? = null,
) {
    TextButton(onClick = onClick) {
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
                colors = ToggleButtonDefaults.toggleButtonColors().copy(checkedContainerColor = checkedContainerColor),
            ) {
                Text(label)
            }
        }
    }
}