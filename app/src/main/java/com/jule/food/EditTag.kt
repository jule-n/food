package com.jule.food

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


// Bottom Sheet for editing a specific tag and its recipes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTagSheet(
    tag: Tag,
    recipes: List<Recipe>,
    onDeleteTag: () -> Unit,
    onChangeTagName: (String) -> Unit,
    onChangeTagIconIndex: (Int) -> Unit,
    onChangeTagRecipes: (List<Recipe>) -> Unit,
    state: SheetState,
    onDismissRequest: () -> Unit
) {
    var nameValue by remember { mutableStateOf(TextFieldValue(tag.name)) }
    var lastValueWithoutError by remember { mutableStateOf(tag.name) }
    var isEditing by remember { mutableStateOf(false) }
    var iconSelectionExpanded by remember { mutableStateOf(false) }
    var focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val selectedRecipes = remember { recipes.filter { it.tags.contains(tag.id) }.toMutableStateList() }

    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = {
            onChangeTagRecipes(selectedRecipes)
            if (isEditing) {
                focusManager.clearFocus(true)
            }
            else
                onDismissRequest()
        },
//        modifier = Modifier.height(height = if (state.currentValue == SheetValue.PartiallyExpanded) 100.dp else 500.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.size(48.dp))
                        FilterChip(selected = false, onClick = {}, label = {
                            EditableText(
                                editable = true,
                                textState = nameValue,
                                textAlign = TextAlign.Center,
                                onTextChange = {
                                    if (!isTagError(it.text))
                                        lastValueWithoutError = it.text
                                    nameValue = it
                                },
                                onSubmit = {
                                    focusManager.clearFocus(true)
                                },
                                submitOnFocusLoss = false,
                                modifier = Modifier
                                    .height(40.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .onFocusChanged { focusState ->
                                        if (focusState.isFocused) {
                                            isEditing = true
                                            scope.launch {
                                                nameValue =
                                                    nameValue.copy(
                                                        selection = TextRange(
                                                            0,
                                                            nameValue.text.length
                                                        )
                                                    )
                                            }
                                        } else {
                                            isEditing = false
                                            lastValueWithoutError = lastValueWithoutError.trim()
                                            onChangeTagName(lastValueWithoutError)
                                            nameValue = nameValue.copy(text = lastValueWithoutError)
                                        }
                                    })
                        },
                            enabled = true,
                            leadingIcon = {
                                IconButton(
                                    onClick = { iconSelectionExpanded = true },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(tagIcons[tag.iconIndex]),
                                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        contentDescription = "Edit"
                                    )
                                }
                                IconSelectionDropdown(
                                    expanded = iconSelectionExpanded,
                                    icons = tagIcons,
                                    selectedIconIndex = tag.iconIndex,
                                    onSelectIconIndex = onChangeTagIconIndex,
                                    onDismissRequest = { iconSelectionExpanded = false })
                            }
                        )
                        IconButton(
                            onClick = onDeleteTag,
                        ) {
                            Icon(painter = painterResource(R.drawable.delete), contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }

//                    TextButtonWithIcon(
//                        text = {
//                            Text(
//                                stringResource(R.string.delete),
//                                color = MaterialTheme.colorScheme.onBackground
//                            )
//                        },
//                        onClick = onDeleteTag,
//                        icon = R.drawable.delete,
//                        iconTint = MaterialTheme.colorScheme.error
//                    )
                    }

                    Text(
                        stringResource(R.string.recipes), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth())

                }
            }

            items(recipes) { recipe ->
                val selected = selectedRecipes.contains(recipe)
                Surface(
                    checked = selected,
                    onCheckedChange = { checked -> if (checked) selectedRecipes.add(recipe) else selectedRecipes.remove(recipe) },
                    //                    onClick = { if (selected) selectedRecipes.remove(recipe) else selectedRecipes.add(recipe) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
//                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(10)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Checkbox(checked = selected, onCheckedChange = null)
                        Text(recipe.name, overflow = TextOverflow.Ellipsis, maxLines = 1)
                    }
                }
            }
        }

