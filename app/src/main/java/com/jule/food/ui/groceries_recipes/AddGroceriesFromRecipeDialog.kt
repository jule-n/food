package com.jule.food.ui.groceries_recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jule.food.R
import com.jule.food.ui.settings.SettingsScreenCategory
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.Recipe
import com.jule.food.ui.groceries.CategorySelectionButtons
import com.jule.food.ui.groceries.GroceryItemDisplay
import com.jule.food.ui.settings.SettingDialog
import com.jule.food.utils.DefaultDialog
import java.util.UUID

enum class GroceryListAddingOption {
    OnlyNoList, AllGroceries
}

val groceryListAddingOptionLabels = listOf(
    R.string.only_no_list,
    R.string.all_groceries
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroceriesFromRecipeDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    recipe: Recipe,
    includeCategoryChoice: Boolean,
    groceryCategories: List<GroceryItemCategory>?,
    firstSelectedCategoryId: UUID? = null,
    onConfirm: (List<GroceryItem>, GroceryListAddingOption, UUID?) -> Unit,
) {
    val selectedItems = remember { recipe.groceries.toMutableStateList() }
    var chosenCategoryId: UUID? by remember { mutableStateOf(firstSelectedCategoryId ?: groceryCategories?.get(0)?.id) }
    var selectedOption by remember { mutableStateOf(GroceryListAddingOption.OnlyNoList) }
    val selectedOptionLabel = stringResource(groceryListAddingOptionLabels[selectedOption.ordinal])

    DefaultDialog(
        title = stringResource(R.string.add_groceries_to_cart),
        onDismissRequest = onDismissRequest,
        buttons = true,
        onConfirm = {
            onConfirm(
                selectedItems,
                selectedOption,
                if (includeCategoryChoice && groceryCategories != null) chosenCategoryId!! else null
            )
        },
        modifier = modifier
    ) {
        SettingsScreenCategory(
            name = stringResource(R.string.recipe),
            modifier = Modifier.fillMaxWidth(),
            textStartPadding = 0.dp
        ) {
            Text(recipe.name, style = MaterialTheme.typography.titleMedium)
        }
        if (includeCategoryChoice && groceryCategories != null) {
            var showDialog by remember { mutableStateOf(false) }

            Column(verticalArrangement = Arrangement.Top) {
                Surface(
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(10),
                    modifier = Modifier.height(30.dp)
                ) {
                    Column(modifier = Modifier.height(30.dp), verticalArrangement = Arrangement.Center) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Icon(
                                painterResource(R.drawable.arrow_left),
                                contentDescription = null,
                                modifier = Modifier.rotate(270f).size(16.dp)
                            )
                            Text(
                                "$selectedOptionLabel ->",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (showDialog) {
                        DropdownMenu(
                            expanded = showDialog,
                            onDismissRequest = { showDialog = false }
                        ) {
                            GroceryListAddingOption.entries.forEachIndexed { index, option ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedOption = option
                                        showDialog = false
                                    },
                                    text = {
                                        Text(stringResource(groceryListAddingOptionLabels[index]))
                                    }
                                )
                            }
                        }
                    }
                }
                CategorySelectionButtons(
                    groceryCategories = groceryCategories,
                    selectedCategoryId = chosenCategoryId!!,
                    onChangeSelectedCategoryId = { chosenCategoryId = it },
                    showBadge = false
                )
            }
        }
//        if (includeCategoryChoice && groceryCategories != null) {
//            SettingsScreenCategory(
//                name = stringResource(R.string.category),
//                textStartPadding = 0.dp,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                CategorySelectionButtons(
//                    groceryCategories = groceryCategories,
//                    selectedCategoryId = chosenCategoryId!!,
//                    onChangeSelectedCategoryId = { chosenCategoryId = it },
//                    showBadge = false
//                )
//            }
//        }
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
                items(
                    recipe.groceries.sortedBy { it.name },
                    key = { item -> item.id }) { groceryItem ->
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