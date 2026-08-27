package com.jule.food.ui.groceries

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jule.food.ui.groceries_recipes.AddGroceriesFromRecipeDialog
import com.jule.food.ui.main.BottomNavigationBar
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.GroceryLocation
import com.jule.food.data.GroceryViewModel
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import com.jule.food.R
import com.jule.food.data.ListOfSaveableGroceryItems
import com.jule.food.data.Recipe
import com.jule.food.ui.settings.SettingDialog
import com.jule.food.ui.settings.SettingDialogElement
import com.jule.food.data.getGroceriesFromSaveable
import com.jule.food.utils.getDownloadsDir
import com.jule.food.data.getJsonFromString
import com.jule.food.data.groceryGroupingOptionsDisplay
import com.jule.food.data.groceryGroupingOptionsIcons
import com.jule.food.utils.shareFile
import com.jule.food.utils.shareText
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID



@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    groceryViewModel: GroceryViewModel,
    getRecipeNameFromId: (UUID) -> String,
    allRecipes: List<Recipe>,
    bottomBar: @Composable () -> Unit,
    onOpenSettings: () -> Unit,
    recipeDataLoaded: Boolean,
    importJsonContent: String?,
    onHandledJsonImport: () -> Unit,
    onPickJsonFile: () -> Unit,
    onStartJsonImport: () -> Unit,
    importingFile: String?,
    onCancelImport: () -> Unit,
    getLocationNameFromId: (UUID) -> String,
    groceryLocations: List<GroceryLocation>,
    addGroceryLocation: (String) -> Unit,
    removeGroceryLocation: (UUID) -> Unit,
    addGroceryNameToLocation: (String, UUID) -> Unit,
    removeGroceryNameFromAllLocations: (String) -> Unit,
    changeGroceryLocationName: (String, UUID) -> Unit,
    reorderGroceryLocations: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val resources = LocalResources.current

    var showAddGroceryDialog by remember { mutableStateOf(false) }
    var showSharingDialog by remember { mutableStateOf(false) }
    var showAddFromRecipeDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }


    val categories = groceryViewModel.groceryItemCategories
    val selectedCategoryId = groceryViewModel.selectedCategoryId
    val selectedGroupingOption = groceryViewModel.selectedGroupingOption

    val selectedCategoryShowDeletedItems = groceryViewModel.selectedCategoryShowDeletedItems

    val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId }
    val activeRecipeIds = selectedCategory?.items?.mapNotNull { it.recipeId } ?: listOf()

    val selectedGroceryItems = remember { mutableStateListOf<UUID>() }
    val selectionModeActive = selectedGroceryItems.isNotEmpty()
    val selectedCategoryGridState = groceryViewModel.getGridStateForSelectedCategory()

    var isEditingCategories by remember { mutableStateOf(false) }


    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = SheetValue.Expanded, enabledValues = setOf(SheetValue.Expanded, SheetValue.Hidden))
    )

    LaunchedEffect(selectionModeActive) {
        if (selectionModeActive) {
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        } else {
            scope.launch {
                scaffoldState.bottomSheetState.hide()
            }
        }
    }
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden || scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
            selectedGroceryItems.clear()
        }
    }

    BackHandler(enabled = selectionModeActive || isEditingCategories) {
        if (selectionModeActive) {
            selectedGroceryItems.clear()
        } else if (isEditingCategories) {
            isEditingCategories = false
        }
    }

    LaunchedEffect(importingFile) {
        if (importingFile != null) {
            Log.d("LaunchedEffect", "Importing File: $importingFile")
            onStartJsonImport()
        }
    }



    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(!selectionModeActive, enter = slideInVertically { it }, exit = slideOutVertically { it }) { bottomBar() }
        },
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        topBar = {
            GroceryScreenTopBar(
                selectionModeActive = selectionModeActive,
                selectedCategory = selectedCategory,
                isEditingCategories = isEditingCategories,
                onOpenSharingDialog = { showSharingDialog = true },
                onBackFromCategoryEditing = { isEditingCategories = false },
                onPickJsonFile = onPickJsonFile,
                onOpenSettings = onOpenSettings,
                onSelectAll = {
                    selectedGroceryItems.clear()
                    selectedGroceryItems.addAll(selectedCategory!!.items.map { it.id })
                },
                selectedGroceryItemsNumber = selectedGroceryItems.count(),
                onClearSelectedGroceryItems = { selectedGroceryItems.clear() }
            )
        },
        floatingActionButton = {
            if (groceryViewModel.dataLoaded) {
                Box(modifier = Modifier.fillMaxWidth()) {

                    AnimatedVisibility(!selectionModeActive && !isEditingCategories, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut(), modifier = Modifier.align(Alignment.Center)) {
                        val elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 6.dp, focusedElevation = 8.dp, hoveredElevation = 8.dp)
                        SplitButtonLayout(
                        modifier = Modifier.align(Alignment.Center),
                            leadingButton = {
                                SplitButtonDefaults.LeadingButton(
                                    onClick = {
                                        showAddGroceryDialog = true
                                    },
                                    elevation = elevation,
                                    modifier = Modifier.height(SplitButtonDefaults.MediumContainerHeight),
                                    shapes = SplitButtonDefaults.leadingButtonShapesFor(SplitButtonDefaults.MediumContainerHeight),
                                    contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(SplitButtonDefaults.MediumContainerHeight),
                                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize))
                                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                    Text(stringResource(R.string.add_grocery))
                                }
                            },
                            trailingButton = {
                                var expanded by remember { mutableStateOf(false) }
                                SplitButtonDefaults.TrailingButton(
                                    enabled = recipeDataLoaded,
                                    checked = expanded,
                                    onCheckedChange = {
                                        expanded = it
                                    },
                                    elevation = elevation,
                                    modifier = Modifier.height(SplitButtonDefaults.MediumContainerHeight),
                                    shapes = SplitButtonDefaults.trailingButtonShapesFor(SplitButtonDefaults.MediumContainerHeight),
                                    contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(SplitButtonDefaults.MediumContainerHeight),
                                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                                ) {
                                    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier
                                        .size(SplitButtonDefaults.TrailingIconSize)
                                        .rotate(rotation))
                                }

                                DropdownMenu(
                                    expanded = expanded,
//                                        modifier = Modifier.align(Alignment.End),
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            showAddFromRecipeDialog = true
                                            expanded = false
                                        },
                                        text = { Text(stringResource(R.string.add_from_recipe)) },
                                        leadingIcon = { Icon(painterResource(R.drawable.book), contentDescription = null)}
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = groceryViewModel.dataLoaded,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            modifier = Modifier.padding(innerPadding)
        ) { dataLoaded ->
            if (dataLoaded) {
                GroceryScreenContent(
                    allCategories = categories,
                    category = selectedCategory!!,
                    gridState = selectedCategoryGridState!!,
                    allRecipes = allRecipes,
                    onChangeSelectedCategoryId = { newId ->
                        if (selectionModeActive)
                            selectedGroceryItems.clear()
                        groceryViewModel.changeSelectedCategoryId(newId)
                    },
                    onRemoveFromGroceries = { index ->
                        groceryViewModel.removeFromGroceries(index, selectedCategoryId!!)
                    },
                    onAddToGroceries = { newItem ->
                        groceryViewModel.addToGroceries(newItem, selectedCategoryId!!)
                    },
                    onChangeItemNameDetails = { id, name, details ->
                        groceryViewModel.changeGroceryItem(id, name, details, selectedCategoryId!!)
                    },
                    onAddNewCategory = { newCategory ->
                        groceryViewModel.addCategory(newCategory)
                    },
                    onDeleteCategory = { id ->
                        groceryViewModel.removeCategory(id)
                    },
                    onChangeCategoryName = { name, id ->
                        groceryViewModel.changeCategoryName(name, id)
                    },
                    onReorderCategories = { fromIndex, toIndex ->
                        groceryViewModel.reorderCategories(fromIndex, toIndex)
                    },
                    onMoveItemsToCategory = { itemIds, fromCategoryId, toCategoryId ->
                        groceryViewModel.moveItemsToCategory(itemIds, fromCategoryId, toCategoryId)
                    },
                    groupingOption = selectedGroupingOption,
                    onChangeGroupingOption = { newOption ->
                        groceryViewModel.changeSelectedGroupingOption(newOption)
                    },
                    getRecipeNameFromId = getRecipeNameFromId,
                    getLocationNameFromId = getLocationNameFromId,
                    selectedGroceryItems = selectedGroceryItems,
                    onAddToSelection = { id -> selectedGroceryItems.add(id) },
                    onRemoveFromSelection = { id -> selectedGroceryItems.remove(id) },
                    onAddToDeletedItems = { groceryItem -> groceryViewModel.addToDeletedItems(groceryItem, selectedCategoryId!!) },
                    onRemoveFromDeletedItems = { id -> groceryViewModel.removeFromDeletedItems(id, selectedCategoryId!!) },
                    onClearDeletedItems = { groceryViewModel.clearDeletedItems(selectedCategoryId!!) },
                    onRestoreDeletedItems = { items -> groceryViewModel.restoreDeletedItems(items, selectedCategoryId!!) },
                    showDeletedItems = selectedCategoryShowDeletedItems,
                    onChangeShowDeletedItems = { showDeletedItems -> groceryViewModel.changeShowDeletedItems(selectedCategoryId!!, showDeletedItems) },
                    scaffoldState = scaffoldState,
                    onClearSelection = { selectedGroceryItems.clear() },
                    isEditingCategories = isEditingCategories,
                    onChangeIsEditingCategories = {
                        if (selectionModeActive) {
                            selectedGroceryItems.clear()
                        }
                        isEditingCategories = it
                    },
                    onChangeRecipeIdGroceries = { itemIds, recipeId ->
                        selectedCategory.items.filter { itemIds.contains(it.id) }.forEach {
                            it.recipeId = recipeId
                        }
                    },
                    activeRecipeIds = activeRecipeIds,
                    groceryLocations = groceryLocations,
                    onAddGroceryLocation = addGroceryLocation,
                    onAddGroceryToLocation = addGroceryNameToLocation,
                    onRemoveGroceryLocation = removeGroceryLocation,
                    onRemoveGroceryFromAllLocations = removeGroceryNameFromAllLocations,
                    onChangeLocationName = changeGroceryLocationName,
                    onReorderLocations = reorderGroceryLocations,
                    onChangeLocationIdGroceries = { itemIds, locationId ->
                        selectedCategory.items.filter { itemIds.contains(it.id) }.forEach {
                            it.locationId = locationId
                            if (locationId == null)
                                removeGroceryNameFromAllLocations(it.name)
                            else
                                addGroceryNameToLocation(it.name, locationId)
                        }

                    },
                    snackbarHostState = snackbarHostState,
                    modifier = modifier
                )
            } else {
                LoadingGroceryGridScreen()
            }
        }
    }

    val addGroceryFocusRequester = remember { FocusRequester() }
