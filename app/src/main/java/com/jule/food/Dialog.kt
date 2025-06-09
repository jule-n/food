package com.jule.food

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.launch


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

@OptIn(ExperimentalMaterial3Api::class)
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
    includeInfoButton: Boolean = false,
    infoText: String? = null,
    properties: DialogProperties = DialogProperties(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(20.dp),
    @StringRes confirmText: Int = R.string.confirm,
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
                    if (includeInfoButton) {
                        val tooltipState = rememberTooltipState()
                        val scope = rememberCoroutineScope()
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Spacer(modifier = Modifier.width(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.width(10.dp))
                            TooltipBox(
                                state = tooltipState,
                                enableUserInput = false,
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                tooltip = { PlainTooltip { Text(infoText ?: "")} }
                            ) {
                                IconButton(onClick = { scope.launch { tooltipState.show() } }, modifier = Modifier.size(24.dp)) {
                                    Icon(painter = painterResource(R.drawable.info), contentDescription = "Info", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    else {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                    }
                    content()
                    if (buttons) {
                        Row() {
                            TextButton(onClick = {
                                onDismissRequest()
                                onCancel?.invoke()
                            }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                            TextButton(onClick = { onConfirm?.invoke() }, enabled = confirmEnabled) {
                                Text(stringResource(id = confirmText))
                            }
                        }
                    }
                }
//                val colorAlpha by animateFloatAsState(targetValue = if (showOverlay) 0.1f else 0f, animationSpec = tween(durationMillis = 250))
//                AnimatedVisibility(showOverlay) {
//                    Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = colorAlpha)))
//                }
            }

        }
    }
}

@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogText: String,
    modifier: Modifier = Modifier
) {
    AlertDialogExample(
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
        dialogText = dialogText,
        confirmButtonText = stringResource(R.string.confirm),
        dismissButtonText = stringResource(R.string.cancel),
        modifier = modifier
    )
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogText: String,
    confirmButtonText: String,
    dismissButtonText: String,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(dismissButtonText)
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun DialogPreview() {
    FoodTheme() {
        Surface(modifier = Modifier.fillMaxSize()) {
            DefaultDialog(
                onDismissRequest = {},
                title = "Dialog Title",
                includeInfoButton = true,
            ) {
                Text("This is a dialog")
            }
        }
    }
}