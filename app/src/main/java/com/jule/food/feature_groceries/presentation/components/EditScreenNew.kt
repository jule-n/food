package com.jule.food.feature_groceries.presentation.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.tooling.LocalCompositionErrorContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.R
import com.jule.food.data.isCategoryNameTooLong
import com.jule.food.feature_groceries.domain.GroceryListUI
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.utils.DeleteDialog
import com.jule.food.utils.FilledIconButtonWithTooltip
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.utils.SheetErrorMessage
import com.jule.food.utils.conditional
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.ReorderableListState
import sh.calvin.reorderable.ReorderableScope
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.abs
import kotlin.math.exp

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun <T> EditScreenNew(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    reorderableListState: ReorderableLazyListState,
    items: List<T>,
    key: (T) -> Any,
    itemComposable: @Composable (ReorderableCollectionItemScope.(Int, T) -> Unit),
    newButtonText: String,
    onPressNewButton: () -> Unit,
    newButtonBackgroundColor: Color
) {
    val focusManager = LocalFocusManager.current

    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .padding(top = 20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus(true)
            }
            .fillMaxHeight(),
    ) {
        itemsIndexed(items, key = { _, item -> key(item) }) { index, item ->
            ReorderableItem(
                state = reorderableListState,
                key = key(item)
            ) {
                itemComposable(index, item)
            }
        }
        item {
            Button(
                onClick = onPressNewButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = newButtonBackgroundColor,
                    contentColor = MaterialTheme.colorScheme.contentColorFor(newButtonBackgroundColor)
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .animateItem()
            ) {
                Icon(painterResource(R.drawable.add), contentDescription = null)
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(newButtonText)
            }
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
    isError: (T, String) -> Boolean,
    errorText: (T, String) -> String?,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier,
    itemOutlineColor: Color? = null,
    isDeleteEnabled: Boolean = true,
    prefixElement: @Composable (() -> Unit)? = null,
    subtitleElement: @Composable (() -> Unit)? = null,
    underElement: @Composable (() -> Unit)? = null,
    textPrefix: @Composable (() -> Unit)? = null,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(20)
) {
    val focusManager = LocalFocusManager.current
    val hapticFeedback = LocalHapticFeedback.current

    var isFocused by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var offset by remember { mutableStateOf(0f) }

    var totalDrag by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    var animatingOffsetTarget by remember { mutableStateOf(0f) }
    val animatingOffset by animateFloatAsState(targetValue = if (isAnimating) animatingOffsetTarget else offset, finishedListener = {
        isAnimating = false
    })


    val widthDelButton = 80.dp
    val k = 0.01f

    val maxOffset = with (density) { widthDelButton.toPx() }
    val threshold = 0.7*maxOffset
    fun calcOffsetFromDrag(): Float {
        // y = L/(e^(-k*(x-x0)))
        // Sigmoidal function
        return -maxOffset * ((1-exp(-k * -totalDrag))/(1+exp(-k * -totalDrag)))
    }


    Column(modifier = modifier) {
        Box {
            Surface(
                color = MaterialTheme.colorScheme.error,
                shape = shape,
                modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 10.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(80.dp)
                            .align(Alignment.CenterEnd),
                        shape = RoundedCornerShape(20),
                        color = Color.Transparent,
                        onClick = onClickDelete
                    ) {
                        Box(modifier = Modifier.align(Alignment.Center).size(24.dp)) {
                            Icon(
                                painterResource(R.drawable.delete),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
            Surface(
                color = itemBackgroundColor,
                shape = shape,
                modifier = Modifier
                    .conditional(sharedElementModifier != null) {
                        sharedElementModifier!!
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(70.dp)
                    .conditional(itemOutlineColor != null) {
                        Modifier.border(BorderStroke(1.dp, itemOutlineColor!!))
                    }.draggable(
                        state = rememberDraggableState { delta ->
                            if (!isDeleteEnabled || isAnimating) return@rememberDraggableState
                            if (totalDrag + delta > 0) {
                                totalDrag = 0f
                                offset = 0f
                            } else {
                                totalDrag += delta
                                offset = calcOffsetFromDrag()
                            }

                        },
                        orientation = Orientation.Horizontal,
                        onDragStopped = {
                            if (abs(offset) < threshold) {
                                // Not enough, animate back
                                isAnimating = true
                                animatingOffsetTarget = 0f
                                offset = 0f
                                totalDrag = 0f
                            } else {
//                                // Enough offset, animate all the way
                                isAnimating = true
                                animatingOffsetTarget = -maxOffset
                                offset = -maxOffset
                                totalDrag = -5.3f / k
                            }
                        }
                    ).offset {
                        IntOffset(
                            x = if (isAnimating) animatingOffset.toInt() else offset.toInt(),
                            y = 0
                        )
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    prefixElement?.invoke()
                    Column(modifier = Modifier.weight(1f)) {
                        BasicTextFieldWithBox(
                            state = textState,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            textStyle = ButtonDefaults.textStyleFor(40.dp),
                            textColor = MaterialTheme.colorScheme.contentColorFor(
                                itemBackgroundColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    isFocused = focusState.isFocused
                                }
                                .height(40.dp)
                                .padding(start = 10.dp),
                            onKeyboardAction = { focusManager.clearFocus() },
                            prefix = textPrefix,
                            colors = TextFieldDefaults.colors().copy(
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.contentColorFor(
                                    itemBackgroundColor
                                ),
                                focusedIndicatorColor = MaterialTheme.colorScheme.contentColorFor(
                                    itemBackgroundColor
                                ),
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                        )
                        subtitleElement?.invoke()
                    }
                    // Checkmark icon when text is being edited
                    AnimatedVisibility(isFocused) {
                        IconButtonWithTooltip(
                            onClick = { focusManager.clearFocus(true) },
                            tooltipText = stringResource(R.string.done)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.done),
                                contentDescription = stringResource(R.string.done)
                            )
                        }

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

//@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//fun <T> ReorderableCollectionItemScope.EditScreenItemNew(
//    item: T,
//    textState: TextFieldState,
//    itemBackgroundColor: Color,
//    sharedElementModifier: Modifier?,
//    onDispose: (T, newName: String) -> Unit,
//    isError: (T, String) -> Boolean,
//    errorText: (T, String) -> String?,
//    onClickDelete: () -> Unit,
//    modifier: Modifier = Modifier,
//    itemOutlineColor: Color? = null,
//    isDeleteEnabled: Boolean = true,
//    prefixElement: @Composable (() -> Unit)? = null,
//    subtitleElement: @Composable (() -> Unit)? = null,
//    underElement: @Composable (() -> Unit)? = null,
//    textPrefix: @Composable (() -> Unit)? = null,
//    actionButtons: @Composable (RowScope.() -> Unit)? = null,
//    shape: Shape = RoundedCornerShape(20)
//) {
//    val focusManager = LocalFocusManager.current
//    val hapticFeedback = LocalHapticFeedback.current
//
//    var isFocused by remember { mutableStateOf(false) }
//
//    Column(modifier = modifier) {
//        Surface(
//            color = itemBackgroundColor,
//            shape = shape,
//            modifier = Modifier.conditional(sharedElementModifier != null) {
//                sharedElementModifier!!
//            }
//            .fillMaxWidth()
//            .padding(horizontal = 10.dp)
//            .height(48.dp)
//            .conditional(itemOutlineColor != null) {
//                Modifier.border(BorderStroke(1.dp, itemOutlineColor!!))
//            }
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
//                prefixElement?.invoke()
//                Column(modifier = Modifier.weight(1f)) {
//                    BasicTextFieldWithBox(
//                        state = textState,
//                        lineLimits = TextFieldLineLimits.SingleLine,
//                        textStyle = ButtonDefaults.textStyleFor(40.dp),
//                        textColor = MaterialTheme.colorScheme.contentColorFor(itemBackgroundColor),
//                        modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
//                            isFocused = focusState.isFocused
//                        }.height(48.dp).padding(start = 10.dp),
//                        onKeyboardAction = { focusManager.clearFocus() },
//                        prefix = textPrefix
//                    )
//                    subtitleElement?.invoke()
//                }
//                // Checkmark icon when text is being edited
//                AnimatedVisibility(isFocused) {
//                    IconButtonWithTooltip(
//                        onClick = { focusManager.clearFocus(true) },
//                        tooltipText = stringResource(R.string.done)
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.done),
//                            contentDescription = stringResource(R.string.done)
//                        )
//                    }
//
//                }
//                if (actionButtons != null) {
//                    actionButtons()
//                }
//                IconButtonWithTooltip(
//                    onClick = {},
//                    tooltipText = stringResource(R.string.reorder_categories),
//                    modifier = Modifier.draggableHandle(
//                        onDragStarted = {
//                            hapticFeedback.performHapticFeedback(
//                                HapticFeedbackType.GestureThresholdActivate
//                            )
//                        },
//                        onDragStopped = {
//                            hapticFeedback.performHapticFeedback(
//                                HapticFeedbackType.GestureEnd
//                            )
//                        }
//                    )
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.drag_handle),
//                        contentDescription = stringResource(R.string.reorder_categories)
//                    )
//                }
//                FilledIconButtonWithTooltip(
//                    onClick = onClickDelete,
//                    tooltipText = stringResource(R.string.delete),
//    //                                enabled = deleteEnabled,
//                    //                                shapes = IconButtonDefaults.shapes(),
//                    colors = IconButtonDefaults.filledIconButtonColors(
//                        containerColor = MaterialTheme.colorScheme.onError,
//                        contentColor = MaterialTheme.colorScheme.error
//                    ),
//                    enabled = isDeleteEnabled
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.delete),
//                        contentDescription = stringResource(R.string.delete)
//                    )
//                }
//            }
//        }
//        underElement?.invoke()
//
//        SheetErrorMessage(
//            isError = isError(item, textState.text.trim().toString()),
//            message = errorText(item, textState.text.trim().toString()) ?: "NULL"
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun EditScreenItemNewPreview() {
    val state = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(state, onMove = { _, _ -> })

    LazyColumn(state = state) {
        item {
            ReorderableItem(
                state = reorderState,
                key = 123
            ) {
                val item = GroceryListUI(currentName = "My Item", id = 0)
                EditScreenItemNew(
                    item = item,
                    textState = TextFieldState(item.currentName),
                    itemBackgroundColor = MaterialTheme.colorScheme.tertiary,
                    sharedElementModifier = null,
                    isError = { _, _ -> false },
                    errorText = { _, _ -> null },
                    onClickDelete = { }
                )
            }
        }
    }
}