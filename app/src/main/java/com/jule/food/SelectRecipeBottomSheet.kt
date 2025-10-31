package com.jule.food

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecipeBottomSheet(
    modifier: Modifier = Modifier,
    allRecipes: List<Recipe>,
    onSelectRecipe: (UUID) -> Unit,
    onDismissRequest: () -> Unit,
    searchFocusRequester: FocusRequester
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            SelectRecipeGrid(
                recipes = allRecipes.filter { recipe -> recipe.groceries.isNotEmpty() },
                onClickRecipe = onSelectRecipe,
                onCancel = onDismissRequest,
                searchFocusRequester = searchFocusRequester,
                subtitle = stringResource(R.string.select_groceries_from_recipe_info),
            )
        }
    }
}