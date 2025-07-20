package com.jule.food

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.material3.surfaceColorAtElevation
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
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableRow
import sh.calvin.reorderable.rememberReorderableLazyGridState
import sh.calvin.reorderable.rememberReorderableLazyListState
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
    onDisplayImage: (imageIndex: Int) -> Unit,
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
            addToGroceries = addToGroceries,
            groceryCategories = groceryCategories,
            onBack = onBack,
            onDelete = { recipeViewModel.removeRecipe(recipe.id) },
            onAddImages = { recipeViewModel.addImagesToRecipe(recipe.id, it) },
            bottomBar = bottomBar,
            onOpenGroceryScreen = { showGroceryScreen = true },
            onDisplayImage = { onDisplayImage(it) },
            onChangeImageOrder = { fromIndex, toIndex ->
                val newImages = recipe.images.toMutableList().apply {
                    add(toIndex, removeAt(fromIndex))
                }
                recipeViewModel.changeRecipeImages(recipe.id, newImages)
            },
            onDeleteImages = { indizesToDelete ->
                for (index in indizesToDelete) {
                    recipeViewModel.deleteRecipeImage(recipe.id, recipe.images[index])
                }

            },
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
    onDisplayImage: (imageIndex: Int) -> Unit,
    onChangeRecipeName: (String) -> Unit,
    allTags: List<Tag>,
    onAddNewTag: (Tag) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
    onChangeTagName: (UUID, String) -> Unit,
    onChangeTagIconIndex: (UUID, Int) -> Unit,
    onChangeTagRecipes: (UUID, List<Recipe>) -> Unit,
    onChangeRecipeTags: (List<UUID>) -> Unit,
    onChangeImageOrder: (fromIndex: Int, toIndex: Int) -> Unit,
    onDeleteImages: (List<Int>) -> Unit,
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    val selectedImagesIndizes = remember { mutableStateListOf<Int>() }
    var anyImageSelected by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(enabled = anyImageSelected) {
        selectedImagesIndizes.clear()
        anyImageSelected = false
    }


    with (LocalNavAnimatedVisibilityScope.current!!) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = bottomBar,
            modifier = modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
            topBar = {
                AnimatedContent(
                    targetState = selectedImagesIndizes.size > 0,
                    transitionSpec = {
                        slideInVertically { -it } togetherWith slideOutVertically { -it }
                    }
                ) { imagesSelected ->
                    if (!imagesSelected) {
                        SpecificRecipeTopBar(
                            recipe = recipe,
                            onBack = onBack,
                            onDelete = onDelete,
                            scrollBehaviour = scrollBehaviour
                        )
                    } else {
                        TopAppBar(
                            title = { Text(stringResource(R.string.n_selected_images, selectedImagesIndizes.size))},
                            navigationIcon = {
                                IconButton(onClick = {
                                    selectedImagesIndizes.clear()
                                    anyImageSelected = false
                                }) {
                                    Icon(painterResource(R.drawable.clear), contentDescription = "Clear Selection")
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    val imageCount = selectedImagesIndizes.size
                                    onDeleteImages(selectedImagesIndizes)
                                    selectedImagesIndizes.clear()
                                    anyImageSelected = false

                                    coroutineScope.launch {
                                        val result = snackbarHostState.showSnackbar(message = context.getString(R.string.deleted_n_images, imageCount), actionLabel = context.getString(R.string.undo), duration = SnackbarDuration.Short)
//                                        if (result == SnackbarResult.ActionPerformed) {
//                                            groceryViewModel.addToGroceries(item, currentCategoryIndex)
//                                        }
                                    }
                                }) {
                                    Icon(painterResource(R.drawable.delete), contentDescription = "Delete Images")
                                }
                            },
                            scrollBehavior = scrollBehaviour
                        )
                    }
                }
            }
        ) { innerPadding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 12.dp)
                    .fillMaxSize()
            ) {

                val editNameFocusRequester = remember { FocusRequester() }
                var showEditNameDialog by remember { mutableStateOf(false)}
                Box(
                    modifier = Modifier.height(TopAppBarDefaults.MediumFlexibleAppBarWithoutSubtitleExpandedHeight - 48.dp)
                ) {

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
                }
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
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = isEditing || anyImageSelected,
                        onClick = {
                            if (isEditing) {
                                focusManager.clearFocus(true)
                            }
                            if (anyImageSelected) {
                                selectedImagesIndizes.clear()
                                anyImageSelected = false
                            }
                        }
                    )
            ) {
                SpecificRecipeImages(
                    recipe = recipe,
                    onAddImages = onAddImages,
                    onDisplayImage = onDisplayImage,
                    onChangeImageOrder = { fromIndex, toIndex ->
                        selectedImagesIndizes.remove(fromIndex)
                        selectedImagesIndizes.add(toIndex)
                        onChangeImageOrder(fromIndex, toIndex)
                    },
                    selectedImagesIndizes = selectedImagesIndizes,
                    onAddSelectedImageIndex = { selectedImagesIndizes.add(it) },
                    onRemoveSelectedImageIndex = { selectedImagesIndizes.remove(it) },
                    anyImageSelected = anyImageSelected,
                    onChangeAnyImageSelected = { anyImageSelected = it }
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
                Spacer(Modifier.height(10.dp))
//                FilledTonalButton()
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Icon(painterResource(R.drawable.delete), contentDescription = "Delete")
                    Text(stringResource(R.string.delete_recipe))
                }
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeTopBar(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    scrollBehaviour: TopAppBarScrollBehavior
) {
    TopAppBar(
        modifier = modifier,
        title = { },
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
    )
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
    onDisplayImage: (imageIndex: Int) -> Unit,
    onAddImages: (List<String>) -> Unit,
    onChangeImageOrder: (fromIndex: Int, toIndex: Int) -> Unit,
    selectedImagesIndizes: List<Int>,
    onAddSelectedImageIndex: (Int) -> Unit,
    onRemoveSelectedImageIndex: (Int) -> Unit,
    anyImageSelected: Boolean,
    onChangeAnyImageSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var imageEditDialogActive by remember { mutableStateOf(false) }

    SpecificRecipeSection(
        modifier = modifier,
        icon = R.drawable.outline_image_24,
        title = stringResource(R.string.images),
        actionButtons = {
            SelectImagesIconButton(maxImages = 10, onSelectImages = onAddImages)
        }
    ) {
        RecipeImageGallery(recipeId = recipe.id, images = recipe.images, onDisplayImage = onDisplayImage, onChangeImageOrder = onChangeImageOrder,
            selectedImagesIndizes = selectedImagesIndizes, onAddSelectedImageIndex = onAddSelectedImageIndex, onRemoveSelectedImageIndex = onRemoveSelectedImageIndex,
            anyImageSelected = anyImageSelected, onChangeAnyImageSelected = onChangeAnyImageSelected)
    }
}


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecipeImageGallery(
    recipeId: UUID,
    onDisplayImage: (imageIndex: Int) -> Unit,
    images: List<String>,
    onChangeImageOrder: (fromIndex: Int, toIndex: Int) -> Unit,
    selectedImagesIndizes: List<Int>,
    onAddSelectedImageIndex: (Int) -> Unit,
    onRemoveSelectedImageIndex: (Int) -> Unit,
    anyImageSelected: Boolean,
    onChangeAnyImageSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty())
        return

    val context = LocalContext.current

    val rowState = rememberLazyListState()
    val reorderableRowState = rememberReorderableLazyListState(lazyListState = rowState, onMove = { from, to ->
        onChangeImageOrder(from.index, to.index)
    })


    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 20.dp),
        state = rowState,
        modifier = modifier.height(200.dp)
    ) {
        itemsIndexed(images, key = { _, path -> path }) { index, path ->
            ReorderableItem(
                state = reorderableRowState,
                key = path
            ) { isDragging ->
                val interactionSource = remember { MutableInteractionSource() }
                val model = if (index == 0) {
                    ImageRequest.Builder(context)
                        .data(File(path))
                        .crossfade(true)
                        .placeholderMemoryCacheKey(path)
                        .memoryCacheKey(path)
                        .build()
                } else {
                    ImageRequest.Builder(context)
                        .data(File(path))
                        .crossfade(true)
                        .placeholderMemoryCacheKey(path)
                        .memoryCacheKey(path)
                        .build()
                }

                val selected = selectedImagesIndizes.contains(index)
                val onlySelected = selected && selectedImagesIndizes.size == 1

                val mod = if (!anyImageSelected) {
                    Modifier.longPressDraggableHandle(interactionSource = interactionSource, onDragStarted = {
                        onAddSelectedImageIndex(index)
                    }, onDragStopped = {
                        onChangeAnyImageSelected(true)
                    })
                } else if (onlySelected) {
                    Modifier.draggableHandle(interactionSource = interactionSource)
                } else {
                    Modifier
                }

                key (anyImageSelected) {
                    Surface(
                        onClick = {
                            if (!anyImageSelected && selectedImagesIndizes.isEmpty()) {
                                onDisplayImage(index)
                            }
                            else if (anyImageSelected) {
                                if (selectedImagesIndizes.contains(index)) {
                                    onRemoveSelectedImageIndex(index)
                                    if (selectedImagesIndizes.isEmpty())
                                        onChangeAnyImageSelected(false)
                                } else {
                                    onAddSelectedImageIndex(index)
                                }
                            }
                        },
                        color = Color.Transparent,
                        modifier = mod
                    ) {
                        with(LocalSharedTransitionScope.current!!) {
                            AsyncImage(
                                model = model,
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
                                    .conditional(selected) {
                                        Modifier.border(
                                            width = 3.dp,
                                            color = if (onlySelected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(10)
                                        )
                                    }
    //                                .clickable(onClick = { onSelectImage(index) }, interactionSource = interactionSource, indication = LocalIndication.current)
                            )
                        }
                    }
                }
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
                    onDisplayImage = { },
                    isPop = false
                )
            }
        }
    }
}