package com.jule.food

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.ui.theme.FoodTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CategoriesConnectedButtonsCustom(
    allCategories: List<GroceryItemCategory>,
    selectedCategoryId: UUID,
    onChangeSelectedCategoryId: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    with (LocalSharedTransitionScope.current!!) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            items(allCategories) { category ->
                val selected = category.id == selectedCategoryId
                val color by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant
                )
                val textColor by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                val shapeCornerRadius by animateIntAsState(
                    targetValue = if (selected) 50 else 20
                )
                Button(
                    onClick = { onChangeSelectedCategoryId(category.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color,
                        contentColor = textColor
                    ),
                    shapes = ButtonDefaults.shapes(shape = RoundedCornerShape(shapeCornerRadius), pressedShape = RoundedCornerShape(10)),
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(category.id),
                        animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                    ).height(40.dp)
                ) {
                    Text(category.name)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CategoriesEditScreen(
    allCategories: List<GroceryItemCategory>,
    onAddNewCategory: (GroceryItemCategory) -> Unit,
    onDeleteCategory: (UUID) -> Unit,
    onChangeCategoryName: (newName: String, id: UUID) -> Unit,
    onReorderCategories: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val deleteEnabled = allCategories.size > 1
    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onReorderCategories(from.index, to.index)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    var showAddCategorySheet by remember { mutableStateOf(false) }
    val addCategoryFocusRequester = remember { FocusRequester() }
    var showConfirmDeleteCategoryDialog by remember { mutableStateOf(false) }
    var categoryIdToDelete: UUID? by remember { mutableStateOf(null) }

    with (LocalSharedTransitionScope.current!!) {
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = modifier
                .padding(top = 20.dp)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    focusManager.clearFocus(true)
                }
                .fillMaxHeight()
            ,
        ) {
            items(allCategories, key = { it.id }) { category ->

                val textState = rememberTextFieldState(category.name)
                LaunchedEffect(textState.text) {
                    onChangeCategoryName(textState.text.trim().toString(), category.id)
                }

                ReorderableItem(
                    state = reorderableListState,
                    key = category.id
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(20),
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(category.id),
                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                        )
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize().padding(start = 10.dp)) {
                            BasicTextFieldWithBox(
                                state = textState,
                                lineLimits = TextFieldLineLimits.SingleLine,
                                textStyle = ButtonDefaults.textStyleFor(40.dp),
                                textColor = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButtonWithTooltip(
                                onClick = {},
                                tooltipText = stringResource(R.string.reorder_categories),
                                modifier = Modifier.draggableHandle(
                                    onDragStarted = { hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate) },
                                    onDragStopped = { hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd) }
                                )
                            ) {
                                Icon(painter = painterResource(R.drawable.drag_handle), contentDescription = stringResource(R.string.reorder_categories))
                            }
                            FilledIconButtonWithTooltip(
                                onClick = {
                                    categoryIdToDelete = category.id
                                    showConfirmDeleteCategoryDialog = true
                                },
                                tooltipText = stringResource(R.string.delete),
                                enabled = deleteEnabled,
//                                shapes = IconButtonDefaults.shapes(),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onError,
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(painter = painterResource(id = R.drawable.delete), contentDescription = stringResource(R.string.delete))
                            }
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = { showAddCategorySheet = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    modifier = Modifier.padding(start = 10.dp).animateItem()
                ) {
                    Text(stringResource(id = R.string.new_category))
                }
            }
        }
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
                placeholder = { Text(
                    text = "${stringResource(R.string.new_category)}...",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )},
                contentPadding = PaddingValues(15.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                onKeyboardAction = {
                    val name = textFieldState.text.trim().toString()
                    onAddNewCategory(GroceryItemCategory(name))
                    showAddCategorySheet = false
                }
            )
            SheetErrorMessage(
                isError = (showEmptyCategoryError && isCategoryNameEmpty) || isCategoryNameTooLong,
                message = if (isCategoryNameTooLong) stringResource(R.string.name_too_long, 40) else
                        if (isCategoryNameEmpty) stringResource(R.string.name_empty) else ""
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

    if (showConfirmDeleteCategoryDialog && categoryIdToDelete != null) {
        val deletedCategoryName = allCategories.fastFirstOrNull { it.id == categoryIdToDelete }?.name ?: ""
        DeleteDialog(
            title = stringResource(R.string.delete_category),
            onDismissRequest = {
                showConfirmDeleteCategoryDialog = false
                categoryIdToDelete = null
            },
            onConfirm = {
                Toast.makeText(context, context.getString(R.string.deleted_category_name, deletedCategoryName), Toast.LENGTH_SHORT).show()

                onDeleteCategory(categoryIdToDelete!!)
                showConfirmDeleteCategoryDialog = false
                categoryIdToDelete = null
            }
        ) {
            Text(stringResource(R.string.are_you_sure_you_want_to_delete_category_name, deletedCategoryName))
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun CategoriesConnectedButtonsCustomPreview() {
    val categories = remember { mutableStateListOf(
        GroceryItemCategory("Lebensmittel"),
        GroceryItemCategory("Drogerie"),
        GroceryItemCategory("Heute"),
        GroceryItemCategory("Deine Oma")
    )}
    var selectedCategoryId by remember { mutableStateOf(categories.first().id) }
    var editScreen by remember { mutableStateOf(true) }



    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            FoodTheme {
                Scaffold { innerPadding ->
                    AnimatedContent(
                        targetState = editScreen,
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { onEditScreen ->
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            Column {
                                TextButton(onClick = { editScreen = !editScreen} ) {
                                    Text("Toggle Edit Screen")
                                }
                                if (!onEditScreen) {
                                    CategoriesConnectedButtonsCustom(
                                        allCategories = categories,
                                        selectedCategoryId = selectedCategoryId,
                                        onChangeSelectedCategoryId = { selectedCategoryId = it }
                                    )
                                } else {
                                    CategoriesEditScreen(
                                        allCategories = categories,
                                        onDeleteCategory = {},
                                        onAddNewCategory = {},
                                        onChangeCategoryName = { _, _ -> },
                                        onReorderCategories = { fromIndex, toIndex ->
                                            categories.apply {
                                                add(toIndex, removeAt(fromIndex))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}