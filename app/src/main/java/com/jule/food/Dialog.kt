package com.jule.food

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jule.food.ui.theme.FoodTheme


@Composable
fun EnterTextDialog(
    title: String,
    placeholder: (@Composable () -> Unit)? = null,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    confirmWithKeyboard: Boolean = false,
    singleLine: Boolean = true,
    isError: (String) -> Boolean,
    focusRequester: FocusRequester
) {
    var text by remember { mutableStateOf("")}
    val error = isError(text)

    DefaultDialog(onDismissRequest = onDismissRequest, title = title, onConfirm = { onConfirm(text) }, confirmEnabled = !error, buttons = true) {
        OutlinedTextField(value = text, onValueChange = { text = it }, singleLine = singleLine, modifier = Modifier.width(250.dp).focusRequester(focusRequester), shape = RoundedCornerShape(20), placeholder = placeholder, keyboardOptions = if (confirmWithKeyboard) KeyboardOptions.Default.copy(imeAction = ImeAction.Done) else KeyboardOptions.Default, keyboardActions = KeyboardActions(onDone = {
            if (!error && confirmWithKeyboard)
                onConfirm(text)
        }))
    }
}

@Composable
fun DefaultDialog(
    title: String,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    buttons: Boolean = false,
    onClickDialogEnabled: Boolean = false,
    onClickDialog: (() -> Unit)? = null,
    confirmEnabled: Boolean = true,
    properties: DialogProperties = DialogProperties(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(20.dp),
    @StringRes confirmText: Int = R.string.confirm,
    confirmColor: Color = MaterialTheme.colorScheme.primary,
    titleBackgroundColor: Color? = null,
    content: @Composable () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(modifier = modifier.clickable(
            onClick = { onClickDialog?.invoke() },
            enabled = onClickDialogEnabled,
            indication = null,
            interactionSource = interactionSource
            ),
            shape = RoundedCornerShape(10)
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = verticalArrangement,
                    modifier = Modifier.padding(10.dp)
                ) {
                    if (titleBackgroundColor == null) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                    } else {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = titleBackgroundColor
                        ) {
                            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.contentColorFor(titleBackgroundColor))
                        }
                    }
                    content()
                    if (buttons) {
                        Row {
                            TextButton(onClick = {
                                onDismissRequest()
                                onCancel?.invoke()
                            }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                            TextButton(onClick = { onConfirm?.invoke() }, enabled = confirmEnabled) {
                                Text(stringResource(id = confirmText), color = confirmColor)
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun DeleteDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(20.dp),
    content: @Composable () -> Unit = {},
) {
    DefaultDialog(
        title = title,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm,
        buttons = true,
        properties = properties,
        verticalArrangement = verticalArrangement,
        confirmColor = MaterialTheme.colorScheme.error,
        content = content,
        titleBackgroundColor = MaterialTheme.colorScheme.errorContainer
    )
}


@Preview
@Composable
fun DialogPreview() {
    FoodTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DeleteDialog(
                onDismissRequest = {},
                title = "Delete Dialog",
                onConfirm = {}
            ) {
                Text("This is a dialog")
            }
        }
    }
}