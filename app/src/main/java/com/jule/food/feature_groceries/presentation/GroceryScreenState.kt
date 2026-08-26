package com.jule.food.feature_groceries.presentation

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemPresentation
import com.jule.food.feature_groceries.domain.GroceryListNew
import com.jule.food.feature_groceries.domain.GroceryListPresentation
import com.jule.food.feature_locations.domain.GroceryLocationPresentation

data class GroceryScreenState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val lists: List<GroceryListPresentation> = listOf(),
    val locations: List<GroceryLocationPresentation> = listOf(),
    val selectedListId: Int? = null,
    val selectedList: GroceryListPresentation? = null,
    val itemsInCurrentList: List<GroceryItemPresentation> = listOf(),
    val isDataLoaded: Boolean = false,
    val selectedItemIds: List<Int> = listOf(),
    val isSelectionModeActive: Boolean = false,
    val showAddGrocerySheet: Boolean = false,
    val showEditListScreen: Boolean = false,
    val showSelectLocationDialog: Boolean = false,
    val showEditLocationDialog: Boolean = false,
    val showGroupingOptionDialog: Boolean = false,
    val groupingOption: GroceryGroupingOption = GroceryGroupingOption.None,
)