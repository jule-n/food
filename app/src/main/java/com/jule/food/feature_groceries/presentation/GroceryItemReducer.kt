package com.jule.food.feature_groceries.presentation

import android.util.Log
import androidx.compose.foundation.text.input.clearText
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewModelScope
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent.ItemEvent
import com.jule.food.others.Mutation
import com.jule.food.others.MviReducer
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroceryItemReducer: MviReducer<GroceryScreenState, GroceryScreenEvent.ItemEvent> {

//    data class ChangeIsSelectionModeActive(val value: Boolean): ItemEvent()
//    data class AddItemIdsToSelection(val ids: List<Int>): ItemEvent()
//    data class RemoveItemIdsFromSelection(val ids: List<Int>): ItemEvent()
//    data class ToggleItemIdSelection(val id: Int): ItemEvent()
//
//    data class ChangeShowAddGrocerySheet(val show: Boolean): ItemEvent()
//    object AddGrocery: ItemEvent()
//
//    data class ChangeShowGroupingOptionDialog(val show: Boolean): ItemEvent()
//    data class ChangeGroupingOption(val value: GroceryGroupingOption): ItemEvent()
//
//    data class FinishItem(val id: Int): ItemEvent()
//    data class RestoreFinishedItem(val id: Int): ItemEvent()
//    object DeleteFinishedItems: ItemEvent()
//    object RestoreDeletedItems: ItemEvent()
    override fun reduce(state: GroceryScreenState, event: GroceryScreenEvent.ItemEvent): GroceryScreenState {
        return when (event) {
            is GroceryScreenEvent.ItemEvent.ChangeIsSelectionModeActive -> state.onChangeIsSelectionModeActive(event.value)
            is GroceryScreenEvent.ItemEvent.AddItemIdsToSelection -> state.onAddItemIdsToSelection(event.ids)
            is GroceryScreenEvent.ItemEvent.RemoveItemIdsFromSelection -> state.onRemoveItemIdsFromSelection(event.ids)
            is GroceryScreenEvent.ItemEvent.ToggleItemIdSelection -> state.onToggleItemIdSelection(event.id)

            is GroceryScreenEvent.ItemEvent.ChangeShowAddGrocerySheet -> state.onChangeShowAddGrocerySheet(event.show)
            is GroceryScreenEvent.ItemEvent.AddGrocery -> state

            is GroceryScreenEvent.ItemEvent.ChangeShowGroupingOptionDialog -> state.onChangeShowGroupingDialog(event.show)
            is GroceryScreenEvent.ItemEvent.ChangeGroupingOption -> state.onChangeGroupingOption(event.value)

            is GroceryScreenEvent.ItemEvent.FinishItem -> state
            is GroceryScreenEvent.ItemEvent.RestoreFinishedItem -> state
            is GroceryScreenEvent.ItemEvent.DeleteFinishedItems -> state
            is GroceryScreenEvent.ItemEvent.RestoreDeletedItems -> state
        }
    }

    fun GroceryScreenState.onChangeIsSelectionModeActive(value: Boolean): GroceryScreenState {
        if (isSelectionModeActive == value) return this

        // Set Selection mode to value
        var state = copy(isSelectionModeActive = value)

        // If the sheet is activated and there are still items selected, deselect them
        if (value && selectedItemIds.isNotEmpty()) {
            state = state.copy(selectedItemIds = setOf())
        }

        return state
    }
    fun GroceryScreenState.onAddItemIdsToSelection(itemIds: List<Int>): GroceryScreenState {
        if (itemIds.isEmpty()) return this

        var editingItem = editingItem
//        // If there is just one item selected, update the editing item
        if (selectedItemIds.isEmpty() && itemIds.size == 1) {
            editingItem = activeItemsInCurrentList.fastFirstOrNull { it.id == itemIds[0] }
        }

        var state = this.copy()
        // If the selection mode wasn't active up until now, change it
        if (!isSelectionModeActive) {
            state = state.onChangeIsSelectionModeActive(true)
        }

        return state.copy(
            selectedItemIds = selectedItemIds + itemIds,
            editingItem = editingItem
        )
    }
    fun GroceryScreenState.onRemoveItemIdsFromSelection(itemIds: List<Int>): GroceryScreenState {
        if (!selectedItemIds.any { itemIds.contains(it) }) return this
        if (selectedItemIds.size == itemIds.size)
            return copy(isSelectionModeActive = false)

        val newSelectedItemIds = selectedItemIds - itemIds.toSet()
        var editingItem = editingItem
        // If there is just one item selected, update the editing item
        if (newSelectedItemIds.size == 1) {
            editingItem = activeItemsInCurrentList.fastFirstOrNull { it.id == newSelectedItemIds.first() }
        }
        return copy(
            selectedItemIds = newSelectedItemIds,
            editingItem = editingItem
        )
    }
    fun GroceryScreenState.onToggleItemIdSelection(id: Int): GroceryScreenState {
        val isSelected = selectedItemIds.any { it == id }
        if (isSelected)
            return this.onRemoveItemIdsFromSelection(listOf(id))
        else
            return this.onAddItemIdsToSelection(listOf(id))
    }

    fun GroceryScreenState.onChangeShowAddGrocerySheet(show: Boolean): GroceryScreenState {
        if (showAddGrocerySheet == show) return this
        return copy(showAddGrocerySheet = show)
    }
//    fun GroceryScreenState.onAddGrocery(): GroceryScreenState {
//        // Check for error
//        if (addSheetNameState.text.isBlank() || selectedListId == null) return this
//        // Empty the text fields
//        addSheetNameState.clearText()
//        addSheetDetailState.clearText()
//        return this
//    }

    fun GroceryScreenState.onChangeShowGroupingDialog(show: Boolean): GroceryScreenState {
        if (showGroupingOptionDialog == show) return this
        return copy(showGroupingOptionDialog = show)
    }
    fun GroceryScreenState.onChangeGroupingOption(value: GroceryGroupingOption): GroceryScreenState {
        if (groupingOption == value) return this
        return copy(groupingOption = value)
    }
}