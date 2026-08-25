package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository

class RemoveRecipeIdFromGroceries(
    private val repository: GroceriesRepository
) {
    suspend fun invoke(recipeId: Int) {
        repository.removeRecipeIdFromGroceries(recipeId)
    }
}