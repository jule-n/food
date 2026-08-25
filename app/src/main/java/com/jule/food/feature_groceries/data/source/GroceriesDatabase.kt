package com.jule.food.feature_groceries.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jule.food.feature_groceries.data.GroceriesDao
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListNew

@Database(entities = [GroceryItemNew::class, GroceryListNew::class], version = 1)
abstract class GroceriesDatabase: RoomDatabase() {
    abstract fun groceriesDao(): GroceriesDao
}