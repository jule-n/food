package com.jule.food.feature_groceries.presentation

import android.util.Log
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewModelScope
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent.ItemEvent
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent.ListEvent
import com.jule.food.others.MviReducer
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroceryLocationReducer: MviReducer<GroceryScreenState, GroceryScreenEvent.LocationEvent> {
    override fun reduce(state: GroceryScreenState, event: GroceryScreenEvent.LocationEvent): GroceryScreenState {
        return when (event) {
            is GroceryScreenEvent.LocationEvent.ChangeShowSelectLocationDialog -> state.onChangeShowSelectLocationDialog(event.show)
            is GroceryScreenEvent.LocationEvent.SelectLocationId -> state.onSelectLocationId(event.id)
            is GroceryScreenEvent.LocationEvent.AddLocation -> state
            is GroceryScreenEvent.LocationEvent.DeleteLocation -> state
        }
    }

    fun GroceryScreenState.onChangeShowSelectLocationDialog(show: Boolean): GroceryScreenState {
        if (showSelectLocationDialog == show) return this
        return copy(showSelectLocationDialog = show)
    }
    fun GroceryScreenState.onSelectLocationId(id: Int?): GroceryScreenState {
        if (!showAddGrocerySheet && !isSelectionModeActive || (showAddGrocerySheet && isSelectionModeActive)) return this
        val locationName = locations.fastFirstOrNull { it.id == id }?.name?.text?.toString()
        // Change add sheet location ID and Name
        if (showAddGrocerySheet) {
            return copy(
                addSheetSelectedLocationId = id,
                addSheetSelectedLocationName = locationName
            )
        }

        // Editing items, therefore change selected items location ID and Name
        return copy(
            activeItemsInCurrentList = activeItemsInCurrentList.map {
                if (selectedItemIds.contains(it.id)) it.copy(locationId = id, locationName = locationName ?: "NO_LOC") else it
            }
        )
    }
}