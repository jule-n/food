package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListNew
import kotlinx.coroutines.flow.Flow

class GetAllLists(
    private val repository: GroceriesRepository
) {
    operator fun invoke(): Flow<List<GroceryListNew>> {
        return repository.getAllLists()
    }
}