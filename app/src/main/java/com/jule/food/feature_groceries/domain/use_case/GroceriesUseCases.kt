package com.jule.food.feature_groceries.domain.use_case

data class GroceriesUseCases (
    val addGroceryItem: AddGroceryItem,
    val addGroceryList: AddGroceryList,
    val addGroceryItems: AddGroceryItems,
    val deleteGroceryItems: DeleteGroceryItems,
    val deleteGroceryList: DeleteGroceryList,
    val getGroceriesInList: GetGroceriesInList,
    val getAllLists: GetAllLists,
    val removeRecipeIdFromGroceries: RemoveRecipeIdFromGroceries
)