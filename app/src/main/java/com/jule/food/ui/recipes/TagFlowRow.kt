package com.jule.food.ui.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import com.jule.food.R
import com.jule.food.data.Recipe
import com.jule.food.data.Tag
import com.jule.food.data.tagIcons
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandableTagSelectionFlowRow(
    tags: List<Tag>,
    possibleTagIdsToSelect: List<UUID>,
    selectedTagIds: List<UUID>,
    onRemoveFromSelectedTagIds: (UUID) -> Unit,
    onAddToSelectedTagIds: (UUID) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showExpansionButton: Boolean = true,
    extraContent: @Composable () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth().animateContentSize()
        ) {
            for (tag in tags.sortedBy { if (selectedTagIds.contains(it.id)) 0 else 1 }) {
                key(tag.id) {
                    val tagVisible = possibleTagIdsToSelect.contains(tag.id)
                    val alpha by animateFloatAsState(targetValue = if (tagVisible) 1f else 0f, label = "alpha")
                    val scale by animateFloatAsState(targetValue = if (tagVisible) 1f else 0.8f, label = "scale") // Optional scale

                    if (alpha > 0) {
                        val selected = selectedTagIds.contains(tag.id)
                        FilterChip(
                            selected = selected,
                            onClick = { if (selectedTagIds.contains(tag.id)) onRemoveFromSelectedTagIds(tag.id) else onAddToSelectedTagIds(tag.id) },
                            label = { Text(tag.name) },
                            leadingIcon = {
                                val painter = if (selected) R.drawable.done else tagIcons[tag.iconIndex]
                                Icon(
                                    painter = painterResource(painter),
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            },
                            modifier = Modifier
                                .padding(0.dp)
                                .height(FilterChipDefaults.Height)
                                .animateContentSize()
                                .graphicsLayer {
                                    this.alpha = alpha
                                    this.scaleX = scale
                                    this.scaleY = scale
                                },
                            colors = FilterChipDefaults.filterChipColors().copy(containerColor = MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
        }
        AnimatedVisibility (showExpansionButton) {
            Row {
                AssistChip(
                    onClick = {
                        onExpandedChange(!expanded)
                    },
                    label = { Text(if (!expanded) "Show more" else "Show less") },
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                extraContent()
            }
        }
    }
}
@Composable
fun TagSelectionFlowRow(
    tags: List<Tag>,
    selectedTagIds: List<UUID>,
    onRemoveFromSelectedTagIds: (UUID) -> Unit,
    onAddToSelectedTagIds: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.padding(horizontal = 10.dp).fillMaxWidth()
    ) {
        for (tag in tags) {
//        for (tag in tags.sortedBy { if (selectedTagIds.contains(it.id)) 0 else 1 }) {
            val selected = selectedTagIds.contains(tag.id)
            FilterChip(
                selected = selected,
                onClick = { if (selectedTagIds.contains(tag.id)) onRemoveFromSelectedTagIds(tag.id) else onAddToSelectedTagIds(tag.id) },
                label = { Text(tag.name) },
                leadingIcon = {
                    val painter = if (selected) R.drawable.done else tagIcons[tag.iconIndex]
                    Icon(
                        painter = painterResource(painter),
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                modifier = Modifier
                    .padding(0.dp)
                    .height(FilterChipDefaults.Height),
                colors = FilterChipDefaults.filterChipColors().copy(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    }
}

@Composable
fun TagDisplayFlowRow(
    allTags: List<Tag>,
    recipe: Recipe,
) {
    TagDisplayFlowRow(
        recipe.tags.map { id ->
            allTags.fastFirst { tag -> tag.id == id }
        }
    )
}

@Composable
fun TagDisplayFlowRow(
    tags: List<Tag>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.padding(horizontal = 10.dp).fillMaxWidth()
    ) {
        tags.forEach { tag ->
            FilterChip(
                selected = true,
                enabled = false,
                label = { Text(tag.name) },
                onClick = { },
                leadingIcon = {
                    Icon(
                        painter = painterResource(tagIcons[tag.iconIndex]),
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(disabledSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer, disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer, disabledLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer),
                modifier = Modifier.padding(0.dp).height(FilterChipDefaults.Height)
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun ExpandableTagSelectionFlowRowPreview() {
    val fishTag = Tag("Nudeeln", 0)
    val salzigTag = Tag("Salzig", 1)
    val saladTag = Tag("Salat", 2)
    val appleTag = Tag("Apfel", 3)
    val kaeseTag = Tag("Käse", 4)
    val kaeseTag2 = Tag("Käes2", 4)
    val kaeseTag3 = Tag("Käse3", 4)

    val tags = listOf(fishTag, salzigTag, saladTag, appleTag, kaeseTag, kaeseTag2, kaeseTag3)

    var expanded by remember { mutableStateOf(false) }

        FoodTheme {
                ExpandableTagSelectionFlowRow(
                    tags = tags,
                    selectedTagIds = listOf(tags[0].id, tags[2].id),
                    possibleTagIdsToSelect = tags.map { it.id },
                    onRemoveFromSelectedTagIds = {},
                    onAddToSelectedTagIds = {},
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                )
            }
}

@Preview(showBackground = true)
@Composable
fun TagDisplayFlowRowPreview(){
    val fishTag = Tag("Nudeeln", 0)
    val salzigTag = Tag("Salzig", 1)
    val saladTag = Tag("Salat", 2)
    val appleTag = Tag("Apfel", 3)
    val kaeseTag = Tag("Käse", 4)
    val kaeseTag2 = Tag("Käes2", 4)
    val kaeseTag3 = Tag("Käse3", 4)

    val tags = listOf(fishTag, salzigTag, saladTag, appleTag, kaeseTag, kaeseTag2, kaeseTag3)
    FoodTheme {
//        Scaffold() { innerPadding ->
            TagDisplayFlowRow(
                tags = tags
            )
//        }
    }
}