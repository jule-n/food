package com.jule.food.feature_groceries.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListNew
import kotlinx.coroutines.flow.Flow

class GroceriesRepository(
    private val dao: GroceriesDao
) {
    fun getGroceriesInList(listId: Int): Flow<List<GroceryItemNew>> {
        return dao.getGroceriesInList(listId)
    }
    fun getAllLists(): Flow<List<GroceryListNew>> {
        return dao.getAllLists()
    }

    suspend fun deleteGroceryItem(groceryItem: GroceryItemNew) {
        dao.deleteGroceryItem(groceryItem)
    }

    suspend fun addGroceryItem(groceryItem: GroceryItemNew) {
        dao.addGroceryItem(groceryItem)
    }

    suspend fun deleteGroceryList(groceryList: GroceryListNew) {
        dao.deleteGroceryList(groceryList)
    }

    suspend fun addGroceryList(groceryList: GroceryListNew) {
        dao.addGroceryList(groceryList)
    }
    suspend fun removeListIdFromGroceries(listId: Int) {
        dao.removeListIdFromGroceries(listId)
    }
    suspend fun removeRecipeIdFromGroceries(recipeId: Int) {
        dao.removeRecipeIdFromGroceries(recipeId)
    }
}