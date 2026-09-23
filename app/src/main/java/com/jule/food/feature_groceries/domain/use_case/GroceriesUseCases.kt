package com.jule.food.feature_groceries.domain.use_case

data class GroceriesUseCases (
    val addGroceryItem: AddGroceryItem,
    val addGroceryList: AddGroceryList,
    val addGroceryLists: AddGroceryLists,
    val addGroceryItems: AddGroceryItems,
    val deleteGroceryItems: DeleteGroceryItems,
    val deleteGroceryLists: DeleteGroceryLists,
    val getGroceriesInList: GetGroceriesInList,
    val getAllLists: GetAllLists,
    val removeRecipeIdFromGroceries: RemoveRecipeIdFromGroceries
)