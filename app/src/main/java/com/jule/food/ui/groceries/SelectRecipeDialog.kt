package com.jule.food.ui.groceries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jule.food.R
import com.jule.food.data.Recipe
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecipeDialog(
    modifier: Modifier = Modifier,
    allRecipes: List<Recipe>,
    activeRecipeIds: List<UUID>?,
    selectedRecipeIds: List<UUID>?,
    onClickRecipe: (UUID) -> Unit,
    onDismissRequest: () -> Unit,
    showSubtitle: Boolean,
    showSelectionCheckboxes: Boolean = false
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val searchFocusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        LaunchedEffect(Unit) {
            searchFocusRequester.requestFocus()
        }
        Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp), modifier = modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { focusManager.clearFocus() }) {
            SelectRecipeGrid(
                recipes = allRecipes,
                searchFocusRequester = searchFocusRequester,
                activeRecipeIds = activeRecipeIds,
                onClickRecipe = onClickRecipe,
                onCancel = onDismissRequest,
                selectedRecipeIds = selectedRecipeIds,
                isBottomSheet = false,
                subtitle = if (showSubtitle) stringResource(R.string.select_groceries_from_recipe_info) else null,
                showSelectionCheckboxes = showSelectionCheckboxes
            )
        }
    }
}