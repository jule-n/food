package com.jule.food

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EditGroceriesBottomSheet(
    allCategories: List<GroceryItemCategory>,
    category: GroceryItemCategory,
    allRecipes: List<Recipe>,
    editingGroceryItems: List<UUID>,
    onMoveItemsToCategory: (items: List<UUID>, fromCategoryId: UUID, toCategoryId: UUID) -> Unit,
    onFinishAction: () -> Unit
) {
    val singleItem = editingGroceryItems.size == 1
    var selectRecipeDialogActive by remember { mutableStateOf(false) }
    var selectCategoryDialogActive by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.height(170.dp)
    ) {
        AnimatedVisibility (singleItem, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically(), modifier = Modifier.weight(1f)) {
            Column(
                verticalArrangement = Arrangement.Top,
            ) {
                Text("Edit Name", modifier = Modifier.height(30.dp), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(5.dp))
                Text("Edit Details", modifier = Modifier.height(20.dp))
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(80.dp)
        ) {
            item {
                BottomSheetSmallAction(
                    onClick = {
                        selectRecipeDialogActive = true
                    },
                    icon = R.drawable.book,
                    label = "Move to recipe"
                )
            }
            item {
                BottomSheetSmallAction(
                    onClick = {
                        selectCategoryDialogActive = true
                    },
                    icon = R.drawable.arrow_right_full,
                    label = "Move to category"
                )
            }
        }
    }

    if (selectRecipeDialogActive) {
        SelectRecipeDialog(
            recipes = allRecipes,
            onDismissRequest = { selectRecipeDialogActive = false },
            onClickRecipe = { recipeId ->
                category.items.forEach { item ->
                    if (editingGroceryItems.contains(item.id)) {
                        item.recipeId = recipeId
                    }
                }
                selectRecipeDialogActive = false
                onFinishAction()
            }
        )
    }

    if (selectCategoryDialogActive) {
        SelectCategoryDialog(
            categories = allCategories,
            selectedCategory = category.id,
            onDismissRequest = { selectCategoryDialogActive = false },
            onSelectCategory = { categoryId ->
                onMoveItemsToCategory(editingGroceryItems, category.id, categoryId)

                selectCategoryDialogActive = false
                onFinishAction()
            }
        )
    }
}

@Composable
fun BottomSheetSmallAction(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    label: String
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(10),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(5.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EditGroceriesBottomSheetPreview() {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded, skipHiddenState = false)
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold() { innerPadding ->
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = { EditGroceriesBottomSheet(
                allCategories = listOf(),
                category = GroceryItemCategory("Default 1"),
                editingGroceryItems = listOf(UUID.randomUUID()),
                allRecipes = listOf(),
                onFinishAction = {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                },
                onMoveItemsToCategory = {_, _, _ -> }
            ) },
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(onClick = {
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }) {
                Text("Show Scaffold")
            }
        }
    }
}