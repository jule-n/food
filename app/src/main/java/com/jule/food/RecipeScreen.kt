package com.jule.food

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import coil3.request.ImageRequest
import com.jule.food.ui.theme.FoodTheme
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeScreen(
    bottomBar: @Composable () -> Unit,
    recipeViewModel: RecipeViewModel,
    addToGroceries: (List<GroceryItem>, Int) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val recipes = recipeViewModel.recipes
    val tags = recipeViewModel.tags
    val favoriteTagIds = recipeViewModel.favoriteTags

    val showRecipeId = recipeViewModel.selectedRecipeIndex
    val showRecipeEnabled = recipeViewModel.showRecipeEnabled

    var isRemovingRecipe by remember { mutableStateOf(false)}


    fun onChangeTagRecipes(tagId: UUID, newRecipes: List<Recipe>) {
        recipeViewModel.changeTagRecipes(tagId, newRecipes)
    }

    val recipeGridState = rememberLazyGridState()

    BackHandler(
        enabled = showRecipeEnabled,
        onBack = { recipeViewModel.changeShowRecipe(false) }
    )

    SharedTransitionLayout() {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            AnimatedVisibility(
                visible = !showRecipeEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                    RecipeGridScreen(
                        modifier = modifier,
                        bottomBar = bottomBar,
                        recipes = recipes,
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
                            recipeViewModel.changeShowRecipe(true)
                            recipeViewModel.changeSelectedRecipe(id)
                        },
                        onChangeSelectedRecipe = { id ->
                            recipeViewModel.changeShowRecipe(true)
                            recipeViewModel.changeSelectedRecipe(id)
                        },
                        onChangeTagRecipes = { id, newRecipes ->
                            onChangeTagRecipes(
                                id,
                                newRecipes
                            )
                        },
                        recipeGridState = recipeGridState
                    )
                }
            }

            AnimatedVisibility(
                visible = showRecipeEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                    if (!isRemovingRecipe) {
                        SpecificRecipeScreen(
                            modifier = modifier,
                            bottomBar = bottomBar,
                            recipe = getRecipeFromId(showRecipeId, recipes),
                            recipes = recipes,
                            onChangeRecipeName = { newName -> recipeViewModel.changeRecipeName(showRecipeId, newName) },
                            allTags = recipeViewModel.tags,
                            onDeleteTagId = { tagId -> recipeViewModel.deleteTagId(tagId) },
                            onChangeTagName = { tagId, newName -> recipeViewModel.changeTagName(tagId, newName) },
                            onChangeTagIconIndex = { tagId, newIndex -> recipeViewModel.changeTagIconIndex(tagId, newIndex) },
                            onChangeRecipeTags = { tags ->
                                recipeViewModel.changeRecipeTags(showRecipeId, tags)
                            },
                            onBack = { recipeViewModel.changeShowRecipe(false) },
                            onAddImages = { paths ->
                                recipeViewModel.addImagesToRecipe(showRecipeId, paths)
                            },
                            onDelete = {
                                recipeViewModel.changeShowRecipe(false)
                                isRemovingRecipe = true
                                recipeViewModel.removeRecipe(showRecipeId)

                                coroutineScope.launch {
                                    delay(500)
                                    isRemovingRecipe = false
                                }
                            },
                            onAddNewTag = { recipeViewModel.addTag(it) },
                            onChangeRecipeImages = { newPaths -> recipeViewModel.changeRecipeImages(showRecipeId, newPaths) },
                            onDeleteRecipeImages = { paths -> recipeViewModel.deleteRecipeImages(paths) },
                            onChangeGroceries = { newItems -> recipeViewModel.changeRecipeGroceries(showRecipeId, newItems) },
                            addToGroceries = addToGroceries,
                            groceryCategories = groceryCategories,
                            onChangeTagRecipes = { id, newRecipes -> onChangeTagRecipes(id, newRecipes) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun RecipeGridScreen(
    bottomBar: @Composable () -> Unit,
    recipes: List<Recipe>,
    tags: List<Tag>,
    favoriteTagIds: List<UUID>,
    onChangeTagName: (UUID, String) -> Unit,
    onChangeTagIconIndex: (UUID, Int) -> Unit,
    onChangeTagRecipes: (UUID, List<Recipe>) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
    onAddNewRecipe: (name: String) -> Unit,
    onChangeSelectedRecipe: (UUID) -> Unit,
    recipeGridState: LazyGridState,
    modifier: Modifier = Modifier
) {
    val selectedTagIds = remember { mutableStateListOf<UUID>() }


//    var isSearching by remember { mutableStateOf(false) }

    var searchValue by remember { mutableStateOf("") }

    var showNewRecipeDialog by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    val focusRequesterSearch = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var editTag: Tag? by remember { mutableStateOf(tags[0]) }
    var showEditTagDialog by remember { mutableStateOf(false) }
    var showEditTagSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()


    val editTagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val recipesSelected = recipes.filter {recipe ->
        if (selectedTagIds.isEmpty() || recipe.tags.containsAll(selectedTagIds)) {
            if (searchValue != "") {
                return@filter recipe.name.contains(other = searchValue, ignoreCase = true)
            }
            return@filter true
        }

        return@filter false
    }
    var possibleTagsToSelect by remember { mutableStateOf(tags.toList()) }
//    LaunchedEffect(recipesSelected) {
//        if (recipesSelected.size == recipes.size) {
//            possibleTagsToSelect = tags.toList()
//            return@LaunchedEffect
//        }
//        possibleTagsToSelect = (tags.filter { tag ->
//            if (selectedTagIds.contains(tag.id))
//                return@filter true
//            recipesSelected.forEach { recipe ->
//                if (recipe.tags.contains(tag.id)) {
//                    return@filter true
//                }
//            }
//            return@filter false
//        })
//        Log.d("Selected Tags", getTagsFromIds(selectedTagIds, tags).joinToString(separator = ", "){it.name})
//        Log.d("Possible Tags", possibleTagsToSelect.joinToString(separator = ", "){it.name})
////        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
//    }

    BackHandler(
        enabled = searchValue != "",
        onBack = {
            searchValue = ""
            focusManager.clearFocus(true)
        }
    )
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
//    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val inputField = @Composable {
        SearchBarDefaults.InputField(
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = { },
//            onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(
                        onClick = { scope.launch { searchBarState.animateToCollapsed() } }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            trailingIcon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
            placeholder = {
                Text(stringResource(R.string.search_recipes), textAlign = TextAlign.Center)
            }
        )
    }
    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        topBar = {
            TopSearchBar(
//                scrollBehavior = scrollBehavior,
                state = searchBarState,
                inputField = inputField
            )
            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = inputField,
//                tonalElevation = 0.dp,
                colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                val gridState = rememberLazyGridState()
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
                    if (textFieldState.text == "") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(all = 10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("SUGGESTIONS")
                        }
                    } else {
                        val searchResults = recipes.filter { recipe -> recipe.name.contains(other = textFieldState.text, ignoreCase = true) }

                        if (searchResults.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxSize()
                            ) {
                                Text("No results found :|", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.displaySmallEmphasized)
                            }
                        } else {
                            LazyVerticalGrid (
                                columns = GridCells.Fixed(3)
                            ) {
                                items(searchResults) { item ->
                                    Surface(
                                        onClick = {  },
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
                                                if (item.images.isNotEmpty()) {
                                                    with(LocalSharedTransitionScope.current!!) {
                                                        AsyncImage(
                                                            model = item.images[0],
                                                            contentDescription = null,
                                                            modifier = Modifier
//                                                                .sharedElement(
//                                                                    rememberSharedContentState(key = item.id),
//                                                                    animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
//                                                                )
                                                                .clip(RoundedCornerShape(10))
                                                                .fillMaxSize(),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    }
                                                } else {
                                                    Box(modifier = Modifier.fillMaxSize().background(color = Color.Red, shape = RoundedCornerShape(10)))
                                                }
                                            }
                                            Box(modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                                .padding(horizontal = 5.dp)
                                            ) {
                                                Text(
                                                    text = item.name,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 2,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    textAlign = TextAlign.Center,
                                                    style = MaterialTheme.typography.bodyMedium.copy(hyphens = Hyphens.Auto, lineBreak = LineBreak.Simple),
                                                    autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 16.sp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
//                            RecipeGrid(
//                                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 100.dp),
//                                recipes = searchResults,
//                                onClickRecipe = { listIndex -> onChangeSelectedRecipe(searchResults[listIndex].id) },
//                                recipeGridState = gridState,
//                            )
                        }
                    }
//                }
            }
        },
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
                    enabled = searchValue != "",
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        focusManager.clearFocus(true)
                    }
                )
        ) {
            Spacer(Modifier.height(10.dp))

            var maxLines by remember { mutableIntStateOf(2) }

            if (favoriteTagIds.isNotEmpty()) {
                ContextualTagSelectionFlowRow(
                    tags = possibleTagsToSelect.sortedBy { if (selectedTagIds.contains(it.id)) 0 else 1 },
                    itemCount = possibleTagsToSelect.size,
                    selectedTags = selectedTagIds,
                    removeFromSelectedTags = { selectedTagIds.remove(it) },
                    addToSelectedTags = { selectedTagIds.add(it) },
                    maxLines = maxLines,
                    overflow = ContextualFlowRowOverflow.expandOrCollapseIndicator(
                        minRowsToShowCollapse = 3,
                        expandIndicator = {
                            val more = possibleTagsToSelect.size - shownItemCount
                            FilterChip(
                                selected = false,
                                onClick = { maxLines = Int.MAX_VALUE },
                                label = { Text(stringResource(R.string.more_items, more)) },
                                modifier = Modifier.height(FilterChipDefaults.Height)
                            )
                        },
                        collapseIndicator = {
                            FilterChip(
                                selected = false,
                                onClick = { maxLines = 2 },
                                label = { Text(stringResource(R.string.show_less)) },
                                modifier = Modifier.height(FilterChipDefaults.Height)
                            )
                        }),
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .animateContentSize(),
                    onLongClick = { id ->
                        editTag = getTagFromId(id, tags)
                        showEditTagSheet = true
//                    scope.launch {
//                        editTagSheetState.show()
//                    }
//                    showEditTagDialog = true
                    }
                )
            }
            Spacer(Modifier.height(10.dp))
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5, bottomStartPercent = 0, bottomEndPercent = 0),
//                shape = RoundedCornerShape(5),
                modifier = Modifier.weight(1f)
            ) {
                RecipeGrid(
                    contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp, start = 10.dp, end = 10.dp),
                    recipes = recipesSelected,
                    onClickRecipe = { listIndex -> onChangeSelectedRecipe(recipesSelected[listIndex].id) },
                    recipeGridState = recipeGridState,
                )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalAnimationGraphicsApi::class
)
@Composable
fun RecipeTopBar(
    focusRequesterSearch: FocusRequester,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    isSearching: Boolean,
    onStartSearching: () -> Unit,
    onStopSearching: () -> Unit,
    onOpenFilterDialogue: () -> Unit,
    modifier: Modifier = Modifier,
    badgeNumber: Int? = null
) {
//    val searchWidth by animateDpAsState(targetValue = if (isSearching) 200.dp else 0.dp)
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequesterSearch.requestFocus()
        }
    }

    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(R.string.recipes), modifier = Modifier.width(200.dp), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Box(modifier = Modifier
                .width(200.dp)
                .height(30.dp)) {
                AnimatedVisibility(visible = isSearching, enter = fadeIn() + slideInVertically(), exit = fadeOut() + slideOutVertically()) {
                    BasicTextFieldCustom(
                        value = searchValue,
                        onValueChange = { onSearchValueChange(it) },
                        textStyle = MaterialTheme.typography.titleSmall.copy(textAlign = TextAlign.Center),
                        placeholder = { Text(stringResource(R.string.search_recipes), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), style = MaterialTheme.typography.titleSmall.copy(textAlign = TextAlign.Center)) },
                        modifier = Modifier
                            .width(200.dp)
                            .height(30.dp)
                            .focusRequester(focusRequesterSearch),
//                    trailingIcon = {
//                        IconButton(onClick = { onStopSearching() }) {
//                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
//                        }
//                    },
                        colors = TextFieldDefaults.colors().copy(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(20)
                    )
                }
            }
