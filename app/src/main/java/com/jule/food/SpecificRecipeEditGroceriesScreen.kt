package com.jule.food

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeEditGroceriesScreen(
    bottomBar: @Composable () -> Unit,
    recipe: Recipe,
    onConfirm: (List<GroceryItem>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    val temporaryGroceries = remember { recipe.groceries.toMutableStateList() }
    val groceriesChanged = recipe.groceries.toList() != temporaryGroceries.toList()

    val deletedGroceries = remember { mutableStateListOf<GroceryItem>() }
    var showAddGrocerySheet by remember { mutableStateOf(false) }
    var showEditGrocerySheet by remember { mutableStateOf(false) }
    var editGroceryId: UUID? by remember { mutableStateOf(null) }

    val onCancelRequest = {
        if (groceriesChanged)
            showCancelDialog = true
        else
            onCancel()
    }


    BackHandler(
        onBack = onCancelRequest
    )

    Scaffold(
        bottomBar = bottomBar,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = stringResource(R.string.edit_groceries), style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(text = recipe.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.height(20.dp))
                    }
                },
                navigationIcon = {
                    IconButtonWithTooltip(onClick = onCancelRequest, tooltipText = stringResource(R.string.cancel)) {
                        Icon(painterResource(R.drawable.clear), contentDescription = stringResource(R.string.cancel))
                    }
                },
                actions = {
                    TextButton(
                        { onConfirm(temporaryGroceries) },
                        enabled = groceriesChanged
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddGrocerySheet = true },
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
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                        shape = RoundedCornerShape(10)
                    ) {
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
                                IconButtonWithTooltip(onClick = { showImages = false }, modifier = Modifier.align(Alignment.TopEnd), tooltipText = stringResource(R.string.close)) {
                                    Icon(painterResource(R.drawable.clear), contentDescription = stringResource(R.string.close))
                                }
                            }
                        }
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                items(items = temporaryGroceries.sortedBy { it.name }, key = { item -> item.id } ) { groceryItem ->
                    GroceryItemDisplay(
                        item = groceryItem,
                        onClick = {
                            temporaryGroceries.remove(groceryItem)
                            deletedGroceries.add(groceryItem)
                        },
                        onLongClick = {
                            editGroceryId = groceryItem.id
                            showEditGrocerySheet = true
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
                            onLongClick = { },
                            center = true,
                            deleted = true,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
        val addGroceryFocusRequester = remember { FocusRequester() }
        LaunchedEffect(showAddGrocerySheet) {
            if (showAddGrocerySheet) {
                addGroceryFocusRequester.requestFocus()
            }
        }
        if (showAddGrocerySheet) {
            AddGroceryBottomSheetBasic(
                onDismissRequest = { showAddGrocerySheet = false },
                focusRequester = addGroceryFocusRequester,
                onConfirm = { newItem ->
                    temporaryGroceries.add(newItem)
                }
            )
        }

        if (showEditGrocerySheet) {
            val editGrocery = temporaryGroceries.fastFirstOrNull { it.id == editGroceryId }
            if (editGrocery != null) {
                val nameState = rememberTextFieldState(initialText = editGrocery.name)
                val detailState = rememberTextFieldState(initialText = editGrocery.details)

                EditGroceryBottomSheetBasic(
                    onDismissRequest = { showEditGrocerySheet = false },
                    groceryNameState = nameState,
                    groceryDetailState = detailState,
                    onChangeItemNameDetails = { newName, newDetails ->
                        editGrocery.name = newName
                        editGrocery.details = newDetails
                    }
                )
            }
        }
    }

    if (showCancelDialog) {
        DefaultDialog(
            stringResource(R.string.cancel),
            onDismissRequest = { showCancelDialog = false },
            onConfirm = { onCancel() },
            buttons = true,
        ) {
            Text(stringResource(R.string.are_you_sure_you_want_to_cancel_editing))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpecificRecipeEditGroceriesScreenPreview() {
    FoodTheme {
        SpecificRecipeEditGroceriesScreen(
            bottomBar = {},
            recipe = Recipe("Recipe 1", groceries = remember { mutableStateListOf(GroceryItem("GroceryItem", "")) }),
            onConfirm = {},
            onCancel = {}
        )
    }
}