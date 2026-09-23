package com.jule.food.feature_groceries.presentation

import android.util.Log
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.feature_groceries.domain.GroceryListUI
import com.jule.food.feature_groceries.domain.MAX_LENGTH_LIST_NAME
import com.jule.food.others.ErrorType
import com.jule.food.others.MviReducer

class GroceryListReducer: MviReducer<GroceryScreenState, GroceryScreenEvent.ListEvent> {
    override fun reduce(state: GroceryScreenState, event: GroceryScreenEvent.ListEvent): GroceryScreenState {
        return when (event) {
            is GroceryScreenEvent.ListEvent.ChangeSelectedListId -> state // Change settings repository, not state directly
            is GroceryScreenEvent.ListEvent.OpenEditListScreen -> state.onOpenEditListScreen()
            is GroceryScreenEvent.ListEvent.DoneEditListScreen -> state.onDoneEditListScreen()
            is GroceryScreenEvent.ListEvent.ChangeShowFinishedItems -> state.onChangeShowFinishedItems(event.show)
            is GroceryScreenEvent.ListEvent.AddList -> state.onAddList()
            is GroceryScreenEvent.ListEvent.DeleteList -> state.onDeleteList(event.id)
            is GroceryScreenEvent.ListEvent.ReorderLists -> state.onReorderLists(event.fromIndex, event.toIndex)
            is GroceryScreenEvent.ListEvent.ListNameChanged -> state.onListNameChanged(event.id, event.newName)
            is GroceryScreenEvent.ListEvent.ChangeShowListDialog -> state.onChangeShowListDialog(event.show)
            is GroceryScreenEvent.ListEvent.SelectListInDialog -> state.onSelectListInDialog(event.id)
        }
    }


    fun checkListError(listName: String): ErrorType? {
        // Check for Errors
        val isBlank = listName.isBlank()
        val isTooLong = listName.length > MAX_LENGTH_LIST_NAME
        val isError = isBlank || isTooLong
        // If there is an error, update list with error type
        if (isError) {
            return if (isBlank) ErrorType.IsEmpty else ErrorType.TooLong(MAX_LENGTH_LIST_NAME)
        }
        return null
    }

    fun GroceryScreenState.onOpenEditListScreen(): GroceryScreenState {
        if (showEditListScreen == true) return this
        if (isSelectionModeActive)
            return copy(showEditListScreen = true, isSelectionModeActive = false)
        return copy(showEditListScreen = true)
    }
    fun GroceryScreenState.onDoneEditListScreen(): GroceryScreenState {
        if (!showEditListScreen || !isDoneEditListButtonEnabled) return this
        return copy(showEditListScreen = false, pendingNewListId = -1)
    }
    fun GroceryScreenState.onChangeShowFinishedItems(show: Boolean): GroceryScreenState {
        if (selectedList == null || selectedListId == null || selectedList.showFinishedItems == show) return this
        val changedList = selectedList.copy(showFinishedItems = show)
        return copy(
            lists = lists.map { if (it.id == selectedListId) changedList else it },
            selectedList = changedList
        )
    }
    fun GroceryScreenState.onAddList(): GroceryScreenState {
        return copy(
            lists = lists + GroceryListUI(id = pendingNewListId),
            pendingNewListId = pendingNewListId - 1
        )
    }
    fun GroceryScreenState.onDeleteList(id: Int): GroceryScreenState {
        if (!lists.any { it.id == id } || lists.size == 1) return this
        return copy(
            lists = lists.mapNotNull { if (it.id == id) null else it }
        )
    }
    fun GroceryScreenState.onListNameChanged(id: Int, newName: String): GroceryScreenState {
        val list = lists.fastFirstOrNull { it.id == id } ?: return this
        if (newName == list.currentName) return this
        // Get error type (null if no error)
        val errorType = checkListError(newName)

        // If there is an error, update list with error type
        if (errorType != null) {
            Log.d("handleListNameChange", "List \"${list.currentName}\" ($id) has an error with name \"$newName\" ($errorType)")
            return copy(
                lists = lists.map { if (it.id == id) it.copy(
                    currentName = newName,
                    isNameError = true,
                    nameErrorType = errorType
                ) else it },
                // Disable isDoneEdit button
                isDoneEditListButtonEnabled = false
            )
        }
        Log.d("handleListNameChange", "List $id has new Name $newName")
        // If there is no error, Update state to reflect that there is no error
        // If no other error existed, enable isDone button
        if (list.isNameError) {
            return copy(
                lists = lists.map { if (it.id == id) it.copy(currentName = newName, isNameError = false) else it},
                isDoneEditListButtonEnabled = !(lists-list).any { it.isNameError }
            )
        }
        return copy(lists = lists.map { if (it.id == id) it.copy(currentName = newName) else it })
    }

    fun GroceryScreenState.onReorderLists(fromIndex: Int, toIndex: Int): GroceryScreenState {
        if (fromIndex == toIndex) return this
        return copy(lists = lists.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        })
    }

    fun GroceryScreenState.onChangeShowListDialog(show: Boolean): GroceryScreenState {
        if (showSelectListDialog == show) return this
        if (show) {
            return copy(
                showSelectListDialog = true,
                listDialogListIdSelected = selectedListId
            )
        }
        return copy(showSelectListDialog = false)
    }

    fun GroceryScreenState.onSelectListInDialog(id: Int): GroceryScreenState {
        // Move selected items to other list
        if (!isSelectionModeActive || selectedItemIds.isEmpty() || !showSelectListDialog) return this
        return copy(showSelectListDialog = false, isSelectionModeActive = false)
    }
}