package com.jule.food

import android.util.Log
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID



@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    groceryViewModel: GroceryViewModel,
    getRecipeNameFromId: (UUID) -> String,
    allRecipes: List<Recipe>,
    bottomBar: @Composable () -> Unit,
    onOpenSettings: () -> Unit,
//    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAddGroceryDialog by remember { mutableStateOf(false) }
    var showGroupingDialog by remember { mutableStateOf(false) }
    var showAddFromRecipeDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }


    val categories = groceryViewModel.groceryItemCategories
    val selectedCategoryId = groceryViewModel.selectedCategoryId
    val selectedGroupingOption = groceryViewModel.selectedGroupingOption
    val showDeletedItems = groceryViewModel.showDeletedItems

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
            Log.d("GroceryScreen", "Target Value Hidden or Partially Expanded")
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
                                    IconButtonWithTooltip(onClick = { showGroupingDialog = true }, tooltipText = stringResource(R.string.group_groceries)) {
                                        Icon(painter = painterResource(id = R.drawable.group_groceries), contentDescription = stringResource(R.string.group_groceries))
                                    }
                                },
                                navigationIcon = {
                                    IconButtonWithTooltip (onClick = onOpenSettings, tooltipText = stringResource(R.string.settings)) {
                                        Icon(Icons.Outlined.Settings, stringResource(R.string.settings))
                                    }
                                },
                            )
                        } else {
                            EditScreenTopBar(
                                title = stringResource(R.string.edit_categories),
                                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
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
                    showDeletedItems = showDeletedItems,
                    getRecipeNameFromId = getRecipeNameFromId,
                    selectedGroceryItems = selectedGroceryItems,
                    onAddToSelection = { id -> selectedGroceryItems.add(id) },
                    onRemoveFromSelection = { id -> selectedGroceryItems.remove(id) },
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

//    var tempGroupingOption by remember { mutableStateOf(groceryViewModel.selectedGroupingOption) }
    if (showGroupingDialog) {
        ModalBottomSheet(
            onDismissRequest = {
//                groceryViewModel.changeSelectedGroupingOption(tempGroupingOption)
                showGroupingDialog = false
            },
            sheetMaxWidth = Dp.Unspecified
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier.padding(horizontal = 10.dp)
//                modifier = Modifier.padding(20.dp)
            ) {
                Text(stringResource(R.string.group_groceries), style = MaterialTheme.typography.headlineSmall)
                SettingsScreenCategory(
                    name = stringResource(R.string.group_by)
                ) {
                    ConnectedButtonGroup(
                        options = groceryGroupingOptionsDisplay.values.toList().map { stringResource(it) },
                        selectedOptionIndex = GroceryGroupingOption.entries.indexOf(selectedGroupingOption),
                        onSelectedOptionChange = { index ->
                            groceryViewModel.changeSelectedGroupingOption(GroceryGroupingOption.entries[index])
//                    tempGroupingOption = GroceryGroupingOption.entries[index]
                        },
                        checkedContainerColor = MaterialTheme.colorScheme.primary
                    )
                }
                DialogCheckbox(
                    checked = showDeletedItems,
                    onCheckedChange = { groceryViewModel.changeShowDeletedItems(it) },
                    label = stringResource(R.string.show_deleted_items)
                )
            }
        }
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
    showDeletedItems: Boolean,
    groupingOption: GroceryGroupingOption,
    getRecipeNameFromId: (UUID) -> String,
    selectedGroceryItems: List<UUID>,
    onAddToSelection: (UUID) -> Unit,
    onRemoveFromSelection: (UUID) -> Unit,
    onClearSelection: () -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    isEditingCategories: Boolean,
    onChangeIsEditingCategories: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val deletedGroceryItems = remember { mutableStateListOf<GroceryItem>() }

    val selectionModeActive = selectedGroceryItems.isNotEmpty()
    val singleSelection = selectedGroceryItems.size == 1

    val editGroceryNameState = rememberTextFieldState("")
    val editGroceryDetailState = rememberTextFieldState("")


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
        }
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
                                        modifier = Modifier
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth().height(26.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        EditButton(
                                            text = stringResource(R.string.edit_categories),
                                            onClick = { onChangeIsEditingCategories(true) }
                                        )
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
            Spacer(modifier = Modifier.height(10.dp))
            if (category.items.isEmpty() && (!showDeletedItems || deletedGroceryItems.isEmpty())) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        "No items yet ₍^. .^₎⟆",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.displaySmallEmphasized
                    )
                }
            } else {
                AnimatedVisibility(
                    visible = !isEditingCategories,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(100.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 1000.dp)
                            .padding(horizontal = 10.dp)
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
                                groups.addAll(recipeGroups.values)
                            }

                            GroceryGroupingOption.Location -> {
                                groupNames.add("Location A")
                                groups.add(allItems)
                            }
                        }

                        if (showDeletedItems && deletedGroceryItems.isNotEmpty()) {
                            groupNames.add(context.getString(R.string.deleted))
                            groups.add(deletedGroceryItems)
                        }

                        groups.forEachIndexed { index, groceryItems ->
                            val isLast = index == groups.size - 1
                            val isDeletedItems = showDeletedItems && deletedGroceryItems.isNotEmpty() && isLast

//                        val customKey: String = "${groupNames[index]}$index"

                            if (index > 0 || groupingOption != GroceryGroupingOption.None) {
                                gridGroupTitle(
                                    title = groupNames[index],
                                    key = "${groupNames[index]}_${isDeletedItems}",
                                    animate = true
                                )
                            }
                            items(
                                groceryItems,
                                key = { groceryItem -> groceryItem.id }) { groceryItem ->
                                GroceryItemDisplay(
                                    item = groceryItem,
                                    onClick = {
                                        if (selectionModeActive) {
                                            if (selectedGroceryItems.contains(groceryItem.id))
                                                onRemoveFromSelection(groceryItem.id)
                                            else
                                                onAddToSelection(groceryItem.id)
                                        } else {
                                            if (isDeletedItems) {
                                                deletedGroceryItems.remove(groceryItem)
                                                onAddToGroceries(groceryItem)
                                            } else {
                                                onRemoveFromGroceries(allItems.indexOf(groceryItem))
                                                deletedGroceryItems.add(groceryItem)
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        if (!isDeletedItems && !selectionModeActive) {
                                            onAddToSelection(groceryItem.id)
                                        }
                                    },
                                    getRecipeNameFromId = getRecipeNameFromId,
                                    showRecipeName = groupingOption != GroceryGroupingOption.Recipe,
                                    deleted = isDeletedItems,
                                    isSelected = selectedGroceryItems.contains(groceryItem.id),
                                    showSelection = selectionModeActive,
                                    modifier = Modifier.animateItem()
                                )
                            }

                            if (!isLast) {
                                gridSpacer(20.dp)
//                            item(span = { GridItemSpan(maxLineSpan) }) {
//                                Spacer(modifier = Modifier.height(20.dp))
//                            }
                            }
                        }
                    }
                }
            }

        }
    }


