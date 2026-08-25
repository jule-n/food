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
    val locationId: MutableState<Int?> = mutableStateOf(null)
)