package com.jule.food.feature_locations.domain.use_case

import com.jule.food.feature_locations.data.LocationsRepository
import com.jule.food.feature_locations.domain.GroceryLocationUI

class AddLocations(
    private val repository: LocationsRepository
) {
    suspend operator fun invoke(locations: List<GroceryLocationUI>) {
        return repository.addLocations(locations.mapIndexed { index, loc ->  loc.toGroceryLocation(index) })
    }
}