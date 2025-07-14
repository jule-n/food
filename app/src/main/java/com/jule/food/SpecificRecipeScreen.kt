package com.jule.food

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jule.food.ui.theme.FoodTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeScreen(
    bottomBar: @Composable () -> Unit,
    recipe: Recipe,
    recipeViewModel: RecipeViewModel,
    addToGroceries: (List<GroceryItem>, Int) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    onBack: () -> Unit,
    onSelectImage: (imageIndex: Int) -> Unit,
    isPop: Boolean,
    modifier: Modifier = Modifier
) {
//    var expanded by remember { mutableStateOf(false) }
    var showGroceryScreen by remember { mutableStateOf(false) }
    val recipes = recipeViewModel.recipes
    val tags = recipeViewModel.tags

    AnimatedVisibility(visible = !showGroceryScreen, enter = completeSlideIn(false), exit = completeSlideOut(true)) {
        SpecificRecipeScreenMain(
            recipe = recipe,
            recipes = recipes,
            onChangeRecipeName = { recipeViewModel.changeRecipeName(recipe.id, it) },
            allTags = tags,
            onAddNewTag = { recipeViewModel.addTag(it) },
            onDeleteTagId = { recipeViewModel.deleteTagId(it) },
            onChangeTagName = { id, newName -> recipeViewModel.changeTagName(id, newName) },
            onChangeTagIconIndex = { id, newIndex -> recipeViewModel.changeTagIconIndex(id, newIndex) },
            onChangeTagRecipes = { id, newRecipes -> recipeViewModel.changeTagRecipes(id, newRecipes) },
            onChangeRecipeTags = { recipeViewModel.changeRecipeTags(recipe.id, it) },
            onChangeRecipeImages = { recipeViewModel.changeRecipeImages(recipe.id, it) },
            onDeleteRecipeImage = { recipeViewModel.deleteRecipeImage(recipe.id, it) },
            addToGroceries = addToGroceries,
            groceryCategories = groceryCategories,
            onBack = onBack,
            onDelete = { recipeViewModel.removeRecipe(recipe.id) },
            onAddImages = { recipeViewModel.addImagesToRecipe(recipe.id, it) },
            bottomBar = bottomBar,
            onOpenGroceryScreen = { showGroceryScreen = true },
            onSelectImage = { onSelectImage(it) },
            isPop = isPop,
            modifier = modifier
        )
    }
    AnimatedVisibility(
        visible = showGroceryScreen,
        enter = completeSlideIn(true),
        exit = completeSlideOut(false)
    ) {
        SpecificRecipeEditGroceriesScreen(
            bottomBar = bottomBar,
            recipe = recipe,
            onConfirm = { newGroceries ->
                showGroceryScreen = false
                recipeViewModel.changeRecipeGroceries(recipe.id, newGroceries)
            },
            modifier = modifier
        )
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeScreenMain(
    bottomBar: @Composable () -> Unit,
    recipe: Recipe,
    recipes: List<Recipe>,
    onSelectImage: (imageIndex: Int) -> Unit,
    onChangeRecipeName: (String) -> Unit,
    allTags: List<Tag>,
    onAddNewTag: (Tag) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
    onChangeTagName: (UUID, String) -> Unit,
    onChangeTagIconIndex: (UUID, Int) -> Unit,
    onChangeTagRecipes: (UUID, List<Recipe>) -> Unit,
    onChangeRecipeTags: (List<UUID>) -> Unit,
    onChangeRecipeImages: (List<String>) -> Unit,
    onDeleteRecipeImage: (String) -> Unit,
    addToGroceries: (List<GroceryItem>, Int) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onAddImages: (List<String>) -> Unit,
    onOpenGroceryScreen: () -> Unit,
    isPop: Boolean,
    modifier: Modifier = Modifier
) {
    var titleValue by remember { mutableStateOf(TextFieldValue(recipe.name)) }
    val titleFocusRequester = remember { FocusRequester() }
    val isEditing by remember { mutableStateOf(false) }
    LaunchedEffect(isEditing) {
        if (isEditing) {
            titleFocusRequester.requestFocus()
            titleValue = titleValue.copy(selection = TextRange(0, titleValue.text.length))
        }
    }
    var focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()


    with (LocalNavAnimatedVisibilityScope.current!!) {
        Scaffold(
            bottomBar = bottomBar,
            modifier = modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
            topBar = {
                MediumFlexibleTopAppBar(
                    title = {
                        val editNameFocusRequester = remember { FocusRequester() }
                        var showEditNameDialog by remember { mutableStateOf(false)}
                        Text(
                            recipe.name,
                            maxLines = 2,
                            style = MaterialTheme.typography.displaySmallEmphasized,
                            autoSize = TextAutoSize.StepBased(maxFontSize = 30.sp),
                            modifier = Modifier.combinedClickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onLongClick = {
                                    showEditNameDialog = true
                                },
                                onClick = {}
                            )
                        )
                        LaunchedEffect(showEditNameDialog) {
                            if (showEditNameDialog) {
                                editNameFocusRequester.requestFocus()
                            }
                        }
                        if (showEditNameDialog) {
                            val textFieldState by remember { mutableStateOf(TextFieldState(recipe.name)) }
                            DefaultDialog(
                                title = stringResource(R.string.change_recipe_name),
                                buttons = true,
                                onDismissRequest = {
                                    if (textFieldState.text.toString() != recipe.name)
                                        showEditNameDialog = false
                                    else
                                        focusManager.clearFocus(true)
                               },
                                onConfirm = {
                                    onChangeRecipeName(textFieldState.text.toString())
                                    showEditNameDialog = false
                                },
                                confirmEnabled = !isRecipeError(textFieldState.text.toString()),
                                onCancel = { showEditNameDialog = false },
                                onClickDialogEnabled = true,
                                onClickDialog = { focusManager.clearFocus(true) }
                            ) {
                                focusManager = LocalFocusManager.current
                                OutlinedTextField(
                                    state = textFieldState,
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .width(250.dp)
                                        .focusRequester(editNameFocusRequester),
                                    shape = RoundedCornerShape(20),
                                    placeholder = { Text(stringResource(id = R.string.name)) },
                                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                    onKeyboardAction = { onDone ->
                                        focusManager.clearFocus(true)
                                    }
                                )
                            }
                        }
                    },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(painterResource(R.drawable.arrow_left), contentDescription = "Back") } },
                    actions = {
                        var showDeleteDialog by remember { mutableStateOf(false) }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(painterResource(R.drawable.delete), contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                        if (showDeleteDialog) {
                            DefaultDialog(
                                title = stringResource(R.string.delete_recipe),
                                buttons = true,
                                onDismissRequest = { showDeleteDialog = false },
                                onConfirm = onDelete
                            ) {
                                Text(stringResource(R.string.are_you_sure_you_want_to_delete_this_item, recipe.name), textAlign = TextAlign.Center)
                            }
                        }
                    },
                    scrollBehavior = scrollBehaviour,
//                    modifier = Modifier.animateEnterExit(enter = if (isPop) slideInVertically() else fadeIn(), exit = if (isPop) slideOutVertically() else fadeOut())
                )
            }
        ) { innerPadding ->
            val scrollState = rememberScrollState()
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 10.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = isEditing,
                        onClick = {
                            focusManager.clearFocus(true)
                        }
                    )
//                    .animateEnterExit(
//                        enter = if (isPop) slideInHorizontally(initialOffsetX = { it / 2 }) else fadeIn(),
//                        exit = if (isPop) slideOutHorizontally(targetOffsetX = { it / 2 }) else fadeOut()
//                    )
            ) {
                    SpecificRecipeImages(
                        recipe = recipe,
                        onAddImages = onAddImages,
                        onChangeRecipeImages = onChangeRecipeImages,
                        onDeleteRecipeImage = onDeleteRecipeImage,
                        onSelectImage = onSelectImage
                    )
                    SpecificRecipeGroceries(
                        recipe = recipe,
                        addToGroceries = addToGroceries,
                        groceryCategories = groceryCategories,
                        onOpenGroceryScreen = onOpenGroceryScreen
                    )
                    SpecificRecipeSection(
                        icon = R.drawable.list,
                        title = stringResource(R.string.notes),
                        actionButtons = {
                            FilledIconButton(shapes = IconButtonDefaults.shapes(), onClick = { }) {
                                Icon(painterResource(R.drawable.edit), contentDescription = "Edit")
                            }
                        },
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text("Here are some notes")
                        }
                    }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(Modifier.height(10.dp))
//                }
                SpecificRecipeTags(
                    recipe = recipe,
                    recipes = recipes,
                    allTags = allTags,
                    onAddNewTag = onAddNewTag,
                    onDeleteTagId = onDeleteTagId,
                    onChangeTagName = onChangeTagName,
                    onChangeTagIconIndex = onChangeTagIconIndex,
                    onChangeRecipeTags = onChangeRecipeTags,
                    onChangeTagRecipes = onChangeTagRecipes
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeEditGroceriesScreen(
    bottomBar: @Composable () -> Unit,
    recipe: Recipe,
    onConfirm: (List<GroceryItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    val temporaryGroceries = remember { recipe.groceries.toMutableStateList() }

    val deletedGroceries = remember { mutableStateListOf<GroceryItem>() }
    var showAddGroceryDialog by remember { mutableStateOf(false) }
    var showEditGroceryDialog by remember { mutableStateOf(false) }
    var editGroceryId: UUID? by remember { mutableStateOf(null) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { recipe.images.size })


    BackHandler(
        onBack = {
            onConfirm(temporaryGroceries)
        }
    )

    Scaffold(
        bottomBar = bottomBar,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = stringResource(R.string.edit_groceries), style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(text = recipe.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.height(20.dp))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onConfirm(temporaryGroceries)
                    }) {
                        Icon(painterResource(R.drawable.arrow_left), contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddGroceryDialog = true },
                text = { Text(stringResource(R.string.add_grocery)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (recipe.images.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HorizontalPager(
                        state = pagerState,
                    ) { index ->
                        ZoomableImage(recipe.images[index], modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.3f), contentScale = ContentScale.Fit)
                        //                Image(painter = painterResource(R.drawable.cauliflower_wings), contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f), contentDescription = null)
                    }
                    if (recipe.images.size > 1) {
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            for(i in 0..<recipe.images.size) {
                                Box(modifier = Modifier
                                    .size(5.dp)
                                    .background(
                                        color = if (i == pagerState.currentPage) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.6f
                                        ), shape = CircleShape
                                    ))
                            }
                        }
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
//        contentPadding = PaddingValues(5.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                val br = Brush.linearGradient(listOf(Color.LightGray, Color.White))
                items(items = temporaryGroceries.sortedBy { it.name }, key = { item -> item.id } ) { groceryItem ->
                    GroceryItemDisplay(
                        item = groceryItem,
                        onClick = {
                            temporaryGroceries.remove(groceryItem)
                            deletedGroceries.add(groceryItem)
                        },
                        onLongClick = {
                            editGroceryId = groceryItem.id
                            showEditGroceryDialog = true
                        },
                        center = true,
                        modifier = Modifier.animateItem()
                    )
                }
                if (deletedGroceries.isNotEmpty()) {
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {}
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .animateItem()) {
                            Text(
                                stringResource(R.string.deleted), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), modifier = Modifier.align(
                                    Alignment.BottomStart))
                        }
                    }
                    items(items = deletedGroceries.sortedBy { it.name }, key = { item -> item.id } ) { groceryItem ->
                        GroceryItemDisplay(
                            item = groceryItem,
                            onClick = {
                                deletedGroceries.remove(groceryItem)
                                temporaryGroceries.add(groceryItem)
                            },
                            itemBrush = br,
                            onLongClick = { },
                            textColor = Color.Red,
                            detailTextColor = Color.Black.copy(alpha = 0.6f),
                            center = true,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
        val groceryDialogFocusRequester = remember { FocusRequester() }
        LaunchedEffect(showAddGroceryDialog) {
            if (showAddGroceryDialog) {
                groceryDialogFocusRequester.requestFocus()
            }
        }
        if (showAddGroceryDialog) {
            AddGroceryDialog(
                onDismissRequest = { showAddGroceryDialog = false },
                onConfirm = { newGrocery ->
                    temporaryGroceries.add(newGrocery)
                },
                focusRequester = groceryDialogFocusRequester,
                focusDetailsOnNext = true
            )
        }
        if (showEditGroceryDialog) {
            val editGroceryIndex = temporaryGroceries.indexOfFirst { it.id == editGroceryId!! }
            AddGroceryDialog(
                title = stringResource(R.string.edit_grocery),
                onDismissRequest = { showEditGroceryDialog = false },
                onConfirm = { newItem ->
                    temporaryGroceries.removeAt(editGroceryIndex)
                    temporaryGroceries.add(newItem)
                    showEditGroceryDialog = false
                },
                focusDetailsOnNext = false,
                imeActionDone = true,
                allowDismissIfEmpty = true,
                focusRequester = remember { FocusRequester() },
                startValue = temporaryGroceries[editGroceryIndex].name,
                startDetails = temporaryGroceries[editGroceryIndex].details,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SpecificRecipeTags(
    recipe: Recipe,
    recipes: List<Recipe>,
    allTags: List<Tag>,
    onAddNewTag: (Tag) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
    onChangeTagName: (UUID, String) -> Unit,
    onChangeTagIconIndex: (UUID, Int) -> Unit,
    onChangeTagRecipes: (UUID, List<Recipe>) -> Unit,
    onChangeRecipeTags: (List<UUID>) -> Unit,
    modifier: Modifier = Modifier
) {
    var tagSelectionDialogActive by remember { mutableStateOf(false) }
    var editTag: Tag? by remember { mutableStateOf(null) }
    var showEditTagDialog by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    val addTagFocusRequester = remember { FocusRequester() }
    SpecificRecipeSection(
        icon = R.drawable.tag,
        title = stringResource(R.string.tags),
        actionButtons = {
            IconButton(onClick = { tagSelectionDialogActive = true }) {
                Icon(painterResource(R.drawable.edit), contentDescription = "Edit")
            }
        },
//        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        modifier = modifier
    ) {
        if (tagSelectionDialogActive) {
            val selectedTagIds = remember { recipe.tags.toMutableStateList() }
            var dialogFocusManager = LocalFocusManager.current

            DefaultDialog(
                title = stringResource(R.string.select_tags),
                onDismissRequest = {
                    onChangeRecipeTags(selectedTagIds)
                    tagSelectionDialogActive = false
                },
                onClickDialogEnabled = true,
                onClickDialog = { dialogFocusManager.clearFocus(true) }
            ) {
                dialogFocusManager = LocalFocusManager.current

                TagSelectionFlowRow(
                    tags = allTags.sortedBy { if (selectedTagIds.contains(it.id)) 0 else 1 },
                    selectedTags = selectedTagIds,
                    removeFromSelectedTags = { selectedTagIds.remove(it) },
                    addToSelectedTags = { selectedTagIds.add(it) },
                    onLongClick = { id ->
                        editTag = getTagFromId(id, allTags)
                        showEditTagDialog = true
                    }
                )

                TextButtonWithIcon(
                    text = { Text(stringResource(R.string.new_tag)) },
                    onClick = { showAddTagDialog = true },
                    icon = R.drawable.add
                )
            }
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
        LaunchedEffect(showAddTagDialog) {
            if (showAddTagDialog) {
                addTagFocusRequester.requestFocus()
            }
        }
        if (showAddTagDialog) {
            AddTagDialog(
                onConfirm = { tag ->
                    onAddNewTag(tag)
                    showAddTagDialog = false
                },
                onCancel = { showAddTagDialog = false},
                focusRequester = addTagFocusRequester
            )
        }
        DisplayTagsRow(getTagsFromIds(recipe.tags, allTags))
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SpecificRecipeImages(
    recipe: Recipe,
    onSelectImage: (imageIndex: Int) -> Unit,
    onAddImages: (List<String>) -> Unit,
    onChangeRecipeImages: (List<String>) -> Unit,
    onDeleteRecipeImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var imageEditDialogActive by remember { mutableStateOf(false) }
    var imageBottomSheetIndex by remember { mutableIntStateOf(0) }
    var imageBottomSheetActive by remember { mutableStateOf(false) }

    SpecificRecipeSection(
        modifier = modifier,
        icon = R.drawable.outline_image_24,
        title = stringResource(R.string.images),
        actionButtons = {
            SelectImagesIconButton(maxImages = 10, onSelectImages = onAddImages)
        }
    ) {
        RecipeImageGallery(recipeId = recipe.id, images = recipe.images, onLongClickImage = { imageIndex ->
            imageBottomSheetActive = true
            imageBottomSheetIndex = imageIndex
        }, onSelectImage = onSelectImage)

        if (imageBottomSheetActive) {
            ModalBottomSheet(
                onDismissRequest = { imageBottomSheetActive = false }
            ) {

                BottomSheetButton(
                    text = stringResource(R.string.reorder_images),
                    onClick = { imageEditDialogActive = true },
                    icon = R.drawable.drag_handle,
                    enabled = recipe.images.size > 1
                )
                BottomSheetButton(
                    text = stringResource(R.string.delete),
                    onClick = { onDeleteRecipeImage(recipe.images[imageBottomSheetIndex]) },
                    icon = R.drawable.delete,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (imageEditDialogActive) {

            val images = remember { recipe.images.toMutableStateList() }

            DefaultDialog(
                title = stringResource(R.string.reorder_images),
                onDismissRequest = {
                    if (images.toList() == recipe.images.toList()) {
                        imageEditDialogActive = false
                        imageBottomSheetActive = false
                    }
                },
                buttons = true,
                onConfirm = {
                    imageEditDialogActive = false
                    imageBottomSheetActive = false
                    onChangeRecipeImages(images)
                },
                confirmEnabled = images.toList() != recipe.images.toList(),
                confirmText = R.string.save
            ) {
//                    var list by remember { mutableStateOf(List(100) { "Item $it" }) }
                val lazyGridState = rememberLazyGridState()
                val reorderableLazyGridState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
                    images.apply {
                        add(to.index, removeAt(from.index))
                    }
                }
                var imageViewDialogActive by remember { mutableStateOf(false) }
                var imageViewDialogIndex by remember { mutableIntStateOf(0) }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 80.dp),
//                        modifier = Modifier.fillMaxSize(),
                    state = lazyGridState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(images, key = {_, image -> image }) { index, image ->
                        ReorderableItem(reorderableLazyGridState, key = image) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                            Surface(shadowElevation = elevation, shape = RoundedCornerShape(10), modifier = Modifier
                                .fillMaxWidth()
                                .draggableHandle()
                                .aspectRatio(0.75f),
                                onClick = {
                                    imageViewDialogActive = true
                                    imageViewDialogIndex = index
                                }
                            ) {
                                AsyncImage(
                                    model = image,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Box(modifier = Modifier.align(Alignment.BottomStart)) {
                                        Text((index+1).toString(), color = Color.Black, style = MaterialTheme.typography.labelSmall.copy(shadow = Shadow(color = Color.White, offset = Offset(1f, 1f))))
                                    }
                                }
                            }
                        }
                    }
                }

                if (imageViewDialogActive) {
                    Dialog(onDismissRequest = { imageViewDialogActive = false }, properties = DialogProperties()) {
                        AsyncImage(
                            model = images[imageViewDialogIndex],
                            contentDescription = null,
                            placeholder = painterResource(R.drawable.hamburger),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(0.75f)
                                .clickable {
                                    imageViewDialogActive = false
                                }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeImageGallery(
    recipeId: UUID,
    images: List<String>,
    onSelectImage: (imageIndex: Int) -> Unit,
    onLongClickImage: (imageIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty())
        return

    val context = LocalContext.current

//    val uris = images.map { filePath -> Uri.fromFile(File(filePath)) }
//    var currentImageIndex by remember { mutableIntStateOf(0) }

    var imageViewerStartIndex by remember { mutableIntStateOf(0) }
    var imageViewerActive by remember { mutableStateOf(false) }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = modifier.height(200.dp)
    ) {
        itemsIndexed(images) { index, path ->
            val model = if (index == 0) {
                ImageRequest.Builder(context)
                    .data(File(path))
                    .crossfade(true)
                    .placeholderMemoryCacheKey(recipeId.toString())
                    .memoryCacheKey(recipeId.toString())
                    .build()
            } else {
                ImageRequest.Builder(context)
                    .data(File(path))
                    .crossfade(true)
                    .build()
            }

            val imageKey = if (index == 0) recipeId else "${recipeId}_${index}"

            with(LocalSharedTransitionScope.current!!) {
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = imageKey),
                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                        )
                        .clip(RoundedCornerShape(10))
                        .width(150.dp)
                        .combinedClickable(onClick = {
                            onSelectImage(index)
                        }, onLongClick = { onLongClickImage(index) })
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpecificRecipeScreenPreview() {
    val fishTag = Tag("Fisch", 0)
    val salzigTag = Tag("Salzig", 1)
    val saladTag = Tag("Salat", 2)
    val appleTag = Tag("Apfel", 3)
    val kaeseTag = Tag("Käse", 4)
    val groceries = listOf(GroceryItem("Apfel", ""), GroceryItem("Salat", ""), GroceryItem("Käse", ""))
    val recipe = Recipe(name = "Dorade in Salzkruste", tags = mutableListOf(fishTag.id, salzigTag.id, appleTag.id), groceries = remember {groceries.toMutableStateList() })

    FoodTheme {
        AnimatedVisibility(true) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                SpecificRecipeScreen(
                    bottomBar = {},
                    recipe = recipe,
                    onBack = { },
                    recipeViewModel = viewModel(),
                    addToGroceries = { _, _ -> },
                    groceryCategories = listOf(),
                    onSelectImage = { },
                    isPop = false
                )
            }
        }
    }
}