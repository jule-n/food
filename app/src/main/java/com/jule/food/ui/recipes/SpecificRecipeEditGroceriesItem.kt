package com.jule.food.ui.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.utils.FilledExpressiveIconButtonWithTooltip
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryLocation
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.R
import com.jule.food.data.GroceryItemCategory
import com.jule.food.ui.groceries.SelectCategoryDialog
import com.jule.food.ui.groceries_recipes.SelectEditLocationButtons
import com.jule.food.utils.conditional
import com.jule.food.ui.groceries.GroceryBottomSheetSelectionField
import com.jule.food.ui.groceries.items
import com.jule.food.ui.theme.FoodTheme
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReorderableCollectionItemScope.SpecificRecipeEditGroceriesItem(
    modifier: Modifier = Modifier,
    allLocations: List<GroceryLocation>,
    onAddLocation: (String) -> Unit,
    onRemoveLocation: (UUID) -> Unit,
    onChangeLocationName: (String, UUID) -> Unit,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit,
    getLocationNameFromId: (UUID) -> String,
    allCategories: List<GroceryItemCategory>,
    getCategoryNameFromId: (UUID) -> String,
    onDelete: () -> Unit,
    focusRequester: FocusRequester?,
    nameState: TextFieldState,
    detailState: TextFieldState,
    locationId: UUID?,
    onChangeLocationId: (UUID?) -> Unit,
    categoryId: UUID?,
    onChangeCategoryId: (UUID?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val hapticFeedback = LocalHapticFeedback.current
    val detailFocusRequester = remember { FocusRequester() }

    var showLocationSelection by remember { mutableStateOf(false) }
    var showCategorySelection by remember { mutableStateOf(false) }

    val itemColor = MaterialTheme.colorScheme.primary
    val contentColorTitle = MaterialTheme.colorScheme.onPrimary
    val placeholderColorTitle = contentColorTitle.copy(alpha = 0.5f)

    val contentColor = MaterialTheme.colorScheme.onSurface
    val placeholderColor = contentColor.copy(alpha = 0.5f)

    val prim1 = lerp(itemColor, Color.White, 0.1f)
    val prim2 = lerp(itemColor, Color.White, 0.3f)
    val brush = Brush.linearGradient(listOf(prim1, prim2))

    var isDetailsFocused by remember { mutableStateOf(false) }
    var openedDetailsWithoutClosing by remember { mutableStateOf(false) }
    val showDetails = isDetailsFocused || detailState.text.isNotEmpty() || openedDetailsWithoutClosing

    Column(
        modifier = modifier.background(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        shape = RoundedCornerShape(topStart = 22.5.dp, topEnd = 22.5.dp, bottomStart = 10.dp, bottomEnd = 10.dp)
    )) {
        Column (
            modifier = modifier.background(
                brush = brush,
                shape = RoundedCornerShape(50))
        ) {
            Row(
                modifier = Modifier.height(45.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextFieldWithBox(
                    state = nameState,
                    placeholder = {
                        Text(
                            "${stringResource(R.string.grocery_name)}...",
                            maxLines = 1,
                            color = placeholderColorTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    textColor = contentColorTitle,
                    textStyle = MaterialTheme.typography.titleMedium,
                    contentPadding = PaddingValues(10.dp),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    onKeyboardAction = KeyboardActionHandler {
                        focusManager.clearFocus(true)
                    },
                    modifier = Modifier
                        .fillMaxWidth().conditional(focusRequester != null) {
                            Modifier.focusRequester(focusRequester!!)
                        }
                        .weight(1f)
                )
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
                        tint = contentColorTitle,
                        contentDescription = stringResource(R.string.reorder_categories)
                    )
                }
                FilledExpressiveIconButtonWithTooltip(
                    onClick = onDelete,
                    tooltipText = stringResource(R.string.delete),
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        painterResource(R.drawable.delete),
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
        }
        
        Spacer(Modifier.height(5.dp))

        LaunchedEffect(openedDetailsWithoutClosing) {
            if (openedDetailsWithoutClosing) {
                detailFocusRequester.requestFocus()

            }
        }

        AnimatedVisibility(showDetails) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp).fillMaxWidth()
            ) {
                Icon(
                    painterResource(R.drawable.text),
                    contentDescription = null,
                    tint = if (detailState.text.isEmpty()) placeholderColor else contentColor
                )
                Spacer(Modifier.width(5.dp))
                BasicTextFieldWithBox(
                    state = detailState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(detailFocusRequester)
                        .onFocusChanged() { focusState ->
                            if (isDetailsFocused && openedDetailsWithoutClosing)
                                openedDetailsWithoutClosing = false
                            isDetailsFocused = focusState.isFocused
                        }
                        .padding(vertical = 5.dp),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    onKeyboardAction = KeyboardActionHandler {
                        focusManager.clearFocus(true)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    textColor = contentColor,
                    placeholder = {
                        Text(
                            stringResource(id = R.string.details),
                            maxLines = 1,
                            color = placeholderColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .horizontalScroll(state = rememberScrollState())
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 5.dp, bottom = 5.dp)
        ) {
            AnimatedVisibility(!showDetails,
                enter = fadeIn() + expandVertically(
                    expandFrom = Alignment.Bottom,
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ) + expandHorizontally(expandFrom = Alignment.End, animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()),
                exit = fadeOut() + shrinkVertically(
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ) + shrinkHorizontally (shrinkTowards = Alignment.End, animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()),
            ) {
                Surface(
                    onClick = {
                        openedDetailsWithoutClosing = true
                    },
                    modifier = Modifier.height(40.dp).padding(end=10.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.add),
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Icon(
                            painterResource(R.drawable.text),
                            contentDescription = null,
                            tint = contentColor
                        )
                        Spacer(Modifier.size(6.dp))
                    }
                }
            }
            GroceryBottomSheetSelectionField(
                text = if (locationId != null) getLocationNameFromId(locationId) else stringResource(
                    R.string.no_location
                ),
                icon = R.drawable.location,
                isActive = locationId != null,
                inactiveColor = contentColor.copy(alpha = 0.7f),
                inactiveIconColor = contentColor,
                activeColor = contentColor,
                backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                onClick = { showLocationSelection = true },
                onClear = { onChangeLocationId(null) }
            )
            Spacer(Modifier.width(10.dp))
            GroceryBottomSheetSelectionField(
                text = if (categoryId != null) getCategoryNameFromId(categoryId) else stringResource(
                    R.string.no_list
                ),
                icon = R.drawable.group_groceries,
                isActive = categoryId != null,
                inactiveColor = contentColor.copy(alpha = 0.7f),
                inactiveIconColor = contentColor,
                activeColor = contentColor,
                backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                onClick = { showCategorySelection = true },
                onClear = { onChangeCategoryId(null) }
            )
        }
        if (showLocationSelection) {
            Dialog(
                onDismissRequest = { showLocationSelection = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                SelectEditLocationButtons(
                    onCancel = { showLocationSelection = false },
                    allLocations = allLocations,
                    onAddLocation = onAddLocation,
                    onRemoveLocation = onRemoveLocation,
                    onChangeLocationName = onChangeLocationName,
                    onReorderLocations = onReorderLocations,
                    selectedLocation = locationId,
                    onSelectLocation = {
                        onChangeLocationId(it)
                        showLocationSelection = false
                    }
                )
            }
        }
        if (showCategorySelection) {
            SelectCategoryDialog(
                onDismissRequest = { showCategorySelection = false },
                categories = allCategories,
                selectedCategory = UUID.randomUUID(),
                onSelectCategory = {
                    onChangeCategoryId(it)
                    showCategorySelection = false
                }
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SpecificRecipeEditGroceriesItemPreview() {
    val listState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState = listState, onMove = { _, _ ->})
    FoodTheme {
        LazyColumn {
            item {
                ReorderableItem(state = reorderableListState, key = 1) {
                    SpecificRecipeEditGroceriesItem(
                        getLocationNameFromId = { it.toString() },
                        getCategoryNameFromId = { it.toString() },
                        allLocations = listOf(
                            GroceryLocation("Location1"),
                            GroceryLocation("Location2")
                        ),
                        onAddLocation = { },
                        onRemoveLocation = { },
                        onChangeLocationName = { _, _, -> },
                        onReorderLocations = { _, _ -> },
                        focusRequester = remember { FocusRequester() },
                        onDelete = { },
                        nameState = rememberTextFieldState(),
                        detailState = rememberTextFieldState(),
                        locationId = null,
                        onChangeLocationId = {},
                        categoryId = null,
                        onChangeCategoryId = {},
                        allCategories = listOf()
                    )
                }
            }
        }
    }
}