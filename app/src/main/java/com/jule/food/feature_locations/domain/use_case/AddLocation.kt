package com.jule.food.feature_locations.domain.use_case

import com.jule.food.feature_locations.data.LocationsRepository
import com.jule.food.feature_locations.domain.GroceryLocationNew
import kotlinx.coroutines.flow.Flow

class AddLocation(
    private val repository: LocationsRepository
) {
    suspend operator fun invoke(location: GroceryLocationNew) {
        return repository.addLocation(location)
    }
}