//    LaunchedEffect(showAddGroceryDialog) {
//        if (showAddGroceryDialog) {
//            addGroceryFocusRequester.requestFocus()
//        }
//    }
    if (showAddGroceryDialog && selectedCategoryId != null) {
        AddGroceryBottomSheet(
            onDismissRequest = { showAddGroceryDialog = false },
            focusRequester = addGroceryFocusRequester,
            onConfirm = { newItem ->
                groceryViewModel.addToGroceries(
                    newItem,
                    selectedCategoryId
                )
            },
            allRecipes = allRecipes,
            getRecipeNameFromId = getRecipeNameFromId,
            activeRecipeIds = activeRecipeIds,
            groceryLocations = groceryLocations,
            onAddGroceryLocation = addGroceryLocation,
            onAddGroceryToLocation = addGroceryNameToLocation,
            onRemoveGroceryLocation = removeGroceryLocation,
            onRemoveGroceryFromAllLocations = removeGroceryNameFromAllLocations,
            onChangeLocationName = changeGroceryLocationName,
            onReorderLocations = reorderGroceryLocations
        )
    }
//    val searchRecipesFocusRequester = remember { FocusRequester() }
//    LaunchedEffect(showAddFromRecipeDialog) {
//        if (showAddFromRecipeDialog) {
//            scope.launch {
//                delay(100)
//                searchRecipesFocusRequester.requestFocus()
//            }
//        }
//    }

    if (showAddFromRecipeDialog) {
        var chosenRecipeId: UUID? by remember { mutableStateOf(null) }
        var showAddGroceriesFromRecipeDialog by remember { mutableStateOf(false) }

        SelectRecipeDialog(
            onDismissRequest = { showAddFromRecipeDialog = false },
            allRecipes = allRecipes.filter { recipe -> recipe.groceries.isNotEmpty() },
            onClickRecipe = { recipeId ->
                chosenRecipeId = recipeId
                showAddGroceriesFromRecipeDialog = true
            },
            selectedRecipeIds = null,
            showSubtitle = true,
            activeRecipeIds = null
//            searchFocusRequester = searchRecipesFocusRequester
        )
        if (showAddGroceriesFromRecipeDialog && selectedCategoryId != null) {
            AddGroceriesFromRecipeDialog(
                onDismissRequest = { showAddGroceriesFromRecipeDialog = false },
                recipe = allRecipes.fastFirst { it.id == chosenRecipeId },
                includeCategoryChoice = true,
                groceryCategories = categories,
                firstSelectedCategoryId = selectedCategoryId,
                onConfirm = { groceries, addingOption, categoryId ->
                    groceryViewModel.addToGroceriesFromRecipe(
                        groceries,
                        addingOption,
                        categoryId!!,
                        chosenRecipeId!!,
                        context
                    )
                    showAddGroceriesFromRecipeDialog = false
                    showAddFromRecipeDialog = false
                }
            )
        }
    }

    if (showSharingDialog) {
        ShareGroceriesSheet(
            onDismissRequest = { showSharingDialog = false },
            selectedGroceriesNumber = selectedGroceryItems.size,
            onShare = { shareOption ->
                if (selectedCategoryId == null)
                    return@ShareGroceriesSheet

                val items = selectedCategory!!.items.filter { selectedGroceryItems.contains(it.id) }
                if (shareOption == GroceryShareOption.Text) {
                    val text = getShareTextFromGroceryItems(items)
                    shareText(context, text)
                } else if (shareOption == GroceryShareOption.Json) {
                    val downloadsDir = getDownloadsDir(context)
                    if (downloadsDir == null) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        return@ShareGroceriesSheet
                    }
                    val groceriesJson = groceryViewModel.getJson(items)
                    val groceriesJsonFile = File(
                        downloadsDir,
                        "${resources.getString(R.string.groceries_json)}.json"
                    ).apply { writeText(groceriesJson) }
                    shareFile(context, groceriesJsonFile)
                }
                showSharingDialog = false
            }
        )
    }

    var importGroceryItems: List<GroceryItem> by remember { mutableStateOf(listOf()) }

    LaunchedEffect (importJsonContent) {
        if (importJsonContent == null)
            return@LaunchedEffect
        val importGroceries: ListOfSaveableGroceryItems? =
            getJsonFromString<ListOfSaveableGroceryItems>(context, importJsonContent)
        if (importGroceries == null) {
            Toast.makeText(context, "Could not read Groceries file", Toast.LENGTH_SHORT).show()
            onHandledJsonImport()
            return@LaunchedEffect
        }

        importGroceryItems = getGroceriesFromSaveable(importGroceries)
        importGroceryItems = importGroceryItems.map { item ->
            val doesRecipeExist = item.recipeId != null && allRecipes.any { it.id == item.recipeId }
            val locationId = groceryLocations.fastFirstOrNull { it.groceryNames.contains(item.name) }?.id
            GroceryItem(item.name, item.details, if (doesRecipeExist) item.recipeId else null, locationId, null, item.id)
        }

        Log.d("LaunchedEffect", "Importing Groceries: $importGroceryItems")
    }

    if (importJsonContent != null && importGroceryItems.isNotEmpty()) {
        ImportGroceriesSheet(
            onDismissRequest = {
                importGroceryItems = listOf()
                onHandledJsonImport()
            },
            categories = categories,
            importGroceryItems = importGroceryItems,
            onImport = { chosenImportGroceryItemIds, chosenCategoryId ->
                val items = importGroceryItems.filter { chosenImportGroceryItemIds.contains(it.id) }
                groceryViewModel.addToGroceries(items, chosenCategoryId)
                Toast.makeText(context, resources.getString(R.string.imported_groceries, chosenImportGroceryItemIds.size), Toast.LENGTH_SHORT).show()
                importGroceryItems = listOf()
                onHandledJsonImport()
            },
            importingFileName = importingFile,
            getRecipeNameFromId = getRecipeNameFromId
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun GroceryScreenContent(
    allCategories: List<GroceryItemCategory>,
    category: GroceryItemCategory,
    gridState: LazyGridState,
    allRecipes: List<Recipe>,
    activeRecipeIds: List<UUID>,
    onChangeSelectedCategoryId: (UUID) -> Unit,
    onRemoveFromGroceries: (itemIndex: Int) -> Unit,
    onAddToGroceries: (GroceryItem) -> Unit,
    onChangeItemNameDetails: (id: UUID, name: String, details: String) -> Unit,
    onMoveItemsToCategory: (items: List<UUID>, fromCategoryId: UUID, toCategoryId: UUID) -> Unit,
    onAddNewCategory: (GroceryItemCategory) -> Unit,
    onDeleteCategory: (id: UUID) -> Unit,
    onChangeCategoryName: (String, id: UUID) -> Unit,
    onReorderCategories: (fromIndex: Int, toIndex: Int) -> Unit,
    groupingOption: GroceryGroupingOption,
    onChangeGroupingOption: (GroceryGroupingOption) -> Unit,
    getRecipeNameFromId: (UUID) -> String,
    getLocationNameFromId: (UUID) -> String,
    selectedGroceryItems: List<UUID>,
    onAddToSelection: (UUID) -> Unit,
    onRemoveFromSelection: (UUID) -> Unit,
    onClearSelection: () -> Unit,
    onAddToDeletedItems: (GroceryItem) -> Unit,
    onRemoveFromDeletedItems: (UUID) -> Unit,
    onClearDeletedItems: () -> Unit,
    onRestoreDeletedItems: (List<GroceryItem>) -> Unit,
    showDeletedItems: Boolean,
    onChangeShowDeletedItems: (Boolean) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    isEditingCategories: Boolean,
    onChangeIsEditingCategories: (Boolean) -> Unit,
    onChangeRecipeIdGroceries: (List<UUID>, UUID?) -> Unit,
    groceryLocations: List<GroceryLocation>,
    onAddGroceryLocation: (String) -> Unit,
    onRemoveGroceryLocation: (UUID) -> Unit,
    onAddGroceryToLocation: (String, UUID) -> Unit,
    onRemoveGroceryFromAllLocations: (String) -> Unit,
    onChangeLocationName: (String, UUID) -> Unit,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit,
    onChangeLocationIdGroceries: (List<UUID>, UUID?) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val selectionModeActive = selectedGroceryItems.isNotEmpty()
    val singleSelection = selectedGroceryItems.size == 1

    val editGroceryNameState = rememberTextFieldState("")
    val editGroceryDetailState = rememberTextFieldState("")

    var showGroupingSheet by remember { mutableStateOf(false) }
    
    var isSelectingRecipeInBottomSheet: Boolean by remember { mutableStateOf(false) }
    var isSelectingLocationInBottomSheet: Boolean by remember { mutableStateOf(false) }


    LaunchedEffect(singleSelection) {
        if (singleSelection) {
            val item = category.items.fastFirstOrNull { item -> item.id == selectedGroceryItems[0] }
            if (item != null) {
                editGroceryNameState.setTextAndPlaceCursorAtEnd(item.name)
                editGroceryDetailState.setTextAndPlaceCursorAtEnd(item.details)
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            EditGroceriesBottomSheetContent(
                category = category,
                allCategories = allCategories,
                allRecipes = allRecipes,
                onFinishAction = onClearSelection,
                editingGroceryItems = selectedGroceryItems,
                onMoveItemsToCategory = onMoveItemsToCategory,
                onChangeItemNameDetails = onChangeItemNameDetails,
                groceryNameState = editGroceryNameState,
                groceryDetailState = editGroceryDetailState,
                getRecipeNameFromId = getRecipeNameFromId,
                showRecipeSelection = isSelectingRecipeInBottomSheet,
                onChangeShowRecipeSelection = { isSelectingRecipeInBottomSheet = it },
                activeRecipeIds = activeRecipeIds,
                groceryLocations = groceryLocations,
                onAddGroceryLocation = onAddGroceryLocation,
                onRemoveGroceryLocation = onRemoveGroceryLocation,
                getLocationNameFromId = getLocationNameFromId,
                onAddGroceryToLocation = onAddGroceryToLocation,
                onRemoveGroceryFromAllLocations = onRemoveGroceryFromAllLocations,
                onChangeLocationName = onChangeLocationName,
                onReorderLocations = onReorderLocations,
                showLocationSelection = isSelectingLocationInBottomSheet,
                onChangeShowLocationSelection = { isSelectingLocationInBottomSheet = it }
            )
        },
        sheetSwipeEnabled = !isSelectingRecipeInBottomSheet && !isSelectingLocationInBottomSheet,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetDragHandle = {
            Row(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier
                    .width(50.dp)
                    .height(5.dp)) {}
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    enabled = selectionModeActive,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                    onClearSelection()
                }
        ) {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                    AnimatedContent(
                        targetState = isEditingCategories,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { editingCategories ->
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            if (!editingCategories) {
                                GroceryScreenTop(
                                    allCategories = allCategories,
                                    category = category,
                                    onChangeSelectedCategoryId = onChangeSelectedCategoryId,
                                    onChangeIsEditingCategories = onChangeIsEditingCategories,
                                    groupingOption = groupingOption,
                                    onChangeShowGroupingSheet = { showGroupingSheet = true },
                                )
                            } else {
                                CategoriesEditScreen(
                                    allCategories = allCategories,
                                    onDeleteCategory = onDeleteCategory,
                                    onChangeCategoryName = onChangeCategoryName,
                                    onReorderCategories = onReorderCategories,
                                    onAddNewCategory = onAddNewCategory
                                )
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = !isEditingCategories,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                GroceryGridScreen(
                    category = category,
                    gridState = gridState,
                    groupingOption = groupingOption,
                    onRemoveFromGroceries = onRemoveFromGroceries,
                    onAddToGroceries = onAddToGroceries,
                    getRecipeNameFromId = getRecipeNameFromId,
                    getLocationNameFromId = getLocationNameFromId,
                    selectionModeActive = selectionModeActive,
                    selectedGroceryItems = selectedGroceryItems,
                    onAddToSelection = onAddToSelection,
                    onRemoveFromSelection = onRemoveFromSelection,
                    onAddToDeletedItems = onAddToDeletedItems,
                    onRemoveFromDeletedItems = onRemoveFromDeletedItems,
                    onClearDeletedItems = onClearDeletedItems,
                    onRestoreDeletedItems = onRestoreDeletedItems,
                    showDeletedItems = showDeletedItems,
                    onChangeShowDeletedItems = onChangeShowDeletedItems,
                    onChangeRecipeIdGroceries = onChangeRecipeIdGroceries,
                    onChangeLocationIdGroceries = onChangeLocationIdGroceries,
                    snackbarHostState = snackbarHostState
                )
            }
        }
        
        if (showGroupingSheet) {
            SettingDialog(
                title = stringResource(R.string.group_by),
                onDismissRequest = {
                    showGroupingSheet = false
                }
            ) {
                GroceryGroupingOption.entries.forEachIndexed { index, option ->
                    val selected = groupingOption == option
                    SettingDialogElement(
                        title = stringResource(groceryGroupingOptionsDisplay[option]!!),
                        selected = selected,
                        onClick = {
                            onChangeGroupingOption(option)
                            showGroupingSheet = false
                        },
                        leadingIcon = {
                            Icon(
                                painterResource(groceryGroupingOptionsIcons[option]!!),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingGroceryGridScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 10.dp)) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20)
                    )
            )
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20)
                    )
            )
        }
        Row(modifier = Modifier
            .height(26.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                        shape = FilterChipDefaults.shape
                    )
                    .width(100.dp)
                    .height(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item() {
                Box(
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10)
                        )
                )
            }
            item() {
                Box(
                    modifier = Modifier
                        .aspectRatio(1.6f)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10)
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroceryScreenPreview() {
    val navController = rememberNavController()
    val groceryViewModel: GroceryViewModel = viewModel()

    val recipe1Id = UUID.randomUUID()
    val recipe2Id = UUID.randomUUID()

    fun getRecipeName(id: UUID): String {
        if (id == recipe1Id) {
            return "Gemüse"
        }
        if (id == recipe2Id) {
            return "Saturn"
        }
        return "AAAA"
    }

    if (groceryViewModel.groceryItemCategories.isEmpty()) {
        val defaultId = groceryViewModel.addCategory("Default")
        groceryViewModel.addToGroceries(GroceryItem("Mehl", "500g", recipeId = recipe1Id), defaultId)
        groceryViewModel.addToGroceries(GroceryItem("Mehl", "100g", recipeId = recipe2Id), defaultId)
        groceryViewModel.addToGroceries(GroceryItem("Spaghetti", ""), defaultId)

        val default2Id = groceryViewModel.addCategory("Default 2")
        groceryViewModel.addToGroceries(GroceryItem("Dosentomaten", ""), default2Id)
        groceryViewModel.addToGroceries(GroceryItem("Milch", ""), default2Id)

        groceryViewModel.initializeEmpty()
        groceryViewModel.changeSelectedCategoryId(defaultId)
    }

    groceryViewModel.changeSelectedGroupingOption(GroceryGroupingOption.Recipe)


    FoodTheme {
        GroceryScreen(
            groceryViewModel = groceryViewModel,
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    recipeViewModel = viewModel()
                )
            },
            onOpenSettings = {},
            getRecipeNameFromId = { getRecipeName(it) },
            allRecipes = listOf(),
            recipeDataLoaded = true,
            importJsonContent = null,
            onHandledJsonImport = {},
            onPickJsonFile = { },
            onStartJsonImport = { },
            importingFile = null,
            onCancelImport = { },
            getLocationNameFromId = {it.toString()},
            groceryLocations = listOf(),
            addGroceryLocation = { },
            removeGroceryLocation = { },
            addGroceryNameToLocation = { _, _ -> },
            removeGroceryNameFromAllLocations = { },
            changeGroceryLocationName = { _, _ -> },
            reorderGroceryLocations = { _, _ -> },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun LoadingGroceryScreenPreview() {
    FoodTheme {
        Scaffold(
            topBar = { CenterAlignedTopAppBar(title = { Text("Groceries" )})}
        ) { innerPadding ->
            LoadingGroceryGridScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}