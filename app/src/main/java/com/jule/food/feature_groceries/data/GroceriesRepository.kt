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

    suspend fun deleteGroceryItems(groceryItems: List<GroceryItemNew>) {
        dao.deleteGroceryItems(groceryItems)
    }

    suspend fun addGroceryItems(groceryItems: List<GroceryItemNew>) {
        dao.addGroceryItems(groceryItems)
    }

    suspend fun addGroceryItem(groceryItem: GroceryItemNew) {
        dao.addGroceryItem(groceryItem)
    }

    suspend fun deleteGroceryLists(ids: List<Int>) {
        dao.deleteGroceryLists(ids)
    }

    suspend fun addGroceryList(groceryList: GroceryListNew): Long {
        return dao.addGroceryList(groceryList)
    }

    suspend fun addGroceryLists(groceryLists: List<GroceryListNew>) {
        return dao.addGroceryLists(groceryLists)
    }
//    suspend fun moveGroceryList(listId: Int, oldPos: Int, newPos: Int) {
//        dao.moveGroceryList(listId, oldPos, newPos)
//    }
    suspend fun removeListIdsFromGroceries(listIds: List<Int>) {
        dao.removeListIdsFromGroceries(listIds)
    }
    suspend fun removeRecipeIdFromGroceries(recipeId: Int) {
        dao.removeRecipeIdFromGroceries(recipeId)
    }
}