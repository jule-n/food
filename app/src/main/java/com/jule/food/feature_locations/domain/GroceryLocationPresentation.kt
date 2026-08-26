package com.jule.food.feature_locations.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroceryLocationPresentation(
    val name: TextFieldState,
    val assignedGroceries: SnapshotStateList<String>,
    val id: Int? = null
) {
    fun toGroceryLocation(): GroceryLocationNew {
        return GroceryLocationNew(
            name = name.text.toString(),
            assignedGroceries = assignedGroceries.toList(),
            id = id
        )
    }
}