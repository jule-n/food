package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.room.Entity
import androidx.room.PrimaryKey

const val MAX_LENGTH_LIST_NAME = 30

@Entity
data class GroceryListNew (
    val name: String,
    val sortOrder: Int? = null,
    @PrimaryKey val id: Int? = null
) {
    fun toPresentationList(): GroceryListUI {
        return GroceryListUI(
            currentName = name,
            savedName = name,
            id = id!!
        )
    }
}