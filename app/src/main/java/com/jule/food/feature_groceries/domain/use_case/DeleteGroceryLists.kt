package com.jule.food.feature_groceries.domain.use_case

import android.util.Log
import com.jule.food.feature_groceries.data.GroceriesRepository

class DeleteGroceryLists(
    private val repository: GroceriesRepository
) {
    suspend operator fun invoke(ids: List<Int>) {
        if (ids.isEmpty()) return
        Log.d("DeleteGroceryLists","Removing list ids from groceries and deleting lists $ids")
        repository.removeListIdsFromGroceries(ids)
        repository.deleteGroceryLists(ids)
    }
}