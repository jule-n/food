package com.jule.food.ui.main

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.jule.food.ui.settings.ImportSetting
import com.jule.food.R
import com.jule.food.ui.settings.SettingsScreen
import com.jule.food.data.ColorSetting
import com.jule.food.data.GroceryItem
import com.jule.food.data.GroceryItemCategory
import com.jule.food.data.GroceryViewModel
import com.jule.food.data.Languages
import com.jule.food.data.LocationViewModel
import com.jule.food.data.RecipeViewModel
import com.jule.food.data.ThemeSetting
import com.jule.food.data.getRecipeFromId
import com.jule.food.ui.groceries.GroceryScreen
import com.jule.food.ui.groceries_recipes.GroceryListAddingOption
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import com.jule.food.ui.recipes.RecipeScreen
import com.jule.food.ui.recipes.SpecificRecipeEditGroceriesScreen
import com.jule.food.ui.recipes.SpecificRecipeScreen
import com.jule.food.utils.ImageViewer
import com.jule.food.utils.completeSlideIn
import com.jule.food.utils.completeSlideOut
import java.util.UUID

sealed class BottomNavItem(val route: String, @DrawableRes val icon: Int = 0, @StringRes val label: Int = 0) {
    data object Groceries : BottomNavItem("groceries", R.drawable.grocery, R.string.groceries)
    data object Recipes : BottomNavItem("recipes", R.drawable.book, R.string.recipes)
    data object Settings : BottomNavItem("settings")
    data object SpecificRecipe : BottomNavItem("specific_recipe")
    data object SpecificRecipeImage : BottomNavItem("specific_recipe_image")
    data object SpecificRecipeEditGroceries : BottomNavItem("specific_recipe_edit_groceries")
}

