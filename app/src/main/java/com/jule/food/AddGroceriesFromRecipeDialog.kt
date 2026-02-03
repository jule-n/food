package com.jule.food

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun AddGroceriesFromRecipeDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    recipe: Recipe,
    includeCategoryChoice: Boolean,
    groceryCategories: List<GroceryItemCategory>?,
    onConfirm: (List<GroceryItem>, UUID?) -> Unit,
) {
    val selectedItems = remember { recipe.groceries.toMutableStateList() }
    var chosenCategoryId: UUID? by remember { mutableStateOf(groceryCategories?.get(0)?.id) }

    DefaultDialog(
        title = stringResource(R.string.add_groceries_to_cart),
        onDismissRequest = onDismissRequest,
        buttons = true,
        onConfirm = {
            onConfirm(selectedItems, if (includeCategoryChoice && groceryCategories != null) chosenCategoryId!! else null)
        },
        modifier = modifier
    ) {
        SettingsScreenCategory(name = stringResource(R.string.recipe), modifier = Modifier.fillMaxWidth(), textStartPadding = 0.dp) {
            Text(recipe.name, style = MaterialTheme.typography.titleMedium)
        }
        if (includeCategoryChoice && groceryCategories != null) {
            SettingsScreenCategory(
                name = stringResource(R.string.category),
                textStartPadding = 0.dp
            ) {
                CategorySelectionButtons(
                    groceryCategories = groceryCategories,
                    selectedCategoryId = chosenCategoryId!!,
                    onChangeSelectedCategoryId = { chosenCategoryId = it },
                    showBadge = false
                )
            }
        }
        SettingsScreenCategory(
            name = stringResource(R.string.groceries),
            textStartPadding = 0.dp
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(70.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(recipe.groceries.sortedBy { it.name }, key = { item -> item.id }) { groceryItem ->
                    val isSelected = selectedItems.contains(groceryItem)

                    GroceryItemDisplay(
                        item = groceryItem,
                        onClick = {
                            if (isSelected) {
                                selectedItems.remove(groceryItem)
                            } else {
                                selectedItems.add(groceryItem)
                            }
                        },
                        onLongClick = { },
                        center = true,
                        modifier = Modifier.animateItem(),
                        showSelection = true,
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}