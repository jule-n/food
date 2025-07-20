package com.jule.food

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ContextualFlowRowOverflow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.aghajari.compose.lazyflowlayout.LazyFlowRow
import com.jule.food.ui.theme.FoodTheme
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

fun isRecipeError(name: String): Boolean {
    return name.isEmpty() || name.length > 30
}
fun isTagError(name: String): Boolean {
    return name.isEmpty() || name.length > 15
}

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }


//fun handleRecipeGridScrollOnBack(
//    recipeGridState: LazyGridState,
//    coroutineScope: CoroutineScope,
//    selectedRecipeId: UUID,
//    selectedRecipeIndex: Int
//) {
//    val viewportStartOffset = recipeGridState.layoutInfo.viewportStartOffset
//    val viewportEndOffset = recipeGridState.layoutInfo.viewportEndOffset
//    val isVisible = recipeGridState.layoutInfo.visibleItemsInfo.any {
//        if (it.key == selectedRecipeId) {
//            return@any it.offset.y >= viewportStartOffset && it.offset.y + it.size.height <= viewportEndOffset
//        } else {
//            return@any false
//        }
//    }
//    if (!isVisible) {
//        Log.d("RecipeGrid", "Not visible, scrolling")
//        coroutineScope.launch {
//            recipeGridState.animateScrollToItem(selectedRecipeIndex)
//        }
//    } else {
//        Log.d("RecipeGrid", "Already visible, not scrolling")
//    }
//}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeScreen(
    bottomBar: @Composable () -> Unit,
    recipeViewModel: RecipeViewModel,
    recipeGridState: LazyGridState,
    onOpenSettings: () -> Unit,
    onClickRecipe: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {

    val recipes = recipeViewModel.recipes
    val tags = recipeViewModel.tags
    val favoriteTagIds = recipeViewModel.favoriteTags


    fun onChangeTagRecipes(tagId: UUID, newRecipes: List<Recipe>) {
        recipeViewModel.changeTagRecipes(tagId, newRecipes)
    }


    RecipeGridScreen(
        modifier = modifier,
        bottomBar = bottomBar,
        recipes = recipes,
        recentRecipeIds = recipeViewModel.recentRecipeIds,
        tags = tags,
        onDeleteTagId = { tagId -> recipeViewModel.deleteTagId(tagId) },
        onChangeTagName = { tagId, newName ->
            recipeViewModel.changeTagName(
                tagId,
                newName
            )
        },
        onChangeTagIconIndex = { tagId, newIndex ->
            recipeViewModel.changeTagIconIndex(
                tagId,
                newIndex
            )
        },
        favoriteTagIds = favoriteTagIds,
        onAddNewRecipe = { name ->
            val id = recipeViewModel.addRecipe(name = name.trim())
//            recipeViewModel.changeShowRecipe(true)
            onClickRecipe(id)
        },
        onChangeSelectedRecipe = { id ->
            onClickRecipe(id)
//            recipeViewModel.changeShowRecipe(true)
//            recipeViewModel.changeSelectedRecipe(id)
        },
        onChangeTagRecipes = { id, newRecipes ->
            onChangeTagRecipes(
                id,
                newRecipes
            )
        },
        recipeGridState = recipeGridState,
        onOpenSettings = onOpenSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun RecipeGridScreen(
    bottomBar: @Composable () -> Unit,
    recipes: List<Recipe>,
    recentRecipeIds: List<UUID>,
    tags: List<Tag>,
    favoriteTagIds: List<UUID>,
    onChangeTagName: (UUID, String) -> Unit,
    onChangeTagIconIndex: (UUID, Int) -> Unit,
    onChangeTagRecipes: (UUID, List<Recipe>) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
    onAddNewRecipe: (name: String) -> Unit,
    onChangeSelectedRecipe: (UUID) -> Unit,
    recipeGridState: LazyGridState,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTagIds = remember { mutableStateListOf<UUID>() }


//    var isSearching by remember { mutableStateOf(false) }


    var showNewRecipeDialog by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    val focusManager = LocalFocusManager.current

    var editTag: Tag? by remember { mutableStateOf(tags[0]) }
    var showEditTagDialog by remember { mutableStateOf(false) }
    var showEditTagSheet by remember { mutableStateOf(false) }


    val editTagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val recipesSelected = recipes.filter { recipe -> selectedTagIds.isEmpty() || recipe.tags.containsAll(selectedTagIds) }

    val possibleTagsToSelect = tags.filter { tag ->
            recipesSelected.any { recipe -> recipe.tags.containsAll(selectedTagIds.toList() + tag.id) }
        }



    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        bottomBar = bottomBar,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewRecipeDialog = true },
                text = { Text(stringResource(R.string.add_recipe)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clickable(
                    enabled = expanded,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        focusManager.clearFocus(true)
                    }
                )
        ) {
            val textFieldState = rememberTextFieldState()
            val recipeGridStateSearch = rememberLazyGridState()
            val inputField = @Composable {
                SearchBarDefaults.InputField(
                    state = textFieldState,
                    onSearch = { focusManager.clearFocus(true) },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(
                                onClick = { expanded = false }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    },
                    trailingIcon = { IconButton(onClick = onOpenSettings) { Icon(Icons.Outlined.Settings, contentDescription = "Settings") } },
                    placeholder = {
                        Text(stringResource(R.string.search_recipes), textAlign = TextAlign.Center)
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                SearchBar(
                    inputField = inputField,
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    windowInsets = WindowInsets(
                        left = 0.dp,
                        right = 0.dp,
                        top = 0.dp,
                        bottom = 0.dp
                    )
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (textFieldState.text == "") {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Row() {
                                    Icon(painterResource(R.drawable.clock), contentDescription = null)
                                    Spacer(Modifier.width(5.dp))
                                    Text("Recent")
                                }
                                Spacer(Modifier.height(10.dp))
                                RecipeGrid(
                                    recipes = recentRecipeIds.map { getRecipeFromId(it, recipes) },
                                    onClickRecipe = { listIndex ->
                                        onChangeSelectedRecipe(recentRecipeIds[listIndex])
                                    },
                                    showImages = expanded,
                                    recipeGridState = rememberLazyGridState()
                                )
                            }
                        } else {
                            val searchResults = recipes.filter { recipe ->
                                recipe.name.contains(
                                    other = textFieldState.text,
                                    ignoreCase = true
                                )
                            }

                            if (searchResults.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxSize()
                                ) {
                                    Text(
                                        "No results found :|",
                                        modifier = Modifier.align(Alignment.Center),
                                        style = MaterialTheme.typography.displaySmallEmphasized
                                    )
                                }
                            } else {
                                RecipeGrid(
                                    contentPadding = PaddingValues(
                                        start = 10.dp,
                                        end = 10.dp,
                                        top = 10.dp,
                                        bottom = 100.dp
                                    ),
                                    recipes = searchResults,
                                    onClickRecipe = { listIndex ->
                                        onChangeSelectedRecipe(searchResults[listIndex].id)
                                    },
                                    recipeGridState = recipeGridStateSearch
                                )
                            }
                        }
                    }
                }
            }


            if (!expanded) {
                Spacer(Modifier.height(10.dp))
                var maxLines by remember { mutableIntStateOf(2) }
                Column(
                    verticalArrangement = Arrangement.Top
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        maxLines = maxLines,
                        modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth().animateContentSize()
                    ) {
                        for (tag in tags.sortedBy { if (selectedTagIds.contains(it.id)) 0 else 1 }) {
                            key(tag.id) {
                                val tagVisible = possibleTagsToSelect.contains(tag)
                                val alpha by animateFloatAsState(targetValue = if (tagVisible) 1f else 0f, label = "alpha")
                                val scale by animateFloatAsState(targetValue = if (tagVisible) 1f else 0.8f, label = "scale") // Optional scale

                                if (alpha > 0) {
                                    val selected = selectedTagIds.contains(tag.id)
                                    FilterChipLongClick(
                                        selected = selected,
                                        key1 = tag.id,
                                        onClick = { if (selectedTagIds.contains(tag.id)) selectedTagIds.remove(tag.id) else selectedTagIds.add(tag.id) },
                                        onLongClick = {
                                            editTag = tag
                                            showEditTagSheet = true
                                        },
                                        label = { Text(tag.name) },
                                        leadingIcon = {
                                            val painter = if (selected) R.drawable.done else tagIcons[tag.iconIndex]
                                            Icon(
                                                painter = painterResource(painter),
                                                contentDescription = null,
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(0.dp)
                                            .height(FilterChipDefaults.Height)
                                            .graphicsLayer {
                                                this.alpha = alpha
                                                this.scaleX = scale
                                                this.scaleY = scale
                                            },
                                        colors = FilterChipDefaults.filterChipColors().copy(containerColor = MaterialTheme.colorScheme.background)
                                    )
                                }
                            }
                        }
                    }
                    AssistChip(
                        onClick = {
                            maxLines = if (maxLines == 2) Int.MAX_VALUE else 2
                        },
                        label = { Text(if (maxLines == 2) "Show more" else "Show less") },
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(
                        topStartPercent = 5,
                        topEndPercent = 5,
                        bottomStartPercent = 0,
                        bottomEndPercent = 0
                    ),
//                shape = RoundedCornerShape(5),
                    modifier = Modifier.weight(1f)
                ) {
                    RecipeGrid(
                        contentPadding = PaddingValues(
                            top = 10.dp,
                            bottom = 100.dp,
                            start = 10.dp,
                            end = 10.dp
                        ),
                        recipes = recipesSelected,
                        onClickRecipe = { listIndex ->
                            onChangeSelectedRecipe(recipesSelected[listIndex].id)
                        },
                        recipeGridState = recipeGridState,
                    )
                }
            }
        }
    }


    if (showEditTagSheet) {
        EditTagSheet(
            tag = editTag!!,
            recipes = recipes,
            onDeleteTag = {
                showEditTagSheet = false
                onDeleteTagId(editTag!!.id)
//                showEditTagDialog = false
            },
            onChangeTagName = { newName -> onChangeTagName(editTag!!.id, newName) },
            onChangeTagIconIndex = { newIndex -> onChangeTagIconIndex(editTag!!.id, newIndex) },
            onChangeTagRecipes = { newRecipes -> onChangeTagRecipes(editTag!!.id, newRecipes) },
            onDismissRequest = { showEditTagSheet = false },
            state = editTagSheetState,
        )
    }

    val newRecipeFocusRequester = remember { FocusRequester() }
    LaunchedEffect(showNewRecipeDialog) {
        if (showNewRecipeDialog) {
            newRecipeFocusRequester.requestFocus()
        }
    }
    if (showNewRecipeDialog) {
        EnterTextDialog(
            title = stringResource(R.string.new_recipe),
            onDismissRequest = { showNewRecipeDialog = false },
            onConfirm = { name ->
                onAddNewRecipe(name)
                showNewRecipeDialog = false
            },
            confirmWithKeyboard = true,
            placeholder = { Text(stringResource(id = R.string.name), style = TextStyle.Default) },
            isError = { isRecipeError(it) },
            focusRequester = newRecipeFocusRequester
        )
    }

    if (showEditTagDialog) {
        EditTagDialog(
            tag = editTag!!,
            recipes = recipes,
            onDeleteTag = {
                showEditTagDialog = false
                onDeleteTagId(editTag!!.id)
            },
            onChangeTagName = { newName -> onChangeTagName(editTag!!.id, newName) },
            onChangeTagIconIndex = { newIndex -> onChangeTagIconIndex(editTag!!.id, newIndex) },
            onChangeTagRecipes = { newRecipes -> onChangeTagRecipes(editTag!!.id, newRecipes) },
            onDismissRequest = {
                showEditTagDialog = false
            }
        )
    }
}


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

                Text(stringResource(R.string.recipes), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), modifier = Modifier
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
                    Icon(painter = painterResource(tagIcons[tag.iconIndex]), modifier = Modifier.size(FilterChipDefaults.IconSize), contentDescription = "Edit")
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
                            showImage = false, fallbackBrush = br, minTextSize = 7.sp, maxTextSize = 13.sp
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

@Composable
fun AddTagDialog(
    onConfirm: (Tag) -> Unit,
    onCancel: () -> Unit,
    focusRequester: FocusRequester,
) {
    var nameValue by remember { mutableStateOf(TextFieldValue("")) }
    var isEditing by remember { mutableStateOf(false) }
    var iconIndex by remember { mutableIntStateOf(0)}
    var focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var iconSelectionExpanded by remember { mutableStateOf(false) }

    DefaultDialog(
        title = stringResource(R.string.new_tag),
        onDismissRequest = {
            if (isEditing) {
                focusManager.clearFocus(true)
            } else if (nameValue.text == "") {
                onCancel()
            }
        },
        confirmEnabled = !isTagError(nameValue.text),
        onConfirm = { onConfirm(Tag(name = nameValue.text.trim(), iconIndex = iconIndex))},
        onCancel = onCancel,
        buttons = true,
        onClickDialogEnabled = isEditing,
        onClickDialog = { focusManager.clearFocus(true) }
    ) {
        focusManager = LocalFocusManager.current

        FilterChip(selected = false, onClick = {}, label = {
            EditableText(
                editable = true,
                textState = nameValue,
                textAlign = TextAlign.Center,
                onTextChange = {
                    nameValue = it
                },
                onSubmit = {
                    focusManager.clearFocus(true)
                },
                focusRequester = focusRequester,
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
                        }
                    }) },
            enabled = true,
            leadingIcon = {
                IconButton(onClick = { iconSelectionExpanded = true }, modifier = Modifier.size(30.dp)) {
                    Icon(painter = painterResource(tagIcons[iconIndex]), modifier = Modifier.size(FilterChipDefaults.IconSize), contentDescription = "Edit")
                }
                IconSelectionDropdown(expanded = iconSelectionExpanded, icons = tagIcons, selectedIconIndex = iconIndex, onSelectIconIndex = { iconIndex = it }, onDismissRequest = { iconSelectionExpanded = false })
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeGrid(
    recipes: List<Recipe>,
    onClickRecipe: (listIndex: Int) -> Unit,
    recipeGridState: LazyGridState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 100.dp),
    showImages: Boolean = true,
) {
    LazyVerticalGrid(modifier = modifier, state = recipeGridState, columns = GridCells.Fixed(3), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = contentPadding) {
        itemsIndexed(recipes, key = { _, recipe -> recipe.id }) { index, recipe ->
            RecipeSmallDisplay(
                recipe = recipe, onClick = { onClickRecipe(index) }, showImage = showImages, modifier = Modifier.animateItem()
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeSmallDisplay(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
    shadow: Boolean = true,
    minTextSize: TextUnit = 10.sp,
    maxTextSize: TextUnit = 16.sp,
    fallbackBrush: Brush = Brush.linearGradient( listOf(Color.LightGray, Color.White) )
) {
    val image = recipe.images.isNotEmpty() && showImage
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                if (image) {
                    val sharedTransitionScope = LocalSharedTransitionScope.current
                    val path = recipe.images[0]
                    with(sharedTransitionScope!!) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(File(path))
                                .crossfade(true)
                                .placeholderMemoryCacheKey(path)
                                .memoryCacheKey(path)
                                .size(400, 400)
                                .scale(coil3.size.Scale.FIT)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = recipe.id),
                                    animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                                )
                                .clip(RoundedCornerShape(10))
                                .fillMaxSize()
                        )
                    }
                } else {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(fallbackBrush, shape = RoundedCornerShape(10))
                    )
                }
            }
            val style = if (shadow) MaterialTheme.typography.bodyMedium.copy(shadow = Shadow(offset = Offset(3f, 3f))) else MaterialTheme.typography.bodyMedium
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 5.dp)
            ) {
                Text(
                    text = recipe.name,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(hyphens = Hyphens.Auto, lineBreak = LineBreak.Simple),
                    autoSize = TextAutoSize.StepBased(minFontSize = minTextSize, maxFontSize = maxTextSize)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelectionFlowRow(
    modifier: Modifier = Modifier,
    tags: List<Tag>,
    selectedTags: List<UUID>,
    removeFromSelectedTags: (UUID) -> Unit,
    addToSelectedTags: (UUID) -> Unit,
    onLongClick: ((UUID) -> Unit)? = null,
    overflow: FlowRowOverflow = FlowRowOverflow.Clip,
    @DrawableRes selectedIcon: Int = R.drawable.done,
    additionalContent: @Composable () -> Unit = {}
) {
    FlowRow(overflow = overflow, horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), modifier = modifier) {
        tags.forEach { tag ->
            val selected = selectedTags.contains(tag.id)
//            val onClick = { if (selectedTags.contains(tag.id)) { removeFromSelectedTags(tag.id) } else { addToSelectedTags(tag.id) } }
            FilterChipLongClick(
                selected = selected,
                key1 = tag.id,
//                onClick = onClick,
                onClick = { if (selectedTags.contains(tag.id)) removeFromSelectedTags(tag.id) else addToSelectedTags(tag.id) },
                onLongClick = { onLongClick?.invoke(tag.id) },
                label = { Text(tag.name) },
                leadingIcon = {
                    val painter = if (selected) selectedIcon else tagIcons[tag.iconIndex]
                    Icon(
                        painter = painterResource(painter),
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                modifier = Modifier
                    .padding(0.dp)
                    .height(FilterChipDefaults.Height),
                colors = FilterChipDefaults.filterChipColors().copy(containerColor = MaterialTheme.colorScheme.background)
            )
        }
        additionalContent()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContextualTagSelectionFlowRow(
    modifier: Modifier = Modifier,
    tags: List<Tag>,
    itemCount: Int,
    selectedTags: List<UUID>,
    removeFromSelectedTags: (UUID) -> Unit,
    addToSelectedTags: (UUID) -> Unit,
    onLongClick: ((UUID) -> Unit)? = null,
    overflow: ContextualFlowRowOverflow = ContextualFlowRowOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    @DrawableRes selectedIcon: Int = R.drawable.done,
) {
    ContextualFlowRow(
        itemCount = itemCount,
        overflow = overflow,
        maxLines = maxLines,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) { index ->
        if (index >= tags.size)
        {
//            Log.e("Error", "Index out of bounds")
            return@ContextualFlowRow
        }
        val tag = tags[index]
//        tags.forEach { tag ->
            val selected = selectedTags.contains(tag.id)
//            val onClick = { if (selectedTags.contains(tag.id)) { removeFromSelectedTags(tag.id) } else { addToSelectedTags(tag.id) } }
            FilterChipLongClick(
                selected = selected,
                key1 = tag.id,
//                onClick = onClick,
                onClick = { if (selectedTags.contains(tag.id)) removeFromSelectedTags(tag.id) else addToSelectedTags(tag.id) },
                onLongClick = { onLongClick?.invoke(tag.id) },
                label = { Text(tag.name) },
                leadingIcon = {
                    val painter = if (selected) selectedIcon else tagIcons[tag.iconIndex]
                    Icon(
                        painter = painterResource(painter),
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                modifier = Modifier
                    .padding(0.dp)
                    .height(FilterChipDefaults.Height),
                colors = FilterChipDefaults.filterChipColors().copy(containerColor = MaterialTheme.colorScheme.background)
            )
//        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconSelectionDropdown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    @DrawableRes icons: List<Int>,
    selectedIconIndex: Int,
    onSelectIconIndex: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (icons.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                icons.forEachIndexed { index, icon ->
                    IconButton(onClick = {
                        onSelectIconIndex(index)
                        onDismissRequest()
                    }) {
                        Icon(painter = painterResource(icon), contentDescription = null, tint = if (index == selectedIconIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
        else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp), contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_icons))
            }
        }
    }
}

@Composable
fun SpecificRecipeSection(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    title: String,
    actionButtons: @Composable () -> Unit,
    containerColor: Color = Color.Unspecified,
    content: @Composable () -> Unit
) {
    Surface(
        color = containerColor,
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(icon), null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, modifier = Modifier.weight(1f))
                actionButtons()
            }
            content()
        }

    }
}

@Composable
fun DisplayTagsRow(
    tags: List<Tag>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = modifier
//        modifier = Modifier.padding(20.dp)
    ) {
        items(tags) { tag ->
            FilterChip(selected = true, onClick = { }, label = { Text(tag.name) }, enabled = false, leadingIcon = {
                Icon(
                    painter = painterResource(tagIcons[tag.iconIndex]),
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }, colors = FilterChipDefaults.filterChipColors().copy(disabledSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer, disabledLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer, disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer))
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

    recipeViewModel.addRecipe(name = "Dorade in Salzkruste", tags = listOf(fishTag.id, salzigTag.id, appleTag.id))
    recipeViewModel.addRecipe(name = "Apfelsalat", tags = listOf(saladTag.id, appleTag.id))
    recipeViewModel.addRecipe(name = "Pizza", tags = listOf(kaeseTag.id, kaeseTag2.id, kaeseTag3.id))
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id))
    recipeViewModel.addTags(listOf(fishTag, salzigTag, saladTag, appleTag, kaeseTag, kaeseTag2, kaeseTag3))
    recipeViewModel.changeFavoriteTags(listOf(fishTag.id,appleTag.id,kaeseTag2.id))

    FoodTheme {
        RecipeScreen(bottomBar = { BottomNavigationBar(navController = navController) }, recipeViewModel = recipeViewModel, onOpenSettings = {}, onClickRecipe = {}, recipeGridState = rememberLazyGridState())
    }
}


@Preview(showBackground = true)
@Composable
fun RecipeImagePreview() {
    FoodTheme {
        RecipeSmallDisplay(
            recipe = Recipe(name = "Dorade in Salzkruste"),
            onClick = {},

        )
    }
}