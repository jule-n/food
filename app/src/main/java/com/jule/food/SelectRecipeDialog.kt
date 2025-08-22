package com.jule.food

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.util.UUID

@Composable
fun SelectRecipeDialog(
    onDismissRequest: () -> Unit,
    recipes: List<Recipe>,
    onClickRecipe: (UUID) -> Unit,
) {
    DefaultDialog(
        title = "Select recipe",
        onDismissRequest = onDismissRequest
    ) {
        RecipeGrid(
            recipes = recipes,
            onClickRecipe = { index ->
                val recipeId = recipes[index].id
                onClickRecipe(recipeId)
            },
            isRecipePage = false,
            recipeGridState = rememberLazyGridState(),
            contentPadding = PaddingValues()
        )
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
    recipe: Recipe,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    ) {
        Text(text = recipe.name, modifier = Modifier.padding(10.dp), textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeTinyDisplayPreview() {
    val recipes = mutableListOf<Recipe>()
    for(i in 1..10) {
        recipes.add(Recipe("Recipe $i"))
    }
    LazyVerticalGrid(GridCells.Fixed(4)) {
        items(recipes) { recipe ->
            RecipeTinyDisplay(
                recipe = recipe,
                onClick = {}
            )
        }
    }
}