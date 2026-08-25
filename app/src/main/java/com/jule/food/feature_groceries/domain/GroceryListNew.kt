package com.jule.food.feature_groceries.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroceryListNew (
    val name: String,
    @PrimaryKey val id: Int
)