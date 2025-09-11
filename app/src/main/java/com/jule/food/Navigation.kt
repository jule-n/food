package com.jule.food

import android.util.Log
import androidx.activity.BackEventCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jule.food.BottomNavItem.Groceries
import com.jule.food.BottomNavItem.Recipes
import com.jule.food.ui.theme.FoodTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

sealed class BottomNavItem(val route: String, @DrawableRes val icon: Int = 0, @StringRes val label: Int = 0) {
    object Groceries : BottomNavItem("groceries", R.drawable.grocery, R.string.groceries)
    object Recipes : BottomNavItem("recipes", R.drawable.book, R.string.recipes)
    object Settings : BottomNavItem("settings")
    object SpecificRecipe : BottomNavItem("specific_recipe")
    object SpecificRecipeImage : BottomNavItem("specific_recipe_image")
}

// The global navigation bar
@Composable
fun BottomNavigationBar(
    navController: NavController,
    recipeViewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val items = listOf(Groceries, Recipes)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier,
//        containerColor = MaterialTheme.colorScheme.background
    ) {
        val groceriesSelected = currentRoute == Groceries.route
        val recipesSelected = currentRoute == Recipes.route || (
                currentRoute != null && (currentRoute.startsWith(BottomNavItem.SpecificRecipe.route) || currentRoute.startsWith(BottomNavItem.SpecificRecipeImage.route))
            )
        NavigationBarItem(
            selected = groceriesSelected,
            onClick = {
                if (!groceriesSelected) {
                    navController.navigate(Groceries.route) {
                        launchSingleTop = true
//                        launchSingleTop = true
//                        popUpTo(Recipes.route)
                    }
                }
            },
            icon = { Icon(painterResource(Groceries.icon), contentDescription = null) },
            label = { Text(stringResource(Groceries.label)) },
            modifier = Modifier.height(80.dp),
        )
        NavigationBarItem(
            selected = recipesSelected,
            onClick = {
                if (!recipesSelected) {
                    val poppingSuccessful = navController.popBackStack(Recipes.route, inclusive = false)
                    if (poppingSuccessful) {
                        Log.d("Navigation", "Popped back to recipes")
                    }
                    if (!poppingSuccessful) {
                        Log.d("Navigation", "Recipes not in back stack")
                        navController.navigate(Recipes.route) {
                            launchSingleTop = true
//                            launchSingleTop = true
//                            popUpTo(Groceries.route)
                        }
                    }
                    if (recipeViewModel.selectedRecipeId != null) {
                        navController.navigate("${BottomNavItem.SpecificRecipe.route}/${recipeViewModel.selectedRecipeId}/${recipeViewModel.lastSelectedRecipeFromSearch}")
                        if (recipeViewModel.selectedRecipeImageIndex != null) {
                            navController.navigate("${BottomNavItem.SpecificRecipeImage.route}/${recipeViewModel.selectedRecipeId}/${recipeViewModel.selectedRecipeImageIndex}")
                        }
                    }
                }
            },
            icon = { Icon(painterResource(Recipes.icon), contentDescription = null) },
            label = { Text(stringResource(Recipes.label)) },
            modifier = Modifier.height(80.dp),
        )
    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
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
    onPickFile: () -> Unit,
    onExport: () -> Unit,
    importingFile: String?,
    onCancelImport: () -> Unit,
    onStartImport: (ImportSetting) -> Unit,
    addToGroceries: (List<GroceryItem>, categoryId: UUID, recipeId: UUID) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    onDeleteRecipeImage: (UUID, String) -> Unit,
    groceryViewModel: GroceryViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel()
) {
    // Contained in SharedTransitionLayout to enable shared element transitions between destinations
    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            var scheduledDeletionOfCurrentRecipe by remember { mutableStateOf(false) }
            val recipeGridState = rememberLazyGridState()

            // Start at grocery screen
            NavHost(
                navController,
                startDestination = Groceries.route
            ) {
                composable(Groceries.route) {
                    GroceryScreen(
                        groceryViewModel = groceryViewModel,
                        onOpenSettings = {
                            navController.navigate(BottomNavItem.Settings.route) {
                                launchSingleTop = true
                            }
                        },
                        getRecipeNameFromId = { id -> recipeViewModel.getRecipeNameFromId(id) },
                        allRecipes = recipeViewModel.recipes,
                        bottomBar = bottomBar
                    )
                }
                composable(Recipes.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@composable) {
                        RecipeScreen(
                            bottomBar = bottomBar,
                            recipeViewModel = recipeViewModel,
                            recipeGridState = recipeGridState,
                            onOpenSettings = {
                                navController.navigate(BottomNavItem.Settings.route) {
                                    launchSingleTop = true
                                }
                            },
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


                    DisposableEffect(Unit) {
                        onDispose {
                            // Delete recipe if it is scheduled
                            if (scheduledDeletionOfCurrentRecipe) {
                                recipeViewModel.removeRecipe(recipeViewModel.selectedRecipeId!!)
                            }
                            val currentRoute = navController.currentDestination?.route
                            if (currentRoute != null && currentRoute != Groceries.route && !currentRoute.startsWith(BottomNavItem.SpecificRecipeImage.route)) {
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
                            addToGroceries = addToGroceries,
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
                            fromRecipeSearch = fromRecipeSearch
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

                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@composable) {
                        ImageViewer(
                            recipeId = id,
                            bottomBar = bottomBar,
                            images = recipe.images,
                            startIndex = imageIndex,
                            onDeleteRecipeImage = { path -> onDeleteRecipeImage(id, path) },
                            onClose = {
                                navController.popBackStack()
                            }
                        )
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            if (navController.currentDestination?.route != Groceries.route) {
                                // Reset selected recipe image
                                recipeViewModel.setSelectedRecipeImageIndex(null)
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
                        onPickFile = onPickFile,
                        onExport = onExport,
                        importingFile = importingFile,
                        onCancelImport = onCancelImport,
                        onStartImport = onStartImport
                    )
                }
            }
        }
    }
}

