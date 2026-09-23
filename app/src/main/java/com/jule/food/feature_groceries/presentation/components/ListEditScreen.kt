package com.jule.food.feature_groceries.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import com.jule.food.R
import com.jule.food.feature_groceries.domain.GroceryListUI
import com.jule.food.others.getLabelFromErrorType
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ListEditScreen(
    lists: List<GroceryListUI>,
    onListNameChanged: (Int, String) -> Unit,
    onAddList: () -> Unit,
    onDeleteList: (Int) -> Unit,
    onReorderLists: (fromIndex: Int, toIndex: Int) -> Unit,
    lastListFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val resources = LocalResources.current

    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onReorderLists(from.index, to.index)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    with (LocalSharedTransitionScope.current!!) {
        EditScreenNew<GroceryListUI>(
            modifier = modifier,
            lazyListState = lazyListState,
            reorderableListState = reorderableListState,
            items = lists,
            key = { it.id },
            itemComposable = { index, item ->
                val state = rememberTextFieldState(item.currentName)
                LaunchedEffect(state.text) {
                    onListNameChanged(item.id, state.text.toString())
                }
                EditScreenItemNew(
                    item = item,
                    textState = state,
                    itemBackgroundColor = MaterialTheme.colorScheme.tertiary,
                    sharedElementModifier = Modifier.sharedElement(rememberSharedContentState(item.id!!), LocalNavAnimatedVisibilityScope.current!!),
                    isError = { item, itemName ->
                        item.isNameError
                    },
                    errorText = { item, itemName ->
                        if (item.nameErrorType == null) return@EditScreenItemNew null
                        getLabelFromErrorType(item.nameErrorType, resources)
                    },
                    isDeleteEnabled = lists.size > 1,
                    onClickDelete = {
                        onDeleteList(item.id)
                    },
                    modifier = if (index == lists.lastIndex) Modifier.focusRequester(lastListFocusRequester) else Modifier
                )
            },
            newButtonText = stringResource(R.string.new_category),
            onPressNewButton = onAddList,
            newButtonBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    }
}