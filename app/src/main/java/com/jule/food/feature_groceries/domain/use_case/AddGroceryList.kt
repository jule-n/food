package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.domain.GroceryListNew

class AddGroceryList(
    private val repository: GroceriesRepository
) {
    suspend operator fun invoke(groceryList: GroceryListNew) {
        repository.addGroceryList(groceryList)
    }
}