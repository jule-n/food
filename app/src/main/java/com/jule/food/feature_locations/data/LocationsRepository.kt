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

    suspend fun addLocations(locations: List<GroceryLocationNew>) {
        dao.addLocations(locations)
    }

    suspend fun deleteLocations(locationIds: List<Int>) {
        dao.deleteLocations(locationIds)
    }
}