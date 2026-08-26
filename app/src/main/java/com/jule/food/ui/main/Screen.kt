package com.jule.food.ui.main

import kotlinx.serialization.Serializable

class Screen {
    @Serializable
    object GroceryScreen
    @Serializable
    object RecipeScreen
    @Serializable
    data class SpecificRecipeScreen(val recipeId: Int)
    @Serializable
    object SettingsScreen
}