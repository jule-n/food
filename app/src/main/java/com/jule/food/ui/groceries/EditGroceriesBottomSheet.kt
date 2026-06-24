package com.jule.food.ui.groceries

import android.util.Log
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.GroceryLocation
import com.jule.food.data.GroceryViewModel
import com.jule.food.R
import com.jule.food.data.Recipe
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
    getLocationNameFromId: (UUID) -> String,
    showRecipeSelection: Boolean,
    onChangeShowRecipeSelection: (Boolean) -> Unit,
    showLocationSelection: Boolean,
    onChangeShowLocationSelection: (Boolean) -> Unit,
    activeRecipeIds: List<UUID>,
    groceryLocations: List<GroceryLocation>,
    onAddGroceryLocation: (String) -> Unit,
    onRemoveGroceryLocation: (UUID) -> Unit,
    onAddGroceryToLocation: (String, UUID) -> Unit,
    onRemoveGroceryFromAllLocations: (String) -> Unit,
    onChangeLocationName: (String, UUID) -> Unit,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val resources = LocalResources.current

    val focusRequester = remember { FocusRequester() }
    
    var editingGroceryItemsLocal by remember { mutableStateOf(editingGroceryItems) }
    LaunchedEffect(editingGroceryItems.size) {
        if (editingGroceryItems.isNotEmpty()) {
            editingGroceryItemsLocal = editingGroceryItems.toList()
            Log.d("LaunchedEffect", "Changed Size to ${editingGroceryItemsLocal.size}")
        }
    }
    LaunchedEffect(editingGroceryItemsLocal.size) {
        Log.d("LaunchedEffect2", "Selected Grocery Items Local is Size ${editingGroceryItemsLocal.size}")
    }

    val singleItem = editingGroceryItemsLocal.size == 1
    var selectCategoryDialogActive by remember { mutableStateOf(false) }

    val placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)


    val itemsRecipeIds = if (singleItem) {
        val id = category.items.fastFirstOrNull { it.id == editingGroceryItemsLocal[0] }?.recipeId
        if (id != null)
            listOf(id)
        else
            listOf()
    } else {
        category.items.filter { editingGroceryItemsLocal.contains(it.id) }
            .mapNotNull { it.recipeId }
    }
    val selectedRecipeId = if (itemsRecipeIds.toSet().size == 1) itemsRecipeIds[0] else null

    val itemsLocationIds = if (singleItem) {
        val id = category.items.fastFirstOrNull { it.id == editingGroceryItemsLocal[0] }?.locationId
        if (id != null)
            listOf(id)
        else
            listOf()
    } else {
        category.items.filter { editingGroceryItemsLocal.contains(it.id) }.mapNotNull { it.locationId }
    }
    val selectedLocationId = if (itemsLocationIds.toSet().size == 1) itemsLocationIds[0] else null

    LaunchedEffect(groceryNameState.text, groceryDetailState.text) {
        if (editingGroceryItemsLocal.isNotEmpty())
            onChangeItemNameDetails(
                editingGroceryItemsLocal[0],
                groceryNameState.text.toString().trim(),
                groceryDetailState.text.toString().trim()
            )
    }

    GroceryBottomSheetContentWithRecipeSelection(
        modifier = modifier,
        showRecipeSelection = showRecipeSelection,
        onExitRecipeSelection = { onChangeShowRecipeSelection(false) },
        onSelectRecipe = { recipeId ->
            category.items.forEach { item ->
                if (editingGroceryItemsLocal.contains(item.id)) {
                    item.recipeId = recipeId
                }
            }
            onChangeShowRecipeSelection(false)
//            onFinishAction()
        },
        allRecipes = allRecipes,
        activeRecipeIds = activeRecipeIds,
        showLocationSelection = showLocationSelection,
        onExitLocationSelection = {
            onChangeShowLocationSelection(false)
        },
        onSelectLocation = { locationId ->
            category.items.forEach { item ->
                if (editingGroceryItemsLocal.contains(item.id)) {
                    item.locationId = locationId
                }
            }
            onChangeShowLocationSelection(false)
            editingGroceryItemsLocal.forEach { itemId ->
                val itemName = category.items.firstOrNull { it.id == itemId }?.name
                if (itemName != null)
                    onAddGroceryToLocation(itemName, locationId)
            }
        },
        onAddNewLocation = onAddGroceryLocation,
        onRemoveLocation = onRemoveGroceryLocation,
        allLocations = groceryLocations,
        onChangeLocationName = onChangeLocationName,
        onReorderLocations = onReorderLocations,
        selectedLocationId = selectedLocationId,
        selectedRecipeId = selectedRecipeId
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                singleItem || editingGroceryItemsLocal.isEmpty(),
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
                val displayNamesRecipes = if (itemsRecipeIds.isEmpty()) null else {
                    val distinctIdsNumber = itemsRecipeIds.toSet().size
                    if (distinctIdsNumber == 1) {
                        getRecipeNameFromId(itemsRecipeIds[0])
                    } else {
                        stringResource(R.string.n_recipes, distinctIdsNumber)
                    }
                }
                GroceryBottomSheetSelectionField(
                    text = displayNamesRecipes ?: stringResource(R.string.no_recipe),
                    icon = R.drawable.book,
                    isActive = displayNamesRecipes != null,
                    inactiveColor = placeholderColor,
                    onClick = { onChangeShowRecipeSelection(true) },
                    onClear = {
                        editingGroceryItemsLocal.forEach { itemId ->
                            category.items.fastFirstOrNull { it.id == itemId }?.recipeId = null
                        }
                    }
                )
                val displayNamesLocations = if (itemsLocationIds.isEmpty()) null else {
                    val distinctIdsNumber = itemsLocationIds.toSet().size
                    if (distinctIdsNumber == 1) {
                        getLocationNameFromId(itemsLocationIds[0])
                    } else {
                        stringResource(R.string.n_locations, distinctIdsNumber)
                    }
                }
                GroceryBottomSheetSelectionField(
                    text = displayNamesLocations ?: stringResource(R.string.no_location),
                    icon = R.drawable.location,
                    isActive = displayNamesLocations != null,
                    inactiveColor = placeholderColor,
                    onClick = { onChangeShowLocationSelection(true) },
                    onClear = {
                        editingGroceryItemsLocal.forEach { itemId ->
                            val item = category.items.fastFirstOrNull { it.id == itemId }
                            if (item != null) {
                                item.locationId = null
                                onRemoveGroceryFromAllLocations(item.name)
                            }
                        }

//                        changedLocationManually = true
                    }
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
                    onMoveItemsToCategory(editingGroceryItemsLocal, category.id, categoryId)

                    Toast.makeText(
                        context,
                        resources.getString(
                            R.string.moved_n_groceries_to_category,
                            editingGroceryItemsLocal.size,
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
//        Spacer(Modifier.height(10.dp))
//        FlowRow(
//            modifier = Modifier.padding(start = 10.dp),
//            horizontalArrangement = Arrangement.spacedBy(10.dp)
//        ) {
//            GroceryBottomSheetSelectionField(
//                text = "No Location",
//                icon = R.drawable.location,
//                isActive = false,
//                inactiveColor = placeholderColor,
//                onClick = { },
//                onClear = { }
//            )
//        }
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
                onMoveItemsToCategory = { _, _, _ -> },
                onChangeItemNameDetails = { _, _, _ -> },
                groceryNameState = rememberTextFieldState(item.name),
                groceryDetailState = rememberTextFieldState(item.details),
                getRecipeNameFromId = { it.toString() },
                showRecipeSelection = false,
                onChangeShowRecipeSelection = {},
                activeRecipeIds = listOf(),
                getLocationNameFromId = { "" },
                onChangeShowLocationSelection = { },
                groceryLocations = listOf(),
                onAddGroceryLocation = { },
                onRemoveGroceryLocation = { },
                onAddGroceryToLocation = { _, _ -> },
                onRemoveGroceryFromAllLocations = { },
                onChangeLocationName = { _, _ -> },
                onReorderLocations = { _, _ -> },
                showLocationSelection = false
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