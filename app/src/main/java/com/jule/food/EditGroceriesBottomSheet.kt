package com.jule.food

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EditGroceriesBottomSheetContent(
    modifier: Modifier = Modifier,
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
            onChangeItemNameDetails(
                editingGroceryItems[0],
                groceryNameState.text.toString().trim(),
                groceryDetailState.text.toString().trim()
            )
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
            AnimatedVisibility(
                singleItem || editingGroceryItems.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    GroceryBottomSheetInputs(
                        groceryNameState = groceryNameState,
                        groceryDetailState = groceryDetailState,
                        focusRequester = focusRequester,
                        onConfirm = {
                            if (singleItem) {
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
                    val id =
                        category.items.fastFirstOrNull { it.id == editingGroceryItems[0] }?.recipeId
                    if (id != null)
                        listOf(id)
                    else
                        listOf()
                } else {
                    category.items.filter { editingGroceryItems.contains(it.id) }
                        .mapNotNull { it.recipeId }
                }
                val displayNames = if (itemsRecipeIds.isEmpty()) null else {
                    val distinctIdsNumber = itemsRecipeIds.toSet().size
                    if (distinctIdsNumber == 1) {
                        getRecipeNameFromId(itemsRecipeIds[0])
                    } else {
                        stringResource(R.string.n_recipes, distinctIdsNumber)
                    }
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
                    icon = R.drawable.group_groceries,
                    isActive = true,
                    inactiveColor = placeholderColor,
                    onClick = { selectCategoryDialogActive = true },
                    showClearButton = false
                )
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
                        context.getString(
                            R.string.moved_n_groceries_to_category,
                            editingGroceryItems.size,
                            allCategories.fastFirstOrNull { it.id == categoryId }?.name ?: "NULL"
                        ),
                        Toast.LENGTH_SHORT
                    ).show()

                    selectCategoryDialogActive = false
                    onFinishAction()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroceryBottomSheetBasic(
    onDismissRequest: () -> Unit,
    groceryNameState: TextFieldState,
    groceryDetailState: TextFieldState,
    onChangeItemNameDetails: (name: String, details: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    val placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    LaunchedEffect(groceryNameState.text, groceryDetailState.text) {
        onChangeItemNameDetails(
            groceryNameState.text.toString().trim(),
            groceryDetailState.text.toString().trim()
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet (
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        modifier = modifier
    ) {
        val focusManager = LocalFocusManager.current

        GroceryBottomSheetInputs(
            groceryNameState = groceryNameState,
            groceryDetailState = groceryDetailState,
            focusRequester = focusRequester,
            onConfirm = { focusManager.clearFocus(true) }
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.padding(start = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GroceryBottomSheetSelectionField(
                text = "No Location",
                icon = R.drawable.location,
                isActive = false,
                inactiveColor = placeholderColor,
                onClick = { },
                onClear = { }
            )
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


    Scaffold { innerPadding ->
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = { EditGroceriesBottomSheetContent(
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