package com.jule.food.feature_locations.domain.use_case

import com.jule.food.feature_locations.data.LocationsRepository
import com.jule.food.feature_locations.domain.GroceryLocationNew

class AddItemNameToLocation(
    private val repository: LocationsRepository
) {
    suspend operator fun invoke(itemName: String, locationId: Int?, locations: List<GroceryLocationNew>) {
        val newLocations = locations.map {
            if (it.id == locationId && !it.assignedGroceries.contains(itemName)) {
                return@map it.copy(assignedGroceries = it.assignedGroceries + itemName)
            }
            if (it.id != locationId && it.assignedGroceries.contains(itemName)) {
                return@map it.copy(assignedGroceries = it.assignedGroceries - itemName)
            }
            return@map it
        }
        repository.addLocations(newLocations)
    }
}