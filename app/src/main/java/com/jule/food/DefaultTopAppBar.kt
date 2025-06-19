package com.jule.food

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    titleState: TextFieldValue,
    editingTitle: Boolean,
    onSubmitTitleChange: () -> Unit,
    onTitleChange: (TextFieldValue) -> Unit,
    titleFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            EditableText(
                textState = titleState,
                onTextChange = onTitleChange,
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                textAlign = TextAlign.Center,
                focusRequester = titleFocusRequester,
                editable = editingTitle,
                onSubmit = onSubmitTitleChange,
            )
//            Text(text = title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = { navigationIcon?.invoke() },
        actions = { actions?.invoke() },
        windowInsets = WindowInsets(left = 0.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = { navigationIcon?.invoke() },
        actions = { actions?.invoke() },
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}


@Composable
fun DefaultTopAppBar(
    titleState: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    editingTitle: Boolean,
    onSubmitTitleChange: () -> Unit,
    titleFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    menuContent: @Composable () -> Unit,
    menuExpanded: Boolean,
    onChangeMenuExpansion: (Boolean) -> Unit,
) {
    DefaultTopAppBar(titleState = titleState, onTitleChange = onTitleChange, editingTitle = editingTitle, onSubmitTitleChange = onSubmitTitleChange, titleFocusRequester = titleFocusRequester, modifier = modifier, navigationIcon = navigationIcon, actions = {
        actions?.invoke()
        IconButton(onClick = { onChangeMenuExpansion(true) }, modifier = Modifier.size(50.dp)) {
            Icon(painter = painterResource(id = R.drawable.more_vert), contentDescription = "More")
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { onChangeMenuExpansion(false) }) {
            menuContent()
        }
    })
}
