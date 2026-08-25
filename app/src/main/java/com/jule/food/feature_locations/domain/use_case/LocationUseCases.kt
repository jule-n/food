package com.jule.food.feature_locations.domain.use_case

data class LocationUseCases (
    val addLocation: AddLocation,
    val deleteLocation: DeleteLocation,
    val getAllLocations: GetAllLocations
)