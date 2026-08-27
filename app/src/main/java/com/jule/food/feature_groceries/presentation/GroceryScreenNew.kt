package com.jule.food.feature_groceries.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SplitButton
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jule.food.R
import com.jule.food.feature_groceries.presentation.components.AddGroceryBottomSheetNew
import com.jule.food.feature_groceries.presentation.components.GroceryScreenContentNew
import com.jule.food.feature_groceries.presentation.components.GroceryScreenTopBarNew
import com.jule.food.feature_groceries.presentation.components.SelectEditLocationButtonsNew
import com.jule.food.ui.groceries.LoadingGroceryGridScreen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreenNew(
    viewModel: GroceryViewModelNew,
    onNavigateToRecipes: () -> Unit,
    onNavigateToSettings: () -> Unit,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.currentState
    val onEvent = viewModel::onEvent
    val focusManager = LocalFocusManager.current
    val resources = LocalResources.current

//    var showAddGroceryDialog by remember { mutableStateOf(false) }
//    var showSharingDialog by remember { mutableStateOf(false) }
//    var showAddFromRecipeDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }


//    val categories = groceryViewModel.groceryItemCategories
//    val selectedCategoryId = groceryViewModel.selectedCategoryId
//    val selectedGroupingOption = groceryViewModel.selectedGroupingOption
//
//    val selectedCategoryShowDeletedItems = groceryViewModel.selectedCategoryShowDeletedItems

//    val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId }
//    val activeRecipeIds = selectedCategory?.items?.mapNotNull { it.recipeId } ?: listOf()

//    val selectedGroceryItems = remember { mutableStateListOf<UUID>() }
//    val selectionModeActive = selectedGroceryItems.isNotEmpty()
//    val selectedCategoryGridState = groceryViewModel.getGridStateForSelectedCategory()


    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
    )

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is GroceryViewModelNew.UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = resources.getString(event.message),
                        actionLabel = if (event.action != null) resources.getString(event.action) else null
                    )
                    if (event.onAction != null && result == SnackbarResult.ActionPerformed) {
                        event.onAction()
                    }
                }
                is GroceryViewModelNew.UiEvent.ClearFocus -> {
                    focusManager.clearFocus(true)
                }
            }
        }
    }

//    LaunchedEffect(selectionModeActive) {
//        if (selectionModeActive) {
//            scope.launch {
//                scaffoldState.bottomSheetState.expand()
//            }
//        } else {
//            scope.launch {
//                scaffoldState.bottomSheetState.hide()
//            }
//        }
//    }
//    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
//        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden || scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
//            selectedGroceryItems.clear()
//        }
//    }

//    BackHandler(enabled = selectionModeActive || isEditingCategories) {
//        if (selectionModeActive) {
//            selectedGroceryItems.clear()
//        } else if (isEditingCategories) {
//            isEditingCategories = false
//        }
//    }

