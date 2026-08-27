package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListNew

class DeleteGroceryList(
    private val repository: GroceriesRepository
) {
    suspend operator fun invoke(groceryList: GroceryListNew) {
        repository.removeListIdFromGroceries(groceryList.id!!)
        repository.deleteGroceryList(groceryList)
    }
}