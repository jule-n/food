package com.jule.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import com.jule.food.ui.theme.FoodTheme
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpecificRecipeGroceries(
    recipe: Recipe,
    addToGroceries: (List<GroceryItem>, categoryId: UUID) -> Unit,
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
            IconButtonWithTooltip(onClick = { onOpenGroceryScreen() }, tooltipText = stringResource(R.string.edit_groceries)) {
                Icon(painterResource(R.drawable.edit), contentDescription = stringResource(R.string.edit_groceries))
            }
            FilledExpressiveIconButtonWithTooltip(onClick = { showGroceryAddDialog = true }, shapes = IconButtonDefaults.shapes(), enabled = recipe.groceries.isNotEmpty(), tooltipText = stringResource(R.string.add_groceries_to_cart)) {
                Icon(painterResource(R.drawable.grocery), contentDescription = stringResource(R.string.add_groceries_to_cart))
            }
        }
    ) {
        if (recipe.groceries.isNotEmpty()) {
            LazyHorizontalGrid(
                rows = GridCells.FixedSize(60.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
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
        }
    }
    if (showGroceryAddDialog) {
        AddGroceriesFromRecipeDialog(
            onDismissRequest = { showGroceryAddDialog = false },
            recipe = recipe,
            groceryCategories = groceryCategories,
            includeCategoryChoice = true,
            onConfirm = { groceries, categoryId ->
                if (categoryId != null) {
                    addToGroceries(groceries, categoryId)
                    showGroceryAddDialog = false
                }
            }
        )
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

@Preview(showBackground = true)
@Composable
fun SpecificRecipeGroceriesPreview() {
    val groceries = remember { mutableStateListOf(GroceryItem("Mehl", "500g"), GroceryItem("Spaghetti", "")) }
    val recipe = Recipe(name = "Recipe", images = remember { mutableStateListOf() }, groceries = groceries, tags = remember { mutableStateListOf() }, note = "")
//    val recipeViewModel: RecipeViewModel = viewModel()
//    recipeViewModel.addRecipe(recipe)
    FoodTheme {
        SpecificRecipeGroceries(recipe = recipe, addToGroceries = { _, _ ->}, onOpenGroceryScreen = {}, groceryCategories = listOf(GroceryItemCategory("Default"), GroceryItemCategory("Vegan")))
    }

}
