package com.jule.food.ui.recipes

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryLocation
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.R
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.Recipe
import com.jule.food.ui.groceries.items
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.utils.TextButtonWithIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpecificRecipeEditGroceriesScreen(
    bottomBar: @Composable () -> Unit,
    recipe: Recipe,
    onBack: () -> Unit,
    allLocations: List<GroceryLocation>,
    onAddLocation: (String) -> Unit,
    onRemoveLocation: (UUID) -> Unit,
    onChangeLocationName: (String, UUID) -> Unit,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit,
    getLocationNameFromId: (UUID) -> String,
    allCategories: List<GroceryItemCategory>,
    getCategoryNameFromId: (UUID) -> String,
    onChangeRecipeGroceries: (List<GroceryItem>) -> Unit,
    onDispose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val resources = LocalResources.current

    val localGroceryItems = remember { recipe.groceries.toMutableStateList() }

    val focusRequesters = remember { items.map { it.id to FocusRequester() }.toMutableStateMap() }

    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localGroceryItems.apply {
            add(to.index, removeAt(from.index))
        }
        onChangeRecipeGroceries(localGroceryItems)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }
    val snackbarHostState = remember { SnackbarHostState() }

//    BackHandler(
//        onBack = onBack
//    )

    // Transfer on dispose event up the chain so the changes get saved to file
    DisposableEffect(Unit) {
        onDispose {
            onDispose()
        }
    }


    Scaffold(
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.edit_groceries), style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(text = recipe.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.height(20.dp))
                    }
                },
                navigationIcon = {
                    IconButtonWithTooltip(
                        onClick = onBack,
                        tooltipText = stringResource(R.string.back)
                    ) {
                        Icon(
                            painterResource(R.drawable.arrow_left),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val newItem = GroceryItem("", "")
                    localGroceryItems.add(newItem)
                    focusRequesters[newItem.id] = FocusRequester()
                    scope.launch {
                        lazyListState.animateScrollToItem(localGroceryItems.count()-1)
                        delay(200.milliseconds)
                        focusRequesters[newItem.id]?.requestFocus()
                    }
                },
                text = { Text(stringResource(R.string.add_grocery)) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                focusManager.clearFocus(true)
            }
        ) {
            var showImages by remember { mutableStateOf(false) }
            AnimatedContent(
                targetState = showImages
            ) { show ->
                if (!show) {
                    TextButtonWithIcon(
                        text = { Text(stringResource(R.string.show_recipe_images)) },
                        icon = R.drawable.outline_image_24,
                        onClick = { showImages = true },
                        enabled = recipe.images.isNotEmpty()
                    )
                } else {
                    val pagerState = rememberPagerState(initialPage = 0, pageCount = { recipe.images.size })
                    Box {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            HorizontalPager(
                                state = pagerState,
                            ) { index ->
                                ZoomableImage(recipe.images[index], modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.3f), contentScale = ContentScale.Inside)
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
                                Spacer(modifier = Modifier.height(0.dp))
                            }
                        }
                        Box(modifier = Modifier.matchParentSize()) {
                            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                                IconButtonWithTooltip(
                                    onClick = { showImages = false },
                                    tooltipText = stringResource(R.string.close)
                                ) {
                                    Icon(
                                        painterResource(R.drawable.clear),
                                        contentDescription = stringResource(R.string.close)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5)) {
                if (localGroceryItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("No groceries added yet", style = MaterialTheme.typography.displaySmallEmphasized, modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    LazyColumn(
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        contentPadding = PaddingValues(
                            start = 15.dp,
                            end = 15.dp,
                            top = 15.dp,
                            bottom = 500.dp
                        ),
                        modifier = Modifier.heightIn(min = 1000.dp)
                    ) {
                        items(localGroceryItems, key = { item -> item.id }) { groceryItem ->
                            val nameState = rememberTextFieldState(groceryItem.name)
                            val detailState = rememberTextFieldState(groceryItem.details)

                            var locationId by remember { mutableStateOf(groceryItem.locationId) }
                            var categoryId by remember { mutableStateOf(groceryItem.categoryId) }

                            LaunchedEffect(nameState.text, detailState.text) {
                                groceryItem.name = nameState.text.trim().toString()
                                groceryItem.details = detailState.text.trim().toString()
                                // Update recipe view model with items, but not if name is empty
                                Log.d("SpecificRecipeEditGroceriesScreen", "Change Recipe groceries (launchedeffect)")
                                onChangeRecipeGroceries(localGroceryItems.filter { it.name != "" })

                                allLocations.forEach { location ->
                                    if (location.groceryNames.contains(groceryItem.name)) {
                                        locationId = location.id
                                        return@forEach
                                    }
                                }
                            }

                            LaunchedEffect(locationId, categoryId) {
                                groceryItem.locationId = locationId
                                groceryItem.categoryId = categoryId
                                // Update recipe view model with items, but not if name is empty
                                onChangeRecipeGroceries(localGroceryItems.filter { it.name != "" })
                            }

                            ReorderableItem(
                                state = reorderableListState,
                                key = groceryItem.id
                            ) {
                                SpecificRecipeEditGroceriesItem(
                                    allLocations = allLocations,
                                    onAddLocation = onAddLocation,
                                    onRemoveLocation = onRemoveLocation,
                                    onChangeLocationName = onChangeLocationName,
                                    onReorderLocations = onReorderLocations,
                                    getLocationNameFromId = getLocationNameFromId,
                                    allCategories = allCategories,
                                    getCategoryNameFromId = getCategoryNameFromId,
                                    focusRequester = focusRequesters[groceryItem.id],
                                    onDelete = {
                                        var index = localGroceryItems.indexOf(groceryItem)
                                        localGroceryItems.removeAt(index)
                                        onChangeRecipeGroceries(localGroceryItems)
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar("Deleted item \"{groceryItem}\"", actionLabel = resources.getString(R.string.undo), duration = SnackbarDuration.Short)
                                            if (result == SnackbarResult.ActionPerformed) {
                                                if (index > localGroceryItems.count())
                                                    index = localGroceryItems.count()
                                                localGroceryItems.add(index, groceryItem)
                                                onChangeRecipeGroceries(localGroceryItems)
                                            }
                                        }
                                    },
                                    locationId = locationId,
                                    categoryId = categoryId,
                                    onChangeLocationId = { locationId = it },
                                    onChangeCategoryId = { categoryId = it },
                                    nameState = nameState,
                                    detailState = detailState
                                )
                            }
                        }
                    }
                }
            }
//            LazyVerticalGrid(
//                columns = GridCells.Adaptive(80.dp),
//                verticalArrangement = Arrangement.spacedBy(10.dp),
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                modifier = modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp)
//            ) {
//                items(items = temporaryGroceries.sortedBy { it.name }, key = { item -> item.id } ) { groceryItem ->
//                    GroceryItemDisplay(
//                        item = groceryItem,
//                        onClick = {
//                            temporaryGroceries.remove(groceryItem)
//                            deletedGroceries.add(groceryItem)
//                        },
//                        onLongClick = {
//                            editGroceryId = groceryItem.id
//                            showEditGrocerySheet = true
//                        },
//                        center = true,
//                        modifier = Modifier.animateItem()
//                    )
//                }
//                if (deletedGroceries.isNotEmpty()) {
//                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {}
//                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
//                        Box(modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp)
//                            .animateItem()) {
//                            Text(
//                                stringResource(R.string.deleted), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), modifier = Modifier.align(
//                                    Alignment.BottomStart))
//                        }
//                    }
//                    items(items = deletedGroceries.sortedBy { it.name }, key = { item -> item.id } ) { groceryItem ->
//                        GroceryItemDisplay(
//                            item = groceryItem,
//                            onClick = {
//                                deletedGroceries.remove(groceryItem)
//                                temporaryGroceries.add(groceryItem)
//                            },
//                            onLongClick = { },
//                            center = true,
//                            deleted = true,
//                            modifier = Modifier.animateItem()
//                        )
//                    }
//                }
//            }
        }
//        val addGroceryFocusRequester = remember { FocusRequester() }
//        LaunchedEffect(showAddGrocerySheet) {
//            if (showAddGrocerySheet) {
//                addGroceryFocusRequester.requestFocus()
//            }
//        }
//        if (showAddGrocerySheet) {
//            AddGroceryBottomSheetBasic(
//                onDismissRequest = { showAddGrocerySheet = false },
//                focusRequester = addGroceryFocusRequester,
//                onConfirm = { newItem ->
//                    temporaryGroceries.add(newItem)
//                }
//            )
//        }

//        if (showEditGrocerySheet) {
//            val editGrocery = temporaryGroceries.fastFirstOrNull { it.id == editGroceryId }
//            if (editGrocery != null) {
//                val nameState = rememberTextFieldState(initialText = editGrocery.name)
//                val detailState = rememberTextFieldState(initialText = editGrocery.details)
//
//                EditGroceryBottomSheetBasic(
//                    onDismissRequest = { showEditGrocerySheet = false },
//                    groceryNameState = nameState,
//                    groceryDetailState = detailState,
//                    onChangeItemNameDetails = { newName, newDetails ->
//                        editGrocery.name = newName
//                        editGrocery.details = newDetails
//                    }
//                )
//            }
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpecificRecipeEditGroceriesScreenPreview() {
    FoodTheme {
        SpecificRecipeEditGroceriesScreen(
            bottomBar = {},
            recipe = Recipe("Recipe 1", groceries = remember { items.toMutableStateList() }),
            onBack = {},
            allLocations = listOf(GroceryLocation("Location1"), GroceryLocation("Location2")),
            onAddLocation = { },
            onRemoveLocation = { },
            onChangeLocationName = { _, _, -> },
            onReorderLocations = { _, _ -> },
            getLocationNameFromId = { it.toString() },
            onChangeRecipeGroceries = { },
            onDispose = { },
            allCategories = listOf(),
            getCategoryNameFromId = { it.toString() }
        )
    }
}