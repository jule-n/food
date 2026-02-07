package com.jule.food

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTagSheet(
    allRecipes: List<Recipe>,
    onAddTag: (tag: Tag, recipeIds: List<UUID>) -> Unit,
    focusRequester: FocusRequester,
    onDismissRequest: () -> Unit
) {
    val nameState = rememberTextFieldState("")
    var iconIndex by remember { mutableIntStateOf(0) }
    val selectedRecipeIds = remember { mutableStateListOf<UUID>() }

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = {
            onDismissRequest()
        },
        dragHandle = null
    ) {
        AddEditTagSheetContent(
            nameState = nameState,
            currentIconIndex = iconIndex,
            onSelectIconIndex = { iconIndex = it },
            isAddSheet = true,
            allRecipes = allRecipes,
            selectedRecipeIds = selectedRecipeIds,
            onRemoveSelectedRecipeId = { selectedRecipeIds.remove(it) },
            onAddSelectedRecipeId = { selectedRecipeIds.add(it) },
            onAddNewTag = {
                val newTag = Tag(nameState.text.trim().toString(), iconIndex)
                onAddTag(newTag, selectedRecipeIds)
                onDismissRequest()
            },
            focusRequester = focusRequester
        )
    }
}

// Bottom Sheet for editing a specific tag and its recipes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTagSheet(
    tag: Tag,
    allRecipes: List<Recipe>,
    onDeleteTag: () -> Unit,
    onChangeTagName: (String) -> Unit,
    onChangeTagIconIndex: (Int) -> Unit,
    onChangeTagRecipeIds: (List<UUID>) -> Unit,
    onDismissRequest: () -> Unit
) {
    val nameState = rememberTextFieldState(tag.name)

    val selectedRecipeIds = remember { allRecipes.filter { it.tags.contains(tag.id) }.map { it.id }.toMutableStateList() }

    LaunchedEffect(nameState.text) {
        val isTagNameEmpty = nameState.text.isEmpty()
        val isTagNameTooLong = isTagNameTooLong(nameState.text.toString())
        if (!isTagNameEmpty && !isTagNameTooLong) {
            onChangeTagName(nameState.text.trim().toString())
        }
    }

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(),
        onDismissRequest = {
            onChangeTagRecipeIds(selectedRecipeIds)
            onDismissRequest()
        },
        dragHandle = null
    ) {
        AddEditTagSheetContent(
            nameState = nameState,
            currentIconIndex = tag.iconIndex,
            onSelectIconIndex = onChangeTagIconIndex,
            isAddSheet = false,
            allRecipes = allRecipes,
            selectedRecipeIds = selectedRecipeIds,
            onRemoveSelectedRecipeId = { selectedRecipeIds.remove(it) },
            onAddSelectedRecipeId = { selectedRecipeIds.add(it) },
            onDelete = onDeleteTag
        )
    }


}

