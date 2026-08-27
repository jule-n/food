package com.jule.food.feature_groceries.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import com.jule.food.R
import com.jule.food.feature_groceries.domain.GroceryListPresentation

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ListConnectedButtons(
    lists: List<GroceryListPresentation>,
    selectedListId: Int,
    onChangeSelectedListId: (Int) -> Unit,
    onEnableEditMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val indexOfSelected = lists.indexOfFirst { it.id == selectedListId }
    val lazyRowState = rememberLazyListState(initialFirstVisibleItemIndex = indexOfSelected)
    with (LocalSharedTransitionScope.current!!) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            state = lazyRowState
        ) {
            item {
                IconButtonWithTooltip(
                    onClick = onEnableEditMode,
                    tooltipText = stringResource(R.string.edit_categories),
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.edit),
                        contentDescription = stringResource(R.string.edit_categories),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            items(lists) { list ->
                ListButton(
                    name = list.nameState.text.toString(),
                    selected = list.id == selectedListId,
                    onClick = {
                        onChangeSelectedListId(list.id)
                    },
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(list.id),
                        animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                    )
                )
            }
        }
    }
}
