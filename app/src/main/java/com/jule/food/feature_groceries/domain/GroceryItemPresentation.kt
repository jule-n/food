package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.text.input.TextFieldState

data class GroceryItemPresentation(
    val text: TextFieldState = TextFieldState(),
    val details: TextFieldState = TextFieldState(),
    val listId: Int,
    val recipeId: Int? = null,
    val locationId: Int? = null,
    val locationName: String = "NO_LOC",
    val isFinished: Boolean = false,
    val id: Int
) {
    fun toGroceryItem(): GroceryItemNew {
        return GroceryItemNew(
            text = text.text.toString(),
            details = details.text.toString(),
            listId = listId,
            recipeId = recipeId,
            isFinished = isFinished,
            id = id
        )
    }
}