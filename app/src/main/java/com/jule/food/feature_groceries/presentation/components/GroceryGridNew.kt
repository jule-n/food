package com.jule.food.feature_groceries.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.R
import com.jule.food.feature_groceries.domain.GroceryItemPresentation
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent
import com.jule.food.feature_groceries.presentation.GroceryScreenState
import com.jule.food.ui.groceries.gridGroupTitle
import com.jule.food.ui.groceries.gridSpacer
import java.util.UUID
import kotlin.collections.map

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroceryGridNew(
    state: GroceryScreenState,
    onEvent: (GroceryScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
//    var lastDeletedItemAlpha: Float? by remember { mutableStateOf(null) }
//
//    // Prevent flashing of last deleted item when changing categories
//    LaunchedEffect(category) {
//        lastDeletedItemAlpha = 0f
//        delay(1000)
//        lastDeletedItemAlpha = null
//    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        modifier = modifier
    ) {
        if (state.activeItemsInCurrentList.isEmpty() && state.finishedItemsInCurrentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    stringResource(R.string.all_done),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.displaySmallEmphasized
                )
            }
        }
        val spacerHeight = if (state.activeItemsInCurrentList.isEmpty() && state.finishedItemsInCurrentList.isEmpty()) 0.dp else 10.dp
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = spacerHeight, bottom = 200.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 1000.dp),
            state = state.selectedList!!.gridState
        ) {
            val groupNames: MutableList<String> = mutableListOf()
            val groups: MutableList<List<GroceryItemPresentation>> = mutableListOf()
            var recipeIds: List<UUID?> = listOf()

            when (state.groupingOption) {
                GroceryGroupingOption.None -> {
                    groupNames.add("")
                    groups.add(state.activeItemsInCurrentList)
                }

                GroceryGroupingOption.Recipe -> {
                    groupNames.add("RECIPE")
                    groups.add(state.activeItemsInCurrentList)
//                    val recipeGroups = state.itemsInCurrentList.groupBy { it.recipeId }.toSortedMap(compareBy { it.value })
//                    groupNames.addAll(recipeGroups.keys.map { recipeId ->
//                        if (recipeId != null) getRecipeNameFromId(recipeId) else resources.getString(
//                            R.string.no_recipe)
//                    })
//                    recipeIds = recipeGroups.keys.toList()
//                    groups.addAll(recipeGroups.values)
                }

                GroceryGroupingOption.Location -> {
                    val locationGroups = state.activeItemsInCurrentList.groupBy { it.locationName }.toSortedMap(compareBy { it })
                    groups.addAll(locationGroups.values)
                    groupNames.addAll(locationGroups.keys)
//                    recipeIds = locationGroups.keys.toList()
                }
            }

//                        if (showDeletedItems && deletedGroceryItems.isNotEmpty()) {
//                            groupNames.add(context.getString(R.string.deleted))
//                            groups.add(deletedGroceryItems)
//                        }
            if (!(groupNames.count() == 1 && groups[0].isEmpty())) {
                groups.forEachIndexed { index, groceryItems ->
                    if(state.groupingOption != GroceryGroupingOption.None) {
                        val isAllSelected = state.selectedItemIds.containsAll(groceryItems.map { it.id })
                        gridGroupTitle(
                            title = groupNames[index],
                            key = groupNames[index],
                            animate = true,
                            showMoveHereButton = false,
//                                showMoveHereButton = (groupingOption == GroceryGroupingOption.Recipe || groupingOption == GroceryGroupingOption.Location) && selectionModeActive && !selectedGroceryItems.any { selectedItem -> groceryItems.any { it.id == selectedItem } },
//                                onMoveHere = {
//                                    if (groupingOption == GroceryGroupingOption.Recipe)
//                                        onChangeRecipeIdGroceries(
//                                            selectedGroceryItems,
//                                            recipeIds[index]
//                                        )
//                                    else if (groupingOption == GroceryGroupingOption.Location)
//                                        onChangeLocationIdGroceries(
//                                            selectedGroceryItems,
//                                            recipeIds[index]
//                                        )
//                                },
                            onMoveHere = { },
                            isAllSelected = isAllSelected,
                            onSelectAll = {
//                                if (isAllSelected) {
//                                    // All are selected, deselect all
//                                    groceryItems.forEach { item ->
//                                        onRemoveFromSelection(item.id)
//                                    }
//                                } else {
//                                    // Not all are selected, select all that are not selected yet
//                                    groceryItems.filter { !selectedGroceryItems.contains(it.id) }
//                                        .forEach { item ->
//                                            onAddToSelection(item.id)
//                                        }
//                                }
                            },
                            showSelectAllButton = state.isSelectionModeActive
                        )
                    }
                    items(
                        groceryItems,
                        key = { groceryItem -> groceryItem.id }
                    ) { groceryItem ->
                        GroceryItemDisplayNew(
                            item = groceryItem,
                            onClick = {
                                if (state.isSelectionModeActive) {
                                    onEvent(GroceryScreenEvent.ItemEvent.ToggleItemIdSelection(groceryItem.id))
                                } else {
                                    onEvent(GroceryScreenEvent.ItemEvent.FinishItem(groceryItem.id))
                                }
                            },
                            onLongClick = {
                                if (!state.isSelectionModeActive) {
                                    onEvent(GroceryScreenEvent.ItemEvent.ChangeIsSelectionModeActive(true))
                                    onEvent(GroceryScreenEvent.ItemEvent.AddItemIdsToSelection(listOf(groceryItem.id)))
                                }
                            },
                            showRecipeName = state.groupingOption != GroceryGroupingOption.Recipe,
                            deleted = false,
                            scaleSelection = true,
                            isSelected = state.isSelectionModeActive && state.selectedItemIds.contains(groceryItem.id),
                            showSelection = state.isSelectionModeActive,
                            modifier = Modifier.animateItem()
                        )
                    }

                    gridSpacer(20.dp)
                }
            }
            item(
                key = 5092038540945087,
                span = { GridItemSpan(maxLineSpan) }
            ) {
                val alphaText by animateFloatAsState(if (state.finishedItemsInCurrentList.isEmpty() || !state.selectedList.showFinishedItems) 0.5f else 0.8f)
                val alphaBackground by animateFloatAsState(if (state.finishedItemsInCurrentList.isEmpty() || !state.selectedList.showFinishedItems) 0.1f else 0.2f)
                Box(modifier = Modifier.animateItem()) {
                    Row {
                        Surface(
                            onClick = { onEvent(GroceryScreenEvent.ListEvent.ChangeShowFinishedItems(!state.selectedList.showFinishedItems)) },
                            shape = RoundedCornerShape(20),
                            enabled = true,
                            color = Color.Transparent
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                            ) {
                                val degrees by animateFloatAsState(if (state.selectedList.showFinishedItems) 270f else 180f)
                                Spacer(Modifier.width(5.dp))
                                Icon(
                                    painter = painterResource(R.drawable.arrow_left),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText),
                                    modifier = Modifier.rotate(degrees = degrees).size(12.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaBackground),
                                    modifier = Modifier.height(20.dp).widthIn(min=20.dp)
                                ) {
                                    Box {
                                        Text(
                                            state.finishedItemsInCurrentList.size.toString(),
                                            modifier = Modifier.align(Alignment.Center),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = alphaText
                                            )
                                        )
                                    }
                                }
                                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                Text(
                                    stringResource(R.string.finished),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText),
                                    style = MaterialTheme.typography.labelLarge
                                )
//                                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
//                                Icon(
//                                    painterResource(R.drawable.done),
//                                    contentDescription = stringResource(R.string.show_finished_items),
//                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText)
//                                )
                            }
                        }
                        IconButtonWithTooltip(
                            onClick = {
                                onEvent(GroceryScreenEvent.ItemEvent.DeleteFinishedItems)
                            },
                            enabled = state.finishedItemsInCurrentList.isNotEmpty(),
                            tooltipText = stringResource(R.string.delete_finished_items)
                        ) {
                            Icon(
                                painterResource(R.drawable.delete),
                                contentDescription = stringResource(R.string.delete_finished_items),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText)
                            )
                        }
                    }
                }
            }
            if (state.selectedList.showFinishedItems) {
                items(
                    state.finishedItemsInCurrentList,
                    key = { it.id }
                ) { groceryItem ->
                    GroceryItemDisplayNew(
                        item = groceryItem,
                        onClick = {
                            if (!state.isSelectionModeActive) {
                                onEvent(GroceryScreenEvent.ItemEvent.RestoreFinishedItem(groceryItem.id))
                            }
                        },
                        onLongClick = null,
//                        getRecipeNameFromId = getRecipeNameFromId,
                        showRecipeName = true,
                        deleted = true,
                        isSelected = false,
                        showSelection = false,
                        modifier = Modifier.animateItem()
//                                        .scale(scaleX = scale, scaleY = scale)
                    )
                }
            }
//            else {
//                if (category.finishedItems.isNotEmpty()) {
//                    item(
//                        key = category.finishedItems.last().id
//                    ) {
//                        var loadedItem by remember { mutableStateOf(false) }
//                        val alpha by animateFloatAsState(targetValue = if (loadedItem) 0f else 1f)
//                        LaunchedEffect(Unit) {
//                            loadedItem = true
//                        }
//                        val groceryItem = category.finishedItems.last()
//                        GroceryItemDisplay(
//                            item = groceryItem,
//                            onClick = { },
//                            onLongClick = null,
//                            clickingEnabled = false,
//                            getRecipeNameFromId = getRecipeNameFromId,
//                            showRecipeName = groupingOption != GroceryGroupingOption.Recipe,
//                            deleted = true,
//                            isSelected = false,
//                            showSelection = false,
//                            modifier = Modifier.animateItem().alpha(lastDeletedItemAlpha ?: alpha)
//                        )
//                    }
//
//                }
//            }
        }
    }
}