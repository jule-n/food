package com.jule.food.feature_groceries.presentation

import androidx.annotation.StringRes
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemNew

sealed class GroceryScreenEvent {
    sealed class ItemEvent: GroceryScreenEvent() {
        data class ChangeIsSelectionModeActive(val value: Boolean): ItemEvent()
        data class AddItemIdsToSelection(val ids: List<Int>): ItemEvent()
        data class RemoveItemIdsFromSelection(val ids: List<Int>): ItemEvent()
        data class ToggleItemIdSelection(val id: Int): ItemEvent()

        data class ChangeShowAddGrocerySheet(val show: Boolean): ItemEvent()
        object AddGrocery: ItemEvent()

        data class ChangeShowGroupingOptionDialog(val show: Boolean): ItemEvent()
        data class ChangeGroupingOption(val value: GroceryGroupingOption): ItemEvent()

        data class FinishItem(val id: Int): ItemEvent()
        data class RestoreFinishedItem(val id: Int): ItemEvent()
        object DeleteFinishedItems: ItemEvent()
        object RestoreDeletedItems: ItemEvent()
    }
    sealed class ListEvent: GroceryScreenEvent() {
        data class ChangeSelectedListId(val value: Int): ListEvent()
        data class ChangeShowEditListScreen(val show: Boolean): ListEvent()
        data class ChangeShowFinishedItems(val show: Boolean): ListEvent()
        data class AddList(val name: String): ListEvent()
        data class DeleteList(val id: Int): ListEvent()

    }
    sealed class LocationEvent: GroceryScreenEvent() {
        data class ChangeShowSelectLocationDialog(val show: Boolean): LocationEvent()
        data class AddLocation(val name: String): LocationEvent()
        data class DeleteLocation(val id: Int): LocationEvent()
        data class SelectLocationId(val id: Int?): LocationEvent()
    }
}