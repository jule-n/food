package com.jule.food

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jule.food.BottomNavItem.Groceries
import com.jule.food.BottomNavItem.Recipes
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID

sealed class BottomNavItem(val route: String, @DrawableRes val icon: Int = 0, @StringRes val label: Int = 0) {
    object Groceries : BottomNavItem("groceries", R.drawable.grocery, R.string.groceries)
    object Recipes : BottomNavItem("recipes", R.drawable.book, R.string.recipes)
    object Settings : BottomNavItem("settings")
    object SpecificRecipe : BottomNavItem("specific_recipe")
    object SpecificRecipeImage : BottomNavItem("specific_recipe_image")
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(Groceries, Recipes)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier,
//        containerColor = MaterialTheme.colorScheme.background
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.route == currentRoute,
                onClick = {
                    if (item.route != currentRoute) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(painterResource(item.icon), contentDescription = null) },
                label = { Text(stringResource(item.label)) },
                modifier = Modifier.height(80.dp),
//                colors = NavigationBarItemDefaults.colors().copy(selectedIconColor = MaterialTheme.colorScheme.primary, selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer)
            )
        }
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
    addToGroceries: (List<GroceryItem>, Int) -> Unit,
    groceryCategories: List<GroceryItemCategory>,
    onDeleteRecipeImage: (UUID, String) -> Unit,
    groceryViewModel: GroceryViewModel = viewModel(),
    recipeViewModel: RecipeViewModel = viewModel()
) {
    val groceriesRecipesEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        fadeIn(spring(stiffness = Spring.StiffnessLow)) + scaleIn(
            spring(
                stiffness = Spring.StiffnessLow
            ), initialScale = 0.9f
        )
    }
    val groceriesRecipesExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
        ExitTransition.None
//        fadeOut(spring(stiffness = Spring.StiffnessLow))
    }
    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            val defaultEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = { fadeIn() }
            NavHost(
                navController,
                startDestination = Groceries.route
            ) {
                composable(
                    Groceries.route,
//                    enterTransition = groceriesRecipesEnterTransition,
//                    exitTransition = groceriesRecipesExitTransition,
                ) {
                    GroceryScreen(
                        darkTheme = darkTheme,
                        groceryViewModel = groceryViewModel,
                        onOpenSettings = {
                            navController.navigate(BottomNavItem.Settings.route) {
                                launchSingleTop = true
                            }
                        },
                        bottomBar = bottomBar,
                        currentTheme = currentTheme,
                        onChangeTheme = onChangeTheme,
                        language = language,
                        onChangeLanguage = onChangeLanguage
                    )
                }
                composable(
                    Recipes.route,
//                    enterTransition = groceriesRecipesEnterTransition,
//                    exitTransition = groceriesRecipesExitTransition,
                ) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@composable) {
                        RecipeScreen(
                            bottomBar = bottomBar,
                            recipeViewModel = recipeViewModel,
                            onOpenSettings = {
                                navController.navigate(BottomNavItem.Settings.route) {
                                    launchSingleTop = true
                                }
                            },
                            onClickRecipe = { navController.navigate("${BottomNavItem.SpecificRecipe.route}/${it}")}
                        )
                    }
                }
                composable(
                    "${BottomNavItem.SpecificRecipe.route}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                    enterTransition = { fadeIn() + slideInHorizontally(initialOffsetX = { it/2 })},
                    popExitTransition = { fadeOut() + slideOutHorizontally(targetOffsetX = { it/2 })},
                    popEnterTransition = { fadeIn() }
//                    popExitTransition = { fadeOut() }
//                    enterTransition = { scaleIn(spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessVeryLow), initialScale = 0.9f) },
//                    popEnterTransition = defaultEnterTransition
                ) { backStackEntry ->
                    val id = UUID.fromString(backStackEntry.arguments?.getString("id"))
                    val recipe = getRecipeFromId(id, recipeViewModel.recipes)

                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this@composable) {
                        SpecificRecipeScreen(
                            recipe = recipe,
                            bottomBar = bottomBar,
                            recipeViewModel = recipeViewModel,
                            addToGroceries = addToGroceries,
                            groceryCategories = groceryCategories,
                            onBack = { navController.popBackStack() },
                            isPop = false,
                            onSelectImage = { imageIndex -> navController.navigate("${BottomNavItem.SpecificRecipeImage.route}/${id}/${imageIndex}") }
                        )
                    }
                }
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
                            images = recipe.images,
                            startIndex = imageIndex,
                            onDeleteRecipeImage = { path -> onDeleteRecipeImage(id, path) },
                            onClose = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
                composable(
                    BottomNavItem.Settings.route,
                    popExitTransition = {
                        scaleOut(spring(stiffness = Spring.StiffnessLow), targetScale = 0.9f) + fadeOut()
                    }
//                    enterTransition = {
//                        slideInHorizontally(
//                            spring(stiffness = Spring.StiffnessLow),
//                            initialOffsetX = { it / 3 })
//                    },
//                    exitTransition = {
//                        slideOutHorizontally(
//                            spring(stiffness = Spring.StiffnessLow),
//                            targetOffsetX = { it / 3 })
//                    },
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

