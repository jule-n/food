package com.jule.food.ui.recipes

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jule.food.utils.FilledExpressiveIconButtonWithTooltip
import com.jule.food.R
import com.jule.food.data.Recipe
import com.jule.food.data.Tag
import com.jule.food.utils.DefaultDialog
import java.util.UUID

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpecificRecipeTags(
    recipe: Recipe,
    allTags: List<Tag>,
    onChangeRecipeTags: (List<UUID>) -> Unit,
    modifier: Modifier = Modifier
) {
    var tagSelectionDialogActive by remember { mutableStateOf(false) }

    SpecificRecipeSection(
        icon = R.drawable.tag,
        title = stringResource(R.string.tags),
        actionButtons = {
            FilledExpressiveIconButtonWithTooltip(
                onClick = { tagSelectionDialogActive = true },
                shapes = IconButtonDefaults.shapes(),
                tooltipText = stringResource(R.string.select_tags)
            ) {
                Icon(painterResource(R.drawable.edit), contentDescription = "Edit")
            }
        },
//        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        modifier = modifier
    ) {
        TagDisplayFlowRow(allTags = allTags, recipe = recipe)

        if (tagSelectionDialogActive) {
            val currentSelectedTagIds = recipe.tags.toList()
            val selectedTagIds = remember { recipe.tags.toMutableStateList() }
            var dialogFocusManager = LocalFocusManager.current

            DefaultDialog(
                title = stringResource(R.string.select_tags),
                onDismissRequest = {
                    onChangeRecipeTags(selectedTagIds)
                    tagSelectionDialogActive = false
                },
                onClickDialogEnabled = true,
                onClickDialog = { dialogFocusManager.clearFocus(true) }
            ) {
                dialogFocusManager = LocalFocusManager.current

                TagSelectionFlowRow(
                    tags = allTags.sortedBy { if (currentSelectedTagIds.contains(it.id)) 0 else 1 },
//                    tags = allTags.sortedBy { if (selectedTagIds.contains(it.id)) 0 else 1 },
                    selectedTagIds = selectedTagIds,
                    onRemoveFromSelectedTagIds = { selectedTagIds.remove(it) },
                    onAddToSelectedTagIds = { selectedTagIds.add(it) }
                )
            }
        }
//        DisplayTagsRow(getTagsFromIds(recipe.tags, allTags))
    }
}