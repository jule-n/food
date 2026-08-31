package com.jule.food.feature_groceries.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jule.food.R
import com.jule.food.feature_groceries.domain.GroceryListPresentation
import com.jule.food.others.ErrorType
import com.jule.food.others.getLabelFromErrorType
import com.jule.food.ui.groceries_recipes.EditScreen
import com.jule.food.ui.groceries_recipes.EditScreenItem
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.utils.SheetErrorMessage
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ListEditScreen(
    lists: List<GroceryListPresentation>,
    addListNameState: TextFieldState,
    isAddListError: Boolean,
    addListErrorType: ErrorType?,
    onAddNewList: (String) -> Unit,
    onDeleteList: (Int) -> Unit,
    onReorderLists: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val motionScheme = MaterialTheme.motionScheme
    val resources = LocalResources.current

    val deleteEnabled = lists.size > 1
    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onReorderLists(from.index, to.index)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    var showAddListSheet by remember { mutableStateOf(false) }
    val addListFocusRequester = remember { FocusRequester() }
    var listToDelete: GroceryListPresentation? by remember { mutableStateOf(null) }

    with (LocalSharedTransitionScope.current!!) {
        EditScreenNew<GroceryListPresentation>(
            modifier = modifier,
            lazyListState = lazyListState,
            reorderableListState = reorderableListState,
            items = lists,
            key = { it.id },
            itemName = { it.nameState.text.toString() },
            itemComposable = { item ->
                EditScreenItemNew(
                    item = item,
                    textState = item.nameState,
                    itemBackgroundColor = MaterialTheme.colorScheme.tertiary,
                    sharedElementModifier = Modifier.sharedElement(rememberSharedContentState(item.id), LocalNavAnimatedVisibilityScope.current!!),
                    onDispose = { item, itemName ->
//                        if(isCategoryNameTooLong(itemName) ||
//                            itemName.isEmpty() ||
//                            allCategories.filter { it.id != item.id }.any { it.name == itemName}) {
//                            return@EditScreenItem
//                        }
//                        onChangeCategoryName(itemName, item.id)
                    },
                    isError = { item, itemName ->
                        item.isNameError
//                        isCategoryNameTooLong(itemName) ||
//                                itemName.isEmpty() ||
//                                allCategories.filter { it.id != item.id }.any { it.name == itemName}
                    },
                    errorText = { item, itemName ->
                        return@EditScreenItemNew when (item.nameErrorType) {
                            is ErrorType.IsEmpty -> resources.getString(R.string.name_empty)
                            is ErrorType.TooLong -> resources.getString(R.string.name_too_long, item.nameErrorType.maxLength)
                            is ErrorType.NameSame -> resources.getString(R.string.name_already_exists)
                            null -> null
                        }

                    },
                    onClickDelete = {
                        listToDelete = item
                    }
                )
            },
            newButtonText = stringResource(R.string.new_category),
            onPressNewButton = { showAddListSheet = true },
            newButtonBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            onDelete = { onDeleteList(it.id) },
            confirmDeleteDialogTitle = stringResource(R.string.delete_category),
            onDeleteToastText = { resources.getString(R.string.deleted_category_name, it.nameState.text.toString()) },
            itemToDelete = listToDelete,
            onResetItemToDelete = { listToDelete = null }
        )
//        LazyColumn(
//            state = lazyListState,
//            verticalArrangement = Arrangement.spacedBy(20.dp),
//            modifier = modifier
//                .padding(top = 20.dp)
//                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
//                    focusManager.clearFocus(true)
//                }
//                .fillMaxHeight()
//            ,
//        ) {
//            items(allCategories, key = { it.id }) { category ->
//
//                val textState = rememberTextFieldState(category.name)
//                val isCategoryNameEmpty = textState.text.isEmpty()
//                val isCategoryNameTooLong = isCategoryNameTooLong(textState.text.toString())
//                val isCategoryNameSame = allCategories.filter { it.id != category.id }.any { it.name == textState.text.trim().toString() }
//
//                DisposableEffect(Unit) {
//                    Log.d("CategoriesEditScreen", "onDispose!")
//                    onDispose {
//                        if (!isCategoryNameEmpty && !isCategoryNameTooLong && !isCategoryNameSame)
//                            onChangeCategoryName(textState.text.trim().toString(), category.id)
//                    }
//                }
//
//                ReorderableItem(
//                    state = reorderableListState,
//                    key = category.id
//                ) {
//                    Column() {
//
//                        Surface(
//                            color = MaterialTheme.colorScheme.tertiary,
//                            shape = RoundedCornerShape(20),
//                            modifier = Modifier.sharedElement(
//                                sharedContentState = rememberSharedContentState(category.id),
//                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!,
//                                boundsTransform = { _, _ ->
//                                    motionScheme.slowSpatialSpec()
//                                }
//                            )
//                                .fillMaxWidth()
//                                .padding(horizontal = 10.dp)
//                                .height(48.dp)
//                        ) {
//                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize().padding(start = 10.dp)) {
//                                BasicTextFieldWithBox(
//                                    state = textState,
//                                    lineLimits = TextFieldLineLimits.SingleLine,
//                                    textStyle = ButtonDefaults.textStyleFor(40.dp),
//                                    textColor = MaterialTheme.colorScheme.onTertiary,
//                                    modifier = Modifier.weight(1f).onFocusChanged { focusState ->
//                                        if (!focusState.isFocused && (isCategoryNameEmpty || isCategoryNameTooLong || isCategoryNameSame)) {
//                                            textState.setTextAndPlaceCursorAtEnd(category.name)
//                                        }
//                                    },
//                                    onKeyboardAction = { focusManager.clearFocus() }
//                                )
//                                IconButtonWithTooltip(
//                                    onClick = {},
//                                    tooltipText = stringResource(R.string.reorder_categories),
//                                    modifier = Modifier.draggableHandle(
//                                        onDragStarted = {
//                                            hapticFeedback.performHapticFeedback(
//                                                HapticFeedbackType.GestureThresholdActivate
//                                            )
//                                        },
//                                        onDragStopped = {
//                                            hapticFeedback.performHapticFeedback(
//                                                HapticFeedbackType.GestureEnd
//                                            )
//                                        }
//                                    )
//                                ) {
//                                    Icon(
//                                        painter = painterResource(R.drawable.drag_handle),
//                                        contentDescription = stringResource(R.string.reorder_categories)
//                                    )
//                                }
//                                FilledIconButtonWithTooltip(
//                                    onClick = {
//                                        categoryIdToDelete = category.id
//                                        showConfirmDeleteCategoryDialog = true
//                                    },
//                                    tooltipText = stringResource(R.string.delete),
//                                    enabled = deleteEnabled,
////                                shapes = IconButtonDefaults.shapes(),
//                                    colors = IconButtonDefaults.filledIconButtonColors(
//                                        containerColor = MaterialTheme.colorScheme.onError,
//                                        contentColor = MaterialTheme.colorScheme.error
//                                    )
//                                ) {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.delete),
//                                        contentDescription = stringResource(R.string.delete)
//                                    )
//                                }
//                            }
//                        }
//
//                        SheetErrorMessage(
//                            isError = isCategoryNameEmpty || isCategoryNameTooLong || isCategoryNameSame,
//                            message = if (isCategoryNameTooLong) stringResource(
//                                R.string.name_too_long,
//                                40
//                            ) else
//                                if (isCategoryNameEmpty) stringResource(R.string.name_empty) else
//                                    if (isCategoryNameSame) stringResource(R.string.name_already_exists) else ""
//                        )
//                    }
//                }
//            }
//            item {
//                Button(
//                    onClick = { showAddCategorySheet = true },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
//                    ),
//                    modifier = Modifier.padding(start = 10.dp).animateItem()
//                ) {
//                    Icon(painterResource(R.drawable.add), contentDescription = null)
//                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
//                    Text(stringResource(id = R.string.new_category))
//                }
//            }
//        }
    }

    LaunchedEffect(showAddListSheet) {
        if (showAddListSheet) {
            addListFocusRequester.requestFocus()
        }
    }

    if (showAddListSheet) {
        // Don't show it is empty for the first time it is empty, only show it after text has been entered for the first time
        var showEmptyListError by remember { mutableStateOf(false) }
        LaunchedEffect(addListNameState.text) {
            if (!showEmptyListError && addListNameState.text.isNotEmpty()) {
                showEmptyListError = true
            }
        }
        ModalBottomSheet(
            onDismissRequest = { showAddListSheet = false },
            dragHandle = null
        ) {

            BasicTextFieldWithBox(
                state = addListNameState,
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().focusRequester(addListFocusRequester),
                placeholder = {
                    Text(
                        text = "${stringResource(R.string.new_category)}...",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                },
                contentPadding = PaddingValues(15.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                onKeyboardAction = {
                    if (!isAddListError) {
                        onAddNewList(addListNameState.text.trim().toString())
                        showAddListSheet = false
                    }
                }
            )
            SheetErrorMessage(
                isError = isAddListError,
                message = getLabelFromErrorType(addListErrorType!!, resources)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    enabled = !isAddListError,
                    onClick = {
                        onAddNewList(addListNameState.text.trim().toString())
                        showAddListSheet = false
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }

//    if (showConfirmDeleteCategoryDialog && categoryIdToDelete != null) {
//        val deletedCategoryName = allCategories.fastFirstOrNull { it.id == categoryIdToDelete }?.name ?: ""
//        DeleteDialog(
//            title = stringResource(R.string.delete_category),
//            onDismissRequest = {
//                showConfirmDeleteCategoryDialog = false
//                categoryIdToDelete = null
//            },
//            onConfirm = {
//                Toast.makeText(
//                    context,
//                    resources.getString(R.string.deleted_category_name, deletedCategoryName),
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                onDeleteCategory(categoryIdToDelete!!)
//                showConfirmDeleteCategoryDialog = false
//                categoryIdToDelete = null
//            }
//        ) {
//            Text(
//                stringResource(
//                    R.string.are_you_sure_you_want_to_delete_category_name,
//                    deletedCategoryName
//                )
//            )
//        }
//    }
}