package com.jule.food.ui.groceries

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryLocation
import com.jule.food.R
import com.jule.food.data.Recipe
import com.jule.food.ui.groceries_recipes.SelectEditLocationButtons
import com.jule.food.utils.DefaultDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroceryBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    focusRequester: FocusRequester,
    onConfirm: (GroceryItem) -> Unit,
    allRecipes: List<Recipe>,
    getRecipeNameFromId: ((UUID) -> String),
    activeRecipeIds: List<UUID>,
    groceryLocations: List<GroceryLocation>,
    onAddGroceryLocation: (String) -> Unit,
    onRemoveGroceryLocation: (UUID) -> Unit,
    onAddGroceryToLocation: (String, UUID) -> Unit,
    onRemoveGroceryFromAllLocations: (String) -> Unit,
    onChangeLocationName: (String, UUID) -> Unit,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    val groceryNameState = rememberTextFieldState("")
    val groceryDetailState = rememberTextFieldState("")


    var selectedRecipeId: UUID? by remember { mutableStateOf(null) }
    var showRecipeSelection by remember { mutableStateOf(false) }

    var changedGroceryLocationManually by remember { mutableStateOf(false) }
    var selectedGroceryLocationId: UUID? by remember { mutableStateOf(null) }
    val selectedGroceryLocation = groceryLocations.firstOrNull { it.id == selectedGroceryLocationId }
    var showLocationSelection by remember { mutableStateOf(false) }

    val placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val anythingChanged = groceryNameState.text.isNotEmpty() || groceryDetailState.text.isNotEmpty() || selectedRecipeId != null || selectedGroceryLocationId != null
    var showConfirmDiscardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(groceryNameState.text) {
        if (!changedGroceryLocationManually) {
            val groceryName = groceryNameState.text.toString()
            run {
                // If grocery name is empty, don't bother looking through locations and just reset grocery location
                if (groceryName.isEmpty()) {
                    selectedGroceryLocationId = null
                    return@run
                }
                groceryLocations.forEach { location ->
                    val inLocation = location.groceryNames.contains(groceryName)
                    if (inLocation) {
                        selectedGroceryLocationId = location.id
                        return@run
                    }
                }
                // Name not found in any locations, resetting selected location
                selectedGroceryLocationId = null
            }
        }
    }

    fun confirm(){
        val newGroceryItem = GroceryItem(
            groceryNameState.text.toString().trim(),
            groceryDetailState.text.toString().trim(),
            recipeId = selectedRecipeId,
            locationId = selectedGroceryLocationId
        )
        onConfirm(newGroceryItem)

        if (selectedGroceryLocationId == null && changedGroceryLocationManually) {
            onRemoveGroceryFromAllLocations(groceryNameState.text.toString().trim())
        }

        if (selectedGroceryLocationId != null && changedGroceryLocationManually) {
            onAddGroceryToLocation(groceryNameState.text.toString().trim(), selectedGroceryLocationId!!)
            selectedGroceryLocationId = null
        }

        changedGroceryLocationManually = false

        groceryNameState.clearText()
        groceryDetailState.clearText()
        focusRequester.requestFocus()
    }

    ModalBottomSheet (
        sheetState = sheetState,
        onDismissRequest = {
            if (anythingChanged)
                showConfirmDiscardDialog = true
            else
                onDismissRequest()
        },
        dragHandle = null,
        sheetGesturesEnabled = !showRecipeSelection && !showLocationSelection,
        modifier = modifier
    ) {
        GroceryBottomSheetContentWithRecipeSelection(
            onSelectRecipe = { recipeId ->
                selectedRecipeId = recipeId
                showRecipeSelection = false
                focusRequester.requestFocus()
            },
            onExitRecipeSelection = {
                showRecipeSelection = false
                focusRequester.requestFocus()
            },
            showRecipeSelection = showRecipeSelection,
            showLocationSelection = showLocationSelection,
            onSelectLocation = { locationId ->
                selectedGroceryLocationId = locationId
                changedGroceryLocationManually = true
                showLocationSelection = false
                focusRequester.requestFocus()
            },
            onAddNewLocation = onAddGroceryLocation,
            onRemoveLocation = onRemoveGroceryLocation,
            onExitLocationSelection = {
                showLocationSelection = false
                focusRequester.requestFocus()
            },
            allLocations = groceryLocations,
            allRecipes = allRecipes,
            activeRecipeIds = activeRecipeIds,
            onChangeLocationName = onChangeLocationName,
            onReorderLocations = onReorderLocations,
            selectedLocationId = selectedGroceryLocationId,
            selectedRecipeId = selectedRecipeId
        ) {
            Column {
                GroceryBottomSheetInputs(
                    groceryNameState = groceryNameState,
                    groceryDetailState = groceryDetailState,
                    focusRequester = focusRequester,
                    onConfirm = { if (!showLocationSelection) confirm() else showLocationSelection = false }
                )
                Spacer(Modifier.height(10.dp))
                BoxWithConstraints {
                    val width = this.maxWidth / 3
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                    Row(modifier = Modifier.widthIn(max = width)) {
                        GroceryBottomSheetSelectionField(
                            text = if (selectedRecipeId != null) getRecipeNameFromId(
                                selectedRecipeId!!
                            ) else stringResource(
                                R.string.no_recipe
                            ),
                            icon = R.drawable.book,
                            isActive = selectedRecipeId != null,
                            inactiveColor = placeholderColor,
                            onClick = { showRecipeSelection = true },
                            onClear = { selectedRecipeId = null },

                            )
                    }
                    Row(modifier = Modifier.widthIn(max = width)) {
                        GroceryBottomSheetSelectionField(
                            text = if (selectedGroceryLocationId != null) selectedGroceryLocation!!.name else stringResource(
                                R.string.no_location
                            ),
                            icon = R.drawable.location,
                            isActive = selectedGroceryLocationId != null,
                            inactiveColor = placeholderColor,
                            onClick = { showLocationSelection = true },
                            onClear = {
                                selectedGroceryLocationId = null
                                changedGroceryLocationManually = true
                            }
                        )
                    }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            TextButton(
                                onClick = { confirm() },
                                enabled = groceryNameState.text.isNotEmpty()
                            ) {
                                Text(stringResource(R.string.save), maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
    // Dialog for confirming discard of item
    if (showConfirmDiscardDialog) {
        DefaultDialog(
            title = stringResource(R.string.discard),
            buttons = true,
            onConfirm = {
                showConfirmDiscardDialog = false
                onDismissRequest()
            },
            onDismissRequest = {
                showConfirmDiscardDialog = false
                scope.launch {
                    sheetState.expand()
                }
            }
        ) {
            Text(stringResource(R.string.discard_item))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroceryBottomSheetBasic(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    focusRequester: FocusRequester,
    onConfirm: (GroceryItem) -> Unit,
) {
    val groceryNameState = rememberTextFieldState("")
    val groceryDetailState = rememberTextFieldState("")

    val placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    fun confirm(){
        val newGroceryItem = GroceryItem(
            groceryNameState.text.toString().trim(),
            groceryDetailState.text.toString().trim()
        )
        onConfirm(newGroceryItem)

        groceryNameState.clearText()
        groceryDetailState.clearText()
        focusRequester.requestFocus()
//        recipeActive = false
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet (
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        modifier = modifier
    ) {
        GroceryBottomSheetInputs(
            groceryNameState = groceryNameState,
            groceryDetailState = groceryDetailState,
            focusRequester = focusRequester,
            onConfirm = { confirm() }
        )
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(
                onClick = { confirm() },
                enabled = groceryNameState.text.isNotEmpty()
            ) {
                Text(stringResource(R.string.save), maxLines = 1)
            }
        }
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
//            Box(modifier = Modifier.weight(1f)) {
//                TextButton(
//                    onClick = { confirm() },
//                    enabled = groceryNameState.text.isNotEmpty(),
//                    modifier = Modifier.align(Alignment.CenterEnd)
//                ) {
//                    Text(stringResource(R.string.save), maxLines = 1)
//                }
//            }
//        }
    }
}

@Composable
fun GroceryBottomSheetSelectionField(
    modifier: Modifier = Modifier,
    text: String,
    isActive: Boolean,
    inactiveColor: Color,
    onClick: () -> Unit,
    showClearButton: Boolean = true,
    onClear: (() -> Unit)? = null,
    activeIconColor: Color = MaterialTheme.colorScheme.primary,
    inactiveIconColor: Color = MaterialTheme.colorScheme.onBackground,
    activeColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
    @DrawableRes icon: Int,
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50),
        onClick = onClick,
        modifier = modifier
    ) {
        BoxWithConstraints {
            val textWidth = if (isActive) maxWidth - 24.dp - 5.dp - 40.dp else maxWidth - 24.dp - 5.dp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(40.dp)
                    .animateContentSize()
                    .padding(start = 5.dp)
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = if (isActive) activeIconColor else inactiveIconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isActive) activeColor else inactiveColor,
                    modifier = Modifier.widthIn(max = textWidth)
                )
                if (isActive && showClearButton) {
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = { onClear?.invoke() }
                    ) {
                        Icon(painterResource(R.drawable.clear), contentDescription = "Clear")
                    }
                } else {
                    Spacer(Modifier.width(10.dp))
                }
            }
        }
    }
}

@Composable
fun GroceryBottomSheetInputs(
    groceryNameState: TextFieldState,
    groceryDetailState: TextFieldState,
    focusRequester: FocusRequester,
    onConfirm: () -> Unit,
    placeholderColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
) {
    val detailFocusRequester = remember { FocusRequester() }

    BasicTextFieldWithBox(
        state = groceryNameState,
        placeholder = {
            Text(
                "${stringResource(R.string.new_grocery)}...",
                maxLines = 1,
                color = placeholderColor,
                style = MaterialTheme.typography.titleMedium
            )
        },
        textStyle = MaterialTheme.typography.titleMedium,
        contentPadding = PaddingValues(15.dp),
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        onKeyboardAction = KeyboardActionHandler {
            if (groceryNameState.text.isNotEmpty()) {
                onConfirm()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    )
    Spacer(Modifier.height(5.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 10.dp)
    ) {
        Icon(painterResource(R.drawable.text), contentDescription = null, tint = if (groceryDetailState.text.isEmpty()) placeholderColor else MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.width(5.dp))
        BasicTextFieldWithBox(
            state = groceryDetailState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .focusRequester(detailFocusRequester),
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            onKeyboardAction = KeyboardActionHandler {
                if (groceryNameState.text.isNotEmpty()) {
                    onConfirm()
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    stringResource(id = R.string.details),
                    maxLines = 1,
                    color = placeholderColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}

@Composable
fun GroceryBottomSheetContentWithRecipeSelection(
    modifier: Modifier = Modifier,
    showRecipeSelection: Boolean,
    onExitRecipeSelection: () -> Unit,
    onSelectRecipe: (UUID) -> Unit,
    allRecipes: List<Recipe>,
    showLocationSelection: Boolean,
    onExitLocationSelection: () -> Unit,
    onSelectLocation: (UUID) -> Unit,
    onAddNewLocation: (String) -> Unit,
    onRemoveLocation: (UUID) -> Unit,
    allLocations: List<GroceryLocation>,
    onChangeLocationName: (String, UUID) -> Unit,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit,
    activeRecipeIds: List<UUID>,
    selectedLocationId: UUID?,
    selectedRecipeId: UUID?,
    content: @Composable () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).fillMaxHeight()
    ) {
        Box {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.clickable(interactionSource = interactionSource, indication = null) {
                    focusManager.clearFocus(true)
                }
            ) {
                content()
            }
            if (showRecipeSelection || showLocationSelection) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                                .copy(alpha = 0.5f)
                        )
                        .clickable {
                            onExitRecipeSelection()
                            onExitLocationSelection()
                        }
                )
            }
        }

        val recipeSearchFocusRequester = remember { FocusRequester() }
        LaunchedEffect(showRecipeSelection) {
            if (showRecipeSelection) {
                scope.launch {
                    delay(200)
                    recipeSearchFocusRequester.requestFocus()
                }
            }
        }
        AnimatedVisibility (showRecipeSelection, modifier = Modifier
            .background(
                color = if (showRecipeSelection) MaterialTheme.colorScheme.surfaceColorAtElevation(
                    4.dp
                ).copy(alpha = 0.2f) else Color.Transparent
            )
            .padding(top = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable(interactionSource = interactionSource, indication = null) {
                        focusManager.clearFocus(true)
                    }
                    .fillMaxHeight()
            ) {
                SelectRecipeGrid(
                    recipes = allRecipes,
                    onClickRecipe = onSelectRecipe,
                    onCancel = onExitRecipeSelection,
                    searchFocusRequester = recipeSearchFocusRequester,
                    activeRecipeIds = activeRecipeIds,
                    selectedRecipeId = selectedRecipeId
                )
            }
        }

        if (showLocationSelection && !showRecipeSelection) {
            Dialog(onDismissRequest = onExitLocationSelection, properties = DialogProperties(usePlatformDefaultWidth = false)) {
                SelectEditLocationButtons(
                    onCancel = onExitLocationSelection,
                    allLocations = allLocations,
                    onAddLocation = onAddNewLocation,
                    onSelectLocation = onSelectLocation,
                    onRemoveLocation = onRemoveLocation,
                    onChangeLocationName = onChangeLocationName,
                    onReorderLocations = onReorderLocations,
                    selectedLocation = selectedLocationId
                )
            }

        }

//        AnimatedVisibility(showLocationSelection && !showRecipeSelection, modifier = Modifier.background(
//                color = if (showRecipeSelection) MaterialTheme.colorScheme.surfaceColorAtElevation(
//                    4.dp
//                ).copy(alpha = 0.2f) else Color.Transparent)
//        ) {
//            SelectEditLocationButtons(
//                onCancel = onExitLocationSelection,
//                allLocations = allLocations,
//                onAddLocation = onAddNewLocation,
//                onSelectLocation = onSelectLocation,
//                onRemoveLocation = onRemoveLocation,
//                onChangeLocationName = onChangeLocationName,
//                onReorderLocations = onReorderLocations,
//                selectedLocation = selectedLocationId
//            )
//        }

    }
}


@Preview(showBackground = true)
@Composable
fun GroceryBottomSheetPreview() {
    MaterialTheme {
        AddGroceryBottomSheet(
            onDismissRequest = {},
            focusRequester = remember { FocusRequester() },
            onConfirm = { },
            allRecipes = listOf(
                Recipe("Recipe 1"),
                Recipe("Recipe 2"),
                Recipe("Recipe 3"),
                Recipe("Recipe 4"),
                Recipe("Recipe 5"),
                Recipe("Recipe 6")
            ),
            getRecipeNameFromId = { it.toString() },
            activeRecipeIds = listOf(),
            groceryLocations = listOf(),
            onAddGroceryLocation = { },
            onRemoveGroceryLocation = { },
            onAddGroceryToLocation = { _, _ -> },
            onRemoveGroceryFromAllLocations = { },
            onChangeLocationName = { _, _ -> },
            onReorderLocations = { _, _ -> }
        )
    }
}