//    val editGroceryFocusRequester = remember { FocusRequester() }
//    if (showEditGroceryDialog) {
//        AddGroceryDialog(
//            title = stringResource(R.string.edit_grocery),
//            onDismissRequest = { showEditGroceryDialog = false },
//            onConfirm = { newItem ->
//                onRemoveFromGroceries(editingItemIndex!!)
//                onAddToGroceries(newItem)
//                showEditGroceryDialog = false
//            },
//            imeActionDone = true,
//            focusRequester = editGroceryFocusRequester,
//            startValue = category.items[editingItemIndex!!].name,
//            startDetails = category.items[editingItemIndex!!].details,
//        )
//    }
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
        groceryViewModel.addToGroceries(GroceryItem("Mehl", "500g"), defaultId)
        groceryViewModel.addToGroceries(GroceryItem("Spaghetti", ""), defaultId)

        val default2Id = groceryViewModel.addCategory("Default 2")
        groceryViewModel.addToGroceries(GroceryItem("Dosentomaten",""), default2Id)
        groceryViewModel.addToGroceries(GroceryItem("Milch",""), default2Id)

        groceryViewModel.initializeEmpty()
        groceryViewModel.changeSelectedCategoryId(defaultId)
    }


    FoodTheme {
        GroceryScreen(groceryViewModel = groceryViewModel, bottomBar = { BottomNavigationBar(navController = navController, recipeViewModel = viewModel()) }, onOpenSettings = {}, getRecipeNameFromId = { it.toString() }, allRecipes = listOf())
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