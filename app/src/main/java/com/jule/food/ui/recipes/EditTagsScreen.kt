package com.jule.food.ui.recipes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.R
import com.jule.food.data.Recipe
import com.jule.food.data.Tag
import com.jule.food.data.isTagNameTooLong
import com.jule.food.data.tagIcons
import com.jule.food.ui.groceries.SelectRecipeDialog
import com.jule.food.ui.groceries_recipes.EditScreen
import com.jule.food.ui.groceries_recipes.EditScreenItem
import com.jule.food.utils.DefaultDialog
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.utils.SimpleAddEditBottomSheet
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTagsScreen(
    tags: List<Tag>,
    allRecipes: List<Recipe>,
    onChangeTagName: (tagId: UUID, newName: String) -> Unit,
    onChangeTagIcon: (tagId: UUID, newIconIndex: Int) -> Unit,
    onChangeTagRecipes: (tagId: UUID, recipeIds: List<UUID>) -> Unit,
    onReorderTags: (from: Int, to: Int) -> Unit,
    onAddNewTag: (String) -> Unit,
    onDeleteTagId: (UUID) -> Unit,
) {
    val resources = LocalResources.current

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onReorderTags(from.index, to.index)
    }
    var tagToDelete: Tag? by remember { mutableStateOf(null) }
    var showAddTagSheet by remember { mutableStateOf(false) }
    var selectIconTagId: UUID? by remember { mutableStateOf(null) }
    val showSelectIconDialog = selectIconTagId != null

    var selectRecipesTagId: UUID? by remember { mutableStateOf(null) }
    val showSelectRecipesDialog = selectRecipesTagId != null

    Column(Modifier.padding(horizontal = 10.dp)) {
        Spacer(Modifier.height(SearchBarDefaults.InputFieldHeight + 20.dp))
        EditScreen(
            lazyListState = lazyListState,
            reorderableListState = reorderableLazyListState,
            items = tags,
            key = { it.id },
            itemName = { it.name },
            itemComposable = { item ->
                EditScreenItem(
                    item = item,
                    itemName = item.name,
                    itemBackgroundColor = MaterialTheme.colorScheme.background,
                    sharedElementModifier = null,
                    onDispose = { item, itemName ->
                        if(isTagNameTooLong(itemName) ||
                            itemName.isEmpty()) {
                            return@EditScreenItem
                        }
                        onChangeTagName(item.id, itemName)
                    },
                    isError = { item, itemName ->
                        isTagNameTooLong(itemName) || itemName.isEmpty()
                    },
                    errorText = { item, itemName ->
                        if (isTagNameTooLong(itemName)) {
                            return@EditScreenItem resources.getString(
                                R.string.name_too_long,
                                20
                            )
                        }
                        if (itemName.isEmpty()) {
                            return@EditScreenItem resources.getString(R.string.name_empty)
                        }
                        return@EditScreenItem null

                    },
                    onClickDelete = {
                        tagToDelete = item
                    },
                    itemOutlineColor = MaterialTheme.colorScheme.onBackground,
                    prefixElement = {
                        IconButtonWithTooltip(
                            onClick = { selectIconTagId = item.id },
                            tooltipText = stringResource(R.string.icon)
                        ) {
                            Icon(painterResource(tagIcons[item.iconIndex]), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        }
                    },
                    actionButtons = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButtonWithTooltip(
                                onClick = { selectRecipesTagId = item.id },
                                tooltipText = stringResource(R.string.recipes)
                            ) {
                                Icon(painterResource(R.drawable.book), contentDescription = null, modifier = Modifier.size(24.dp))
                            }
                            Text(stringResource(R.string.n_recipes, allRecipes.filter { it.tags.contains(item.id) }.size))
                        }
                    },
                    subtitleElement = {
                        Text(stringResource(R.string.n_recipes, allRecipes.filter { it.tags.contains(item.id) }.size), style = MaterialTheme.typography.bodySmall)
                    }
                )
            },
            newButtonText = stringResource(R.string.new_tag),
            onPressNewButton = { showAddTagSheet = true },
            newButtonBackgroundColor = MaterialTheme.colorScheme.primary,
            onDelete = { onDeleteTagId(it.id) },
            confirmDeleteDialogTitle = stringResource(R.string.delete_tag),
            onDeleteToastText = { resources.getString(R.string.deleted_tag_name, it.name)},
            itemToDelete = tagToDelete,
            onResetItemToDelete = { tagToDelete = null }
        )
    }
    if (showAddTagSheet) {
        SimpleAddEditBottomSheet(
            onConfirm = { onAddNewTag(it) },
            onDismissRequest = { showAddTagSheet = false },
            placeholderText = stringResource(R.string.new_tag),
            nameTooLongLimit = 20,
            existingNames = tags.map { it.name }
        )
    }
    if (showSelectIconDialog) {
        DefaultDialog(
            title = stringResource(R.string.select_icon_for_tag, tags.fastFirstOrNull { it.id == selectIconTagId }?.name ?: "NULL"),
            onDismissRequest = { selectIconTagId = null }
        ) {
            FlowRow {
                tagIcons.forEachIndexed { index, icon ->
                    IconButton(
                        onClick = {
                            onChangeTagIcon(selectIconTagId!!, index)
                            selectIconTagId = null
                        },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.background
                        ),
                        enabled = ( tags.fastFirstOrNull { it.id == selectIconTagId }?.iconIndex != index )
                    ) {
                        Icon(
                            painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
    if (showSelectRecipesDialog) {
        val tagRecipes = remember { allRecipes.filter { it.tags.contains (selectRecipesTagId!! )}.map { it.id }.toMutableStateList() }
        SelectRecipeDialog(
            allRecipes = allRecipes,
            onDismissRequest = {
                onChangeTagRecipes(selectRecipesTagId!!, tagRecipes)
                selectRecipesTagId = null
            },
            activeRecipeIds = null,
            selectedRecipeIds = tagRecipes,
            onClickRecipe = {
                if (tagRecipes.contains(it)) {
                    tagRecipes.remove(it)
                } else {
                    tagRecipes.add(it)
                }
            },
            showSubtitle = false,
            showSelectionCheckboxes = true
        )
    }
}