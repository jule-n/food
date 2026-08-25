package com.jule.food.feature_groceries.domain.use_case

import com.jule.food.feature_groceries.data.GroceriesRepository
import com.jule.food.feature_groceries.domain.GroceryItemNew
import kotlinx.coroutines.flow.Flow

class GetGroceriesInList(
    private val repository: GroceriesRepository
) {
    operator fun invoke(listId: Int): Flow<List<GroceryItemNew>> {
        return repository.getGroceriesInList(listId)
    }
}