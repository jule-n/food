package com.jule.food.feature_groceries.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.R
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.GroceryLocation
import com.jule.food.data.Recipe
import com.jule.food.data.groceryGroupingOptionsDisplay
import com.jule.food.data.groceryGroupingOptionsIcons
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent
import com.jule.food.feature_groceries.presentation.GroceryScreenState
import com.jule.food.ui.groceries.CategoriesEditScreen
import com.jule.food.ui.groceries.EditGroceriesBottomSheetContent
import com.jule.food.ui.groceries.GroceryGridScreen
import com.jule.food.ui.groceries.GroceryScreenTop
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import com.jule.food.ui.settings.SettingDialog
import com.jule.food.ui.settings.SettingDialogElement
import java.util.UUID


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun GroceryScreenContentNew(
    state: GroceryScreenState,
    onEvent: (GroceryScreenEvent) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier
) {
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            if (state.editingItem != null) {
                EditGroceriesBottomSheetNew(
                    editingGroceryItemIds = state.selectedItemIds,
                    groceryNameState = state.editingItem.text,
                    groceryDetailState = state.editingItem.details,
                    onOpenListSelectionDialog = { },
                    onOpenLocationSelectionDialog = { onEvent(GroceryScreenEvent.LocationEvent.ChangeShowSelectLocationDialog(true)) },
                    onOpenRecipeSelectionDialog = { }
                )
            }
        },
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetDragHandle = {
            Row(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier
                    .width(50.dp)
                    .height(5.dp)) {}
            }
        },
        sheetSwipeEnabled = false,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    enabled = state.isSelectionModeActive,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onEvent(GroceryScreenEvent.ItemEvent.ChangeIsSelectionModeActive(false))
                }
        ) {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                    AnimatedContent(
                        targetState = state.showEditListScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { editingLists ->
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            if (!editingLists && state.selectedListId != null) {
                                GroceryScreenTopNew(
                                    lists = state.lists,
                                    selectedListId = state.selectedListId,
                                    onChangeSelectedListId = { onEvent(GroceryScreenEvent.ListEvent.ChangeSelectedListId(it)) },
                                    onOpenListEditScreen = { onEvent(GroceryScreenEvent.ListEvent.ChangeShowEditListScreen(true)) },
                                    groupingOption = state.groupingOption,
                                    onChangeShowGroupingDialog = { onEvent(GroceryScreenEvent.ItemEvent.ChangeShowGroupingOptionDialog(true)) },
                                )
                            } else {
                                ListEditScreen(
                                    lists = state.lists,
                                    onAddNewList = { onEvent(GroceryScreenEvent.ListEvent.AddList(it)) },
                                    onDeleteList = { onEvent(GroceryScreenEvent.ListEvent.DeleteList(it)) },
                                    onReorderLists = { _, _ -> }
                                )
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = !state.showEditListScreen,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                GroceryGridNew(
                    state = state,
                    onEvent = onEvent
                )
            }
        }

        if (state.showGroupingOptionDialog) {
            SettingDialog(
                title = stringResource(R.string.group_by),
                onDismissRequest = {
                    onEvent(GroceryScreenEvent.ItemEvent.ChangeShowGroupingOptionDialog(false))
                }
            ) {
                GroceryGroupingOption.entries.forEachIndexed { index, option ->
                    val selected = state.groupingOption == option
                    SettingDialogElement(
                        title = stringResource(groceryGroupingOptionsDisplay[option]!!),
                        selected = selected,
                        onClick = {
                            onEvent(GroceryScreenEvent.ItemEvent.ChangeGroupingOption(option))
                        },
                        leadingIcon = {
                            Icon(
                                painterResource(groceryGroupingOptionsIcons[option]!!),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}