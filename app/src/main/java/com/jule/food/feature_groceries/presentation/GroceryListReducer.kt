package com.jule.food.feature_groceries.presentation

import android.util.Log
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.viewModelScope
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListPresentation
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent.ItemEvent
import com.jule.food.feature_groceries.presentation.GroceryScreenEvent.ListEvent
import com.jule.food.others.MviReducer
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroceryListReducer: MviReducer<GroceryScreenState, GroceryScreenEvent.ListEvent> {
    override fun reduce(state: GroceryScreenState, event: GroceryScreenEvent.ListEvent): GroceryScreenState {
        return when (event) {
            is GroceryScreenEvent.ListEvent.ChangeSelectedListId -> state.onChangeSelectedListId(event.value)
            is GroceryScreenEvent.ListEvent.ChangeShowEditListScreen -> state.onChangeShowEditListScreen(event.show)
            is GroceryScreenEvent.ListEvent.ChangeShowFinishedItems -> state.onChangeShowFinishedItems(event.show)
            is GroceryScreenEvent.ListEvent.AddList -> state
            is GroceryScreenEvent.ListEvent.DeleteList -> state
        }
    }

    fun GroceryScreenState.onChangeSelectedListId(value: Int): GroceryScreenState {
        if (selectedListId == value) return this
        val list = lists.fastFirstOrNull { it.id == value } ?: return this
        return copy(selectedListId = value, selectedList = list)
    }
    fun GroceryScreenState.onChangeShowEditListScreen(show: Boolean): GroceryScreenState {
        if (showEditListScreen == show) return this

        // If edit list screen is deactivated, remove errors from list text fields by taking the saved name
        if (!show) {
            lists.forEach { presList ->
                if (presList.isNameError) {
                    val dataName = dataLists.fastFirstOrNull { it.id == presList.id }?.name ?: "NULL"
                    presList.nameState.setTextAndPlaceCursorAtEnd(dataName)
                }
            }
        }
        return copy(showEditListScreen = show)
    }
    fun GroceryScreenState.onChangeShowFinishedItems(show: Boolean): GroceryScreenState {
        if (selectedList == null || selectedListId == null || selectedList.showFinishedItems == show) return this
        val changedList = selectedList.copy(showFinishedItems = show)
        return copy(
            lists = lists.map { if (it.id == selectedListId) changedList else it },
            selectedList = changedList
        )
    }
}