package com.jule.food.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jule.food.R
import com.jule.food.ui.theme.FoodTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAddEditBottomSheet(
    modifier: Modifier = Modifier,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit,
//    focusRequester: FocusRequester,
    placeholderText: String,
    initialText: String = "",
    nameTooLongLimit: Int,
    existingNames: List<String>? = null
) {
    val textFieldState = rememberTextFieldState(initialText)
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        modifier = modifier
    ) {
        var showEmptyError by remember { mutableStateOf(false) }

        val isNameEmpty = textFieldState.text.isEmpty()
        val isNameTooLong = textFieldState.text.length > nameTooLongLimit
        val isNameSame = existingNames?.any { it == textFieldState.text.trim().toString() } ?: false

        // Only show error that field is empty after something has been typed for the first time
        if (!showEmptyError) {
            LaunchedEffect(textFieldState.text) {
                if (textFieldState.text.isNotEmpty())
                    showEmptyError = true
            }
        }

        BasicTextFieldWithBox(
            state = textFieldState,
            lineLimits = TextFieldLineLimits.SingleLine,
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            placeholder = {
                Text(
                    text = "${placeholderText}...",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            },
            contentPadding = PaddingValues(15.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            onKeyboardAction = {
                if (!isNameEmpty && !isNameTooLong && !isNameSame) {
                    onConfirm(textFieldState.text.trim().toString())
                }
            }
        )
        SheetErrorMessage(
            isError = (showEmptyError && isNameEmpty) || isNameTooLong || isNameSame,
            message = if (isNameTooLong) stringResource(
                R.string.name_too_long,
                nameTooLongLimit
            ) else
                if (isNameEmpty) stringResource(R.string.name_empty) else 
            if (isNameSame) stringResource(R.string.name_already_exists) else ""
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                enabled = !isNameEmpty && !isNameTooLong && !isNameSame,
                onClick = {
                    onConfirm(textFieldState.text.trim().toString())
                }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }

}


@Preview
@Composable
fun BottomSheetPreview() {
    FoodTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SimpleAddEditBottomSheet(
                onConfirm = {},
                onDismissRequest = {},
                placeholderText = "New whatever",
                nameTooLongLimit = 10,
//                focusRequester = remember { FocusRequester() }
            )
        }
    }
}