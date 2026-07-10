package com.jule.food.ui.recipes

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.GroceryLocation
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.R
import com.jule.food.data.Recipe
import com.jule.food.data.RecipeViewModel
import com.jule.food.utils.SelectionTopBar
import com.jule.food.utils.SimpleAddEditBottomSheet
import com.jule.food.data.Tag
import com.jule.food.ui.groceries_recipes.GroceryListAddingOption
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.utils.DeleteDialog
import com.jule.food.utils.completeSlideIn
import com.jule.food.utils.completeSlideOut
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.listOf
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpecificRecipeScreen(
    bottomBar: @Composable () -> Unit,
    recipe: Recipe,
    recipeViewModel: RecipeViewModel,
    addToGroceries: (List<GroceryItem>, GroceryListAddingOption, categoryId: UUID, recipeId: UUID) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    onBack: () -> Unit,
    onDeleteRecipe: () -> Unit,
    onDisplayImage: (imageIndex: Int) -> Unit,
    fromRecipeSearch: Boolean,
    onOpenEditGroceriesScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
//    var expanded by remember { mutableStateOf(false) }
//    var showGroceryScreen by remember { mutableStateOf(false) }
    val tags = recipeViewModel.tags

//    AnimatedVisibility(visible = !showGroceryScreen, enter = completeSlideIn(
//        false,
//        MaterialTheme.motionScheme
//    ), exit = completeSlideOut(true, MaterialTheme.motionScheme)
//    ) {
    SpecificRecipeScreenMain(
        recipe = recipe,
        onChangeRecipeName = { recipeViewModel.changeRecipeName(recipe.id, it, context) },
        allTags = tags,
        onChangeRecipeTags = { recipeViewModel.changeRecipeTags(recipe.id, it, context) },
        onChangeRecipeNote = { recipeViewModel.changeRecipeNote(recipe.id, it, context) },
        addToGroceries = { groceries, addingOption, categoryId -> addToGroceries(groceries, addingOption, categoryId, recipe.id) },
        groceryCategories = groceryCategories,
        onBack = onBack,
        onDelete = onDeleteRecipe,
        onAddImages = { recipeViewModel.addImagesToRecipe(recipe.id, it, context) },
        bottomBar = bottomBar,
        onOpenGroceryScreen = onOpenEditGroceriesScreen,
        onDisplayImage = { onDisplayImage(it) },
        onChangeImageOrder = { fromIndex, toIndex ->
            val newImages = recipe.images.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
            recipeViewModel.changeRecipeImages(recipe.id, newImages, context)
        },
        onDeleteImages = { indizesToDelete ->
            val paths = indizesToDelete.map { recipe.images[it] }
            recipeViewModel.deleteRecipeImages(recipe.id, paths, context)

        },
        fromRecipeSearch = fromRecipeSearch,
        modifier = modifier
    )
//    }
//    AnimatedVisibility(
//        visible = showGroceryScreen,
//        enter = completeSlideIn(true, MaterialTheme.motionScheme),
//        exit = completeSlideOut(false, MaterialTheme.motionScheme)
//    ) {
//        SpecificRecipeEditGroceriesScreen(
//            bottomBar = bottomBar,
//            recipe = recipe,
////            onConfirm = { newGroceries ->
////                showGroceryScreen = false
////                recipeViewModel.changeRecipeGroceries(recipe.id, newGroceries, context)
////            },
//            onBack = {
//                showGroceryScreen = false
//            },
//            allLocations = allLocations,
//            onAddLocation = onAddLocation,
//            onRemoveLocation = onRemoveLocation,
//            onChangeLocationName = onChangeLocationName,
//            onReorderLocations = onReorderLocations,
//            getLocationNameFromId = getLocationNameFromId,
//            allCategories = allCategories,
//            getCategoryNameFromId = getCategoryNameFromId,
//            onChangeRecipeGroceries = { recipeViewModel.changeRecipeGroceries(recipe.id, it) },
//            onDispose = {
//                changeLocationsWithNewGroceries(recipe.groceries)
//                recipeViewModel.saveToFile(context)
//            },
//            modifier = modifier
//        )
//    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeScreenMain(
    bottomBar: @Composable () -> Unit,
    recipe: Recipe,
    onDisplayImage: (imageIndex: Int) -> Unit,
    onChangeRecipeName: (String) -> Unit,
    allTags: List<Tag>,
    onChangeRecipeTags: (List<UUID>) -> Unit,
    onChangeRecipeNote: (String) -> Unit,
    onChangeImageOrder: (fromIndex: Int, toIndex: Int) -> Unit,
    onDeleteImages: (List<Int>) -> Unit,
    addToGroceries: (List<GroceryItem>, GroceryListAddingOption, UUID) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onAddImages: (List<String>) -> Unit,
    onOpenGroceryScreen: () -> Unit,
    fromRecipeSearch: Boolean,
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
    val resources = LocalResources.current
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val selectedImagesIndizes = remember { mutableStateListOf<Int>() }
    var anyImageSelected by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(enabled = anyImageSelected) {
        selectedImagesIndizes.clear()
        anyImageSelected = false
    }

    var showConfirmDeleteImagesDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteRecipeDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    var showEditNameSheet by remember { mutableStateOf(false) }
    val editNameFocusRequester = remember { FocusRequester() }

//    val backgroundColor by animateColorAsState(
//        targetValue = if (scrollState.value == 0) MaterialTheme.colorScheme.background else TopAppBarDefaults.topAppBarColors().scrolledContainerColor
//    )


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = bottomBar,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AnimatedContent(
                targetState = selectedImagesIndizes.size > 0,
                transitionSpec = {
                    slideInVertically { -it } togetherWith slideOutVertically { -it }
                }
            ) { imagesSelected ->
                if (!imagesSelected) {
                    SpecificRecipeTopBar(
                        recipeName = recipe.name,
                        onBack = onBack,
                        onEditName = { showEditNameSheet = true },
                        scrollValue = scrollState.value,
                        scrollBehaviour = scrollBehavior
                    )
                } else {
                    SelectionTopBar(
                        numberSelected = selectedImagesIndizes.size,
                        onClearSelection = {
                            selectedImagesIndizes.clear()
                            anyImageSelected = false
                        },
                        actions = {
                            IconButtonWithTooltip(
                                onClick = {
                                    showConfirmDeleteImagesDialog = true
                                },
                                tooltipText = stringResource(R.string.delete_images)
                            ) {
                                Icon(
                                    painterResource(R.drawable.delete),
                                    contentDescription = stringResource(R.string.delete_images),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
//                    .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 12.dp)
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = true,
                    onClick = {
                        if (anyImageSelected) {
                            selectedImagesIndizes.clear()
                            anyImageSelected = false
                        } else {
                            focusManager.clearFocus(true)
                        }
                    }
                )
        ) {

//            Box(
//                modifier = Modifier.height(TopAppBarDefaults.MediumFlexibleAppBarWithoutSubtitleExpandedHeight - 48.dp).background(Color.Transparent).zIndex(1f)
//            ) {
//
//            }

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
//                    .padding(top = TopAppBarDefaults.MediumFlexibleAppBarWithoutSubtitleExpandedHeight - 48.dp)
            ) {
                Text(
                    recipe.name,
                    maxLines = 2,
                    style = MaterialTheme.typography.displaySmallEmphasized,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 30.sp),
                    modifier = Modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                            if (anyImageSelected) {
                                selectedImagesIndizes.clear()
                                anyImageSelected = false
                            }
                        }
                        .fillMaxWidth()
//                        .background(color = backgroundColor)
                        .padding(start = 10.dp)
                )
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
                    onChangeAnyImageSelected = { anyImageSelected = it },
                    fromRecipeSearch = fromRecipeSearch
                )
                SpecificRecipeGroceries(
                    recipe = recipe,
                    addToGroceries = addToGroceries,
                    groceryCategories = groceryCategories,
                    onOpenGroceryScreen = onOpenGroceryScreen
                )
//                var currentText by remember { mutableStateOf(TextFieldValue(recipe.note)) }
                SpecificRecipeNotes(
                    recipe = recipe,
                    onChangeRecipeNote = onChangeRecipeNote
                )
                Spacer(Modifier.height(10.dp))
                SpecificRecipeTags(
                    recipe = recipe,
                    allTags = allTags,
                    onChangeRecipeTags = onChangeRecipeTags,
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { showConfirmDeleteRecipeDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(painterResource(R.drawable.delete), contentDescription = "Delete")
                    Text(stringResource(R.string.delete_recipe))
                }
                Spacer(Modifier.height(10.dp))

            }
        }
    }
    if (showEditNameSheet) {
        SimpleAddEditBottomSheet(
            onConfirm = { newName ->
                onChangeRecipeName(newName)
                showEditNameSheet = false
            },
            onDismissRequest = { showEditNameSheet = false },
            initialText = recipe.name,
            placeholderText = stringResource(R.string.recipe_name),
            nameTooLongLimit = 40
        )
    }

    if (showConfirmDeleteRecipeDialog) {
        DeleteDialog(
            title = stringResource(R.string.delete_recipe),
            onDismissRequest = { showConfirmDeleteRecipeDialog = false },
            onConfirm = onDelete
        ) {
            Text(
                stringResource(R.string.are_you_sure_you_want_to_delete_this_item, recipe.name),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showConfirmDeleteImagesDialog) {
        val imageCount = selectedImagesIndizes.size

        DeleteDialog(
            title = stringResource(R.string.delete_images),
            onDismissRequest = { showConfirmDeleteImagesDialog = false },
            onConfirm = {
                showConfirmDeleteImagesDialog = false

                onDeleteImages(selectedImagesIndizes)
                selectedImagesIndizes.clear()
                anyImageSelected = false

                coroutineScope.launch {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.deleted_n_images, imageCount),
                        Toast.LENGTH_SHORT
                    ).show()
//                    snackbarHostState.showSnackbar(message = context.getString(R.string.deleted_n_images, imageCount), actionLabel = context.getString(R.string.undo), duration = SnackbarDuration.Short)
                }
            }
        ) {
            Text(
                stringResource(R.string.are_you_sure_you_want_to_delete_n_images, imageCount),
                textAlign = TextAlign.Center
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificRecipeTopBar(
    recipeName: String,
    onBack: () -> Unit,
    onEditName: () -> Unit,
    scrollValue: Int,
    scrollBehaviour: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            val density = LocalDensity.current
            with (density) {
                val scrollValueDp = scrollValue.toDp()
                if (scrollValueDp > 0.dp) {
                    val scrollPercentage = (scrollValueDp / 50.dp).coerceIn(0f, 1f)
                    LaunchedEffect(scrollPercentage) {
                        Log.d("SpecificRecipeTopBar", "scrollPercentage: $scrollPercentage")
                    }
                    val requiredOffsetPx = 20.dp.toPx()
                    val position by animateIntAsState(targetValue = (requiredOffsetPx - (requiredOffsetPx * scrollPercentage)).roundToInt())
                    val alpha by animateFloatAsState(0f + (1f * scrollPercentage))

                    Text(text = recipeName, modifier = Modifier.offset { IntOffset(0, position) }.alpha(alpha))

                }
            }
        },
        navigationIcon = {
            IconButtonWithTooltip(onClick = onBack, tooltipText = stringResource(R.string.back)) {
                Icon(
                    painterResource(R.drawable.arrow_left),
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButtonWithTooltip(
                onClick = onEditName,
                tooltipText = stringResource(R.string.change_recipe_name)
            ) {
                Icon(
                    painterResource(R.drawable.edit),
                    contentDescription = stringResource(R.string.change_recipe_name)
                )
            }
        },
        scrollBehavior = scrollBehaviour
    )
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
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
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



@Preview(showBackground = true)
@Composable
fun SpecificRecipeScreenPreview() {
    val fishTag = Tag("Fisch", 0)
    val salzigTag = Tag("Salzig", 1)
    val saladTag = Tag("Salat", 2)
    val appleTag = Tag("Apfel", 3)
    val kaeseTag = Tag("Käse", 4)

    val groceries = listOf(
        GroceryItem("Apfel", ""),
        GroceryItem("Salat", ""),
        GroceryItem("Käse", "")
    )
    val tags = listOf(fishTag, salzigTag, saladTag, appleTag, kaeseTag)
    val recipe = Recipe(
        name = "Dorade in Salzkruste",
        tags = remember { mutableStateListOf(fishTag.id, salzigTag.id, appleTag.id) },
        groceries = remember { groceries.toMutableStateList() },
        note = "NOTES"
    )

    val recipeViewModel: RecipeViewModel = viewModel()
    tags.forEach {
        recipeViewModel.addTagWithoutSaving(it)
    }


    FoodTheme {
        AnimatedVisibility(true) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                SpecificRecipeScreen(
                    bottomBar = {},
                    recipe = recipe,
                    onBack = { },
                    recipeViewModel = recipeViewModel,
                    addToGroceries = { _, _, _, _ -> },
                    groceryCategories = listOf(),
                    onDisplayImage = { },
                    onDeleteRecipe = { },
                    fromRecipeSearch = false,
                    onOpenEditGroceriesScreen = { }
                )
            }
        }
    }
}