package com.jule.food

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit,
    currentTheme: ThemeSetting,
    onChangeTheme: (ThemeSetting) -> Unit,
    language: Languages,
    onChangeLanguage: (Languages) -> Unit,
    onOpenSettings: () -> Unit,
    groceryViewModel: GroceryViewModel = viewModel()
) {
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
        bottomBar = bottomBar,
    ) { innerPadding ->
        GroceryScreenWithoutViews(groceryViewModel = groceryViewModel, onOpenSettings = onOpenSettings, snackbarHostState = snackbarHostState, modifier = Modifier.padding(innerPadding))
    }
}

fun isCategoryError(name: String): Boolean {
    return name.isEmpty() || name.length > 20
}

// Problem: wenn ich die Katergorie lösche, die über der ist (heißt, ein index kleiner) die ich ausgewählt habe, stürt die app ab.

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroceryScreenWithoutViews(
    groceryViewModel: GroceryViewModel,
    onOpenSettings: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val categories = groceryViewModel.groceryItemCategories
    val currentCategoryIndex = groceryViewModel.selectedCategoryIndex
    val selectedCategory = if (categories.isEmpty()) GroceryItemCategory("") else categories[currentCategoryIndex]

    var editingItemIndex: Int? by remember { mutableStateOf(null) }
    var showEditGroceryDialog by remember { mutableStateOf(false) }

    var groceryInputState by remember { mutableStateOf(TextFieldValue(text = "")) }

    var changeModeEnabled by remember { mutableStateOf(false) }

    var showCategoriesDialog by remember { mutableStateOf(false) }
    var showAddGroceryDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun exitEditMode(cancel: Boolean = true) {
        if (groceryInputState.text == "" || cancel) {
            groceryViewModel.removeFromGroceries(
                editingItemIndex!!,
                currentCategoryIndex
            )
        }

        editingItemIndex = null
        groceryInputState = groceryInputState.copy(text = "")
        changeModeEnabled = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column {
            GroceryTitleBar(
                title = selectedCategory.name,
                onShowCategories = { showCategoriesDialog = true },
                onOpenSettings = {
                    onOpenSettings()
                },
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConnectedButtonGroup(
                    options = categories.map { it.name },
                    selectedOptionIndex = currentCategoryIndex,
                    onSelectedOptionChange = { newIndex ->
                        groceryViewModel.changeSelectedCategoryIndex(newIndex)
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                )
                Spacer(Modifier.width(10.dp))
                IconButton(onClick = { showCategoriesDialog = true }) {
                    Icon(painter = painterResource(id = R.drawable.edit), contentDescription = "Edit Categories")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (selectedCategory.items.isEmpty()) {
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
                GroceryGrid(
                    groceryItems = selectedCategory.items,
                    onClickItem = { index ->
                        val item = selectedCategory.items[index]
                        groceryViewModel.removeFromGroceries(index, currentCategoryIndex)

                        scope.launch {
                            val result = snackbarHostState.showSnackbar(message = context.getString(R.string.deleted_item, item.name), actionLabel = context.getString(R.string.undo), duration = SnackbarDuration.Short)
//
                            if (result == SnackbarResult.ActionPerformed) {
                                groceryViewModel.addToGroceries(item, currentCategoryIndex)
                            }
                        }
                    },
                    onLongClickItem = {
                        showEditGroceryDialog = true
                        editingItemIndex = it
                    },
//                editedItemIndex = editingItemIndex,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(1f)
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            ExtendedFloatingActionButton(
                onClick = { showAddGroceryDialog = true },
                text = { Text(stringResource(R.string.add_grocery)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(-20.dp, -20.dp)
            )
        }
    }
    if (showCategoriesDialog) {
        CategoriesDialog(
            categories = categories,
            selectedCategoryIndex = currentCategoryIndex,
            onChangeSelectedCategory = { newIndex ->
                groceryViewModel.changeSelectedCategoryIndex(newIndex)
                showCategoriesDialog = false
            },
            onAddCategory = { newCategory ->
                groceryViewModel.addCategory(newCategory.trim())
            },
            onDeleteCategory = { index ->
                val category = categories[index]
                groceryViewModel.removeCategory(index)

                scope.launch {
                    val result = snackbarHostState.showSnackbar(message = context.getString(R.string.deleted_item, category.name), actionLabel = context.getString(R.string.undo), duration = SnackbarDuration.Long)
//
                    if (result == SnackbarResult.ActionPerformed) {
                        groceryViewModel.addCategory(category)
                    }
                }
            },
            onDismissRequest = { showCategoriesDialog = false },
            onChangeCategoryName = { index, newName ->
                groceryViewModel.changeCategoryName(index, newName)
            },
        )
    }

    val addGroceryFocusRequester = remember { FocusRequester() }
    if (showAddGroceryDialog) {
        AddGroceryDialog(
            onDismissRequest = { showAddGroceryDialog = false },
            onConfirm = { newItem -> groceryViewModel.addToGroceries(newItem, currentCategoryIndex) },
            focusRequester = addGroceryFocusRequester
        )
    }
    LaunchedEffect(showAddGroceryDialog) {
        if (showAddGroceryDialog) {
            addGroceryFocusRequester.requestFocus()
        }
    }
    val editGroceryFocusRequester = remember { FocusRequester() }
//    LaunchedEffect(showEditGroceryDialog) {
//        if (showEditGroceryDialog) {
//            editGroceryFocusRequester.requestFocus()
//        }
//    }
    if (showEditGroceryDialog) {
        AddGroceryDialog(
            title = stringResource(R.string.edit_grocery),
            onDismissRequest = { showEditGroceryDialog = false },
            onConfirm = { newItem ->
                groceryViewModel.removeFromGroceries(editingItemIndex!!, currentCategoryIndex)
                groceryViewModel.addToGroceries(newItem, currentCategoryIndex)
                showEditGroceryDialog = false
            },
            imeActionDone = true,
            focusRequester = editGroceryFocusRequester,
            startValue = selectedCategory.items[editingItemIndex!!].name,
            startDetails = selectedCategory.items[editingItemIndex!!].details,
        )
    }
}

@Composable
fun GroceryTitleBar(
    modifier: Modifier = Modifier,
    title: String,
    onShowCategories: () -> Unit,
    onOpenSettings: () -> Unit
) {

    DefaultTopAppBar(
        title = title,
        actions = {
            IconButton(onClick = onOpenSettings) {
                Icon(painter = painterResource(R.drawable.settings), "Settings")
            }
        },
//        menuExpanded = menuExpanded,
//        onChangeMenuExpansion = { menuExpanded = it },
//        menuContent = {
//            DropdownMenuItem(text = { Text(stringResource(id = R.string.settings)) }, enabled = !editingTitle, leadingIcon = { Icon(
//                painterResource(R.drawable.settings), "Settings")}, onClick = {
//                    onOpenSettings()
//                    menuExpanded = false
//
//            })
//            DropdownMenuItem(text = { Text(stringResource(id = R.string.edit, "\"${titleState.text}\"")) }, enabled = !editingTitle, leadingIcon = { Icon(
//                painterResource(R.drawable.edit), "Edit")}, onClick = {
//                    editingTitle = true
//                    menuExpanded = false
//            })
//            DropdownMenuItem(text = { Text(stringResource(id = R.string.delete, "\"${titleState.text}\"")) }, enabled = !lastCategory, leadingIcon = { Icon(
//                painterResource(R.drawable.delete), "Delete", tint = MaterialTheme.colorScheme.error)}, onClick = {
//                    onDelete()
//                    menuExpanded = false
//            })
//        },
        navigationIcon = {
            IconButton(onClick = onShowCategories, modifier = Modifier.size(50.dp)) {
                Icon(painter = painterResource(id = R.drawable.categories), contentDescription = "Categories")
            }
        },
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun CategoriesDialog(
    categories: List<GroceryItemCategory>,
    selectedCategoryIndex: Int,
    onChangeSelectedCategory: (Int) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    onChangeCategoryName: (Int, String) -> Unit,
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var focusManager = LocalFocusManager.current

    var isEditingIndex: Int? by remember { mutableStateOf(null) }

    DefaultDialog(
        title = stringResource(id = R.string.categories),
        onDismissRequest = {
            if (isEditingIndex == null)
                onDismissRequest()
            else
                focusManager.clearFocus(true)
        },
//        showOverlay = showAddCategoryDialog,
        onClickDialogEnabled = isEditingIndex != null,
        onClickDialog = {
            focusManager.clearFocus(true)
        }
    ) {
        focusManager = LocalFocusManager.current
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            categories.forEachIndexed { index, category ->
                val isSelected = selectedCategoryIndex == index
                var textFieldValue by remember { mutableStateOf(TextFieldValue(category.name)) }
                var lastValueWithoutError by remember { mutableStateOf(category.name) }
//                if (isEditingIndex != index && textFieldValue.text != category.name)
//                    textFieldValue = textFieldValue.copy(text = category.name)

                val focusRequester = remember { FocusRequester() }
//                var submitted by remember { mutableStateOf(false) }

                LaunchedEffect(isEditingIndex) {
                    if (isEditingIndex == index) {
                        focusRequester.requestFocus()
                        textFieldValue = textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
//                        submitted = false
                    }
                }

                Surface(
                    onClick = { onChangeSelectedCategory(index) },
                    enabled = !isSelected,
                    color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(20)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth()) {
                        EditableText(
                            editable = isEditingIndex == index,
                            textAlign = TextAlign.Left,
                            textState = textFieldValue,
                            onTextChange = { newValue ->
                                if (!isCategoryError(newValue.text)) {
                                    lastValueWithoutError = newValue.text
                                }
                                textFieldValue = newValue
                            },
                            onSubmit = {
//                                if (!submitted)
//                                {
                                    isEditingIndex = null
                                    textFieldValue = textFieldValue.copy(text = lastValueWithoutError)
//                                    Log.d("OnSubmit", "Change category Name $index to $lastValueWithoutError")
                                    onChangeCategoryName(index, lastValueWithoutError.trim())
//                                    submitted = true
//                                }
                            },
                            focusRequester = focusRequester,
//                            style = MaterialTheme.typography.titleMedium,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
//                        Text(text = category.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)

                        val image = AnimatedImageVector.animatedVectorResource(R.drawable.edit_to_done)
                        IconButton(onClick = {
                            if (isEditingIndex == null)
                                isEditingIndex = index
                            else {
                                isEditingIndex = null
                                textFieldValue = textFieldValue.copy(text = lastValueWithoutError)
                                onChangeCategoryName(index, lastValueWithoutError.trim())
                            }
                        }) {
                            Icon(rememberAnimatedVectorPainter(image, isEditingIndex == index), contentDescription = "Edit/Done")
                        }
                        val deleteEnabled = categories.size > 1 && isEditingIndex != index
                        IconButton(onClick = { onDeleteCategory(index) }, enabled = deleteEnabled) {
                            Icon(painter = painterResource(id = R.drawable.delete), contentDescription = "Delete", tint = if (deleteEnabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                        }
                    }
                }
            }
            val enabled = categories.size < 10
//            val enabled = true
            if (enabled) {
                FloatingActionButton(
                    onClick = { showAddCategoryDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category")
                }
            } else {
                Surface(
                    color = ButtonDefaults.buttonColors().disabledContainerColor,
                    shape = FloatingActionButtonDefaults.shape,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(55.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(imageVector = Icons.Default.Add, modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center), contentDescription = null, tint = ButtonDefaults.buttonColors().disabledContentColor)
                    }
                }
            }
        }
    }

    val newCategoryFocusRequester = remember { FocusRequester() }
    LaunchedEffect(showAddCategoryDialog) {
        if (showAddCategoryDialog) {
//            delay(1000)
            newCategoryFocusRequester.requestFocus()
//            keyboard?.show()
        }
    }
    if (showAddCategoryDialog) {
        EnterTextDialog(
            title = stringResource(id = R.string.new_category),
            onDismissRequest = { showAddCategoryDialog = false },
            onConfirm = { name ->
                onAddCategory(name)
                showAddCategoryDialog = false
            },
            confirmWithKeyboard = true,
            isError = { isCategoryError(it) },
            focusRequester = newCategoryFocusRequester
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    FoodTheme {
        GroceryScreen(darkTheme = false, bottomBar = { BottomNavigationBar(navController = navController) }, currentTheme = ThemeSetting.System, onChangeTheme = {}, language = Languages.English, onChangeLanguage = {}, onOpenSettings = {})
    }
}