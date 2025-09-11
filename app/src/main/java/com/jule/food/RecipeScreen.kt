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
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Clear
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
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
import kotlin.math.ceil


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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    bottomBar: @Composable () -> Unit,
    recipeViewModel: RecipeViewModel,
    recipeGridState: LazyGridState,
    onOpenSettings: () -> Unit,
    onClickRecipe: (recipeId: UUID, fromSearch: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val recipes = recipeViewModel.recipes
    val tags = recipeViewModel.tags


    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        bottomBar = bottomBar,
        floatingActionButton = {
            var showNewRecipeDialog by remember { mutableStateOf(false) }
            val newRecipeFocusRequester = remember { FocusRequester() }

            ExtendedFloatingActionButton(
                onClick = { showNewRecipeDialog = true },
                text = { Text(stringResource(R.string.add_recipe)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
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
                        val id = recipeViewModel.addRecipe(name = name.trim())
                        onClickRecipe(id, false)
                        showNewRecipeDialog = false
                    },
                    confirmWithKeyboard = true,
                    placeholder = { Text(stringResource(id = R.string.name), style = TextStyle.Default) },
                    isError = { isRecipeError(it) },
                    focusRequester = newRecipeFocusRequester
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
            RecipeGridScreen(
                modifier = modifier,
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
                onClickRecipe = onClickRecipe,
                onChangeTagRecipes = { tagId, newRecipes ->
                    recipeViewModel.changeTagRecipes(tagId, newRecipes)
                },
                recipeGridState = recipeGridState,
                onOpenSettings = onOpenSettings,
                searchBarExpanded = recipeViewModel.isSearchBarExpanded,
                onChangeSearchBarExpanded = { recipeViewModel.changeIsSearchBarExpanded(it) },
                selectedTagIds = recipeViewModel.selectedTagIds,
                onAddSelectedTagId = { recipeViewModel.addSelectedTagId(it) },
                onRemoveSelectedTagId = { recipeViewModel.removeSelectedTagId(it) },
                tagSelectionExpanded = recipeViewModel.isTagSelectionExpanded,
                onChangeTagSelectionExpanded = { recipeViewModel.changeIsTagSelectionExpanded(it) }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun RecipeGridScreen(
    recipes: List<Recipe>,
    recentRecipeIds: List<UUID>,
    tags: List<Tag>,
    selectedTagIds: List<UUID>,
    onAddSelectedTagId: (UUID) -> Unit,
    onRemoveSelectedTagId: (UUID) -> Unit,
    onChangeTagName: (UUID, String) -> Unit,
    onChangeTagIconIndex: (UUID, Int) -> Unit,
    onChangeTagRecipes: (UUID, List<Recipe>) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
    onClickRecipe: (recipeId: UUID, fromSearch: Boolean) -> Unit,
    recipeGridState: LazyGridState,
    onOpenSettings: () -> Unit,
    searchBarExpanded: Boolean,
    onChangeSearchBarExpanded: (Boolean) -> Unit,
    tagSelectionExpanded: Boolean,
    onChangeTagSelectionExpanded: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    val focusManager = LocalFocusManager.current

    var editTag: Tag? by remember { mutableStateOf(null) }
    var showEditTagSheet by remember { mutableStateOf(false) }


    val editTagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val recipesSelected = recipes.filter { recipe -> selectedTagIds.isEmpty() || recipe.tags.containsAll(selectedTagIds) }
    val possibleTagIdsToSelect = tags.map { it.id }.filter { tagId ->
            recipesSelected.any { recipe -> recipe.tags.containsAll(selectedTagIds.toList() + tagId) }
        }

    val textFieldState = rememberTextFieldState()


    val inputField = @Composable {
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.padding(start = 5.dp)
        ) {
            Icon(Icons.Outlined.Settings, contentDescription = "Settings")
        }
        SearchBarDefaults.InputField(
            state = textFieldState,
            onSearch = { searchTerm ->
                focusManager.clearFocus(true)
            },
            expanded = searchBarExpanded,
            onExpandedChange = onChangeSearchBarExpanded,
            leadingIcon = {
                if (searchBarExpanded) {
                    IconButton(
                        onClick = {
                            onChangeSearchBarExpanded(false)
                            textFieldState.clearText()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            trailingIcon = {
                AnimatedContent(
                    targetState = searchBarExpanded && textFieldState.text != ""
                ) { showClearButton ->
                    if (showClearButton) {
                        IconButton(onClick = {
                            textFieldState.edit { delete(0, textFieldState.text.length) }
                        }) {
                            Icon(Icons.Outlined.Clear, contentDescription = "Clear Search")
                        }
                    } else {

                    }
                }
            },
            placeholder = {
                Text(stringResource(R.string.search_recipes), textAlign = TextAlign.Center)
            }
        )
    }

    Box {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            SearchBar(
                inputField = inputField,
                expanded = searchBarExpanded,
                onExpandedChange = {
                    onChangeSearchBarExpanded(it)
                    textFieldState.clearText()
                },
                windowInsets = WindowInsets(
                    left = 0.dp,
                    right = 0.dp,
                    top = 0.dp,
                    bottom = 0.dp
                )
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource
                        ) {
                            focusManager.clearFocus(true)
                        }
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
                                    onClickRecipe(recentRecipeIds[listIndex], true)
                                },
                                showImages = searchBarExpanded,
                                isRecipeSearch = true,
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
                                    onClickRecipe(searchResults[listIndex].id, true)
                                },
                                isRecipeSearch = true,
                                recipeGridState = rememberLazyGridState()
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    enabled = searchBarExpanded,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        focusManager.clearFocus(true)
                    }
                )
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(Modifier.height(SearchBarDefaults.InputFieldHeight + 20.dp))
            ExpandableTagSelectionFlowRow(
                tags = tags,
                selectedTagIds = selectedTagIds,
                onAddToSelectedTagIds = onAddSelectedTagId,
                onRemoveFromSelectedTagIds = onRemoveSelectedTagId,
                possibleTagIdsToSelect = possibleTagIdsToSelect,
                onLongClickTag = { id ->
                    editTag = getTagFromId(id, tags)
                    showEditTagSheet = true
                },
                expanded = tagSelectionExpanded,
                onExpandedChange = onChangeTagSelectionExpanded
            )
            Spacer(Modifier.height(10.dp))
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(
                    topStartPercent = 5,
                    topEndPercent = 5,
                    bottomStartPercent = 0,
                    bottomEndPercent = 0
                ),
            ) {
                BoxWithConstraints {
                    val spacingPerRecipe = 10.dp
                    val recipesPerRow = 3
                    val rowNumber =
                        ceil(recipesSelected.size.toDouble() / recipesPerRow).toInt()
                    val maxWidthWithoutSpacing =
                        maxWidth - ((recipesPerRow + 1) * spacingPerRecipe)
                    val widthPerRecipe = maxWidthWithoutSpacing / 3
                    val heightPerRecipe = widthPerRecipe + 40.dp
                    val spacingBetweenRows = (rowNumber - 1) * spacingPerRecipe
                    val spacingTopBottom = 110.dp

                    val totalHeight =
                        heightPerRecipe * rowNumber + spacingBetweenRows + spacingTopBottom
                    val height = max(totalHeight, 1000.dp)
                    RecipeGrid(
                        modifier = Modifier.height(height),
                        contentPadding = PaddingValues(
                            top = 10.dp,
                            bottom = 100.dp,
                            start = 10.dp,
                            end = 10.dp
                        ),
                        recipes = recipesSelected,
                        onClickRecipe = { listIndex ->
                            onClickRecipe(recipesSelected[listIndex].id, false)
                        },
                        recipeGridState = recipeGridState,
                        userScrollEnabled = false,
                        isRecipeSearch = false,
                        showImages = true
                    )
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
                    onChangeTagIconIndex = { newIndex ->
                        onChangeTagIconIndex(
                            editTag!!.id,
                            newIndex
                        )
                    },
                    onChangeTagRecipes = { newRecipes ->
                        onChangeTagRecipes(
                            editTag!!.id,
                            newRecipes
                        )
                    },
                    onDismissRequest = { showEditTagSheet = false },
                    state = editTagSheetState,
                )
            }
        }
    }

}

// Dialog for adding a tag
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

// Dropdown for selecting a tag icon
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
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id))
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id))
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id))
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id))
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id))
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id))
    recipeViewModel.addRecipe(name = "Caesar's Salad", tags = listOf(kaeseTag.id, saladTag.id))
    recipeViewModel.addTags(listOf(fishTag, salzigTag, saladTag, appleTag, kaeseTag, kaeseTag2, kaeseTag3))

    recipeViewModel.initializeEmpty()

    FoodTheme {
        RecipeScreen(bottomBar = { BottomNavigationBar(navController = navController, recipeViewModel = recipeViewModel) }, recipeViewModel = recipeViewModel, onOpenSettings = {}, onClickRecipe = { _, _ -> }, recipeGridState = rememberLazyGridState())
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