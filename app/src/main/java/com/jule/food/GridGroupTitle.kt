package com.jule.food

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
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun LazyGridScope.gridGroupTitle(
    title: String,
    animate: Boolean = false,
    key: Any? = null,
    showMoveHereButton: Boolean = false,
    onMoveHere: () -> Unit,
    modifier: Modifier = Modifier
) {
    item(span = { GridItemSpan(maxLineSpan) }, key = key) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = modifier.padding(start = 10.dp).conditional(animate) {
                    Modifier.animateItem()
                }
            )
            if (showMoveHereButton) {
                IconButtonWithTooltip(onClick = onMoveHere, tooltipText = stringResource(R.string.move_here), modifier = Modifier.height(20.dp)) {
                    Icon(painter = painterResource(R.drawable.move_item), contentDescription = stringResource(R.string.move_here))
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