//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(20.dp),
//        ) {
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                verticalArrangement = Arrangement.spacedBy(10.dp),
//                horizontalArrangement = Arrangement.spacedBy(5.dp)
//            ) {
//            }
////            LazyColumn(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {}
//
//        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EditTagDialog(
    tag: Tag,
    recipes: List<Recipe>,
    onDeleteTag: () -> Unit,
    onChangeTagName: (String) -> Unit,
    onChangeTagIconIndex: (Int) -> Unit,
    onChangeTagRecipes: (List<Recipe>) -> Unit,
    onDismissRequest: () -> Unit
) {
    var nameValue by remember { mutableStateOf(TextFieldValue(tag.name)) }
    var lastValueWithoutError by remember { mutableStateOf(tag.name) }
    var isEditing by remember { mutableStateOf(false) }
    var iconSelectionExpanded by remember { mutableStateOf(false) }
    var focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
//    val focusRequester = remember { FocusRequester() }

    var showChangeRecipesDialog by remember { mutableStateOf(false) }
    val selectedRecipes = remember { recipes.filter { it.tags.contains(tag.id) }.toMutableStateList() }

    DefaultDialog(
        title = stringResource(R.string.edit_specific_tag, tag.name),
        onDismissRequest = {
            if (isEditing) {
                focusManager.clearFocus(true)
            }
            else
                onDismissRequest()
        },
        onClickDialogEnabled = isEditing,
        onClickDialog = {
            focusManager.clearFocus(true)
        }
    ) {
        focusManager = LocalFocusManager.current

        FilterChip(selected = false, onClick = {}, label = {
            EditableText(
                editable = true,
                textState = nameValue,
                textAlign = TextAlign.Center,
                onTextChange = {
                    if (!isTagError(it.text))
                        lastValueWithoutError = it.text
                    nameValue = it
                },
                onSubmit = {
                    focusManager.clearFocus(true)
                },
                submitOnFocusLoss = false,
                modifier = Modifier
                    .height(40.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            isEditing = true
                            scope.launch {
                                nameValue =
                                    nameValue.copy(selection = TextRange(0, nameValue.text.length))
                            }
                        } else {
                            isEditing = false
                            lastValueWithoutError = lastValueWithoutError.trim()
                            onChangeTagName(lastValueWithoutError)
                            nameValue = nameValue.copy(text = lastValueWithoutError)
                        }
                    }) },
            enabled = true,
            leadingIcon = {
                IconButton(onClick = { iconSelectionExpanded = true }, modifier = Modifier.size(30.dp)) {
                    Icon(painter = painterResource(tagIcons[tag.iconIndex]), modifier = Modifier.size(
                        FilterChipDefaults.IconSize), contentDescription = "Edit")
                }
                IconSelectionDropdown(expanded = iconSelectionExpanded, icons = tagIcons, selectedIconIndex = tag.iconIndex, onSelectIconIndex = onChangeTagIconIndex, onDismissRequest = { iconSelectionExpanded = false })
            }
        )

        TextButtonWithIcon(
            text = { Text(stringResource(R.string.recipes), color = MaterialTheme.colorScheme.onBackground) },
            onClick = { showChangeRecipesDialog = true },
            icon = R.drawable.book
        )
        TextButtonWithIcon(
            text = { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.onBackground) },
            onClick = onDeleteTag,
            icon = R.drawable.delete,
            iconTint = MaterialTheme.colorScheme.error
        )
    }
    if (showChangeRecipesDialog) {
        DefaultDialog(
            title = stringResource(R.string.recipes_with_tag, tag.name),
            onDismissRequest = {
                onChangeTagRecipes(selectedRecipes)
                showChangeRecipesDialog = false
            }
        ) {

            val prim1 = lerp(MaterialTheme.colorScheme.primary, Color.White, 0.1f)
            val prim2 = lerp(MaterialTheme.colorScheme.primary, Color.White, 0.3f)

            LazyVerticalGrid(columns = GridCells.Adaptive(60.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(recipes) { index, recipe ->
                    val isSelected = selectedRecipes.contains(recipe)
                    val color1 by animateColorAsState(targetValue = if (isSelected) prim1 else Color.LightGray)
                    val color2 by animateColorAsState(targetValue = if (isSelected) prim2 else Color.White)
                    val br = Brush.linearGradient(listOf(color1, color2))
                    Box() {
                        RecipeSmallDisplay(recipe, onClick = {
                            if (isSelected)
                                selectedRecipes.remove(recipe)
                            else
                                selectedRecipes.add(recipe)
                        },
                            showImage = false, fallbackBrush = br, minTextSize = 7.sp, maxTextSize = 13.sp, isRecipeSearch = false
                        )
                        Surface(color = Color.Black.copy(alpha = if (isSelected) 0.3f else 0f), shape = RoundedCornerShape(20), border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f)), modifier = Modifier.size(20.dp)) {
                            if (isSelected)
                                Icon(painter = painterResource(R.drawable.done), tint = Color.White, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
//                            Checkbox(checked = isSelected, enabled = false, onCheckedChange= {}, modifier = Modifier.align(Alignment.TopStart).offset(-15.dp, -15.dp))
                    }
                }
            }
        }
    }
}