package com.jule.food.feature_groceries.domain.use_case

data class GroceriesUseCases (
    val addGroceryItem: AddGroceryItem,
    val addGroceryList: AddGroceryList,
    val deleteGroceryItem: DeleteGroceryItem,
    val deleteGroceryList: DeleteGroceryList,
    val getGroceriesInList: GetGroceriesInList,
    val getAllLists: GetAllLists,
    val removeRecipeIdFromGroceries: RemoveRecipeIdFromGroceries
)