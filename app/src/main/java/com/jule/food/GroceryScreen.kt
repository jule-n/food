package com.jule.food

import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
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
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAddGroceryDialog by remember { mutableStateOf(false) }
    var showGroupingDialog by remember { mutableStateOf(false) }
    var showAddFromRecipeDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()


    val categories = groceryViewModel.groceryItemCategories
    val selectedCategoryId = groceryViewModel.selectedCategoryId
    val selectedGroupingOption = groceryViewModel.selectedGroupingOption
    val showDeletedItems = groceryViewModel.showDeletedItems

    val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId }

    val selectedGroceryItems = remember { mutableStateListOf<UUID>() }
    val selectionModeActive = selectedGroceryItems.isNotEmpty()

    val activeRecipes = allRecipes.filter { recipe ->
        selectedCategory!!.items.indexOfFirst { item -> item.recipeId == recipe.id } != -1
    }

    var assignRecipeToSelectedGroceriesDialogActive by remember { mutableStateOf(false) }


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

    BackHandler(enabled = selectionModeActive) {
        selectedGroceryItems.clear()
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
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(R.string.groceries)) },
                        actions = {
                            IconButton(onClick = { showGroupingDialog = true }) {
                                Icon(painter = painterResource(id = R.drawable.categories), contentDescription = "Categories")
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onOpenSettings) {
                                Icon(Icons.Outlined.Settings, "Settings")
                            }
                        },
                    )
                } else {
                    SelectionTopBar(
                        numberSelected = selectedGroceryItems.size,
                        onClearSelection = {
                            selectedGroceryItems.clear()
                        },
                        actions = {

                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (groceryViewModel.dataLoaded) {
                Box(modifier = Modifier.fillMaxWidth()) {

                    AnimatedVisibility(!selectionModeActive, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.Center)) {
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

                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(SplitButtonDefaults.TrailingIconSize).rotate(rotation))
                                }

                                DropdownMenu(
                                    expanded = expanded,
//                                        modifier = Modifier.align(Alignment.End),
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        onClick = { showAddFromRecipeDialog = true },
                                        text = { Text("Add from recipe") },
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
                    onAddCategory = { newCategory ->
                        groceryViewModel.addCategory(newCategory)
                    },
                    onRemoveCategory = { id ->
                        groceryViewModel.removeCategory(id)
                    },
                    onChangeCategoryName = { name, id ->
                        groceryViewModel.changeCategoryName(name, id)
                    },
                    onMoveItemsToCategory = { itemIds, fromCategoryId, toCategoryId ->
                        groceryViewModel.moveItemsToCategory(itemIds, fromCategoryId, toCategoryId)
                    },
                    groupingOption = selectedGroupingOption,
                    showDeletedItems = showDeletedItems,
                    getRecipeNameFromId = getRecipeNameFromId,
                    snackbarHostState = snackbarHostState,
                    selectedGroceryItems = selectedGroceryItems,
                    onAddToSelection = { id -> selectedGroceryItems.add(id) },
                    onRemoveFromSelection = { id -> selectedGroceryItems.remove(id) },
                    scaffoldState = scaffoldState,
                    onClearSelection = { selectedGroceryItems.clear() },
                    modifier = modifier
                )
            } else {
                Box(
                    modifier = modifier.fillMaxSize()
                ) {
                    LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
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
            activeRecipes = activeRecipes,
            getRecipeNameFromId = getRecipeNameFromId
        )
    }

    if (showAddFromRecipeDialog) {
        SelectRecipeBottomSheet(
            onDismissRequest = { showAddFromRecipeDialog = false },
            allRecipes = allRecipes,
            activeRecipes = activeRecipes,
            onClickRecipe = { recipeId ->
                showAddFromRecipeDialog = false
                Toast.makeText(context, "Chose recipe ${allRecipes.fastFirst {it.id == recipeId}.name}", Toast.LENGTH_SHORT).show()
            }
        )
//        SelectRecipeDialog
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

    if (assignRecipeToSelectedGroceriesDialogActive && selectedCategory != null) {
        SelectRecipeBottomSheet(
            onDismissRequest = { assignRecipeToSelectedGroceriesDialogActive = false },
            onClickRecipe = { recipeId ->
                val items = selectedCategory.items
                selectedGroceryItems.forEach { itemId ->
                    val result = items.fastFirstOrNull { it.id == itemId }
                    if (result != null)
                        result.recipeId = recipeId
                }
                assignRecipeToSelectedGroceriesDialogActive = false
            },
            allRecipes = allRecipes,
            activeRecipes = activeRecipes
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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
    onAddCategory: (GroceryItemCategory) -> Unit,
    onRemoveCategory: (id: UUID) -> Unit,
    onChangeCategoryName: (String, id: UUID) -> Unit,
    showDeletedItems: Boolean,
    groupingOption: GroceryGroupingOption,
    getRecipeNameFromId: (UUID) -> String,
    snackbarHostState: SnackbarHostState,
    selectedGroceryItems: List<UUID>,
    onAddToSelection: (UUID) -> Unit,
    onRemoveFromSelection: (UUID) -> Unit,
    onClearSelection: () -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showCategoriesDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                groceryDetailState = editGroceryDetailState
            )
        },
        sheetDragHandle = {
            Box(modifier = Modifier.fillMaxWidth()) {
                BottomSheetDefaults.DragHandle(modifier = Modifier.align(Alignment.Center))
                TextButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = {
                    if (singleSelection) {
                        onChangeItemNameDetails(selectedGroceryItems[0], editGroceryNameState.text.toString().trim(), editGroceryDetailState.text.toString().trim())
                    }
                    onClearSelection()
                }) {
                    Text(stringResource(R.string.done))
                }
            }
        }
//        sheetPeekHeight = 180.dp + 20.dp,
    ) {
        Column(
            modifier = modifier.fillMaxSize().clickable(enabled = selectionModeActive, interactionSource = remember { MutableInteractionSource() }, indication = null) {
                onClearSelection()
            }
        ) {
            CategoriesConnectedButtons(
                allCategories = allCategories,
                selectedCategory = category,
                onChangeSelectedCategoryId = onChangeSelectedCategoryId,
                onEditCategories = { showCategoriesDialog = true },
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (category.items.isEmpty() && deletedGroceryItems.isEmpty()) {
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
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
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

                    if (showDeletedItems) {
                        groupNames.add(context.getString(R.string.deleted))
                        groups.add(deletedGroceryItems)
                    }

                    groups.forEachIndexed { index, groceryItems ->
                        val isLast = index == groups.size - 1
                        val isDeletedItems = showDeletedItems && isLast

//                        val customKey: String = "${groupNames[index]}$index"

                        if (index > 0 || groupingOption != GroceryGroupingOption.None) {
                            gridGroupTitle(
                                title = groupNames[index],
                                key = groupNames[index],
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
    if (showCategoriesDialog) {
        CategoriesDialog(
            allCategories = allCategories,
            onAddCategory = onAddCategory,
            onDeleteCategory = { id ->
                onRemoveCategory(id)

                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = context.getString(
                            R.string.deleted_item,
                            category.name
                        ),
                        actionLabel = context.getString(R.string.undo),
                        duration = SnackbarDuration.Long
                    )
//
                    if (result == SnackbarResult.ActionPerformed) {
                        onAddCategory(category)
                    }
                }
            },
            onDismissRequest = { showCategoriesDialog = false },
            onChangeCategoryName = onChangeCategoryName
        )
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