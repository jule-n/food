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
import kotlinx.serialization.json.Json
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
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                                    IconButtonWithTooltip(onClick = { showSharingDialog = true }, tooltipText = stringResource(R.string.share)) {
                                        Icon(painter = painterResource(id = R.drawable.share), contentDescription = stringResource(R.string.share))
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
                    SelectionTopBar(
                        numberSelected = selectedGroceryItems.size,
                        onClearSelection = {
                            selectedGroceryItems.clear()
                        },
                        actions = {}
                    )
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
//                        FloatingActionButton()
//                    ExtendedFloatingActionButton(
//                        onClick = { },
//                        text = { Text(stringResource(R.string.add_grocery)) },
//                        icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
//                    )
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
                GroceryGridScreen(
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
                val shareCategories = shareCategoryIds.map { id -> categories.fastFirstOrNull { it.id == id } }.requireNoNulls()
                if (shareOption == GroceryShareOption.Text) {
                    val text = getShareTextFromCategories(shareCategories)
                    shareText(context, text)
                    showSharingDialog = false
                } else if (shareOption == GroceryShareOption.Json) {
                    val downloadsDir = getDownloadsDir(context)
                    if (downloadsDir == null) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        return@ShareGroceriesSheet
                    }
                    val groceriesJson = File(downloadsDir, "${context.getString(R.string.groceries_json)}.json").apply { writeText(groceryViewModel.getJson())}
                    shareFile(context, groceriesJson)
                }
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
            onDismissRequest = { onHandledJsonImport() },
            groceryCategories = categories,
            importCategories = importCategories
        )
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun GroceryGridScreen(
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
    val context = LocalContext.current

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
                                Column {
                                    CategoriesConnectedButtonsCustom(
                                        allCategories = allCategories,
                                        selectedCategoryId = category.id,
                                        onChangeSelectedCategoryId = onChangeSelectedCategoryId,
                                        onEnableEditMode = { onChangeIsEditingCategories(true) },
                                        modifier = Modifier
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
//                                        EditButton(
//                                            text = stringResource(R.string.edit_categories),
//                                            onClick = { onChangeIsEditingCategories(true) },
//                                            modifier = Modifier.height(26.dp)
//                                        )
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(20.dp),
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                            shape = RoundedCornerShape(20),
                                            modifier = Modifier.animateContentSize().padding(end = 5.dp, bottom = 5.dp),
                                            onClick = { showGroupingSheet = true }
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp), horizontalAlignment = Alignment.Start, modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 5.dp, top = 5.dp)) {
                                                Text(stringResource(R.string.group_by), style = MaterialTheme.typography.labelSmall)
                                                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(painterResource(groceryGroupingOptionsIcons[groupingOption]!!), contentDescription = null, modifier = Modifier.size(20.dp))
                                                    Text(stringResource(groceryGroupingOptionsDisplay[groupingOption]!!))
                                                }
                                            }
                                        }
                                    }
                                }
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
//            if (category.items.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(10.dp))
//            }
                AnimatedVisibility(
                    visible = !isEditingCategories,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5)
                    ) {
                        if (category.items.isEmpty()) {
                            Box(
                                modifier = Modifier
//                                .padding(10.dp)
                                    .fillMaxSize()
                            ) {
                                Text(
                                    "All done!",
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.displaySmallEmphasized
                                )
                            }
                        }

                        val spacerHeight by animateDpAsState(if (category.items.isEmpty()) 0.dp else 10.dp)
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(100.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 1000.dp)
                            .padding(start = 10.dp, end = 10.dp, top = spacerHeight)
                    ) {
                        val allItems = category.items

                        val groupNames: MutableList<String> = mutableListOf()
                        val groups: MutableList<List<GroceryItem>> = mutableListOf()
                        when (groupingOption) {
                            GroceryGroupingOption.None -> {
                                groupNames.add("")
                                groups.add(allItems)
                            }

                            GroceryGroupingOption.Recipe -> {
                                val recipeGroups = allItems.groupBy { it.recipeId }
                                groupNames.addAll(recipeGroups.keys.map { recipeId ->
                                    if (recipeId != null) getRecipeNameFromId(recipeId) else context.getString(
                                        R.string.no_recipe
                                    )
                                })
                                groupNames.sort()
                                groups.addAll(recipeGroups.values)
                            }

                            GroceryGroupingOption.Location -> {
                                groupNames.add("Location A")
                                groups.add(allItems)
                            }
                        }

//                        if (showDeletedItems && deletedGroceryItems.isNotEmpty()) {
//                            groupNames.add(context.getString(R.string.deleted))
//                            groups.add(deletedGroceryItems)
//                        }
                        if (!(groupNames.count() == 1 && groups[0].isEmpty())) {
                            groups.forEachIndexed { index, groceryItems ->
                                val isLast = index == groups.size - 1

                                if (index > 0 || groupingOption != GroceryGroupingOption.None) {
                                    gridGroupTitle(
                                        title = groupNames[index],
                                        key = groupNames[index],
                                        animate = true
                                    )
                                }
                                items(
                                    groceryItems,
                                    key = { groceryItem -> groceryItem.id }
                                ) { groceryItem ->
                                    GroceryItemDisplay(
                                        item = groceryItem,
                                        onClick = {
                                            if (selectionModeActive) {
                                                if (selectedGroceryItems.contains(groceryItem.id))
                                                    onRemoveFromSelection(groceryItem.id)
                                                else
                                                    onAddToSelection(groceryItem.id)
                                            } else {
                                                onRemoveFromGroceries(allItems.indexOf(groceryItem))
                                                onAddToDeletedItems(groceryItem)
                                            }
                                        },
                                        onLongClick = {
                                            if (!selectionModeActive) {
                                                onAddToSelection(groceryItem.id)
                                            }
                                        },
                                        getRecipeNameFromId = getRecipeNameFromId,
                                        showRecipeName = groupingOption != GroceryGroupingOption.Recipe,
                                        deleted = false,
                                        isSelected = selectedGroceryItems.contains(groceryItem.id),
                                        showSelection = selectionModeActive,
                                        modifier = Modifier.animateItem()
                                    )
                                }

                                gridSpacer(20.dp)
                            }
                        }
                        item(
                            key = 5092038540945087,
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            val alphaText by animateFloatAsState(if (deletedItems.isEmpty() || !showDeletedItems) 0.5f else 0.8f)
                            val alphaBackground by animateFloatAsState(if (deletedItems.isEmpty() || !showDeletedItems) 0.1f else 0.2f)
                            Box(modifier = Modifier.animateItem()) {
                            Surface(
                                onClick = { onChangeShowDeletedItems(!showDeletedItems) },
                                shape = RoundedCornerShape(20),
                                enabled = deletedItems.isNotEmpty(),
                                color = Color.Transparent
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp)) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaBackground),
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Box {
                                            Text(
                                                deletedItems.count().toString(),
                                                modifier = Modifier.align(Alignment.Center),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                    Text(
                                        stringResource(R.string.deleted),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                    Icon(
                                        painterResource(R.drawable.delete),
                                        contentDescription = stringResource(R.string.show_deleted_items),
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = alphaText)
                                    )
                                }}
                            }
                        }
                        if (showDeletedItems) {
                            items(
                                deletedItems.reversed(),
                                key = { it.id }
                            ) { groceryItem ->
                                GroceryItemDisplay(
                                    item = groceryItem,
                                    onClick = {
                                        if (!selectionModeActive) {
                                            onRemoveFromDeletedItems(groceryItem.id)
                                            onAddToGroceries(groceryItem)
                                        }
                                    },
                                    onLongClick = null,
                                    getRecipeNameFromId = getRecipeNameFromId,
                                    showRecipeName = true,
                                    deleted = true,
                                    isSelected = false,
                                    showSelection = false,
                                    modifier = Modifier.animateItem()
//                                        .scale(scaleX = scale, scaleY = scale)
                                )
                            }
                        } else {
                            if (deletedItems.isNotEmpty()) {
                                item(
                                    key = deletedItems.last().id
                                ) {
                                    var loadedItem by remember { mutableStateOf(false) }
                                    val alpha by animateFloatAsState(targetValue = if (loadedItem) 0f else 1f)
                                    LaunchedEffect(Unit) {
                                        loadedItem = true
                                    }
                                    val groceryItem = deletedItems.last()
                                    GroceryItemDisplay(
                                        item = groceryItem,
                                        onClick = { },
                                        onLongClick = null,
                                        clickingEnabled = false,
                                        getRecipeNameFromId = getRecipeNameFromId,
                                        showRecipeName = groupingOption != GroceryGroupingOption.Recipe,
                                        deleted = true,
                                        isSelected = false,
                                        showSelection = false,
                                        modifier = Modifier.animateItem().alpha(alpha)
                                    )
                                }

                            }
                        }
                    }
                }
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
//            ModalBottomSheet(
//                onDismissRequest = {
//                    showGroupingSheet = false
//                },
//                sheetMaxWidth = Dp.Unspecified
//            ) {
//                Column(
//                    modifier = Modifier.padding(horizontal = 10.dp)
////                    modifier = Modifier.padding(horizontal = 10.dp)
////                modifier = Modifier.padding(20.dp)
//                ) {
//                    Text(stringResource(R.string.group_by), style = MaterialTheme.typography.headlineSmall)
////                    Spacer(Modifier.height(10.dp))
//                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 10.dp)) {
//                        GroceryGroupingOption.entries.forEachIndexed { index, option ->
//                            val selected = groupingOption == option
//                            SettingDialogElement(
//                                title = stringResource(groceryGroupingOptionsDisplay[option]!!),
//                                selected = selected,
//                                onClick = {
//                                    onChangeGroupingOption(option)
//                                    showGroupingSheet = false
//                                },
//                                leadingIcon = {
//                                    Icon(
//                                        painterResource(groceryGroupingOptionsIcons[option]!!),
//                                        contentDescription = null
//                                    )
//                                },
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//                    }
//                }
//            }
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

    if (groceryViewModel.groceryItemCategories.isEmpty()) {
        val defaultId = groceryViewModel.addCategory("Default")
//        groceryViewModel.addToGroceries(GroceryItem("Mehl", "500g"), defaultId)
//        groceryViewModel.addToGroceries(GroceryItem("Spaghetti", ""), defaultId)

        val default2Id = groceryViewModel.addCategory("Default 2")
        groceryViewModel.addToGroceries(GroceryItem("Dosentomaten",""), default2Id)
        groceryViewModel.addToGroceries(GroceryItem("Milch",""), default2Id)

        groceryViewModel.initializeEmpty()
        groceryViewModel.changeSelectedCategoryId(defaultId)
    }


    FoodTheme {
        GroceryScreen(groceryViewModel = groceryViewModel, bottomBar = { BottomNavigationBar(navController = navController, recipeViewModel = viewModel()) }, onOpenSettings = {}, getRecipeNameFromId = { it.toString() }, allRecipes = listOf(), recipeDataLoaded = true, importJsonContent = null, onHandledJsonImport = {})
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