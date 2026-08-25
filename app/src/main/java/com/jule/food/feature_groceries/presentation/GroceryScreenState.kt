package com.jule.food.feature_groceries.presentation

import com.jule.food.feature_groceries.domain.GroceryItemPresentation
import com.jule.food.feature_groceries.domain.GroceryListNew

data class GroceryScreenState (
    val lists: List<GroceryListNew> = listOf(),
    val selectedListId: Int? = null,
    val itemsInCurrentList: List<GroceryItemPresentation> = listOf()
)