// The global navigation bar
@Composable
fun BottomNavigationBar(
    navController: NavController,
    recipeViewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier,
//        containerColor = MaterialTheme.colorScheme.background
    ) {
        val groceriesSelected = currentRoute == BottomNavItem.Groceries.route
        val recipesSelected = currentRoute == BottomNavItem.Recipes.route || (
                currentRoute != null && (
                        currentRoute.startsWith(BottomNavItem.SpecificRecipe.route) ||
                        currentRoute.startsWith(BottomNavItem.SpecificRecipeImage.route) ||
                        currentRoute.startsWith(BottomNavItem.SpecificRecipeEditGroceries.route)
                )
            )
        NavigationBarItem(
            selected = groceriesSelected,
            onClick = {
                if (!groceriesSelected) {
                    navController.navigate(BottomNavItem.Groceries.route) {
                        launchSingleTop = true
//                        launchSingleTop = true
//                        popUpTo(Recipes.route)
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
                if (!recipesSelected) {
                    val poppingSuccessful = navController.popBackStack(BottomNavItem.Recipes.route, inclusive = false)
                    if (poppingSuccessful) {
                        Log.d("Navigation", "Popped back to recipes")
                    }
                    if (!poppingSuccessful) {
                        Log.d("Navigation", "Recipes not in back stack")
                        navController.navigate(BottomNavItem.Recipes.route) {
                            launchSingleTop = true
                        }
                    }
                    Log.d("Navigation", "selectedRecipe is ${if (recipeViewModel.selectedRecipeId != null) recipeViewModel.getRecipeNameFromId(recipeViewModel.selectedRecipeId!!) else "NULL"}")
                    if (recipeViewModel.selectedRecipeId != null) {
                        navController.navigate("${BottomNavItem.SpecificRecipe.route}/${recipeViewModel.selectedRecipeId}/${recipeViewModel.lastSelectedRecipeFromSearch}")
                        Log.d("Navigation", "selectedRecipeImageIndex is ${recipeViewModel.selectedRecipeImageIndex}")
                        if (recipeViewModel.selectedRecipeImageIndex != null) {
                            navController.navigate("${BottomNavItem.SpecificRecipeImage.route}/${recipeViewModel.selectedRecipeId}/${recipeViewModel.selectedRecipeImageIndex}")
                        } else {
                            Log.d("Navigation", "editGroceryScreen is ${recipeViewModel.isEditGroceriesScreenActive}")
                            if (recipeViewModel.isEditGroceriesScreenActive) {
                                navController.navigate("${BottomNavItem.SpecificRecipeEditGroceries.route}/${recipeViewModel.selectedRecipeId}")
                            }
                        }
                    }
                } else {
                    if (recipeViewModel.selectedRecipeImageIndex != null) {
                        recipeViewModel.setSelectedRecipeImageIndex(null)
                    }
                    if (recipeViewModel.isEditGroceriesScreenActive) {
                        recipeViewModel.setIsEditGroceriesScreenActive(false)
                    }
                    recipeViewModel.resetSelectedRecipeId()
                    val poppingSuccessful = navController.popBackStack(BottomNavItem.Recipes.route, inclusive = false)
                    if (poppingSuccessful) {
                        Log.d("Navigation", "Popped back to recipes")
                    }
                    if (!poppingSuccessful) {
                        Log.d("Navigation", "Recipes not in back stack")
                        navController.navigate(BottomNavItem.Recipes.route) {
                            launchSingleTop = true
                        }
                    }
                }
            },
            icon = { Icon(painterResource(BottomNavItem.Recipes.icon), contentDescription = null) },
            label = { Text(stringResource(BottomNavItem.Recipes.label)) },
            modifier = Modifier.height(80.dp),
        )
    }
}
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationHost(
    navController: NavHostController,
    darkTheme: Boolean,
    currentTheme: ThemeSetting,
    onChangeTheme: (ThemeSetting) -> Unit,
    currentColor: ColorSetting,
    onChangeColor: (ColorSetting) -> Unit,
    language: Languages,
    onChangeLanguage: (Languages) -> Unit,
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit,
    onPickZipFile: () -> Unit,
    onPickJsonFile: () -> Unit,
    onExport: () -> Unit,
    importingFile: String?,
    onCancelImport: () -> Unit,
    onStartDataImport: (ImportSetting) -> Unit,
    onStartJsonImport: () -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    importJsonContent: String?,
    onHandledJsonImport: () -> Unit,
    groceryViewModel: GroceryViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel()
) {
    if (importJsonContent != null) {
        LaunchedEffect(Unit) {
            navController.navigate(BottomNavItem.Groceries.route) {
                launchSingleTop = true
            }
        }
    }
    // Contained in SharedTransitionLayout to enable shared element transitions between destinations
    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            var scheduledDeletionOfCurrentRecipe by remember { mutableStateOf(false) }
            val recipeGridState = rememberLazyGridState()
            val context = LocalContext.current
            val motionScheme = MaterialTheme.motionScheme

            // Start at grocery screen
            NavHost(
                navController,
                startDestination = BottomNavItem.Groceries.route
            ) {
                composable(BottomNavItem.Groceries.route) {
                    GroceryScreen(
                        groceryViewModel = groceryViewModel,
                        onOpenSettings = {
                            navController.navigate(BottomNavItem.Settings.route) {
                                launchSingleTop = true
                            }
                        },
                        getRecipeNameFromId = { id -> recipeViewModel.getRecipeNameFromId(id) },
                        allRecipes = recipeViewModel.recipes,
                        recipeDataLoaded = recipeViewModel.dataLoaded,
//                        onShare = onShare,
                        bottomBar = bottomBar,
                        importJsonContent = importJsonContent,
                        onHandledJsonImport = onHandledJsonImport,
                        onPickJsonFile = onPickJsonFile,
                        onStartJsonImport = onStartJsonImport,
                        importingFile = importingFile,
                        onCancelImport = onCancelImport,
                        getLocationNameFromId = { locationViewModel.getLocationNameFromId(it) },
                        groceryLocations = locationViewModel.groceryLocations,
                        addGroceryLocation = { locationViewModel.addGroceryLocation(it, context) },
                        removeGroceryLocation = { locationViewModel.removeGroceryLocation(it, context) },
                        addGroceryNameToLocation = { name, locationId -> locationViewModel.addGroceryNameToLocation(name, locationId, context) },
                        removeGroceryNameFromAllLocations = { locationViewModel.removeGroceryFromAllLocations(it, context)},
                        changeGroceryLocationName = { newName, locationId -> locationViewModel.changeGroceryLocationName(newName, locationId, context) },
                        reorderGroceryLocations = { fromIndex, toIndex -> locationViewModel.reorderGroceryLocations(fromIndex, toIndex, context) }
                    )
                }
                composable(BottomNavItem.Recipes.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@composable) {
                        RecipeScreen(
                            bottomBar = bottomBar,
                            recipeViewModel = recipeViewModel,
                            recipeGridState = recipeGridState,
                            onClickRecipe = { recipeId, fromSearch ->
                                // When a recipe is clicked, navigate to the specific recipe screen and update the selectedRecipe variable
                                navController.navigate("${BottomNavItem.SpecificRecipe.route}/${recipeId}/${fromSearch}")
                                recipeViewModel.addToRecentRecipes(recipeId)
                                recipeViewModel.setSelectedRecipeId(recipeId, fromSearch)
                            }
                        )
                    }
                }
                // Specific recipe screen
                composable(
                    "${BottomNavItem.SpecificRecipe.route}/{id}/{fromRecipeSearch}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.StringType },
                        navArgument("fromRecipeSearch") { type = NavType.BoolType }
                    ),
                ) { backStackEntry ->
                    val id = UUID.fromString(backStackEntry.arguments?.getString("id"))
                    val recipe = getRecipeFromId(id, recipeViewModel.recipes)

                    val fromRecipeSearch = backStackEntry.arguments?.getBoolean("fromRecipeSearch") ?: false

                    val context = LocalContext.current


                    DisposableEffect(Unit) {
                        onDispose {
                            // Delete recipe if it is scheduled
                            if (scheduledDeletionOfCurrentRecipe) {
                                recipeViewModel.removeRecipe(id, context)
                                scheduledDeletionOfCurrentRecipe = false
                            }
                            val currentRoute = navController.currentDestination?.route
                            if (currentRoute != null && currentRoute != BottomNavItem.Groceries.route && !currentRoute.startsWith(BottomNavItem.SpecificRecipeImage.route) && !currentRoute.startsWith(BottomNavItem.SpecificRecipeEditGroceries.route)) {
                                // Reset selected recipe
                                recipeViewModel.resetSelectedRecipeId()
                            }
                        }
                    }

                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@composable) {
                        SpecificRecipeScreen(
                            recipe = recipe,
                            bottomBar = bottomBar,
                            recipeViewModel = recipeViewModel,
                            addToGroceries = { groceryItems, addingOption, categoryId, recipeId ->
                                groceryViewModel.addToGroceriesFromRecipe(groceryItems, addingOption, categoryId, recipeId, context)
                            },
                            groceryCategories = groceryCategories,
                            onBack = {
                                navController.popBackStack()
                            },
                            onDeleteRecipe = {
                                scheduledDeletionOfCurrentRecipe = true
                                navController.popBackStack()
                            },
                            onDisplayImage = { imageIndex ->
                                // When an image is clicked, navigate to full screen image and update the selectedRecipeImage variable
                                navController.navigate("${BottomNavItem.SpecificRecipeImage.route}/${id}/${imageIndex}")
                                recipeViewModel.setSelectedRecipeImageIndex(imageIndex)
                            },
                            fromRecipeSearch = fromRecipeSearch,
                            onOpenEditGroceriesScreen = {
                                navController.navigate("${BottomNavItem.SpecificRecipeEditGroceries.route}/$id")
                                recipeViewModel.setIsEditGroceriesScreenActive(true)
                            }
                        )
                    }
                }
                // Full screen image viewer
                composable(
                    "${BottomNavItem.SpecificRecipeImage.route}/{id}/{imageIndex}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }, navArgument("imageIndex") { type = NavType.IntType})
                ) { backStackEntry ->
                    val id = UUID.fromString(backStackEntry.arguments?.getString("id"))
                    val recipe = getRecipeFromId(id, recipeViewModel.recipes)
                    val imageIndex = backStackEntry.arguments?.getInt("imageIndex")!!

//                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@composable) {
                    ImageViewer(
                        bottomBar = bottomBar,
                        images = recipe.images,
                        startIndex = imageIndex,
                        onClose = {
                            navController.popBackStack()
                        }
                    )
//                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            if (navController.currentDestination?.route != BottomNavItem.Groceries.route) {
                                // Reset selected recipe image
                                recipeViewModel.setSelectedRecipeImageIndex(null)
                            }
                        }
                    }

                }
                // Edit Groceries on Specific Recipe Screen
                composable(
                    "${BottomNavItem.SpecificRecipeEditGroceries.route}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                    enterTransition = { completeSlideIn(true, motionScheme) },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                    popExitTransition = { completeSlideOut(false, motionScheme) }
                ) { backStackEntry ->
                    val id = UUID.fromString(backStackEntry.arguments?.getString("id"))
                    val recipe = getRecipeFromId(id, recipeViewModel.recipes)

//                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@composable) {
                    SpecificRecipeEditGroceriesScreen(
                        bottomBar = bottomBar,
                        recipe = recipe,
                        onBack = {
                            navController.popBackStack()
                        },
                        allLocations = locationViewModel.groceryLocations,
                        onAddLocation = { newLocationName ->
                            locationViewModel.addGroceryLocation(newLocationName, context)
                        },
                        onRemoveLocation = { locationId ->
                            locationViewModel.removeGroceryLocation(locationId, context)
                        },
                        onChangeLocationName = { newName, locationId ->
                            locationViewModel.changeGroceryLocationName(
                                newName,
                                locationId,
                                context
                            )
                        },
                        onReorderLocations = { fromIndex, toIndex ->
                            locationViewModel.reorderGroceryLocations(
                                fromIndex,
                                toIndex,
                                context
                            )
                        },
                        getLocationNameFromId = { locationId ->
                            locationViewModel.getLocationNameFromId(locationId)
                        },
                        allCategories = groceryCategories,
                        getCategoryNameFromId = { groceryViewModel.getCategoryNameFromId(it) },
                        onChangeRecipeGroceries = {
                            recipeViewModel.changeRecipeGroceries(
                                id,
                                it
                            )
                        },
                        onDispose = {
                            locationViewModel.changeLocationsWithNewGroceries(
                                recipe.groceries,
                                context
                            )
                            recipeViewModel.saveToFile(context)
                        }
                    )
//                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            if (navController.currentDestination?.route != BottomNavItem.Groceries.route) {
                                // Reset selected recipe image
                                recipeViewModel.setIsEditGroceriesScreenActive(false)
                            }
                        }
                    }

                }
                // Settings
                composable(
                    BottomNavItem.Settings.route,
                    popExitTransition = { scaleOut(spring(stiffness = Spring.StiffnessLow), targetScale = 0.9f) + fadeOut()}
                ) {
                    SettingsScreen(
                        bottomBar = bottomBar,
                        darkTheme = darkTheme,
                        themeSetting = currentTheme,
                        onChangeTheme = onChangeTheme,
                        colorSetting = currentColor,
                        onChangeColor = onChangeColor,
                        language = language,
                        onChangeLanguage = onChangeLanguage,
                        onBack = {
                            navController.popBackStack()
                        },
                        onPickFile = onPickZipFile,
                        onExport = onExport,
                        importingFile = importingFile,
                        onCancelImport = onCancelImport,
                        onStartImport = onStartDataImport
                    )
                }
            }
        }
    }
}

