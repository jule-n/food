package com.jule.food.feature_locations.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.toMutableStateList
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroceryLocationNew(
    val name: String,
    val assignedGroceries: List<String>,
    val sortOrder: Int,
    @PrimaryKey val id: Int? = null
) {
    fun toUILocation(): GroceryLocationUI {
        return GroceryLocationUI(
            currentName = name,
            assignedGroceries = assignedGroceries,
            id = id!!
        )
    }
}