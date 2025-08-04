package com.jule.food

import android.widget.CheckBox
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.launch
import java.util.UUID




// Problem: wenn ich die Katergorie lösche, die über der ist (heißt, ein index kleiner) die ich ausgewählt habe, stürt die app ab.

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    groceryViewModel: GroceryViewModel,
    getRecipeNameFromId: (UUID) -> String,
    bottomBar: @Composable () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {

    var showAddGroceryDialog by remember { mutableStateOf(false) }
    var showGroupingDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }


    val categories = groceryViewModel.groceryItemCategories
    val selectedCategoryIndex = groceryViewModel.selectedCategoryIndex
    val selectedGroupingOption = groceryViewModel.selectedGroupingOption
    val showDeletedItems = groceryViewModel.showDeletedItems


    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
        bottomBar = bottomBar,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.groceries)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, "Settings")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showGroupingDialog = true }, modifier = Modifier.size(50.dp)) {
                        Icon(painter = painterResource(id = R.drawable.categories), contentDescription = "Categories")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddGroceryDialog = true },
                text = { Text(stringResource(R.string.add_grocery)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
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
                    categories = categories,
                    selectedCategoryIndex = selectedCategoryIndex,
                    onChangeSelectedCategoryIndex = { newIndex ->
                        groceryViewModel.changeSelectedCategoryIndex(newIndex)
                    },
                    onRemoveFromGroceries = { index ->
                        groceryViewModel.removeFromGroceries(index, selectedCategoryIndex)
                    },
                    onAddToGroceries = { newItem ->
                        groceryViewModel.addToGroceries(newItem, selectedCategoryIndex)
                    },
                    onAddCategory = { newCategory ->
                        groceryViewModel.addCategory(newCategory)
                    },
                    onRemoveCategory = { index ->
                        groceryViewModel.removeCategory(index)
                    },
                    onChangeCategoryName = { index, name ->
                        groceryViewModel.changeCategoryName(index, name)
                    },
                    groupingOption = selectedGroupingOption,
                    showDeletedItems = showDeletedItems,
                    getRecipeNameFromId = getRecipeNameFromId,
                    snackbarHostState = snackbarHostState,
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
    if (showAddGroceryDialog) {
        AddGroceryDialog(
            onDismissRequest = { showAddGroceryDialog = false },
            onConfirm = { newItem ->
                groceryViewModel.addToGroceries(
                    newItem,
                    selectedCategoryIndex
                )
            },
            focusRequester = addGroceryFocusRequester
        )
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroceryGridScreen(
    categories: List<GroceryItemCategory>,
    selectedCategoryIndex: Int,
    onChangeSelectedCategoryIndex: (Int) -> Unit,
    onRemoveFromGroceries: (itemIndex: Int) -> Unit,
    onAddToGroceries: (GroceryItem) -> Unit,
    onAddCategory: (GroceryItemCategory) -> Unit,
    onRemoveCategory: (index: Int) -> Unit,
    onChangeCategoryName: (index: Int, String) -> Unit,
    showDeletedItems: Boolean,
    groupingOption: GroceryGroupingOption,
    getRecipeNameFromId: (UUID) -> String,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var editingItemIndex: Int? by remember { mutableStateOf(null) }
    var showEditGroceryDialog by remember { mutableStateOf(false) }

    var showCategoriesDialog by remember { mutableStateOf(false) }

    val deletedGroceryItems = remember { mutableStateListOf<GroceryItem>() }

        Column (
            modifier = modifier.fillMaxSize()
        ) {
            CategoriesConnectedButtons(
                categories = categories,
                selectedCategoryIndex = selectedCategoryIndex,
                onChangeSelectedCategoryIndex = onChangeSelectedCategoryIndex,
                onEditCategories = { showCategoriesDialog = true },
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (categories[selectedCategoryIndex].items.isEmpty() && deletedGroceryItems.isEmpty()) {
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
                    val allItems = categories[selectedCategoryIndex].items

                    val groupNames: MutableList<String> = mutableListOf()
                    val groups: MutableList<List<GroceryItem>> = mutableListOf()
                    when(groupingOption) {
                        GroceryGroupingOption.None -> {
                            groupNames.add("")
                            groups.add(allItems)
                        }

                        GroceryGroupingOption.Recipe -> {
                            val recipeGroups = allItems.groupBy { it.recipeId }
                            groupNames.addAll(recipeGroups.keys.map { recipeId ->
                                if (recipeId != null) getRecipeNameFromId(recipeId) else context.getString(R.string.no_recipe)
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
                        val isLast = index == groups.size-1
                        val isDeletedItems = showDeletedItems && isLast

//                        val customKey: String = "${groupNames[index]}$index"

                        if (index > 0 || groupingOption != GroceryGroupingOption.None) {
                            item (span = { GridItemSpan(maxLineSpan) }, key = groupNames[index]) {
                                Text(groupNames[index], style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), modifier = Modifier.padding(start = 10.dp).animateItem())
                            }
                        }
                        items(groceryItems, key = { groceryItem -> groceryItem.id }) { groceryItem ->
                            GroceryItemDisplay(
                                item = groceryItem,
                                onClick = {
                                    if (isDeletedItems) {
                                        deletedGroceryItems.remove(groceryItem)
                                        onAddToGroceries(groceryItem)
                                    } else {
                                        onRemoveFromGroceries(allItems.indexOf(groceryItem))
                                        deletedGroceryItems.add(groceryItem)
                                    }
                                },
                                onLongClick = {
                                    if (!isDeletedItems) {
                                        editingItemIndex = allItems.indexOf(groceryItem)
                                        showEditGroceryDialog = true
                                    }
                                },
                                itemColor = if (isDeletedItems) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary,
                                textColor = if (isDeletedItems) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary,
                                getRecipeNameFromId = getRecipeNameFromId,
                                showRecipeName = groupingOption != GroceryGroupingOption.Recipe,
                                modifier = Modifier.animateItem()
                            )
                        }

                        if (!isLast) {
                            item (span = { GridItemSpan(maxLineSpan) }) {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }
    if (showCategoriesDialog) {
        CategoriesDialog(
            categories = categories,
            selectedCategoryIndex = selectedCategoryIndex,
            onAddCategory = onAddCategory,
            onDeleteCategory = { index ->
                val category = categories[index]
                onRemoveCategory(index)

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

    val editGroceryFocusRequester = remember { FocusRequester() }
    if (showEditGroceryDialog) {
        AddGroceryDialog(
            title = stringResource(R.string.edit_grocery),
            onDismissRequest = { showEditGroceryDialog = false },
            onConfirm = { newItem ->
                onRemoveFromGroceries(editingItemIndex!!)
                onAddToGroceries(newItem)
                showEditGroceryDialog = false
            },
            imeActionDone = true,
            focusRequester = editGroceryFocusRequester,
            startValue = categories[selectedCategoryIndex].items[editingItemIndex!!].name,
            startDetails = categories[selectedCategoryIndex].items[editingItemIndex!!].details,
        )
    }
}

@Composable
fun AddGroceryDialog(
    onDismissRequest: () -> Unit,
    focusRequester: FocusRequester,
    onConfirm: (GroceryItem) -> Unit,
    focusDetailsOnNext: Boolean = false,
    allowDismissIfEmpty: Boolean = true,
    imeActionDone: Boolean = false,
    title: String = stringResource(R.string.new_grocery),
    startValue: String = "",
    startDetails: String = ""
) {
    var currentText by remember { mutableStateOf(startValue) }
    val isError = currentText.isEmpty()

    var currentDetailText by remember { mutableStateOf(startDetails)}
    val detailFocusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    fun confirm(){
        onConfirm(GroceryItem(currentText, currentDetailText))
        currentText = ""
        currentDetailText = ""
    }
    var focusManager = LocalFocusManager.current

    DefaultDialog(
        title = title,
        onDismissRequest = {
            if (currentText.isEmpty() && allowDismissIfEmpty)
                onDismissRequest()
            else {
                focusManager.clearFocus(true)
            }
        },
        onCancel = {
            onDismissRequest()
        },
        confirmEnabled = !isError,
        buttons = true,
        onConfirm = { confirm() }
    ) {
        focusManager = LocalFocusManager.current
        OutlinedTextField(value = currentText, onValueChange = { currentText = it }, modifier = Modifier
            .width(250.dp)
            .focusRequester(focusRequester), shape = RoundedCornerShape(20), placeholder = { Text(stringResource(id = R.string.name)) }, keyboardOptions = KeyboardOptions.Default.copy(imeAction = if (imeActionDone) ImeAction.Done else ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                if (focusDetailsOnNext) {
                    detailFocusRequester.requestFocus()
                } else {
                    confirm()
                }
            }, onDone = {
                confirm()
            })
        )
        val interactionSource = remember { MutableInteractionSource() }
        val detailColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        val detailPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

        BasicTextField(
            value = currentDetailText,
            onValueChange = { currentDetailText = it },
            modifier = Modifier
                .width(250.dp)
                .height(40.dp)
                .focusRequester(detailFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (!isError) {
                    confirm()
                    scope.launch {
                        focusRequester.requestFocus()
                    }
                }
            }),
            textStyle = TextStyle.Default.copy(color = detailColor),
            cursorBrush = SolidColor(detailColor),
            decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = currentDetailText,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                colors = TextFieldDefaults.colors().copy(
                    unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                    unfocusedPlaceholderColor = detailPlaceholderColor, focusedPlaceholderColor = detailPlaceholderColor,
                ),
                shape = RoundedCornerShape(20),
                placeholder = { Text(stringResource(id = R.string.details), maxLines = 1, style = MaterialTheme.typography.bodyMedium) }
            )
//            Box(modifier = Modifier
//                .width(150.dp)
//                .height(40.dp)
//                .padding(horizontal = 5.dp, vertical = 5.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
//                    shape = RoundedCornerShape(20)
//                )) {
//                innerTextField()
//            }
        })
    }
}


@Preview(showBackground = true)
@Composable
fun GroceryScreenPreview() {
    val navController = rememberNavController()
    val groceryViewModel: GroceryViewModel = viewModel()

    if (groceryViewModel.groceryItemCategories.isEmpty()) {
        groceryViewModel.addCategory("Default")
        groceryViewModel.addToGroceries(GroceryItem("Mehl", "500g"), 0)
        groceryViewModel.addToGroceries(GroceryItem("Spaghetti", ""), 0)
        groceryViewModel.addCategory("Default 2")
        groceryViewModel.addToGroceries(GroceryItem("Dosentomaten",""), 1)
        groceryViewModel.addToGroceries(GroceryItem("Milch",""), 1)
    }
    groceryViewModel.initializeEmpty()

    FoodTheme {
        GroceryScreen(groceryViewModel = groceryViewModel, bottomBar = { BottomNavigationBar(navController = navController, recipeViewModel = viewModel()) }, onOpenSettings = {}, getRecipeNameFromId = { it.toString() })
    }
}