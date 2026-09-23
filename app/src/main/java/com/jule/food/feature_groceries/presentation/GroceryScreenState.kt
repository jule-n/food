package com.jule.food.feature_groceries.presentation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.focus.FocusRequester
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemPresentation
import com.jule.food.feature_groceries.domain.GroceryListUI
import com.jule.food.feature_locations.domain.GroceryLocationNew
import com.jule.food.feature_locations.domain.GroceryLocationUI

data class GroceryScreenState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val activeItemsInCurrentList: List<GroceryItemPresentation> = listOf(),
    val finishedItemsInCurrentList: List<GroceryItemPresentation> = listOf(),
    val isDataLoaded: Boolean = false,

    val selectedItemIds: Set<Int> = setOf(),
    val isSelectedItemsSameLocation: Boolean = false,
    val selectedItemsLocationId: Int? = null,
    val editingItem: GroceryItemPresentation? = null,
    val isSelectionModeActive: Boolean = false,

    val showAddGrocerySheet: Boolean = false,
    val addSheetSelectedLocationId: Int? = null,
    val addSheetSelectedLocationName: String? = null,
    val addSheetNameState: TextFieldState = TextFieldState(),
    val addSheetDetailState: TextFieldState = TextFieldState(),

    val lists: List<GroceryListUI> = listOf(),
    val pendingNewListId: Int = -1,
    val showEditListScreen: Boolean = false,
    val selectedListId: Int? = null,
    val selectedList: GroceryListUI? = null,
    val isDoneEditListButtonEnabled: Boolean = true,
    val showSelectListDialog: Boolean = false,
    val listDialogListIdSelected: Int? = null,

    val showSelectLocationDialog: Boolean = false,
    val locations: List<GroceryLocationUI> = listOf(),
    val pendingNewLocationId: Int = -1,
    val showEditLocationDialog: Boolean = false,
    val isDoneEditLocationButtonEnabled: Boolean = true,

    val showGroupingOptionDialog: Boolean = false,
    val groupingOption: GroceryGroupingOption = GroceryGroupingOption.None,
)