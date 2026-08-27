package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.domain.GroceryItemNew

class AddGroceryItems(
    private val repository: GroceriesRepository
) {
    suspend operator fun invoke(groceryItems: List<GroceryItemNew>) {
        repository.addGroceryItems(groceryItems)
    }
}