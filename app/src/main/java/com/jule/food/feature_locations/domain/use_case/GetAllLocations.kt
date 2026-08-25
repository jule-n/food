package com.jule.food.feature_locations.domain.use_case

import com.jule.food.feature_locations.data.LocationsRepository
import com.jule.food.feature_locations.domain.GroceryLocationNew
import kotlinx.coroutines.flow.Flow

class GetAllLocations(
    private val repository: LocationsRepository
) {
    operator fun invoke(): Flow<List<GroceryLocationNew>> {
        return repository.getAllLocations()
    }
}