//    LaunchedEffect(importingFile) {
//        if (importingFile != null) {
//            Log.d("LaunchedEffect", "Importing File: $importingFile")
//            onStartJsonImport()
//        }
//    }



    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
        bottomBar = {
//            bottomBar()
            AnimatedVisibility(!state.isSelectionModeActive, enter = slideInVertically { it }, exit = slideOutVertically { it }) { bottomBar() }
        },
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        topBar = {
            GroceryScreenTopBarNew(
                selectionModeActive = state.isSelectionModeActive,
                selectedList = state.selectedList,
                isEditingLists = state.showEditListScreen,
                onOpenSharingDialog = { },
                onBackFromCategoryEditing = { onEvent(GroceryScreenEvent.ChangeShowEditListScreen(false)) },
                onPickJsonFile = { },
                onOpenSettings = { },
                onSelectAll = {
//                    selectedGroceryItems.clear()
//                    selectedGroceryItems.addAll(selectedCategory!!.items.map { it.id })
                },
                selectedGroceryItemsNumber = 0,
                onClearSelectedGroceryItems = { }
            )
        },
        floatingActionButton = {
            if (state.isDataLoaded && state.selectedList != null) {
                Box(modifier = Modifier.fillMaxWidth()) {

                    AnimatedVisibility(!state.isSelectionModeActive && !state.showEditListScreen, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut(), modifier = Modifier.align(Alignment.Center)) {
                        val elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 6.dp, focusedElevation = 8.dp, hoveredElevation = 8.dp)
                        SplitButton(
                            leadingButton = {
                                SplitButtonDefaults.LeadingButton(
                                    onClick = {
                                        onEvent(GroceryScreenEvent.ChangeShowAddGrocerySheet(true))
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
                                    enabled = false,
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
//                                            showAddFromRecipeDialog = true
                                            expanded = false
                                        },
                                        text = { Text(stringResource(R.string.add_from_recipe)) },
                                        leadingIcon = { Icon(painterResource(R.drawable.book), contentDescription = null)}
                                    )
                                }
                            },
                            modifier = Modifier.align(Alignment.Center),
                            spacing = SplitButtonDefaults.Spacing
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.isDataLoaded && state.selectedList != null,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            modifier = Modifier.padding(innerPadding)
        ) { dataLoaded ->
            if (dataLoaded) {
                GroceryScreenContentNew(
                    state = state,
                    onEvent = viewModel::onEvent,
                    snackbarHostState = snackbarHostState,
                    scaffoldState = scaffoldState,
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
    if (state.showAddGrocerySheet && state.selectedList != null) {
        AddGroceryBottomSheetNew(
            onDismissRequest = { onEvent(GroceryScreenEvent.ChangeShowAddGrocerySheet(false) ) },
            focusRequester = addGroceryFocusRequester,
            onConfirm = {
                onEvent(GroceryScreenEvent.AddGrocery)
            },
            selectedListId = state.selectedListId!!,
            onOpenLocationDialog = { onEvent(GroceryScreenEvent.ChangeShowSelectLocationDialog(true)) },
            selectedLocationId = state.addSheetSelectedLocationId,
            selectedLocationName = state.addSheetSelectedLocationName,
            onClearSelectedLocation = { onEvent(GroceryScreenEvent.ChangeAddSheetSelectedLocationId(null)) },
            nameTextState = state.addSheetNameState,
            detailsTextState = state.addSheetDetailState
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

    if (state.showSelectLocationDialog) {
        Dialog(
            onDismissRequest = { onEvent(GroceryScreenEvent.ChangeShowSelectLocationDialog(false)) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            SelectEditLocationButtonsNew(
                onCancel = { onEvent(GroceryScreenEvent.ChangeShowSelectLocationDialog(false)) },
                allLocations = state.locations,
                onAddLocation = { onEvent(GroceryScreenEvent.AddLocation(it)) },
                onSelectLocationId = { locationId ->
                    onEvent(GroceryScreenEvent.ChangeAddSheetSelectedLocationId(locationId))
                },
                onRemoveLocationId = { onEvent(GroceryScreenEvent.DeleteLocation(it)) },
                onReorderLocations = { _, _ -> },
                selectedLocationId = state.addSheetSelectedLocationId
            )
        }
    }
//
//    if (showAddFromRecipeDialog) {
//        var chosenRecipeId: UUID? by remember { mutableStateOf(null) }
//        var showAddGroceriesFromRecipeDialog by remember { mutableStateOf(false) }
//
//        SelectRecipeDialog(
//            onDismissRequest = { showAddFromRecipeDialog = false },
//            allRecipes = allRecipes.filter { recipe -> recipe.groceries.isNotEmpty() },
//            onClickRecipe = { recipeId ->
//                chosenRecipeId = recipeId
//                showAddGroceriesFromRecipeDialog = true
//            },
//            selectedRecipeIds = null,
//            showSubtitle = true,
//            activeRecipeIds = null
////            searchFocusRequester = searchRecipesFocusRequester
//        )
//        if (showAddGroceriesFromRecipeDialog && selectedCategoryId != null) {
//            AddGroceriesFromRecipeDialog(
//                onDismissRequest = { showAddGroceriesFromRecipeDialog = false },
//                recipe = allRecipes.fastFirst { it.id == chosenRecipeId },
//                includeCategoryChoice = true,
//                groceryCategories = categories,
//                firstSelectedCategoryId = selectedCategoryId,
//                onConfirm = { groceries, addingOption, categoryId ->
//                    groceryViewModel.addToGroceriesFromRecipe(
//                        groceries,
//                        addingOption,
//                        categoryId!!,
//                        chosenRecipeId!!,
//                        context
//                    )
//                    showAddGroceriesFromRecipeDialog = false
//                    showAddFromRecipeDialog = false
//                }
//            )
//        }
//    }
//
//    if (showSharingDialog) {
//        ShareGroceriesSheet(
//            onDismissRequest = { showSharingDialog = false },
//            selectedGroceriesNumber = selectedGroceryItems.size,
//            onShare = { shareOption ->
//                if (selectedCategoryId == null)
//                    return@ShareGroceriesSheet
//
//                val items = selectedCategory!!.items.filter { selectedGroceryItems.contains(it.id) }
//                if (shareOption == GroceryShareOption.Text) {
//                    val text = getShareTextFromGroceryItems(items)
//                    shareText(context, text)
//                } else if (shareOption == GroceryShareOption.Json) {
//                    val downloadsDir = getDownloadsDir(context)
//                    if (downloadsDir == null) {
//                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
//                        return@ShareGroceriesSheet
//                    }
//                    val groceriesJson = groceryViewModel.getJson(items)
//                    val groceriesJsonFile = File(
//                        downloadsDir,
//                        "${resources.getString(R.string.groceries_json)}.json"
//                    ).apply { writeText(groceriesJson) }
//                    shareFile(context, groceriesJsonFile)
//                }
//                showSharingDialog = false
//            }
//        )
//    }
//
//    var importGroceryItems: List<GroceryItem> by remember { mutableStateOf(listOf()) }
//
//    LaunchedEffect (importJsonContent) {
//        if (importJsonContent == null)
//            return@LaunchedEffect
//        val importGroceries: ListOfSaveableGroceryItems? =
//            getJsonFromString<ListOfSaveableGroceryItems>(context, importJsonContent)
//        if (importGroceries == null) {
//            Toast.makeText(context, "Could not read Groceries file", Toast.LENGTH_SHORT).show()
//            onHandledJsonImport()
//            return@LaunchedEffect
//        }
//
//        importGroceryItems = getGroceriesFromSaveable(importGroceries)
//        importGroceryItems = importGroceryItems.map { item ->
//            val doesRecipeExist = item.recipeId != null && allRecipes.any { it.id == item.recipeId }
//            val locationId = groceryLocations.fastFirstOrNull { it.groceryNames.contains(item.name) }?.id
//            GroceryItem(item.name, item.details, if (doesRecipeExist) item.recipeId else null, locationId, null, item.id)
//        }
//
//        Log.d("LaunchedEffect", "Importing Groceries: $importGroceryItems")
//    }
//
//    if (importJsonContent != null && importGroceryItems.isNotEmpty()) {
//        ImportGroceriesSheet(
//            onDismissRequest = {
//                importGroceryItems = listOf()
//                onHandledJsonImport()
//            },
//            categories = categories,
//            importGroceryItems = importGroceryItems,
//            onImport = { chosenImportGroceryItemIds, chosenCategoryId ->
//                val items = importGroceryItems.filter { chosenImportGroceryItemIds.contains(it.id) }
//                groceryViewModel.addToGroceries(items, chosenCategoryId)
//                Toast.makeText(context, resources.getString(R.string.imported_groceries, chosenImportGroceryItemIds.size), Toast.LENGTH_SHORT).show()
//                importGroceryItems = listOf()
//                onHandledJsonImport()
//            },
//            importingFileName = importingFile,
//            getRecipeNameFromId = getRecipeNameFromId
//        )
//    }
}