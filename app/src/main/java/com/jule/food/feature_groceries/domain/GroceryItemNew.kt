package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroceryItemNew(
    val text: String,
    val details: String,
    val listId: Int,
    val recipeId: Int? = null,
    val isFinished: Boolean = false,
    @PrimaryKey val id: Int? = null
) {
    fun toPresentationItem(): GroceryItemPresentation {
        return GroceryItemPresentation(
            text = TextFieldState(text),
            details = TextFieldState(details),
            listId = listId,
            recipeId = recipeId,
            isFinished = isFinished,
            id = id ?: -1
        )
    }
}