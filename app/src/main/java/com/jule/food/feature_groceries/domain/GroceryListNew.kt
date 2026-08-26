package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroceryListNew (
    val name: String,
    @PrimaryKey val id: Int
) {
    fun toPresentationList(): GroceryListPresentation {
        return GroceryListPresentation(
            text = TextFieldState(name),
            id = id
        )
    }
}