package com.jule.food.others

import com.jule.food.feature_groceries.presentation.GroceryScreenState

interface MviReducer<S,E> {
    fun reduce(state: S, event: E): S
}

typealias Mutation<S> = (S) -> S
operator fun <S> Mutation<S>.plus(next: Mutation<S>): Mutation<S> =
    { s -> next(this(s)) }