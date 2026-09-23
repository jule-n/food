package com.jule.food.feature_groceries.presentation

import android.util.Log
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewModelScope
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.MAX_LENGTH_LIST_NAME
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent.ItemEvent
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent.ListEvent
import com.jule.food.feature_locations.domain.GroceryLocationUI
import com.jule.food.others.ErrorType
import com.jule.food.others.MviReducer
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroceryLocationReducer: MviReducer<GroceryScreenState, GroceryScreenEvent.LocationEvent> {
    override fun reduce(state: GroceryScreenState, event: GroceryScreenEvent.LocationEvent): GroceryScreenState {
        return when (event) {
            is GroceryScreenEvent.LocationEvent.ChangeShowSelectLocationDialog -> state.onChangeShowSelectLocationDialog(event.show)
            is GroceryScreenEvent.LocationEvent.SelectLocationId -> state.onSelectLocationId(event.id)
            is GroceryScreenEvent.LocationEvent.OpenEditLocationDialog -> state.onOpenEditLocationDialog()
            is GroceryScreenEvent.LocationEvent.DoneEditLocationDialog -> state.onDoneEditLocationDialog()
            is GroceryScreenEvent.LocationEvent.LocationNameChanged -> state.onLocationNameChanged(event.id, event.newName)
            is GroceryScreenEvent.LocationEvent.AddLocation -> state.onAddLocation()
            is GroceryScreenEvent.LocationEvent.DeleteLocation -> state.onDeleteLocation(event.id)
            is GroceryScreenEvent.LocationEvent.ReorderLocations -> state.onReorderLocations(event.fromIndex, event.toIndex)
        }
    }

    fun GroceryScreenState.onChangeShowSelectLocationDialog(show: Boolean): GroceryScreenState {
        if (showSelectLocationDialog == show) return this
        // If there are no locations, immediately open edit dialog on open
        if (locations.isEmpty() && show) {
            return copy(showSelectLocationDialog = true, showEditLocationDialog = true)
        }
        return copy(showSelectLocationDialog = show)
    }
    fun GroceryScreenState.onSelectLocationId(id: Int?): GroceryScreenState {
        if (
            (!showAddGrocerySheet && !isSelectionModeActive) ||
            (showAddGrocerySheet && isSelectionModeActive) ||
            !showSelectLocationDialog
        ) return this
        val locationName = locations.fastFirstOrNull { it.id == id }?.currentName
        // Change add sheet location ID and Name
        if (showAddGrocerySheet) {
            return copy(
                addSheetSelectedLocationId = id,
                addSheetSelectedLocationName = locationName,
                showSelectLocationDialog = false
            )
        }

        // Editing items, therefore change selected items location ID and Name
        return copy(
            activeItemsInCurrentList = activeItemsInCurrentList.map {
                if (selectedItemIds.contains(it.id)) it.copy(locationId = id, locationName = locationName ?: "") else it
            },
            isSelectedItemsSameLocation = true,
            selectedItemsLocationId = id,
            showSelectLocationDialog = false
        )
    }

    fun checkLocationError(name: String): ErrorType? {
        // Check for Errors
        val isBlank = name.isBlank()
        val isTooLong = name.length > MAX_LENGTH_LIST_NAME
        val isError = isBlank || isTooLong
        // If there is an error, update location with error type
        if (isError) {
            return if (isBlank) ErrorType.IsEmpty else ErrorType.TooLong(MAX_LENGTH_LIST_NAME)
        }
        return null
    }
    fun GroceryScreenState.onLocationNameChanged(id: Int, newName: String): GroceryScreenState {
        val location = locations.fastFirstOrNull { it.id == id } ?: return this
        if (newName == location.currentName) return this
        // Get error type (null if no error)
        val errorType = checkLocationError(newName)

        // If there is an error, update list with error type
        if (errorType != null) {
            Log.d("onLocationNameChanged", "Location \"${location.currentName}\" ($id) has an error with name \"$newName\" ($errorType)")
            return copy(
                locations = locations.map { if (it.id == id) it.copy(
                    currentName = newName,
                    isError = true,
                    errorType = errorType
                ) else it },
                // Disable isDoneEdit button
                isDoneEditListButtonEnabled = false
            )
        }
        Log.d("handleListNameChange", "List $id has new Name $newName")
        // If there is no error, Update state to reflect that there is no error
        // If no other error existed, enable isDone button
        if (location.isError) {
            return copy(
                locations = locations.map { if (it.id == id) it.copy(currentName = newName, isError = false) else it},
                isDoneEditLocationButtonEnabled = !(locations-location).any { it.isError }
            )
        }
        return copy(locations = locations.map { if (it.id == id) it.copy(currentName = newName) else it })
    }
    fun GroceryScreenState.onOpenEditLocationDialog(): GroceryScreenState {
        if (showEditLocationDialog || !showSelectLocationDialog) return this
        return copy(showEditLocationDialog = true)
    }
    fun GroceryScreenState.onDoneEditLocationDialog(): GroceryScreenState {
        if (!showEditLocationDialog || !showSelectLocationDialog) return this
        // Done editing locations, close dialog if there is not an error
        if (locations.any { it.isError }) return this
        // If there are no locations, immediately also close selection dialog
        if (locations.isEmpty()) {
            return copy(
                showEditLocationDialog = false, showSelectLocationDialog = false
            )
        }
        return copy(showEditLocationDialog = false)
    }

    fun GroceryScreenState.onAddLocation(): GroceryScreenState {
        return copy(
            locations = locations + GroceryLocationUI(
                id = pendingNewLocationId
            ),
            pendingNewLocationId = pendingNewLocationId - 1
        )
    }
    fun GroceryScreenState.onDeleteLocation(id: Int): GroceryScreenState {
        if (locations.size == 1 || !locations.any { it.id == id }) return this
        return copy(
            locations = locations.mapNotNull { if (it.id == id ) null else it }
        )
    }
    fun GroceryScreenState.onReorderLocations(fromIndex: Int, toIndex: Int): GroceryScreenState {
        Log.d("onReorderLocations", "Moving locations $fromIndex -> $toIndex")
        if (fromIndex == toIndex) return this
        Log.d("onReorderLocations", "Moved locations $fromIndex -> $toIndex")
        return copy(locations = locations.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        })
    }
}