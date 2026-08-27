package com.jule.food.others

import android.content.res.Resources
import androidx.annotation.StringRes
import com.jule.food.R

sealed class ErrorType {
    object IsEmpty: ErrorType()
    data class TooLong(val maxLength: Int): ErrorType()
    object NameSame: ErrorType()
}
fun getLabelFromErrorType(errorType: ErrorType, resources: Resources): String {
    when (errorType) {
        is ErrorType.IsEmpty -> {
            return resources.getString(R.string.name_empty)
        }
        is ErrorType.TooLong -> {
            return resources.getString(R.string.name_too_long, errorType.maxLength)
        }
        is ErrorType.NameSame -> {
            return resources.getString(R.string.name_already_exists)
        }
    }
}