//            BasicOutlinedTextField(
//                value = searchValue,
//                onValueChange = { onSearchValueChange(it) },
//                textStyle = MaterialTheme.typography.titleSmall,
//                modifier = Modifier
//                    .width(searchWidth)
//                    .height(30.dp)
//                    .focusRequester(focusRequesterSearch),
//                trailingIcon = {
//                    IconButton(onClick = { onStopSearching() }) {
//                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
//                    }
//                }
//            )
        },
        actions = {
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.search_to_clear)
//            if (isSearching) {
                IconButton(onClick = if (isSearching) onStopSearching else onStartSearching) {
                    Icon(rememberAnimatedVectorPainter(image, isSearching), contentDescription = "Start/Stop Search")
                }
//            } else {
//                IconButton(onClick = onStartSearching) {
//                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
//                }
//            }
        },
        modifier = modifier,
//        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.background),
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeGrid(
    recipes: List<Recipe>,
    onClickRecipe: (listIndex: Int) -> Unit,
    recipeGridState: LazyGridState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 100.dp)
) {
    LazyVerticalGrid(modifier = modifier, state = recipeGridState, columns = GridCells.Fixed(3), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = contentPadding) {
        itemsIndexed(recipes, key = { _, recipe -> recipe.id }) { index, recipe ->
            RecipeSmallDisplay(
                recipe = recipe, onClick = { onClickRecipe(index) }, modifier = Modifier.animateItem(placementSpec = null)
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
                    with(sharedTransitionScope!!) {
                        AsyncImage(
                            model = recipe.images[0],
                            contentDescription = null,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = recipe.id),
                                    animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                                )
                                .clip(RoundedCornerShape(10))
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(fallbackBrush, shape = RoundedCornerShape(10)))
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
    content: @Composable () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(10),
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeImageGallery(
    recipeId: UUID,
    images: List<String>,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty())
        return

    val context = LocalContext.current

    val uris = images.map { filePath -> Uri.fromFile(File(filePath)) }
    var currentImageIndex by remember { mutableIntStateOf(0) }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = modifier.height(200.dp)
    ) {
        itemsIndexed(images) { index, path ->
            with (LocalSharedTransitionScope.current!!) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(File(path)).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .conditional(index == 0) {
                            Modifier.sharedElement(
                                rememberSharedContentState(key = recipeId),
                                animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                            )
                        }
                        .clip(RoundedCornerShape(10))
                        .width(150.dp)
                        .clickable(onClick = {
                            currentImageIndex = index

                            val builder =
                                StfalconImageViewer.Builder<Uri>(context, uris) { view, uri ->
                                    Picasso.get().load(uri).into(view)
                                }
                            builder
                                .withStartPosition(index)
                                .show()
                        })
                )
            }
        }
    }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeHeader(
    recipeNameValue: TextFieldValue,
    onRecipeNameChanged: (TextFieldValue) -> Unit,
    onSubmit: () -> Unit,
    editingText: Boolean,
    onStartEditing: () -> Unit,
    focusRequester: FocusRequester,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            EditableText(editable = editingText, textState = recipeNameValue, onTextChange = onRecipeNameChanged, focusRequester = focusRequester, onSubmit = onSubmit, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge)
//            Text(text = , style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
            IconButton(onClick = onBack, modifier = Modifier.size(50.dp)) {
                Icon(painter = painterResource(id = R.drawable.arrow_left), contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(50.dp)) {
                Icon(painter = painterResource(id = R.drawable.more_vert), contentDescription = "More")
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(text = { Text(stringResource(R.string.edit)) }, leadingIcon = { Icon(painterResource(R.drawable.edit), null)}, onClick = {
                    onStartEditing()
                    menuExpanded = false
                })
                DropdownMenuItem(text = { Text(stringResource(id = R.string.delete)) }, leadingIcon = { Icon(painterResource(R.drawable.delete), null, tint = MaterialTheme.colorScheme.error)}, onClick = {
                    onDelete()
                    menuExpanded = false
                })
            }
        }
    )
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
        RecipeScreen(bottomBar = { BottomNavigationBar(navController = navController) }, recipeViewModel = recipeViewModel, addToGroceries = { _, _ -> }, groceryCategories = listOf())
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