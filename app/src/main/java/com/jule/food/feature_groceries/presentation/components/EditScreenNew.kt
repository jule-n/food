package com.jule.food.feature_groceries.presentation.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.tooling.LocalCompositionErrorContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.R
import com.jule.food.data.isCategoryNameTooLong
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.utils.DeleteDialog
import com.jule.food.utils.FilledIconButtonWithTooltip
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.utils.SheetErrorMessage
import com.jule.food.utils.conditional
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.ReorderableListState
import sh.calvin.reorderable.ReorderableScope

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun <T> EditScreenNew(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    reorderableListState: ReorderableLazyListState,
    items: List<T>,
    key: (T) -> Any,
    itemName: (T) -> String,
    itemComposable: @Composable (ReorderableCollectionItemScope.(T) -> Unit),
    newButtonText: String,
    onPressNewButton: () -> Unit,
    newButtonBackgroundColor: Color,
    onDelete: (T) -> Unit,
    confirmDeleteDialogTitle: String,
    onDeleteToastText: (T) -> String,
    itemToDelete: T? = null,
    onResetItemToDelete: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val hapticFeedback = LocalHapticFeedback.current
    val motionScheme = motionScheme
    val showConfirmDeleteDialog = itemToDelete != null
    val context = LocalContext.current

    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .padding(top = 20.dp)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                focusManager.clearFocus(true)
            }
            .fillMaxHeight(),
    ) {
        items(items, key = key) { item ->
            ReorderableItem(
                state = reorderableListState,
                key = key(item)
            ) {
                itemComposable(item)
            }
        }
        item {
            Button(
                onClick = onPressNewButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = newButtonBackgroundColor,
                    contentColor = MaterialTheme.colorScheme.contentColorFor(newButtonBackgroundColor)
                ),
                modifier = Modifier.padding(start = 10.dp).animateItem()
            ) {
                Icon(painterResource(R.drawable.add), contentDescription = null)
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(newButtonText)
            }
        }
    }

    if (showConfirmDeleteDialog && itemToDelete != null) {
        val deletedItemName = itemName(itemToDelete!!)
//        val deletedCategoryName = allCategories.fastFirstOrNull { it.id == categoryIdToDelete }?.name ?: ""
        DeleteDialog(
            title = confirmDeleteDialogTitle,
            onDismissRequest = {
                onResetItemToDelete()
            },
            onConfirm = {
                Toast.makeText(
                    context,
                    onDeleteToastText(itemToDelete),
                    Toast.LENGTH_SHORT
                ).show()

                onDelete(itemToDelete)
                onResetItemToDelete()
            }
        ) {
            Text(
                stringResource(
                    R.string.are_you_sure_you_want_to_delete_category_name,
                    deletedItemName
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ReorderableCollectionItemScope.EditScreenItemNew(
    item: T,
    textState: TextFieldState,
    itemBackgroundColor: Color,
    sharedElementModifier: Modifier?,
    onDispose: (T, newName: String) -> Unit,
    isError: (T, String) -> Boolean,
    errorText: (T, String) -> String?,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier,
    itemOutlineColor: Color? = null,
    prefixElement: @Composable (() -> Unit)? = null,
    subtitleElement: @Composable (() -> Unit)? = null,
    underElement: @Composable (() -> Unit)? = null,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val hapticFeedback = LocalHapticFeedback.current

    DisposableEffect(Unit) {
        onDispose {
            onDispose(item, textState.text.trim().toString())
        }
    }
    Column(modifier = modifier) {
        Surface(
            color = itemBackgroundColor,
            shape = RoundedCornerShape(20),
            modifier = Modifier.conditional(sharedElementModifier != null) {
                sharedElementModifier!!
            }
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(48.dp)
            .conditional(itemOutlineColor != null) {
                Modifier.border(BorderStroke(1.dp, itemOutlineColor!!))
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize().padding(start = 10.dp)) {
                prefixElement?.invoke()
                Column(modifier = Modifier.weight(1f)) {
                    BasicTextFieldWithBox(
                        state = textState,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        textStyle = ButtonDefaults.textStyleFor(40.dp),
                        textColor = MaterialTheme.colorScheme.contentColorFor(itemBackgroundColor),
                        modifier = Modifier.weight(1f).onFocusChanged { focusState ->
//                            if (!focusState.isFocused && isError(item, textState.text.trim().toString())) {
//                                textState.setTextAndPlaceCursorAtEnd(itemName)
//                            }
                        },
                        onKeyboardAction = { focusManager.clearFocus() }
                    )
                    subtitleElement?.invoke()
                }
                if (actionButtons != null) {
                    actionButtons()
                }
                IconButtonWithTooltip(
                    onClick = {},
                    tooltipText = stringResource(R.string.reorder_categories),
                    modifier = Modifier.draggableHandle(
                        onDragStarted = {
                            hapticFeedback.performHapticFeedback(
                                HapticFeedbackType.GestureThresholdActivate
                            )
                        },
                        onDragStopped = {
                            hapticFeedback.performHapticFeedback(
                                HapticFeedbackType.GestureEnd
                            )
                        }
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.drag_handle),
                        contentDescription = stringResource(R.string.reorder_categories)
                    )
                }
                FilledIconButtonWithTooltip(
                    onClick = onClickDelete,
                    tooltipText = stringResource(R.string.delete),
    //                                enabled = deleteEnabled,
                    //                                shapes = IconButtonDefaults.shapes(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.onError,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
        }
        underElement?.invoke()

        SheetErrorMessage(
            isError = isError(item, textState.text.trim().toString()),
            message = errorText(item, textState.text.trim().toString()) ?: "NULL"
        )
    }
}