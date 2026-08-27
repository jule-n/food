package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import com.jule.food.others.ErrorType

data class GroceryListPresentation(
    val nameState: TextFieldState = TextFieldState(),
    val gridState: LazyGridState = LazyGridState(),
    val showFinishedItems: Boolean = false,
    val isNameError: Boolean = false,
    val nameErrorType: ErrorType? = null,
    val id: Int
) {
    fun toGroceryList(): GroceryListNew {
        return GroceryListNew(
            name = nameState.text.toString(),
            id = id
        )
    }
}