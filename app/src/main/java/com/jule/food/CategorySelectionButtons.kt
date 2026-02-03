package com.jule.food

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun CategorySelectionButtons(
    modifier: Modifier = Modifier,
    groceryCategories: List<GroceryItemCategory>,
    selectedCategoryId: UUID,
    onChangeSelectedCategoryId: (UUID) -> Unit,
    showBadge: Boolean = false
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        groceryCategories.forEach { category ->
            CategorySelectionButton(
                category = category,
                selected = selectedCategoryId == category.id,
                onClick = {
                    onChangeSelectedCategoryId(category.id)
                },
                showCheckbox = false,
                showBadge = showBadge
            )
        }
    }
}

@Composable
fun CategorySelectionButtons(
    modifier: Modifier = Modifier,
    groceryCategories: List<GroceryItemCategory>,
    selectedCategoriesIds: List<UUID>,
    onClickCategory: (UUID) -> Unit,
    showBadge: Boolean = false
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        groceryCategories.forEach { category ->
            CategorySelectionButton(
                category = category,
                selected = selectedCategoriesIds.contains(category.id),
                onClick = { onClickCategory(category.id) },
                showCheckbox = true,
                showBadge = showBadge
            )
        }
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CategorySelectionButton(
    modifier: Modifier = Modifier,
    category: GroceryItemCategory,
    selected: Boolean,
    onClick: () -> Unit,
    showCheckbox: Boolean = false,
    showBadge: Boolean = false
) {
    val backgroundColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant
    )
    val textColor = MaterialTheme.colorScheme.contentColorFor(backgroundColor)

    Surface(
        modifier = modifier,
        color = backgroundColor,
        enabled = true,
        onClick = onClick,
        shape = RoundedCornerShape(20)
    ) {
        if (showCheckbox) {
            CustomCheckbox(
                selectionOption = if (selected) SelectionOption.Yes else SelectionOption.No,
                topStartRadius = 50
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                category.name,
                color = textColor
            )
            if (showBadge) {
                CustomBadge(
                    number = category.items.count(),
                    backgroundColor = textColor.copy(alpha = 0.2f),
                    textColor = textColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun CustomBadge(
    modifier: Modifier = Modifier,
    number: Int,
    backgroundColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
    textColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
) {
    Surface(
        shape = CircleShape,
        color = backgroundColor,
        modifier = modifier.size(20.dp)
    ) {
        Box {
            Text(
                number.toString(),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}