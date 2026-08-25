package com.jule.food.feature_locations.domain.use_case

import com.jule.food.feature_locations.data.LocationsRepository
import com.jule.food.feature_locations.domain.GroceryLocationNew
import kotlinx.coroutines.flow.Flow

class DeleteLocation(
    private val repository: LocationsRepository
) {
    suspend fun invoke(location: GroceryLocationNew) {
        return repository.deleteLocation(location)
    }
}