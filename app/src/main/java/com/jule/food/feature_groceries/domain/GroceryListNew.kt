package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.room.Entity
import androidx.room.PrimaryKey

const val MAX_LENGTH_LIST_NAME = 30

@Entity
data class GroceryListNew (
    val name: String,
    @PrimaryKey val id: Int? = null
) {
    fun toPresentationList(): GroceryListPresentation {
        return GroceryListPresentation(
            nameState = TextFieldState(name),
            id = id!!
        )
    }
}