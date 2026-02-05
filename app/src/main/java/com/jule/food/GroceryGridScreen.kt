package com.jule.food

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jule.food.gridGroupTitle
import com.jule.food.gridSpacer
import java.util.UUID

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroceryGridScreen(
    modifier: Modifier = Modifier,
    category: GroceryItemCategory,
    groupingOption: GroceryGroupingOption,
    onRemoveFromGroceries: (itemIndex: Int) -> Unit,
    onAddToGroceries: (GroceryItem) -> Unit,
    getRecipeNameFromId: (UUID) -> String,
    selectionModeActive: Boolean,
    selectedGroceryItems: List<UUID>,
    onAddToSelection: (UUID) -> Unit,
    onRemoveFromSelection: (UUID) -> Unit,
    deletedItems: List<GroceryItem>,
    onAddToDeletedItems: (GroceryItem) -> Unit,
    onRemoveFromDeletedItems: (UUID) -> Unit,
    showDeletedItems: Boolean,
    onChangeShowDeletedItems: (Boolean) -> Unit
) {
    val resources = LocalResources.current
    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        modifier = modifier
    ) {
        if (category.items.isEmpty()) {
            Box(
                modifier = Modifier
//                                .padding(10.dp)
                    .fillMaxSize()
            ) {
                Text(
                    "All done!",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.displaySmallEmphasized
                )
            }
        }

        val spacerHeight by animateDpAsState(if (category.items.isEmpty()) 0.dp else 10.dp)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 1000.dp)
                .padding(start = 10.dp, end = 10.dp, top = spacerHeight)
        ) {
            val allItems = category.items

            val groupNames: MutableList<String> = mutableListOf()
            val groups: MutableList<List<GroceryItem>> = mutableListOf()
            when (groupingOption) {
                GroceryGroupingOption.None -> {
                    groupNames.add("")
                    groups.add(allItems)
                }

                GroceryGroupingOption.Recipe -> {
                    val recipeGroups = allItems.groupBy { it.recipeId }.toSortedMap(compareBy { it })
                    groupNames.addAll(recipeGroups.keys.map { recipeId ->
                        if (recipeId != null) getRecipeNameFromId(recipeId) else resources.getString(R.string.no_recipe)
                    })
                    groups.addAll(recipeGroups.values)
                }

                GroceryGroupingOption.Location -> {
                    groupNames.add("Location A")
                    groups.add(allItems)
                }
            }

//                        if (showDeletedItems && deletedGroceryItems.isNotEmpty()) {
//                            groupNames.add(context.getString(R.string.deleted))
//                            groups.add(deletedGroceryItems)
//                        }
            if (!(groupNames.count() == 1 && groups[0].isEmpty())) {
                groups.forEachIndexed { index, groceryItems ->
                    val isLast = index == groups.size - 1

                    if (index > 0 || groupingOption != GroceryGroupingOption.None) {
                        gridGroupTitle(
                            title = groupNames[index],
                            key = groupNames[index],
                            animate = true
                        )
                    }
                    items(
                        groceryItems,
                        key = { groceryItem -> groceryItem.id }
                    ) { groceryItem ->
                        GroceryItemDisplay(
                            item = groceryItem,
                            onClick = {
                                if (selectionModeActive) {
                                    if (selectedGroceryItems.contains(groceryItem.id))
                                        onRemoveFromSelection(groceryItem.id)
                                    else
                                        onAddToSelection(groceryItem.id)
                                } else {
                                    onRemoveFromGroceries(allItems.indexOf(groceryItem))
                                    onAddToDeletedItems(groceryItem)
                                }
                            },
                            onLongClick = {
                                if (!selectionModeActive) {
                                    onAddToSelection(groceryItem.id)
                                }
                            },
                            getRecipeNameFromId = getRecipeNameFromId,
                            showRecipeName = groupingOption != GroceryGroupingOption.Recipe,
                            deleted = false,
                            isSelected = selectedGroceryItems.contains(groceryItem.id),
                            showSelection = selectionModeActive,
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
                val alphaText by animateFloatAsState(if (deletedItems.isEmpty() || !showDeletedItems) 0.5f else 0.8f)
                val alphaBackground by animateFloatAsState(if (deletedItems.isEmpty() || !showDeletedItems) 0.1f else 0.2f)
                Box(modifier = Modifier.animateItem()) {
                    Surface(
                        onClick = { onChangeShowDeletedItems(!showDeletedItems) },
                        shape = RoundedCornerShape(20),
                        enabled = deletedItems.isNotEmpty(),
                        color = Color.Transparent
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaBackground),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box {
                                    Text(
                                        deletedItems.count().toString(),
                                        modifier = Modifier.align(Alignment.Center),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText)
                                    )
                                }
                            }
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                stringResource(R.string.deleted),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText),
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Icon(
                                painterResource(R.drawable.delete),
                                contentDescription = stringResource(R.string.show_deleted_items),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText)
                            )
                        }}
                }
            }
            if (showDeletedItems) {
                items(
                    deletedItems.reversed(),
                    key = { it.id }
                ) { groceryItem ->
                    GroceryItemDisplay(
                        item = groceryItem,
                        onClick = {
                            if (!selectionModeActive) {
                                onRemoveFromDeletedItems(groceryItem.id)
                                onAddToGroceries(groceryItem)
                            }
                        },
                        onLongClick = null,
                        getRecipeNameFromId = getRecipeNameFromId,
                        showRecipeName = true,
                        deleted = true,
                        isSelected = false,
                        showSelection = false,
                        modifier = Modifier.animateItem()
//                                        .scale(scaleX = scale, scaleY = scale)
                    )
                }
            } else {
                if (deletedItems.isNotEmpty()) {
                    item(
                        key = deletedItems.last().id
                    ) {
                        var loadedItem by remember { mutableStateOf(false) }
                        val alpha by animateFloatAsState(targetValue = if (loadedItem) 0f else 1f)
                        LaunchedEffect(Unit) {
                            loadedItem = true
                        }
                        val groceryItem = deletedItems.last()
                        GroceryItemDisplay(
                            item = groceryItem,
                            onClick = { },
                            onLongClick = null,
                            clickingEnabled = false,
                            getRecipeNameFromId = getRecipeNameFromId,
                            showRecipeName = groupingOption != GroceryGroupingOption.Recipe,
                            deleted = true,
                            isSelected = false,
                            showSelection = false,
                            modifier = Modifier.animateItem().alpha(alpha)
                        )
                    }

                }
            }
        }
    }
}