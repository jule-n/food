package com.jule.food.feature_locations.domain.use_case

import com.jule.food.feature_locations.data.LocationsRepository
import com.jule.food.feature_locations.domain.GroceryLocationNew

class AddItemNamesToLocation(
    private val repository: LocationsRepository
) {
    suspend operator fun invoke(itemNames: List<String>, locationId: Int?, locations: List<GroceryLocationNew>) {
        val newLocations = locations.map {
            if (it.id == locationId && !it.assignedGroceries.containsAll(itemNames)) {
                return@map it.copy(assignedGroceries = (it.assignedGroceries + itemNames).distinct())
            }
            if (it.id != locationId && it.assignedGroceries.any { itemNames.contains(it) }) {
                return@map it.copy(assignedGroceries = it.assignedGroceries - itemNames.toSet())
            }
            return@map it
        }
        repository.addLocations(newLocations)
    }
}