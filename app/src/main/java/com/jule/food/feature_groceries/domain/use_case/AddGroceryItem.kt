package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.domain.GroceryItemNew

class AddGroceryItem(
    private val repository: GroceriesRepository
) {
    suspend fun invoke(groceryItem: GroceryItemNew) {
        repository.addGroceryItem(groceryItem)
    }
}