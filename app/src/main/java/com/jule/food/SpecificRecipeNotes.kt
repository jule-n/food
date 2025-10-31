package com.jule.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpecificRecipeNotes(
    recipe: Recipe,
    onChangeRecipeNote: (String) -> Unit
) {
    val textFieldState = rememberTextFieldState(recipe.note)
    var noNote by remember { mutableStateOf(recipe.note == "") }
    val noteTextFocusRequester by remember { mutableStateOf(FocusRequester()) }

    val coroutineScope = rememberCoroutineScope()

    SpecificRecipeSection(
        icon = R.drawable.text,
        title = stringResource(R.string.notes),
        actionButtons = {
            AnimatedVisibility (noNote, enter = fadeIn() + expandVertically(expandFrom = Alignment.Top), exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)) {
                FilledExpressiveIconButtonWithTooltip(shapes = IconButtonDefaults.shapes(), tooltipText = stringResource(R.string.add_note), onClick = {
                    noNote = false
                    coroutineScope.launch {
                        delay(100)
                        noteTextFocusRequester.requestFocus()
                    }
                }) {
                    Icon(painterResource(R.drawable.add), contentDescription = stringResource(R.string.add_note))
                }
            }
        },
    ) {
        AnimatedVisibility (!noNote) {
            var wasFocused by remember { mutableStateOf(false) }
            Box(modifier = Modifier.padding(horizontal = 10.dp).background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp), RoundedCornerShape(10))) {
                BasicTextField(
                    state = textFieldState,
                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                    lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 2, maxHeightInLines = 10),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (!focusState.isFocused && wasFocused) {
                            val newNote = textFieldState.text.toString()
                            onChangeRecipeNote(newNote)
                            if (newNote == "")
                                noNote = true
                        }
                        if (focusState.isFocused) {
                            wasFocused = true
                        }

                    }.fillMaxWidth().padding(5.dp).focusRequester(noteTextFocusRequester)
                )
            }
        }
    }
}