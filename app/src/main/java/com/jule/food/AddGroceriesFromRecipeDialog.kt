package com.jule.food

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    var chosenCategoryIndex: Int by remember { mutableIntStateOf(0) }

    DefaultDialog(
        title = stringResource(R.string.add_groceries_to_cart),
        onDismissRequest = onDismissRequest,
        buttons = true,
        onConfirm = {
            onConfirm(selectedItems, if (includeCategoryChoice) groceryCategories!![chosenCategoryIndex].id else null)
        },
        modifier = modifier
    ) {
        if (includeCategoryChoice) {
            SettingsScreenCategory(
                name = stringResource(R.string.category)
            ) {
                LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(groceryCategories!!) { index, category ->
                        val color by animateColorAsState(if (chosenCategoryIndex == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                        Surface(
                            onClick = { chosenCategoryIndex = index },
                            enabled = true,
                            color = color,
//                            color = if (chosenCategoryIndex == index) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(20)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(
                                    start = 10.dp,
                                    top = 5.dp,
                                    bottom = 5.dp
                                ).fillMaxWidth().height(40.dp)
                            ) {
                                Text(
                                    text = category.name,
                                    textAlign = TextAlign.Left,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
        SettingsScreenCategory(
            name = stringResource(R.string.groceries)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(70.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(items = recipe.groceries.sortedBy { it.name }, key = { _, item -> item.id }) {index, groceryItem ->
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