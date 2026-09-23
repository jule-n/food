package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.domain.GroceryListUI

class AddGroceryLists(
    private val repository: GroceriesRepository
) {
    suspend operator fun invoke(groceryLists: List<GroceryListUI>) {
        val dataLists = groceryLists.mapIndexed { index, list -> list.toGroceryList(index) }
        return repository.addGroceryLists(dataLists)
    }
}