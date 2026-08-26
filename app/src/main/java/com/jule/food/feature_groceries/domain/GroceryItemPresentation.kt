package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

data class GroceryItemPresentation(
    val text: TextFieldState = TextFieldState(),
    val details: TextFieldState = TextFieldState(),
    val listId: MutableState<Int> = mutableIntStateOf(0),
    val recipeId: MutableState<Int?> = mutableStateOf(null),
    val locationId: MutableState<Int?> = mutableStateOf(null),
    val locationName: MutableState<String> = mutableStateOf("NO_LOC"),
    val isDeleted: MutableState<Boolean> = mutableStateOf(false),
    val id: Int
) {
    fun toGroceryItem(): GroceryItemNew {
        return GroceryItemNew(
            text = text.text.toString(),
            details = details.text.toString(),
            listId = listId.value,
            recipeId = recipeId.value,
            isDeleted = isDeleted.value,
            id = id
        )
    }
}