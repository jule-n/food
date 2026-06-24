package com.jule.food.ui.groceries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.data.GroceryItemCategory
import com.jule.food.ui.recipes.LocalNavAnimatedVisibilityScope
import com.jule.food.ui.recipes.LocalSharedTransitionScope
import com.jule.food.R
import com.jule.food.data.groceryGroupingOptionsDisplay
import com.jule.food.data.groceryGroupingOptionsIcons
import com.jule.food.ui.theme.FoodTheme
import java.util.UUID


@Composable
fun GroceryScreenTop(
    modifier: Modifier = Modifier,
    allCategories: List<GroceryItemCategory>,
    category: GroceryItemCategory,
    onChangeSelectedCategoryId: (UUID) -> Unit,
    onChangeIsEditingCategories: (Boolean) -> Unit,
    onChangeShowGroupingSheet: () -> Unit,
    groupingOption: GroceryGroupingOption,
) {
    Column {
        CategoriesConnectedButtonsCustom(
            allCategories = allCategories,
            selectedCategoryId = category.id,
            onChangeSelectedCategoryId = onChangeSelectedCategoryId,
            onEnableEditMode = { onChangeIsEditingCategories(true) },
            modifier = modifier
        )
        Spacer(Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            val isGrouped = groupingOption != GroceryGroupingOption.None
            val height by animateDpAsState(if (isGrouped) 50.dp else 30.dp)
            Surface(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(20.dp),
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(10),
                modifier = Modifier.animateContentSize().padding(end = 5.dp, bottom = 5.dp).height(height),
                onClick = onChangeShowGroupingSheet
            ) {
                if (isGrouped) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp), horizontalAlignment = Alignment.Start, modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 5.dp, top = 5.dp)) {
                        Text(stringResource(R.string.group_by), style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(groceryGroupingOptionsIcons[groupingOption]!!), contentDescription = null, modifier = Modifier.size(20.dp))
                            Text(stringResource(groceryGroupingOptionsDisplay[groupingOption]!!))
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 5.dp, top = 5.dp)) {
                        Text(stringResource(R.string.group_groceries), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GroceryScreenTopPreview() {
    val categories = remember { mutableStateListOf(
        GroceryItemCategory("Lebensmittel"),
        GroceryItemCategory("Drogerie"),
        GroceryItemCategory("Heute"),
        GroceryItemCategory("Deine Oma")
    )}
    var selectedCategoryId by remember { mutableStateOf(categories.first().id) }



    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            FoodTheme {
                    AnimatedVisibility(true) {

                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            Column {

                                GroceryScreenTop(
                                    allCategories = categories,
                                    category = categories[0],
                                    onChangeSelectedCategoryId = { },
                                    onChangeIsEditingCategories = { },
                                    onChangeShowGroupingSheet = { },
                                    groupingOption = GroceryGroupingOption.None
                                )
                                GroceryScreenTop(
                                    allCategories = categories,
                                    category = categories[0],
                                    onChangeSelectedCategoryId = { },
                                    onChangeIsEditingCategories = { },
                                    onChangeShowGroupingSheet = { },
                                    groupingOption = GroceryGroupingOption.Recipe
                                )
                            }
                        }
                    }
            }
        }
    }
}