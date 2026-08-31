package com.jule.food.feature_groceries.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemPresentation
import com.jule.food.feature_groceries.domain.GroceryListNew
import com.jule.food.feature_groceries.domain.GroceryListPresentation
import com.jule.food.feature_locations.domain.GroceryLocationPresentation
import com.jule.food.others.ErrorType

data class GroceryScreenState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val lists: List<GroceryListPresentation> = listOf(),
    val dataLists: List<GroceryListNew> = listOf(),
    val locations: List<GroceryLocationPresentation> = listOf(),
    val selectedListId: Int? = null,
    val selectedList: GroceryListPresentation? = null,
    val activeItemsInCurrentList: List<GroceryItemPresentation> = listOf(),
    val finishedItemsInCurrentList: List<GroceryItemPresentation> = listOf(),
    val isDataLoaded: Boolean = false,
    val selectedItemIds: Set<Int> = setOf(),
    val editingItem: GroceryItemPresentation? = null,
    val isSelectionModeActive: Boolean = false,
    val showAddGrocerySheet: Boolean = false,
    val showEditListScreen: Boolean = false,
    val showSelectLocationDialog: Boolean = false,
    val addSheetSelectedLocationId: Int? = null,
    val addSheetSelectedLocationName: String? = null,
    val addSheetNameState: TextFieldState = TextFieldState(),
    val addSheetDetailState: TextFieldState = TextFieldState(),
    val showGroupingOptionDialog: Boolean = false,
    val groupingOption: GroceryGroupingOption = GroceryGroupingOption.None,
)