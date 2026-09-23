package com.jule.food.feature_locations.domain

import com.jule.food.others.ErrorType

data class GroceryLocationUI(
    val currentName: String = "",
    val assignedGroceries: List<String> = listOf(),
    val id: Int,
    val isError: Boolean = false,
    val errorType: ErrorType? = null
) {
    fun toGroceryLocation(sortOrder: Int): GroceryLocationNew {
        return GroceryLocationNew(
            name = currentName,
            assignedGroceries = assignedGroceries,
            sortOrder = sortOrder,
            id = if (id < 0) null else id
        )
    }
}