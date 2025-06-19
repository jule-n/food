package com.jule.food

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.jule.food.ui.theme.FoodTheme

@Composable
fun SelectableText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    SelectionContainer() {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            autoSize = autoSize,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style
        )
    }
}


@Composable
fun EditableText(
    modifier: Modifier = Modifier,
    editable: Boolean,
    textAlign: TextAlign,
    textState: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    style: TextStyle = TextStyle.Default,
    onSubmit: () -> Unit,
    submitOnFocusLoss: Boolean = true,
    focusRequester: FocusRequester? = null,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    val startModifier = if (focusRequester != null) modifier.focusRequester(focusRequester) else modifier
    var submitted by remember { mutableStateOf(false) }
    BasicTextField(
        value = textState,
        onValueChange = {
            onTextChange(it)
        },
        textStyle = style.copy(textAlign = textAlign, color = color),
        enabled = editable,
        keyboardActions = KeyboardActions(onDone = {
            onSubmit()
            submitted = true
        }),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        singleLine = true,
        modifier = startModifier.onFocusChanged {
            if (!it.isFocused && submitOnFocusLoss && !submitted) {
//                Log.d("EditableText", "Focus Loss")
                onSubmit()
                submitted = true
            }
            else if (it.isFocused) {
                submitted = false
            }
        }
    )

}