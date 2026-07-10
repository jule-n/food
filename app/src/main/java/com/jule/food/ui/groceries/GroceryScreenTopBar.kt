package com.jule.food.ui.groceries

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.jule.food.ui.recipes.EditScreenTopBar
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.utils.FilledIconButtonWithTooltip
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.utils.SurfaceWithTooltip
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreenTopBar(
    modifier: Modifier = Modifier,
    selectionModeActive: Boolean,
    selectedCategory: GroceryItemCategory?,
    isEditingCategories: Boolean,
    onOpenSharingDialog: () -> Unit,
    onBackFromCategoryEditing: () -> Unit,
    onPickJsonFile: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectAll: () -> Unit,
    selectedGroceryItemsNumber: Int,
    onClearSelectedGroceryItems: () -> Unit
) {
    var selectedGroceryItemsNumberLocal by remember { mutableIntStateOf(selectedGroceryItemsNumber) }
    LaunchedEffect(selectedGroceryItemsNumber) {
        if (selectedGroceryItemsNumber != 0)
            selectedGroceryItemsNumberLocal = selectedGroceryItemsNumber
    }

    AnimatedContent(
        targetState = selectionModeActive,
        transitionSpec = {
            slideInVertically { -it } togetherWith slideOutVertically { -it }
        },
        modifier = modifier
    ) { selectionMode ->
        if (!selectionMode || selectedCategory == null) {
            AnimatedContent(
                targetState = isEditingCategories
            ) { editingCategories ->
                if (!editingCategories) {
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(R.string.groceries)) },
                        actions = {
                            IconButtonWithTooltip(
                                onClick = onSelectAll,
                                tooltipText = stringResource(R.string.select_all)
                            ) {
                                Icon(painterResource(R.drawable.check_circle), contentDescription = stringResource(R.string.select_all))
                            }
                            var menuExpanded by remember { mutableStateOf(false) }
                            IconButtonWithTooltip(
                                onClick = { menuExpanded = true },
                                tooltipText = stringResource(R.string.more)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.more_vert),
                                    contentDescription = stringResource(R.string.more)
                                )
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                DropdownMenuItem(
                                    text = { Column {
                                        Text(stringResource(R.string.import_groceries))
                                        Text(stringResource(R.string.from_json_file), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                                    } },
                                    leadingIcon = { Icon(painter = painterResource(id = R.drawable.import_data), contentDescription = null) },
                                    onClick = {
                                        onPickJsonFile()
                                        menuExpanded = false
                                    }
                                )
                            }
                        },
                        navigationIcon = {
                            IconButtonWithTooltip(
                                onClick = onOpenSettings,
                                tooltipText = stringResource(R.string.settings)
                            ) {
                                Icon(
                                    Icons.Outlined.Settings,
                                    stringResource(R.string.settings)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                    )
                } else {
                    EditScreenTopBar(
                        title = stringResource(R.string.edit_categories),
                        backgroundColor = MaterialTheme.colorScheme.background,
                        onBack = {
                            onBackFromCategoryEditing()
                        },
                        modifier = Modifier.windowInsetsPadding(insets = WindowInsets.statusBars)
                    )
                }
            }
        } else { // Selection Mode Active
            TopAppBar(
                title = {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape,
                        modifier = Modifier
                            .height(64.dp)
                            .padding(vertical = 12.dp)
                    ) {
                        Row(modifier = Modifier.padding(end = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButtonWithTooltip(
                                onClick = onClearSelectedGroceryItems,
                                tooltipText = stringResource(R.string.clear_selection)
                            ) {
                                Icon(
                                    painterResource(R.drawable.clear),
                                    contentDescription = stringResource(R.string.clear_selection)
                                )
                            }
                            Text(selectedGroceryItemsNumberLocal.toString(), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                },
                actions = {
                    SurfaceWithTooltip(
                        shape = CircleShape,
                        onClick = onOpenSharingDialog,
                        tooltipText = stringResource(R.string.share),
                        modifier = Modifier.height(64.dp).width(64.dp).padding(top = 12.dp, bottom = 12.dp, end = 12.dp)
                    ) {
                        Box {
                            Box(modifier = Modifier.size(24.dp).align(Alignment.Center)) {
                                Icon(
                                    painterResource(R.drawable.share),
                                    contentDescription = stringResource(R.string.share)
                                )
                            }
                        }

                    }
//                    Surface(
//                        color = MaterialTheme.colorScheme.background,
//                        shape = CircleShape,
//                        modifier = Modifier
//                            .height(64.dp).width(64.dp)
//                            .padding(top = 12.dp, bottom = 12.dp, end = 12.dp)
//                    ) {
//                        Box(modifier = Modifier.size(40.dp)) {
//                            FilledIconButtonWithTooltip (
//                                onClick = onOpenSharingDialog,
//                                tooltipText = stringResource(R.string.share),
//                                modifier = Modifier.height(64.dp).width(80.dp).padding(top = 12.dp, bottom = 12.dp, end = 12.dp)
//                            ) {
//                                Icon(
//                                    painterResource(R.drawable.share),
//                                    contentDescription = stringResource(R.string.share)
//                                )
//                            }
//                        }
//                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent)
            )
        }
    }
}