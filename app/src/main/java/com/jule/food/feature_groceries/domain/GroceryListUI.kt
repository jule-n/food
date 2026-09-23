package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import com.jule.food.others.ErrorType

data class GroceryListUI(
    val savedName: String? = null,
    val currentName: String = "",
    val gridState: LazyGridState = LazyGridState(),
    val showFinishedItems: Boolean = false,
    val isNameError: Boolean = false,
    val nameErrorType: ErrorType? = null,
    val id: Int
) {
    fun toGroceryList(sortOrder: Int): GroceryListNew {
        return GroceryListNew(
            name = currentName.trim(),
            sortOrder = sortOrder,
            id = if (id < 0) null else id
        )
    }
}