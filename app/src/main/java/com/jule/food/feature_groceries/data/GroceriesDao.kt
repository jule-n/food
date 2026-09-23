package com.jule.food.feature_groceries.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListNew
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceriesDao {
    @Query("SELECT * FROM GroceryItemNew WHERE listId = :listId ")
    fun getGroceriesInList(listId: Int): Flow<List<GroceryItemNew>>

    @Query("SELECT * FROM GroceryListNew")
    fun getAllLists(): Flow<List<GroceryListNew>>

    @Delete
    suspend fun deleteGroceryItems(groceryItems: List<GroceryItemNew>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGroceryItems(groceryItems: List<GroceryItemNew>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGroceryItem(groceryItem: GroceryItemNew)

    @Query("DELETE FROM GroceryListNew WHERE id IN (:ids)")
    suspend fun deleteGroceryLists(ids: List<Int>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGroceryList(groceryList: GroceryListNew): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGroceryLists(groceryLists: List<GroceryListNew>)


    @Query("UPDATE GroceryItemNew SET listId = null WHERE listId IN (:listIds)")
    suspend fun removeListIdsFromGroceries(listIds: List<Int>)

    @Query("UPDATE GroceryItemNew SET recipeId = null WHERE recipeId = :recipeId")
    suspend fun removeRecipeIdFromGroceries(recipeId: Int)
}