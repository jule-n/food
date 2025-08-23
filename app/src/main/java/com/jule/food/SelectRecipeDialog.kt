package com.jule.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecipeGrid(
    modifier: Modifier = Modifier,
    recipes: List<Recipe>,
    onClickRecipe: (UUID) -> Unit,
) {
    val searchState = rememberTextFieldState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            state = searchState,
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = stringResource(R.string.search_recipes))
            },
            shape = SearchBarDefaults.inputFieldShape
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(recipes.filter { recipe -> recipe.name.contains(searchState.text) }) { recipe ->
                RecipeTinyDisplay(
                    recipe = recipe,
                    onClick = { onClickRecipe(recipe.id) }
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecipeBottomSheet(
    onDismissRequest: () -> Unit,
    allRecipes: List<Recipe>,
    activeRecipes: List<Recipe>,
    onClickRecipe: (UUID) -> Unit,
) {
    ModalBottomSheet (
        onDismissRequest = onDismissRequest
    ) {
        val activeRecipesString = stringResource(R.string.active_recipes)
        val allRecipesString = stringResource(R.string.all_recipes)
        val focusManager = LocalFocusManager.current

        val searchBarState = rememberSearchBarState()
        val textFieldState = rememberTextFieldState()

        val otherRecipes = allRecipes.filter { recipe -> !activeRecipes.contains(recipe) }
        val searchResults = if (textFieldState.text.isEmpty()) otherRecipes else otherRecipes.filter { recipe ->
            recipe.name.contains(textFieldState.text)
        }

        SearchBar(
            state = searchBarState,
            inputField = { SearchBarDefaults.InputField(
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = { focusManager.clearFocus(true) }
            )}
        )


        LazyVerticalGrid(GridCells.Fixed(4)) {
            gridGroupTitle(activeRecipesString)
            items(activeRecipes) { recipe ->
                RecipeSmallDisplay(
                    recipe = recipe,
                    onClick = { onClickRecipe(recipe.id) },
                    isRecipePage = false
                )
            }
            gridSpacer(20.dp)
            gridGroupTitle(allRecipesString)
            items(searchResults) { recipe ->
                RecipeSmallDisplay(
                    recipe = recipe,
                    onClick = { onClickRecipe(recipe.id) },
                    isRecipePage = false
                )
            }
        }
    }
}

@Composable
fun RecipeTinyDisplay(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        modifier = modifier.padding(0.dp).aspectRatio(1.5f),
        shape = RoundedCornerShape(20)
    ) {
        Text(
            text = recipe.name,
            modifier = Modifier.wrapContentHeight(Alignment.CenterVertically).padding(5.dp),
            textAlign = TextAlign.Center,
            maxLines = 2,
            autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 16.sp),
//            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeTinyDisplayPreview() {
    FoodTheme {
        RecipeTinyDisplay(
            recipe = Recipe("Recipe 1"),
            onClick = {},
            modifier = Modifier.height(60.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectRecipeGridPreview() {
    val recipes = mutableListOf<Recipe>()
    for(i in 1..10) {
        recipes.add(Recipe("Recipe ${i.toDouble().pow(i.toDouble())}"))
    }
    FoodTheme {
        SelectRecipeGrid(
            recipes = recipes,
            onClickRecipe = {}
        )
    }

}