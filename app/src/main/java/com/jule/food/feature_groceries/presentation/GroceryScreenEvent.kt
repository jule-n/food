package com.jule.food.feature_groceries.presentation

import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemNew

sealed class GroceryScreenEvent {
    data class ChangeIsSelectionModeActive(val value: Boolean): GroceryScreenEvent()
    data class AddItemIdsToSelection(val ids: List<Int>): GroceryScreenEvent()
    data class RemoveItemIdsFromSelection(val ids: List<Int>): GroceryScreenEvent()
    object AddGrocery: GroceryScreenEvent()
    data class ChangeShowAddGrocerySheet(val show: Boolean): GroceryScreenEvent()
    data class ChangeSelectedListId(val value: Int): GroceryScreenEvent()
    data class ChangeShowEditListScreen(val show: Boolean): GroceryScreenEvent()
//    data class ChangeShowEditLocationScreen(val show: Boolean): GroceryScreenEvent()
    data class ChangeShowSelectLocationDialog(val show: Boolean): GroceryScreenEvent()
    data class ChangeShowGroupingOptionDialog(val show: Boolean): GroceryScreenEvent()
    data class ChangeGroupingOption(val value: GroceryGroupingOption): GroceryScreenEvent()
    data class FinishItem(val id: Int): GroceryScreenEvent()
    data class RestoreFinishedItem(val id: Int): GroceryScreenEvent()
    data class ChangeShowFinishedItems(val show: Boolean): GroceryScreenEvent()
    object DeleteFinishedItems: GroceryScreenEvent()
    object RestoreDeletedItems: GroceryScreenEvent()
    data class AddLocation(val name: String): GroceryScreenEvent()
    data class DeleteLocation(val id: Int): GroceryScreenEvent()
    data class ChangeAddSheetSelectedLocationId(val id: Int?): GroceryScreenEvent()
    data class AddList(val name: String): GroceryScreenEvent()
    data class DeleteList(val id: Int): GroceryScreenEvent()
}