@Composable
fun AddEditTagSheetContent(
    nameState: TextFieldState,
    currentIconIndex: Int,
    onSelectIconIndex: (Int) -> Unit,
    selectedRecipeIds: List<UUID>,
    onRemoveSelectedRecipeId: (UUID) -> Unit,
    onAddSelectedRecipeId: (UUID) -> Unit,
    allRecipes: List<Recipe>,
    onDelete: (() -> Unit)? = null,
    onAddNewTag: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    isAddSheet: Boolean
) {
    val focusManager = LocalFocusManager.current

    var showSelectIconDialog by remember { mutableStateOf(false) }
    val placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus(true)
            }
    ) {
        Row {
            Column(modifier = Modifier.weight(1f)) {
                BasicTextFieldWithBox(
                    state = nameState,
                    placeholder = { Text(
                        text = "${if (isAddSheet) stringResource(R.string.new_tag) else stringResource(R.string.tag_name)}...",
                        maxLines = 1, color = placeholderColor, style = MaterialTheme.typography.titleMedium) },
                    textStyle = MaterialTheme.typography.titleMedium,
                    contentPadding = PaddingValues(15.dp),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    onKeyboardAction = KeyboardActionHandler {
                        focusManager.clearFocus(true)
                    },
                    modifier = Modifier.fillMaxWidth().conditional(focusRequester != null) {
                        Modifier.focusRequester(focusRequester!!)
                    }
                )

                var showEmptyTagError by remember { mutableStateOf(false) }
                val isTagNameEmpty = nameState.text.isEmpty()
                val isTagNameTooLong = isTagNameTooLong(nameState.text.toString())

                if (!showEmptyTagError) {
                    LaunchedEffect(nameState.text) {
                        if (nameState.text.isNotEmpty())
                            showEmptyTagError = true
                    }
                }

                SheetErrorMessage(
                    isError = (showEmptyTagError && isTagNameEmpty) || isTagNameTooLong,
                    message = if (isTagNameTooLong) stringResource(R.string.name_too_long, 20) else
                            if (isTagNameEmpty) stringResource(R.string.name_empty) else ""
                )
            }

            if (isAddSheet) {
                TextButton(onClick = { onAddNewTag?.invoke() }, modifier = Modifier.padding(end = 5.dp)) {
                    Text(stringResource(R.string.save))
                }
            } else {
                Button(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(end = 5.dp)
                ) {
                    Icon(painterResource(R.drawable.delete), contentDescription = "Delete")
                    Text(stringResource(R.string.delete_tag))
                }
            }
        }
        Spacer(Modifier.height(5.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
            shape = RoundedCornerShape(50),
            onClick = {
                showSelectIconDialog = true
            },
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
            ) {
                Text("${stringResource(R.string.icon)}:", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(5.dp))
                Icon(painterResource(tagIcons[currentIconIndex]), contentDescription = "Icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.recipes), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = 10.dp))
        BoxWithConstraints (modifier = Modifier.padding(10.dp)){
            val itemSize = (maxWidth - 30.dp - 10.dp) / 4
            FlowRow(
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
//                modifier = Modifier.width(maxWidth)
            ) {
                allRecipes.forEach { recipe ->
                    val selected = selectedRecipeIds.contains(recipe.id)
                    RecipeTinyDisplay(
                        recipe = recipe,
                        modifier = Modifier.width(itemSize),
                        onClick = {
                            if (selected) {
                                onRemoveSelectedRecipeId(recipe.id)
                            } else {
                                onAddSelectedRecipeId(recipe.id)
                            }
                        },
                        selected = selected
                    )
                }
            }
        }
    }

    if (showSelectIconDialog) {
        DefaultDialog(title = stringResource(R.string.select_icon), onDismissRequest = { showSelectIconDialog = false }) {
            FlowRow {
                tagIcons.forEachIndexed { index, icon ->
                    IconButton(
                        onClick = {
                            onSelectIconIndex(index)
                            showSelectIconDialog = false
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        DefaultDialog(
            title = stringResource(R.string.delete_tag),
            buttons = true,
            onDismissRequest = { showDeleteConfirmDialog = false },
            onConfirm = onDelete
        ) {
            Text(stringResource(R.string.are_you_sure_you_want_to_delete_this_item, nameState.text), textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditTagSheetPreview() {
    val recipeViewModel: RecipeViewModel = viewModel()

    val fishTag = Tag("Nudeeln", 0)
    val salzigTag = Tag("Salzig", 1)
    val saladTag = Tag("Salat", 2)
    val appleTag = Tag("Apfel", 3)
    val kaeseTag = Tag("Käse", 4)

    recipeViewModel.addRecipe(name = "Dorade in Salzkruste", tags = listOf(fishTag.id, salzigTag.id, appleTag.id), id = UUID.randomUUID())
    recipeViewModel.addRecipe(name = "Apfelsalat", tags = listOf(saladTag.id, appleTag.id), id = UUID.randomUUID())
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id), id = UUID.randomUUID())
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id), id = UUID.randomUUID())
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id), id = UUID.randomUUID())
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id), id = UUID.randomUUID())

    FoodTheme {
        EditTagSheet(
            tag = saladTag,
            allRecipes = recipeViewModel.recipes,
            onDeleteTag = {},
            onChangeTagName = {},
            onChangeTagIconIndex = {},
            onChangeTagRecipeIds = {},
            onDismissRequest = {}
        )
    }
}