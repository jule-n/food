package com.jule.food.feature_groceries.domain

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

data class GroceryListPresentation(
    val text: TextFieldState = TextFieldState(),
    val gridState: LazyGridState = LazyGridState(),
    val showDeletedItems: MutableState<Boolean> = mutableStateOf(false),
    val id: Int
)