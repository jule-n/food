package com.jule.food.ui.main

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jule.food.data.RecipeViewModel
import kotlin.text.startsWith

inline fun <reified T: Any> isRouteX(backStackEntry: NavBackStackEntry?): Boolean {
    return backStackEntry?.destination?.hierarchy?.any { it.hasRoute<T>() } ?: false
}

// The global navigation bar
@Composable
fun BottomNavigationBarNew(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier
    ) {
        val groceriesSelected = isRouteX<Screen.GroceryScreen>(navController.currentBackStackEntry)
        val recipesSelected = isRouteX<Screen.RecipeScreen>(navController.currentBackStackEntry)
        NavigationBarItem(
            selected = groceriesSelected,
            onClick = {
                if (!groceriesSelected) {
                    navController.navigate(Screen.GroceryScreen) {
                        launchSingleTop = true
                    }
                }
            },
            icon = { Icon(painterResource(BottomNavItem.Groceries.icon), contentDescription = null) },
            label = { Text(stringResource(BottomNavItem.Groceries.label)) },
            modifier = Modifier.height(80.dp),
        )
        NavigationBarItem(
            selected = recipesSelected,
            onClick = {
            },
            icon = { Icon(painterResource(BottomNavItem.Recipes.icon), contentDescription = null) },
            label = { Text(stringResource(BottomNavItem.Recipes.label)) },
            modifier = Modifier.height(80.dp),
        )
    }
}