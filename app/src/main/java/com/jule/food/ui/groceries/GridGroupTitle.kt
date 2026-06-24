package com.jule.food.ui.groceries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.R
import com.jule.food.utils.CustomCheckbox
import com.jule.food.utils.SelectionOption
import com.jule.food.utils.conditional

fun LazyGridScope.gridGroupTitle(
    title: String,
    animate: Boolean = false,
    key: Any? = null,
    showMoveHereButton: Boolean = false,
    onMoveHere: () -> Unit,
    showSelectAllButton: Boolean = false,
    isAllSelected: Boolean = false,
    onSelectAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    item(span = { GridItemSpan(maxLineSpan) }, key = key) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.conditional(animate) { Modifier.animateItem() }) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = modifier.padding(start = 10.dp)
            )
            Row {
            if (showMoveHereButton) {
                IconButtonWithTooltip(
                    onClick = onMoveHere,
                    tooltipText = stringResource(R.string.move_here),
                    modifier = Modifier.height(20.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.move_item),
                        contentDescription = stringResource(R.string.move_here)
                    )
                }
            }
            if (showSelectAllButton) {
                Surface(
                    onClick = onSelectAll,
//                    tooltipText = stringResource(R.string.select_all),
                    modifier = Modifier.size(20.dp)
                ) {
                    CustomCheckbox(
                        selectionOption = if (isAllSelected) SelectionOption.Yes else SelectionOption.No,
                        selectedBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        borderColor = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            }
        }
    }
}

fun LazyGridScope.gridSpacer(
    size: Dp
) {
    item(span = { GridItemSpan(maxLineSpan)}) {
        Spacer(modifier = Modifier.size(size))
    }
}