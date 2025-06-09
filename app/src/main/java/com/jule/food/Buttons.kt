package com.jule.food

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

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

@Composable
fun ButtonWithIcon(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    iconTint: Color? = null,
) {
    Button(onClick = onClick) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint ?: LocalContentColor.current
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        text()
    }
}
