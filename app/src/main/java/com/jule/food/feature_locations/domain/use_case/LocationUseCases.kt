package com.jule.food.feature_locations.domain.use_case

data class LocationUseCases (
    val addLocations: AddLocations,
    val deleteLocations: DeleteLocations,
    val getAllLocations: GetAllLocations,
    val addItemNameToLocation: AddItemNameToLocation,
    val addItemNamesToLocation: AddItemNamesToLocation
)