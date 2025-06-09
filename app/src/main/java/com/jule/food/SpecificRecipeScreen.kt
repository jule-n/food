package com.jule.food

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeScreen(
    recipe: Recipe,
    recipes: List<Recipe>,
    onChangeRecipeName: (String) -> Unit,
    allTags: List<Tag>,
    onAddNewTag: (Tag) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
    onChangeTagName: (UUID, String) -> Unit,
    onChangeTagIconIndex: (UUID, Int) -> Unit,
    onChangeTagRecipes: (UUID, List<Recipe>) -> Unit,
    onChangeRecipeTags: (List<UUID>) -> Unit,
    onChangeRecipeImages: (List<String>) -> Unit,
    onDeleteRecipeImages: (List<String>) -> Unit,
    onChangeGroceries: (List<GroceryItem>) -> Unit,
    addToGroceries: (List<GroceryItem>, Int) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onAddImages: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
//    var expanded by remember { mutableStateOf(false) }

    var titleValue by remember { mutableStateOf(TextFieldValue(recipe.name)) }
    val titleFocusRequester = remember { FocusRequester() }
    var isEditing by remember { mutableStateOf(false) }
    var lastValueWithoutError by remember { mutableStateOf(recipe.name) }

    var showGroceryScreen by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val temporaryGroceries = remember { recipe.groceries.toMutableStateList() }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            titleFocusRequester.requestFocus()
            titleValue = titleValue.copy(selection = TextRange(0, titleValue.text.length))
        }
    }
    fun onCloseGroceryScreen() {
        onChangeGroceries(temporaryGroceries)
    }

    BackHandler(
        enabled = showGroceryScreen,
        onBack = {
            showGroceryScreen = false
            onCloseGroceryScreen()
        }
    )

    AnimatedVisibility(visible = !showGroceryScreen, enter = completeSlideIn(false), exit = completeSlideOut(true)) {
        Column(modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEditing,
                onClick = {
                    focusManager.clearFocus(true)
                }), verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SpecificRecipeHeader(
                recipeNameValue = titleValue,
                onRecipeNameChanged = {
                    if (!isRecipeError(it.text))
                        lastValueWithoutError = it.text
                    titleValue = it
                },
                onBack = onBack,
                onStartEditing = {
                    isEditing = true
                },
                editingText = isEditing,
                onDelete = onDelete,
                onSubmit = {
                    isEditing = false
                    onChangeRecipeName(lastValueWithoutError)
                    titleValue = titleValue.copy(text = lastValueWithoutError)
                },
                focusRequester = titleFocusRequester
            )
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
            SpecificRecipeImages(
                recipe = recipe,
                onAddImages = onAddImages,
                onChangeRecipeImages = onChangeRecipeImages,
                onDeleteRecipeImages = onDeleteRecipeImages
            )
            SpecificRecipeGroceries(
                recipe = recipe,
                addToGroceries = addToGroceries,
                groceryCategories = groceryCategories,
                onChangeGroceries = onChangeGroceries,
                onOpenGroceryScreen = { showGroceryScreen = true }
            )
        }
    }
    AnimatedVisibility(visible = showGroceryScreen, enter = completeSlideIn(true), exit = completeSlideOut(false)) {
        val deletedGroceries = remember { mutableStateListOf<GroceryItem>() }
        var showAddGroceryDialog by remember { mutableStateOf(false) }
        var showEditGroceryDialog by remember { mutableStateOf(false) }
        var editGroceryId: UUID? by remember { mutableStateOf(null) }
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { recipe.images.size })

        Column(modifier = modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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
                        showGroceryScreen = false
                        onCloseGroceryScreen()
                    }) {
                        Icon(painterResource(R.drawable.arrow_left), contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
            if (recipe.images.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HorizontalPager(
                        state = pagerState,
                    ) { index ->
                        ZoomableImage(recipe.images[index], modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f), contentScale = ContentScale.Fit)
                        //                Image(painter = painterResource(R.drawable.cauliflower_wings), contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f), contentDescription = null)
                    }
                    if (recipe.images.size > 1) {
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            for(i in 0..<recipe.images.size) {
                                Box(modifier = Modifier
                                    .size(5.dp)
                                    .background(color = if (i == pagerState.currentPage) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), shape = CircleShape))
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
                    .fillMaxWidth().padding(horizontal = 20.dp)
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
                        Box(modifier = Modifier.fillMaxWidth().height(50.dp).animateItem()) {
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


        Box(modifier = Modifier.fillMaxSize()) {
            ExtendedFloatingActionButton(
                onClick = { showAddGroceryDialog = true },
                text = { Text(stringResource(R.string.add_grocery)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset((-20).dp, (-20).dp)
            )
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SpecificRecipeImages(
    recipe: Recipe,
    onAddImages: (List<String>) -> Unit,
    onChangeRecipeImages: (List<String>) -> Unit,
    onDeleteRecipeImages: (List<String>) -> Unit
) {
    var imageEditDialogActive by remember { mutableStateOf(false) }

    SpecificRecipeSection(
        icon = R.drawable.outline_image_24,
        title = stringResource(R.string.images),
        actionButtons = {
            IconButton(onClick = { imageEditDialogActive = true }, enabled = recipe.images.isNotEmpty()) {
                Icon(painterResource(R.drawable.edit), contentDescription = "Edit")
            }
            SelectImagesIconButton(maxImages = 10, onSelectImages = onAddImages)
        }
    ) {
        RecipeImageGallery(recipeId = recipe.id, images = recipe.images)

        if (imageEditDialogActive) {

            val images = remember { recipe.images.toMutableStateList() }
            val deletedImages = remember { mutableStateListOf<String>() }
            DefaultDialog(
                title = stringResource(R.string.edit_images),
                onDismissRequest = {
                    imageEditDialogActive = false
                },
                buttons = true,
                onConfirm = {
                    imageEditDialogActive = false
                    onChangeRecipeImages(images)
                    onDeleteRecipeImages(deletedImages)
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
                                .aspectRatio(0.75f),
                                onClick = {
                                    imageViewDialogActive = true
                                    imageViewDialogIndex = index
                                }
                            ) {
                                AsyncImage(
                                    model = image,
                                    contentDescription = null,
                                    placeholder = painterResource(R.drawable.hamburger),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                                Box(modifier = Modifier.fillMaxSize()) {
                                    IconButton(
                                        modifier = Modifier
                                            .draggableHandle()
                                            .size(30.dp)
                                            .align(Alignment.TopStart),
                                        onClick = {},
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.drag_handle),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier
                                                .offset(1.dp, 1.dp)
                                                .zIndex(0f)
                                        )
                                        Icon(
                                            painterResource(R.drawable.drag_handle),
                                            contentDescription = "Reorder",
                                            tint = Color.Black,
                                            modifier = Modifier.zIndex(1f)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            images.remove(image)
                                            deletedImages.add(image)
                                        }, modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.delete),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier
                                                .offset(1.dp, 1.dp)
                                                .zIndex(0f)
                                        )
                                        Icon(
                                            painterResource(R.drawable.delete),
                                            contentDescription = "Delete",
                                            tint = Color.Black,
                                            modifier = Modifier.zIndex(1f)
                                        )
                                    }
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