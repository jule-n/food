package com.jule.food.feature_groceries.presentation.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.utils.FilledIconButtonWithTooltip
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.data.GroceryItemCategory
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import com.jule.food.R
import com.jule.food.utils.SheetErrorMessage
import com.jule.food.data.isCategoryNameTooLong
import com.jule.food.feature_groceries.domain.GroceryListNew
import com.jule.food.feature_groceries.domain.GroceryListPresentation
import com.jule.food.ui.groceries.GroceryScreenTop
import com.jule.food.ui.groceries_recipes.EditScreen
import com.jule.food.ui.groceries_recipes.EditScreenItem
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.utils.CustomCheckbox
import com.jule.food.utils.DeleteDialog
import com.jule.food.utils.SelectionOption
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID

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
                    name = list.text.text.toString(),
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
