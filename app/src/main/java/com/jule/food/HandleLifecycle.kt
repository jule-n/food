package com.jule.food

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


@Composable
fun HandleLifeCycle(
    settingsViewModel: SettingsViewModel,
    groceryViewModel: GroceryViewModel,
    recipeViewModel: RecipeViewModel
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                settingsViewModel.saveSettingsToPreferences(context)
                groceryViewModel.saveToFile(context)
                recipeViewModel.saveToFile(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}