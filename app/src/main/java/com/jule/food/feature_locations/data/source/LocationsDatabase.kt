package com.jule.food.feature_locations.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jule.food.feature_locations.data.LocationsDao
import com.jule.food.feature_locations.domain.GroceryLocationNew

@Database(entities = [GroceryLocationNew::class], version = 1)
abstract class LocationsDatabase: RoomDatabase() {
    abstract fun locationsDao(): LocationsDao
}