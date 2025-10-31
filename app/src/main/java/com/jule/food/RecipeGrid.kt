package com.jule.food

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.jule.food.ui.theme.FoodTheme
import java.io.File
import kotlin.math.ceil


// Display a grid of recipes
@Composable
fun RecipeGrid(
    recipes: List<Recipe>,
    onClickRecipe: (listIndex: Int) -> Unit,
    recipeGridState: LazyGridState,
    isRecipeSearch: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 100.dp),
    showImages: Boolean = true,
    userScrollEnabled: Boolean = true
) {
    LazyVerticalGrid(modifier = modifier, state = recipeGridState, columns = GridCells.Fixed(3), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = contentPadding, userScrollEnabled = userScrollEnabled) {
        itemsIndexed(recipes, key = { _, recipe -> recipe.id }) { index, recipe ->
            RecipeSmallDisplay(
                recipe = recipe, onClick = { onClickRecipe(index) }, showImage = showImages, isRecipeSearch = isRecipeSearch, modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
fun RecipeGridWithConstrainedHeight(
    recipes: List<Recipe>,
    onClickRecipe: (listIndex: Int) -> Unit,
    recipeGridState: LazyGridState,
    isRecipeSearch: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 100.dp),
    showImages: Boolean = true,
    userScrollEnabled: Boolean = true
) {
    BoxWithConstraints {
        val spacingPerRecipe = 10.dp
        val recipesPerRow = 3
        val rowNumber =
            ceil(recipes.size.toDouble() / recipesPerRow).toInt()
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
            modifier = modifier.height(height),
            contentPadding = contentPadding,
            recipes = recipes,
            onClickRecipe = onClickRecipe,
            recipeGridState = recipeGridState,
            userScrollEnabled = userScrollEnabled,
            isRecipeSearch = isRecipeSearch,
            showImages = showImages,
        )
    }
}

// Display a single recipe
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecipeSmallDisplay(
    recipe: Recipe,
    onClick: () -> Unit,
    isRecipeSearch: Boolean,
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
    minTextSize: TextUnit = 10.sp,
    maxTextSize: TextUnit = 16.sp,
    fallbackBrush: Brush = Brush.linearGradient( listOf(MaterialTheme.colorScheme.secondaryContainer, Color.White) )
) {
    val image = recipe.images.isNotEmpty() && showImage
    Surface(
        shape = RoundedCornerShape(10),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        onClick = onClick,
//        modifier = modifier.clickable(enabled = onClick != null) {
//            onClick?.invoke()
//        }
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
                    val path = recipe.images[0]
                    val model = ImageRequest.Builder(LocalContext.current)
                        .data(File(path))
                        .crossfade(true)
                        .placeholderMemoryCacheKey(path)
                        .memoryCacheKey(path)
                        .size(400, 400)
                        .scale(coil3.size.Scale.FIT)
                        .build()

                    with(sharedTransitionScope!!) {
                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = if (isRecipeSearch) "${recipe.id}_search" else "${recipe.id}_grid"),
                                    animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                                )
                                .clip(RoundedCornerShape(10))
                                .fillMaxSize()
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(fallbackBrush, shape = RoundedCornerShape(10)))
                    Box(modifier = Modifier
                        .fillMaxSize(0.8f).align(alignment = Alignment.Center)
                    ) {
                        Text(
                            text = recipe.name,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
//                            fontFamily = displayLargeFontFamily,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.displaySmallEmphasized.copy(hyphens = Hyphens.Auto, lineBreak = LineBreak.Heading),
                            autoSize = TextAutoSize.StepBased(maxFontSize = 30.sp),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
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

@Preview(showBackground = true)
@Composable
fun RecipeSmallDisplayPreview() {
    val recipe = Recipe("Karottenkuchen")
    FoodTheme {
        Surface(
            modifier = Modifier.padding(20.dp)
        ) {
            RecipeSmallDisplay(
                recipe = recipe,
                isRecipeSearch = false,
                onClick = { }
            )
        }
    }
}