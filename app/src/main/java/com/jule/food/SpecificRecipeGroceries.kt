package com.jule.food

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Precision
import coil3.size.Size
import com.jule.food.ui.theme.FoodTheme
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import java.io.File

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpecificRecipeGroceries(
    recipe: Recipe,
    addToGroceries: (List<GroceryItem>, Int) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    onOpenGroceryScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showGroceryAddDialog by remember { mutableStateOf(false) }

    SpecificRecipeSection(
        modifier = modifier.heightIn(max = 180.dp),
        icon = R.drawable.grocery,
        title = stringResource(id = R.string.groceries),
        actionButtons = {
            IconButton(onClick = { onOpenGroceryScreen() }) {
                Icon(painterResource(R.drawable.edit), contentDescription = "Edit")
            }
            FilledIconButton(onClick = { showGroceryAddDialog = true }, shapes = IconButtonDefaults.shapes(), enabled = recipe.groceries.isNotEmpty()) {
                Icon(painterResource(R.drawable.grocery), contentDescription = "Add to shopping cart")
            }
        }
    ) {
        LazyHorizontalGrid (
            rows = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        ) {
            items(recipe.groceries.sortedBy { it.name }) { grocery ->
                GroceryItemDisplay(
                    grocery,
                    onClick = { },
                    onLongClick = { },
                    center = true
                )
            }
        }
//        GroceryGrid(
//            groceryItems = recipe.groceries.sortedBy { it.name },
//            onClickItem = {},
//            onLongClickItem = {},
//            center = true,
//            modifier = Modifier.padding(horizontal = 20.dp),
//            minSize = 70.dp
//        )
    }
    if (showGroceryAddDialog) {
        val selectedItems = remember { recipe.groceries.toMutableStateList() }
        var chosenCategoryIndex: Int by remember { mutableStateOf(0) }

        DefaultDialog(
            title = stringResource(R.string.add_groceries_to_cart),
            onDismissRequest = { showGroceryAddDialog = false },
            buttons = true,
            onConfirm = {
                showGroceryAddDialog = false
                addToGroceries(selectedItems, chosenCategoryIndex)
//                addToGroceries(selectedItems)
//                showGroceryAddDialog = false
            }
        ) {
            SettingsScreenCategory(
                name = stringResource(R.string.category)
            ) {
                LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(groceryCategories) { index, category ->
                        val isSelected = chosenCategoryIndex == index
                        Surface(
                            onClick = { chosenCategoryIndex = index },
                            enabled = true,
                            color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(20)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(
                                    start = 10.dp,
                                    top = 5.dp,
                                    bottom = 5.dp
                                ).fillMaxWidth().height(40.dp)
                            ) {
                                Text(
                                    text = category.name,
                                    textAlign = TextAlign.Left,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            SettingsScreenCategory(
                name = stringResource(R.string.groceries)
            ) {

                val prim1 = lerp(MaterialTheme.colorScheme.primary, Color.White, 0.1f)
                val prim2 = lerp(MaterialTheme.colorScheme.primary, Color.White, 0.3f)

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(70.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(items = recipe.groceries.sortedBy { it.name }, key = { _, item -> item.id }) {index, groceryItem ->
                        val isSelected = selectedItems.contains(groceryItem)
                        val itemColor = if (isSelected) null else MaterialTheme.colorScheme.onSurfaceVariant
                        val textColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Black)

                        val color1 by animateColorAsState(targetValue = if (isSelected) prim1 else Color.LightGray)
                        val color2 by animateColorAsState(targetValue = if (isSelected) prim2 else Color.White)
                        val br = Brush.linearGradient(listOf(color1, color2))
                        val detailTextColor = textColor.copy(alpha = 0.6f)

                        Box(modifier = Modifier.fillMaxSize()) {
                            GroceryItemDisplay(
                                item = groceryItem,
                                onClick = {
                                    if (isSelected) {
                                        selectedItems.remove(groceryItem)
                                    } else {
                                        selectedItems.add(groceryItem)
                                    }
                                },
                                onLongClick = { },
                                itemBrush = br,
//                                itemColor = itemColor,
                                textColor = textColor,
                                detailTextColor = detailTextColor,
                                center = true,
                                modifier = Modifier.animateItem()
                            )
                            Surface(color = Color.Black.copy(alpha = if (isSelected) 0.3f else 0f), shape = RoundedCornerShape(20), border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f)), modifier = Modifier.size(20.dp)) {
                                if (isSelected)
                                    Icon(painter = painterResource(R.drawable.done), tint = Color.White, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
//                            Checkbox(checked = selectedItems.contains(groceryItem), onCheckedChange = {}, enabled = false, modifier = Modifier.offset(x = -15.dp, y = -15.dp).padding(0.dp))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ZoomableImage(
    imagePath: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current

// Create an Image composable with zooming and panning.
    AsyncImage(
        model = ImageRequest.Builder(context).size(Size.ORIGINAL).data(File(imagePath)).build(),
//        painter = painterResource(R.drawable.cauliflower_wings), // Replace 'imagePainter' with your image
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier.zoomable(rememberZoomableState(zoomSpec = ZoomSpec(maxZoomFactor = 5f, preventOverOrUnderZoom = false)))
    )
}

@Preview
@Composable
fun SpecificRecipeGroceriesPreview() {
    val groceries = remember { mutableStateListOf(GroceryItem("Mehl", "500g"), GroceryItem("Spaghetti", "")) }
    val recipe = Recipe(name = "Recipe", images = remember { mutableStateListOf() }, groceries = groceries, tags = mutableListOf())
//    val recipeViewModel: RecipeViewModel = viewModel()
//    recipeViewModel.addRecipe(recipe)
    FoodTheme() {
        Surface(modifier = Modifier.fillMaxSize()) {
            SpecificRecipeGroceries(recipe = recipe, addToGroceries = { _, _ ->}, onOpenGroceryScreen = {}, groceryCategories = listOf(GroceryItemCategory("Default"), GroceryItemCategory("Vegan")))
        }
    }

}
