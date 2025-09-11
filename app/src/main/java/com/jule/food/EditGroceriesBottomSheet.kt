package com.jule.food

import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroceriesBottomSheetContent(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    allCategories: List<GroceryItemCategory>,
    category: GroceryItemCategory,
    allRecipes: List<Recipe>,
    editingGroceryItems: List<UUID>,
    onMoveItemsToCategory: (items: List<UUID>, fromCategoryId: UUID, toCategoryId: UUID) -> Unit,
    groceryNameState: TextFieldState,
    groceryDetailState: TextFieldState,
    onFinishAction: () -> Unit,
    onChangeItemNameDetails: (id: UUID, name: String, details: String) -> Unit,
    getRecipeNameFromId: (UUID) -> String,
) {
    val singleItem = editingGroceryItems.size == 1
    val context = LocalContext.current
    var showRecipeSelection by remember { mutableStateOf(false) }
    var selectCategoryDialogActive by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    val placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    LaunchedEffect(groceryNameState.text, groceryDetailState.text) {
        if (editingGroceryItems.isNotEmpty())
            onChangeItemNameDetails(editingGroceryItems[0], groceryNameState.text.toString().trim(), groceryDetailState.text.toString().trim())
    }

    GroceryBottomSheetContentWithRecipeSelection(
        modifier = modifier,
        showRecipeSelection = showRecipeSelection,
        onExitRecipeSelection = { showRecipeSelection = false },
        onSelectRecipe = { recipeId ->
            category.items.forEach { item ->
                if (editingGroceryItems.contains(item.id)) {
                    item.recipeId = recipeId
                }
            }
            showRecipeSelection = false
//            onFinishAction()
        },
        allRecipes = allRecipes
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility (singleItem || editingGroceryItems.isEmpty(), enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column {
                    GroceryBottomSheetInputs(
                        groceryNameState = groceryNameState,
                        groceryDetailState = groceryDetailState,
                        focusRequester = focusRequester,
                        onConfirm = {
                            if (singleItem) {
                                onChangeItemNameDetails(editingGroceryItems[0], groceryNameState.text.toString().trim(), groceryDetailState.text.toString().trim())
                                focusManager.clearFocus(true)
                            }
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }

            FlowRow(
                modifier = Modifier.padding(start = 10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val itemsRecipeIds = if (singleItem) {
                    val id = category.items.fastFirstOrNull { it.id == editingGroceryItems[0] }?.recipeId
                    if (id != null)
                        listOf(id)
                    else
                        listOf()
                } else {
                    category.items.filter { editingGroceryItems.contains(it.id) }.mapNotNull { it.recipeId }
                }
                val displayNames = if (itemsRecipeIds.isEmpty()) null else {
                    val distinctIdsNumber = itemsRecipeIds.toSet().size
                    if (distinctIdsNumber == 1) { getRecipeNameFromId(itemsRecipeIds[0]) }
                    else { stringResource(R.string.n_recipes, distinctIdsNumber) }
                }
                GroceryBottomSheetSelectionField(
                    text = displayNames ?: stringResource(R.string.no_recipe),
                    icon = R.drawable.book,
                    isActive = displayNames != null,
                    inactiveColor = placeholderColor,
                    onClick = { showRecipeSelection = true },
                    onClear = {
                        editingGroceryItems.forEach { itemId ->
                            category.items.fastFirstOrNull { it.id == itemId }?.recipeId = null
                        }
                    }
                )
                GroceryBottomSheetSelectionField(
                    text = "No Location",
                    icon = R.drawable.location,
                    isActive = false,
                    inactiveColor = placeholderColor,
                    onClick = { },
                    onClear = { }
                )
                GroceryBottomSheetSelectionField(
                    text = category.name,
                    icon = R.drawable.categories,
                    isActive = true,
                    inactiveColor = placeholderColor,
                    onClick = { selectCategoryDialogActive = true },
                    showClearButton = false
                )
//                Box(modifier = Modifier.weight(1f)) {
//                    TextButton(
//                        onClick = {  },
//                        enabled = groceryNameState.text.isNotEmpty(),
//                        modifier = Modifier.align(Alignment.CenterEnd)
//                    ) {
//                        Text(stringResource(R.string.save), maxLines = 1)
//                    }
//                }
            }
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.height(80.dp).padding(start = 10.dp)
//            ) {
////                BottomSheetSmallAction(
////                    onClick = { showRecipeSelection = true },
////                    icon = R.drawable.book,
////                    label = "Move to recipe"
////                )
//                BottomSheetSmallAction(
//                    onClick = {
//                        selectCategoryDialogActive = true
//                    },
//                    icon = R.drawable.arrow_right_full,
//                    label = "Move to category"
//                )
//            }
        }
    }


    if (selectCategoryDialogActive) {
        SelectCategoryDialog(
            categories = allCategories,
            selectedCategory = category.id,
            onDismissRequest = { selectCategoryDialogActive = false },
            onSelectCategory = { categoryId ->
                onMoveItemsToCategory(editingGroceryItems, category.id, categoryId)

                Toast.makeText(
                    context,
                    context.getString(R.string.moved_n_groceries_to_category, editingGroceryItems.size, allCategories.fastFirstOrNull { it.id == categoryId }?.name ?: "NULL"),
                    Toast.LENGTH_SHORT
                ).show()

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

    val groceryViewModel: GroceryViewModel = viewModel()
    val id = groceryViewModel.addCategory("Default 1")
    val item = GroceryItem("Item 1", "Details 1")
    val itemId = item.id
    groceryViewModel.addToGroceries(item, id)
    groceryViewModel.addToGroceries(GroceryItem("Item 2", "Details 2"), id)


    Scaffold() { innerPadding ->
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = { EditGroceriesBottomSheetContent(
                sheetState = scaffoldState.bottomSheetState,
                allCategories = groceryViewModel.groceryItemCategories,
                category = groceryViewModel.groceryItemCategories[0],
                editingGroceryItems = listOf(itemId),
                allRecipes = listOf(),
                onFinishAction = {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                },
                onMoveItemsToCategory = {_, _, _ -> },
                onChangeItemNameDetails = { _, _, _ -> },
                groceryNameState = rememberTextFieldState(item.name),
                groceryDetailState = rememberTextFieldState(item.details),
                getRecipeNameFromId = { it.toString() }
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