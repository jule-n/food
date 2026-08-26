package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.domain.GroceryItemNew

class DeleteGroceryItem(
    private val repository: GroceriesRepository
) {
    suspend operator fun invoke(groceryItem: GroceryItemNew) {
        repository.deleteGroceryItem(groceryItem)
    }
}