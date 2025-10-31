package com.jule.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreenSearchInputField(
    textFieldState: TextFieldState,
    searchBarExpanded: Boolean,
    onChangeSearchBarExpanded: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    SearchBarDefaults.InputField(
        state = textFieldState,
        onSearch = { _ ->
            focusManager.clearFocus(true)
        },
        expanded = searchBarExpanded,
        onExpandedChange = onChangeSearchBarExpanded,
        leadingIcon = {
            if (searchBarExpanded) {
                IconButtonWithTooltip(
                    onClick = {
                        onChangeSearchBarExpanded(false)
                        textFieldState.clearText()
                    },
                    tooltipText = stringResource(R.string.back)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            } else {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        },
        trailingIcon = {
            AnimatedVisibility(searchBarExpanded && textFieldState.text != "", enter = fadeIn(), exit = fadeOut()) {
                IconButtonWithTooltip(onClick = {
                    textFieldState.edit { delete(0, textFieldState.text.length) }
                }, tooltipText = stringResource(R.string.clear_search)
                ) {
                    Icon(Icons.Outlined.Clear, contentDescription = stringResource(R.string.clear_search))
                }
            }
        },
        placeholder = {
            Text(stringResource(R.string.search_recipes), textAlign = TextAlign.Center)
        },
        colors = TextFieldDefaults.colors().copy(unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp), focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreenSearch(
    expanded: Boolean,
    onChangeExpanded: (Boolean) -> Unit,
    textFieldState: TextFieldState,
    interactionSource: MutableInteractionSource,
    recipes: List<Recipe>,
    recentRecipeIds: List<UUID>,
    onClickRecipe: (recipeId: UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f)
    ) {
        SearchBar(
            inputField = { RecipeScreenSearchInputField(textFieldState = textFieldState, searchBarExpanded = expanded, onChangeSearchBarExpanded = onChangeExpanded)},
            expanded = expanded,
            onExpandedChange = {
                onChangeExpanded(it)
                textFieldState.clearText()
            },
            windowInsets = WindowInsets(
                left = 0.dp,
                right = 0.dp,
                top = 0.dp,
                bottom = 0.dp
            ),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)
            )
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) {
                        focusManager.clearFocus(true)
                    }
            ) {
                if (textFieldState.text == "") {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Row {
                            Icon(
                                painterResource(R.drawable.clock),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(5.dp))
                            Text("Recent")
                        }
                        Spacer(Modifier.height(10.dp))
                        RecipeGrid(
                            recipes = recentRecipeIds.map {
                                getRecipeFromId(
                                    it,
                                    recipes
                                )
                            },
                            onClickRecipe = { listIndex ->
                                onClickRecipe(recentRecipeIds[listIndex])
                            },
                            showImages = expanded,
                            isRecipeSearch = true,
                            recipeGridState = rememberLazyGridState()
                        )
                    }
                } else {
                    val searchResults = recipes.filter { recipe ->
                        recipe.name.contains(
                            other = textFieldState.text,
                            ignoreCase = true
                        )
                    }

                    if (searchResults.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize()
                        ) {
                            Text(
                                "No results found :|",
                                modifier = Modifier.align(Alignment.Center),
                                style = MaterialTheme.typography.displaySmallEmphasized
                            )
                        }
                    } else {
                        RecipeGrid(
                            contentPadding = PaddingValues(
                                start = 10.dp,
                                end = 10.dp,
                                top = 10.dp,
                                bottom = 100.dp
                            ),
                            recipes = searchResults,
                            onClickRecipe = { listIndex ->
                                onClickRecipe(searchResults[listIndex].id)
                            },
                            isRecipeSearch = true,
                            recipeGridState = rememberLazyGridState()
                        )
                    }
                }
            }
        }
    }
}