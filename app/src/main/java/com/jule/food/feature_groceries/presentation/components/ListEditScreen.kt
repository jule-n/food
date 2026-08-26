package com.jule.food.feature_groceries.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
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
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.isCategoryNameTooLong
import com.jule.food.feature_groceries.domain.GroceryListNew
import com.jule.food.ui.groceries_recipes.EditScreen
import com.jule.food.ui.groceries_recipes.EditScreenItem
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.utils.SheetErrorMessage
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ListEditScreen(
    lists: List<GroceryListNew>,
    onAddNewCategory: (GroceryListNew) -> Unit,
    onDeleteCategory: (UUID) -> Unit,
    onChangeCategoryName: (newName: String, id: UUID) -> Unit,
    onReorderCategories: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val motionScheme = MaterialTheme.motionScheme
    val resources = LocalResources.current

    val deleteEnabled = allCategories.size > 1
    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onReorderCategories(from.index, to.index)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    var showAddCategorySheet by remember { mutableStateOf(false) }
    val addCategoryFocusRequester = remember { FocusRequester() }
    var showConfirmDeleteCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete: GroceryItemCategory? by remember { mutableStateOf(null) }

    with (LocalSharedTransitionScope.current!!) {
        EditScreen(
            lazyListState = lazyListState,
            reorderableListState = reorderableListState,
            items = allCategories,
            key = { it.id },
            itemName = { it.name },
            itemComposable = { item ->
                EditScreenItem(
                    item = item,
                    itemName = item.name,
                    itemBackgroundColor = MaterialTheme.colorScheme.tertiary,
                    sharedElementModifier = Modifier.sharedElement(rememberSharedContentState(item.id), LocalNavAnimatedVisibilityScope.current!!),
                    onDispose = { item, itemName ->
                        if(isCategoryNameTooLong(itemName) ||
                            itemName.isEmpty() ||
                            allCategories.filter { it.id != item.id }.any { it.name == itemName}) {
                            return@EditScreenItem
                        }
                        onChangeCategoryName(itemName, item.id)
                    },
                    isError = { item, itemName ->
                        isCategoryNameTooLong(itemName) ||
                                itemName.isEmpty() ||
                                allCategories.filter { it.id != item.id }.any { it.name == itemName}
                    },
                    errorText = { item, itemName ->
                        if (isCategoryNameTooLong(itemName)) {
                            return@EditScreenItem resources.getString(R.string.name_too_long, 40)
                        }
                        if (itemName.isEmpty()) {
                            return@EditScreenItem resources.getString(R.string.name_empty)
                        }
                        if (allCategories.filter { it.id != item.id }.any { it.name == itemName}) {
                            return@EditScreenItem resources.getString(R.string.name_already_exists)
                        }
                        return@EditScreenItem null

                    },
                    onClickDelete = {
                        categoryToDelete = item
                    }
                )
            },
            newButtonText = stringResource(R.string.new_category),
            onPressNewButton = { showAddCategorySheet = true },
            newButtonBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            onDelete = { onDeleteCategory(it.id) },
            confirmDeleteDialogTitle = stringResource(R.string.delete_category),
            onDeleteToastText = { resources.getString(R.string.deleted_category_name, it.name) },
            itemToDelete = categoryToDelete,
            onResetItemToDelete = { categoryToDelete = null }
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

    LaunchedEffect(showAddCategorySheet) {
        if (showAddCategorySheet) {
            addCategoryFocusRequester.requestFocus()
        }
    }

    if (showAddCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddCategorySheet = false },
            dragHandle = null
        ) {
            val textFieldState = rememberTextFieldState()

            var showEmptyCategoryError by remember { mutableStateOf(false) }
            val isCategoryNameEmpty = textFieldState.text.isEmpty()
            val isCategoryNameTooLong = isCategoryNameTooLong(textFieldState.text.toString())
            val isCategoryNameSame = allCategories.any { it.name == textFieldState.text.trim().toString() }

            if (!showEmptyCategoryError) {
                LaunchedEffect(textFieldState.text) {
                    if (textFieldState.text.isNotEmpty())
                        showEmptyCategoryError = true
                }
            }

            BasicTextFieldWithBox(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().focusRequester(addCategoryFocusRequester),
                placeholder = {
                    Text(
                        text = "${stringResource(R.string.new_category)}...",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                },
                contentPadding = PaddingValues(15.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                onKeyboardAction = {
                    if (!isCategoryNameEmpty && !isCategoryNameTooLong && !isCategoryNameSame) {
                        val name = textFieldState.text.trim().toString()
                        onAddNewCategory(GroceryItemCategory(name))
                        showAddCategorySheet = false
                    }
                }
            )
            SheetErrorMessage(
                isError = (showEmptyCategoryError && isCategoryNameEmpty) || isCategoryNameTooLong || isCategoryNameSame,
                message = if (isCategoryNameTooLong) stringResource(R.string.name_too_long, 40) else
                    if (isCategoryNameEmpty) stringResource(R.string.name_empty) else
                        if (isCategoryNameSame) stringResource(R.string.name_already_exists) else ""
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    enabled = !isCategoryNameEmpty && !isCategoryNameTooLong,
                    onClick = {
                        val name = textFieldState.text.trim().toString()
                        onAddNewCategory(GroceryItemCategory(name))
                        showAddCategorySheet = false
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