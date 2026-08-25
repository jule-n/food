package com.jule.food.feature_locations.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jule.food.feature_locations.domain.GroceryLocationNew
import kotlinx.coroutines.flow.Flow

class LocationsRepository(
    private val dao: LocationsDao
) {
    fun getAllLocations(): Flow<List<GroceryLocationNew>> {
        return dao.getAllLocations()
    }

    suspend fun addLocation(location: GroceryLocationNew) {
        dao.addLocation(location)
    }

    suspend fun deleteLocation(location: GroceryLocationNew) {
        dao.deleteLocation(location)
    }
}