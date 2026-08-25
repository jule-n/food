package com.jule.food.feature_locations.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroceryLocationNew(
    val name: String,
    val assignedGroceries: List<String>,
    @PrimaryKey val id: Int? = null
)