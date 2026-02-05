package com.jule.food

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.delay
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

    val selectedCategoryDeletedItems = groceryViewModel.selectedCategoryDeletedItems
    val selectedCategoryShowDeletedItems = groceryViewModel.selectedCategoryShowDeletedItems

    val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId }

    val selectedGroceryItems = remember { mutableStateListOf<UUID>() }
    val selectionModeActive = selectedGroceryItems.isNotEmpty()

    var isEditingCategories by remember { mutableStateOf(false) }


    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
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
    LaunchedEffect(scaffoldState.bottomSheetState.targetValue) {
        if (scaffoldState.bottomSheetState.targetValue == SheetValue.Hidden || scaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded) {
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
            AnimatedContent(
                targetState = selectionModeActive,
                transitionSpec = {
                    slideInVertically { -it } togetherWith slideOutVertically { -it }
                }
            ) { selectionMode ->
                if (!selectionMode || selectedCategory == null) {
                    AnimatedContent(
                        targetState = isEditingCategories
                    ) { editingCategories ->
                        if (!editingCategories) {
                            CenterAlignedTopAppBar(
                                title = { Text(stringResource(R.string.groceries)) },
                                actions = {
                                    var menuExpanded by remember { mutableStateOf(false) }
                                    IconButtonWithTooltip(onClick = { menuExpanded = true }, tooltipText = stringResource(R.string.more)) {
                                        Icon(painter = painterResource(R.drawable.more_vert), contentDescription = stringResource(R.string.more))
                                    }
                                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.share_groceries)) },
                                            leadingIcon = { Icon(painter = painterResource(id = R.drawable.share), contentDescription = null) },
                                            onClick = {
                                                showSharingDialog = true
                                                menuExpanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Column {
                                                Text(stringResource(R.string.import_groceries))
                                                Text(stringResource(R.string.from_json_file), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                                            } },
                                            leadingIcon = { Icon(painter = painterResource(id = R.drawable.import_data), contentDescription = null) },
                                            onClick = {
                                                onPickJsonFile()
                                                menuExpanded = false
                                            }
                                        )
                                    }
                                },
                                navigationIcon = {
                                    IconButtonWithTooltip (onClick = onOpenSettings, tooltipText = stringResource(R.string.settings)) {
                                        Icon(Icons.Outlined.Settings, stringResource(R.string.settings))
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                            )
                        } else {
                            EditScreenTopBar(
                                title = stringResource(R.string.edit_categories),
                                backgroundColor = MaterialTheme.colorScheme.background,
                                onBack = {
                                    isEditingCategories = false
                                },
                                modifier = Modifier.windowInsetsPadding(insets = WindowInsets.statusBars)
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.height(TopAppBarDefaults.MediumAppBarCollapsedHeight)){

                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            shape = CircleShape,
                            modifier = Modifier.height(TopAppBarDefaults.MediumAppBarCollapsedHeight).padding(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(end = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                                IconButtonWithTooltip(
                                    onClick = { selectedGroceryItems.clear() },
                                    tooltipText = stringResource(R.string.clear_selection)
                                ) {
                                    Icon(painterResource(R.drawable.clear), contentDescription = stringResource(R.string.clear_selection))
                                }
                                Text(selectedGroceryItems.size.toString())
                            }
                        }
                    }
//                    SelectionTopBar(
//                        numberSelected = selectedGroceryItems.size,
//                        onClearSelection = {
//                            selectedGroceryItems.clear()
//                        },
//                        actions = {}
//                    )
                }
            }
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
                    selectedGroceryItems = selectedGroceryItems,
                    onAddToSelection = { id -> selectedGroceryItems.add(id) },
                    onRemoveFromSelection = { id -> selectedGroceryItems.remove(id) },
                    deletedItems = selectedCategoryDeletedItems,
                    onAddToDeletedItems = { groceryItem -> groceryViewModel.addToDeletedItems(groceryItem, selectedCategoryId!!) },
                    onRemoveFromDeletedItems = { id -> groceryViewModel.removeFromDeletedItems(id, selectedCategoryId!!) },
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
                    modifier = modifier
                )
            } else {
                LoadingGroceryGridScreen()
            }
        }
    }

    val addGroceryFocusRequester = remember { FocusRequester() }
    LaunchedEffect(showAddGroceryDialog) {
        if (showAddGroceryDialog) {
            addGroceryFocusRequester.requestFocus()
        }
    }
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
            getRecipeNameFromId = getRecipeNameFromId
        )
    }
    val searchRecipesFocusRequester = remember { FocusRequester() }
    LaunchedEffect(showAddFromRecipeDialog) {
        if (showAddFromRecipeDialog) {
            scope.launch {
                delay(100)
                searchRecipesFocusRequester.requestFocus()
            }
        }
    }

    if (showAddFromRecipeDialog) {
        var chosenRecipeId: UUID? by remember { mutableStateOf(null) }
        var showAddGroceriesFromRecipeDialog by remember { mutableStateOf(false) }

        SelectRecipeBottomSheet(
            onDismissRequest = { showAddFromRecipeDialog = false },
            allRecipes = allRecipes,
            onSelectRecipe = { recipeId ->
                chosenRecipeId = recipeId
                showAddGroceriesFromRecipeDialog = true
            },
            searchFocusRequester = searchRecipesFocusRequester
        )
        if (showAddGroceriesFromRecipeDialog && selectedCategoryId != null) {
            AddGroceriesFromRecipeDialog(
                onDismissRequest = { showAddGroceriesFromRecipeDialog = false },
                recipe = allRecipes.fastFirst { it.id == chosenRecipeId },
                includeCategoryChoice = false,
                groceryCategories = null,
                onConfirm = { groceries, _ ->
                    groceryViewModel.addToGroceries(groceries, selectedCategoryId, chosenRecipeId!!, context)
                    showAddGroceriesFromRecipeDialog = false
                    showAddFromRecipeDialog = false
                }
            )
        }
    }

    if (showSharingDialog && selectedCategoryId != null) {
        ShareGroceriesSheet(
            onDismissRequest = { showSharingDialog = false },
            groceryCategories = categories,
            currentCategoryId = selectedCategoryId,
            onShare = { shareCategoryIds, shareOption ->
                val shareCategories = categories.filter { shareCategoryIds.contains(it.id) }
                if (shareOption == GroceryShareOption.Text) {
                    val text = getShareTextFromCategories(shareCategories)
                    shareText(context, text)
                } else if (shareOption == GroceryShareOption.Json) {
                    val downloadsDir = getDownloadsDir(context)
                    if (downloadsDir == null) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        return@ShareGroceriesSheet
                    }
                    val groceriesJson = groceryViewModel.getJson(shareCategories)
                    val groceriesJsonFile = File(downloadsDir, "${resources.getString(R.string.groceries_json)}.json").apply { writeText(groceriesJson)}
                    shareFile(context, groceriesJsonFile)
                }
                showSharingDialog = false
            }
        )
    }

    if (importJsonContent != null) {
        val importSaveableCategories: SaveableGroceryItemCategories? = getJsonFromString<SaveableGroceryItemCategories>(context, importJsonContent)
        if (importSaveableCategories == null) {
            Toast.makeText(context, "Could not read Groceries file", Toast.LENGTH_SHORT).show()
            onHandledJsonImport()
            return
        }

        val importCategories = getCategoriesFromSaveable(importSaveableCategories)


        ImportGroceriesSheet(
            onDismissRequest = {
                onHandledJsonImport()
            },
            groceryCategories = categories,
            importCategories = importCategories,
            onImport = { chosenImportCategoriesIds, chosenImportOptions ->
                chosenImportCategoriesIds.forEachIndexed { index, importCategoryId ->
                    val importCategory = importCategories.fastFirstOrNull { it.id == importCategoryId }
                    if (importCategory == null) {
                        return@forEachIndexed
                    }
                    val categoryWithSameName = categories.fastFirstOrNull { it.name == importCategory.name }
                    // Has same name -> check option for conflict resolution
                    if (categoryWithSameName != null) {
                        val chosenOption = chosenImportOptions[index]
                        Log.d("onImport", "Name \"${importCategory.name}\" already exists. Chosen option: $chosenOption")
                        when (chosenOption) {
                            ImportGroceryCategoryOption.Merge -> {
                                groceryViewModel.addToGroceries(items = importCategory.items, categoryId = categoryWithSameName.id)
                            }
                            ImportGroceryCategoryOption.Replace -> {
                                groceryViewModel.removeCategory(categoryWithSameName.id)
                                groceryViewModel.addCategory(importCategory)
                            }
                            ImportGroceryCategoryOption.AddNew -> {
                                groceryViewModel.addCategory(importCategory)
                            }
                        }
                    } else {
                        Log.d("onImport", "Name \"${importCategory.name}\" doesn't exist yet. Adding as new category.")
                        groceryViewModel.addCategory(importCategory)
                    }
                }
                onHandledJsonImport()
            },
            importingFileName = importingFile
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
    allRecipes: List<Recipe>,
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
    selectedGroceryItems: List<UUID>,
    onAddToSelection: (UUID) -> Unit,
    onRemoveFromSelection: (UUID) -> Unit,
    onClearSelection: () -> Unit,
    deletedItems: List<GroceryItem>,
    onAddToDeletedItems: (GroceryItem) -> Unit,
    onRemoveFromDeletedItems: (UUID) -> Unit,
    showDeletedItems: Boolean,
    onChangeShowDeletedItems: (Boolean) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    isEditingCategories: Boolean,
    onChangeIsEditingCategories: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectionModeActive = selectedGroceryItems.isNotEmpty()
    val singleSelection = selectedGroceryItems.size == 1

    val editGroceryNameState = rememberTextFieldState("")
    val editGroceryDetailState = rememberTextFieldState("")

    var showGroupingSheet by remember { mutableStateOf(false) }


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
                getRecipeNameFromId = getRecipeNameFromId
            )
        },
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetDragHandle = {
            Row(modifier = Modifier.height(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier.width(50.dp).height(5.dp)) {}
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    enabled = selectionModeActive,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
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
                    groupingOption = groupingOption,
                    onRemoveFromGroceries = onRemoveFromGroceries,
                    onAddToGroceries = onAddToGroceries,
                    getRecipeNameFromId = getRecipeNameFromId,
                    selectionModeActive = selectionModeActive,
                    selectedGroceryItems = selectedGroceryItems,
                    onAddToSelection = onAddToSelection,
                    onRemoveFromSelection = onRemoveFromSelection,
                    deletedItems = deletedItems,
                    onAddToDeletedItems = onAddToDeletedItems,
                    onRemoveFromDeletedItems = onRemoveFromDeletedItems,
                    showDeletedItems = showDeletedItems,
                    onChangeShowDeletedItems = onChangeShowDeletedItems
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
                    .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20))
            )
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20))
            )
        }
        Row(modifier = Modifier.height(26.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    shape = FilterChipDefaults.shape
                ).width(100.dp).height(26.dp)
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
                    modifier = Modifier.aspectRatio(1.6f)
                        .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10))
                )
            }
            item() {
                Box(
                    modifier = Modifier.aspectRatio(1.6f)
                        .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10))
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
        groceryViewModel.addToGroceries(GroceryItem("Dosentomaten",""), default2Id)
        groceryViewModel.addToGroceries(GroceryItem("Milch",""), default2Id)

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
            onCancelImport = { }
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