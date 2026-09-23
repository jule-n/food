package com.jule.food.feature_locations.domain.use_case

import com.jule.food.feature_locations.data.LocationsRepository

class DeleteLocations(
    private val repository: LocationsRepository
) {
    suspend operator fun invoke(locationIds: List<Int>) {
        if (locationIds.isEmpty()) return
        repository.deleteLocations(locationIds)
    }
}