package com.jule.food.ui.recipes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.jule.food.utils.AddTagSheet
import com.jule.food.ui.main.BottomNavigationBar
import com.jule.food.utils.EditTagSheet
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.R
import com.jule.food.data.Recipe
import com.jule.food.data.RecipeViewModel
import com.jule.food.utils.SimpleAddEditBottomSheet
import com.jule.food.data.Tag
import com.jule.food.data.getTagFromId
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.utils.DeleteDialog
import java.util.UUID


val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecipeScreen(
    bottomBar: @Composable () -> Unit,
    recipeViewModel: RecipeViewModel,
    recipeGridState: LazyGridState,
    onClickRecipe: (recipeId: UUID, fromSearch: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val recipes = recipeViewModel.recipes
    val tags = recipeViewModel.tags

    var isEditingTags by remember { mutableStateOf(false) }


    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        bottomBar = bottomBar,
        floatingActionButton = {
            var showNewRecipeSheet by remember { mutableStateOf(false) }
            val newRecipeFocusRequester = remember { FocusRequester() }

            AnimatedVisibility(!isEditingTags, enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
                ExtendedFloatingActionButton(
                    onClick = { showNewRecipeSheet = true },
                    text = { Text(stringResource(R.string.add_recipe)) },
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
                )
            }
            LaunchedEffect(showNewRecipeSheet) {
                if (showNewRecipeSheet) {
                    newRecipeFocusRequester.requestFocus()
                }
            }
            if (showNewRecipeSheet) {
                SimpleAddEditBottomSheet(
                    onDismissRequest = { showNewRecipeSheet = false },
                    onConfirm = { name ->
                        val id = recipeViewModel.addRecipe(name = name, context = context)
                        onClickRecipe(id, false)
                        showNewRecipeSheet = false
                    },
                    focusRequester = newRecipeFocusRequester,
                    placeholderText = stringResource(R.string.new_recipe),
                    nameTooLongLimit = 40
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = recipeViewModel.dataLoaded,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            modifier = Modifier.padding(innerPadding)
        ) { dataLoaded ->
            if (dataLoaded) {
            RecipeScreenMain(
                modifier = modifier,
                recipes = recipes,
                recentRecipeIds = recipeViewModel.recentRecipeIds,
                tags = tags,
                onDeleteTagId = { tagId -> recipeViewModel.deleteTagId(tagId, context) },
                onAddTag = { tag, recipeIds ->
                    recipeViewModel.addTag(tag, context)
                    recipeViewModel.changeTagRecipes(tag.id, recipeIds, context)
                },
                onChangeTagName = { tagId, newName ->
                    recipeViewModel.changeTagName(
                        tagId,
                        newName,
                        context
                    )
                },
                onChangeTagIconIndex = { tagId, newIndex ->
                    recipeViewModel.changeTagIconIndex(
                        tagId,
                        newIndex,
                        context
                    )
                },
                onClickRecipe = onClickRecipe,
                onChangeTagRecipeIds = { tagId, newRecipeIds ->
                    recipeViewModel.changeTagRecipes(tagId, newRecipeIds, context)
                },
                recipeGridState = recipeGridState,
                searchBarExpanded = recipeViewModel.isSearchBarExpanded,
                onChangeSearchBarExpanded = { recipeViewModel.changeIsSearchBarExpanded(it) },
                selectedTagIds = recipeViewModel.selectedTagIds,
                onAddSelectedTagId = { recipeViewModel.addSelectedTagId(it) },
                onRemoveSelectedTagId = { recipeViewModel.removeSelectedTagId(it) },
                tagSelectionExpanded = recipeViewModel.isTagSelectionExpanded,
                onChangeTagSelectionExpanded = { recipeViewModel.changeIsTagSelectionExpanded(it) },
                isEditingTags = isEditingTags,
                onChangeIsEditingTags = { isEditingTags = it }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreenMain(
    recipes: List<Recipe>,
    recentRecipeIds: List<UUID>,
    tags: List<Tag>,
    selectedTagIds: List<UUID>,
    onAddSelectedTagId: (UUID) -> Unit,
    onRemoveSelectedTagId: (UUID) -> Unit,
    onChangeTagName: (UUID, String) -> Unit,
    onChangeTagIconIndex: (UUID, Int) -> Unit,
    onChangeTagRecipeIds: (tagId: UUID, newRecipeIds: List<UUID>) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
    onAddTag: (tag: Tag, recipeIds: List<UUID>) -> Unit,
    onClickRecipe: (recipeId: UUID, fromSearch: Boolean) -> Unit,
    recipeGridState: LazyGridState,
    searchBarExpanded: Boolean,
    onChangeSearchBarExpanded: (Boolean) -> Unit,
    tagSelectionExpanded: Boolean,
    onChangeTagSelectionExpanded: (Boolean) -> Unit,
    isEditingTags: Boolean,
    onChangeIsEditingTags: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val focusManager = LocalFocusManager.current

    var editTagId: UUID? by remember { mutableStateOf(null) }
    var showEditTagSheet by remember { mutableStateOf(false) }
    var showAddTagSheet by remember { mutableStateOf(false) }

    var showConfirmDeleteTagDialog by remember { mutableStateOf(false) }

    val recipesSelected = recipes.filter { recipe -> selectedTagIds.isEmpty() || recipe.tags.containsAll(selectedTagIds) }
    val possibleTagIdsToSelect = tags.map { it.id }.filter { tagId ->
            recipesSelected.any { recipe -> recipe.tags.containsAll(selectedTagIds.toList() + tagId) }
        }

    val textFieldState = rememberTextFieldState()

    
    BackHandler(enabled = isEditingTags) {
        onChangeIsEditingTags(false)
    }

    Box(modifier = modifier
        .fillMaxSize()
        .clickable(
            enabled = searchBarExpanded,
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                focusManager.clearFocus(true)
            }
        ).background(MaterialTheme.colorScheme.background)
    ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Start,
                contentPadding = PaddingValues(bottom = 100.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))) {
                        Column {
                            Spacer(Modifier.height(SearchBarDefaults.InputFieldHeight + 20.dp))
                            AnimatedVisibility(isEditingTags) {
                                Text(
                                    text = stringResource(R.string.edit_tag_screen_info),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
                                )
                            }
                            ExpandableTagSelectionFlowRow(
                                tags = tags,
                                selectedTagIds = if (!isEditingTags) selectedTagIds else listOf(),
                                onAddToSelectedTagIds = { id ->
                                    if (!isEditingTags) onAddSelectedTagId(id) else {
                                        editTagId = id
                                        showEditTagSheet = true
                                    }
                                },
                                onRemoveFromSelectedTagIds = onRemoveSelectedTagId,
                                possibleTagIdsToSelect = if (!isEditingTags) possibleTagIdsToSelect else tags.map { it.id },
                                expanded = tagSelectionExpanded || isEditingTags,
                                onExpandedChange = onChangeTagSelectionExpanded,
                                showExpansionButton = !isEditingTags,
                                extraContent = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 10.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        EditButton(
                                            text = stringResource(R.string.edit_tags),
                                            onClick = { onChangeIsEditingTags(true) }
                                        )
                                    }
                                }
                            )
                            Spacer(Modifier.height(10.dp))
                            AnimatedVisibility(isEditingTags) {
                                Button(
                                    onClick = { showAddTagSheet = true },
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Icon(painterResource(R.drawable.add), contentDescription = null)
                                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                    Text(stringResource(R.string.new_tag))
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(topStart=10.dp, topEnd=10.dp)
                            ).fillMaxWidth().height(10.dp).align(Alignment.BottomCenter)
                        )
                    }
                }
                itemsIndexed(recipesSelected, key = { _, recipe -> recipe.id }) { index, recipe ->
                    val left = index % 3 == 0
                    val right = (index+1) % 3 == 0
                    val middle = !left && !right

                    val paddingLeft = if (left) 10.dp else (if (middle) 6.67.dp else 3.33.dp)
                    val paddingRight = if (right) 10.dp else (if (middle) 6.67.dp else 3.33.dp)
                    val paddingTop = if (index >= 3) 10.dp else 0.dp
                    RecipeSmallDisplay(
                        recipe = recipe, onClick = { onClickRecipe(recipe.id, false) }, showImage = true, isRecipeSearch = false, modifier = Modifier.animateItem().padding(start = paddingLeft, end = paddingRight, top = paddingTop)
                    )
                }
            }
//            AnimatedVisibility (!isEditingTags, enter = slideInVertically(initialOffsetY = { it }), exit = slideOutVertically(targetOffsetY = { it })) {
//                Box(
//                    modifier = Modifier.background(
//                        color = MaterialTheme.colorScheme.background,
//                        shape = RoundedCornerShape(
//                            topStartPercent = 5,
//                            topEndPercent = 5,
//                            bottomStartPercent = 0,
//                            bottomEndPercent = 0
//                        )
//                    )
//                ) {
//                    RecipeGridWithConstrainedHeight(
//                        recipes = recipesSelected,
//                        onClickRecipe = { listIndex ->
//                            onClickRecipe(recipesSelected[listIndex].id, false)
//                        },
//                        contentPadding = PaddingValues(
//                            top = 10.dp,
//                            bottom = 10.dp,
//                            start = 10.dp,
//                            end = 10.dp
//                        ),
//                        recipeGridState = recipeGridState,
//                        userScrollEnabled = false,
//                        isRecipeSearch = false,
//                        showImages = true
//                    )
//                }
//            }
//        }
        AnimatedContent (targetState = isEditingTags) { editingTags ->
            if (!editingTags) {
                RecipeScreenSearch(
                    expanded = searchBarExpanded,
                    onChangeExpanded = onChangeSearchBarExpanded,
                    textFieldState = textFieldState,
                    interactionSource = interactionSource,
                    recipes = recipes,
                    recentRecipeIds = recentRecipeIds,
                    onClickRecipe = { id -> onClickRecipe(id, true) }
                )
            } else {
                EditScreenTopBar(
                    title = stringResource(R.string.edit_tags),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    onBack = { onChangeIsEditingTags(false) }
                )
            }
        }
    }


    if (showEditTagSheet && editTagId != null) {
        EditTagSheet(
            tag = getTagFromId(editTagId!!, tags),
            allRecipes = recipes,
            onDeleteTag = {
                showConfirmDeleteTagDialog = true
            },
            onChangeTagName = { newName -> onChangeTagName(editTagId!!, newName) },
            onChangeTagIconIndex = { newIndex ->
                onChangeTagIconIndex(
                    editTagId!!,
                    newIndex
                )
            },
            onChangeTagRecipeIds = { newRecipeIds ->
                onChangeTagRecipeIds(
                    editTagId!!,
                    newRecipeIds
                )
            },
            onDismissRequest = { showEditTagSheet = false }
        )
    }
    val addTagSheetFocusRequester = remember { FocusRequester() }
    LaunchedEffect(showAddTagSheet) {
        if (showAddTagSheet) {
            addTagSheetFocusRequester.requestFocus()
        }
    }
    if (showAddTagSheet) {
        AddTagSheet(
            allRecipes = recipes,
            onAddTag = onAddTag,
            onDismissRequest = { showAddTagSheet = false },
            focusRequester = addTagSheetFocusRequester
        )
    }
    
    if (showConfirmDeleteTagDialog && editTagId != null) {
        DeleteDialog(
            title = stringResource(R.string.delete_tag),
            onDismissRequest = { showConfirmDeleteTagDialog = false },
            onConfirm = {
                showEditTagSheet = false
                onDeleteTagId(editTagId!!)
                editTagId = null
            }
        )
    }
}



@Composable
fun EditButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = FilterChipDefaults.shape,
        color = Color.Transparent,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(5.dp)
        ) {
            Icon(painterResource(R.drawable.edit), contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f))
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreenTopBar(
    title: String,
    backgroundColor: Color,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = backgroundColor,
        modifier = modifier.padding(10.dp),
        shape = SearchBarDefaults.inputFieldShape
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(SearchBarDefaults.InputFieldHeight)
        ) {
            IconButtonWithTooltip(onClick = onBack, tooltipText = stringResource(R.string.back)) {
                Icon(painterResource(R.drawable.arrow_left), contentDescription = "Back")
            }
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RecipeScreenPreview() {
    val navController = rememberNavController()
    val recipeViewModel: RecipeViewModel = viewModel()

    val fishTag = Tag("Nudeeln", 0)
    val salzigTag = Tag("Salzig", 1)
    val saladTag = Tag("Salat", 2)
    val appleTag = Tag("Apfel", 3)
    val kaeseTag = Tag("Käse", 4)
    val kaeseTag2 = Tag("Käes2", 4)
    val kaeseTag3 = Tag("Käse3", 4)

//    recipeViewModel.addRecipe(name = "Dorade in Salzkruste", tags = listOf(fishTag.id, salzigTag.id, appleTag.id))
//    recipeViewModel.addRecipe(name = "Apfelsalat", tags = listOf(saladTag.id, appleTag.id))
//    recipeViewModel.addRecipe(name = "Pizza", tags = listOf(kaeseTag.id, kaeseTag2.id, kaeseTag3.id))
    for (i in 0..30) {
        for (j in 0..2) {
            recipeViewModel.addRecipe(name = "Caesar's Salad ${i}${j}", tags = listOf(kaeseTag.id, saladTag.id), id = UUID.randomUUID())
        }
    }

    listOf(fishTag, salzigTag, saladTag, appleTag, kaeseTag, kaeseTag2, kaeseTag3).forEach {
        recipeViewModel.addTagWithoutSaving(it)
    }

    recipeViewModel.initializeEmpty()

    FoodTheme {
        RecipeScreen(bottomBar = {
            BottomNavigationBar(
                navController = navController,
                recipeViewModel = recipeViewModel
            )
        }, recipeViewModel = recipeViewModel, onClickRecipe = { _, _ -> }, recipeGridState = rememberLazyGridState())
    }
}


@Preview(showBackground = true)
@Composable
fun RecipeImagePreview() {
    FoodTheme {
        RecipeSmallDisplay(
            recipe = Recipe(name = "Dorade in Salzkruste"),
            onClick = {},
            isRecipeSearch = false
        )
    }
}