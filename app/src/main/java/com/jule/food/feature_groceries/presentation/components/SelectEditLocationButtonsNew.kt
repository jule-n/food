package com.jule.food.feature_groceries.presentation.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import com.jule.food.R
import com.jule.food.utils.SimpleAddEditBottomSheet
import com.jule.food.data.GroceryLocation
import com.jule.food.feature_locations.domain.GroceryLocationNew
import com.jule.food.feature_locations.domain.GroceryLocationUI
import com.jule.food.others.getLabelFromErrorType
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.utils.DeleteDialog
import com.jule.food.utils.FilledIconButtonWithTooltip
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.utils.SheetErrorMessage
import com.jule.food.utils.conditional
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun SelectEditLocationButtonsNew(
    modifier: Modifier = Modifier,
    allLocations: List<GroceryLocationUI>,
    isEditMode: Boolean,
    onChangeIsEditMode: (Boolean) -> Unit,
    onAddLocation: () -> Unit,
    onLocationNameChanged: (Int, String) -> Unit,
    onDeleteLocationId: (Int) -> Unit,
    onSelectLocationId: (Int?) -> Unit,
    showSelectedLocationId: Boolean,
    selectedLocationId: Int? = null,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit,
    lastLocationFocusRequester: FocusRequester,
    doneEnabled: Boolean
) {
    Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp), modifier = modifier) {
//        SharedTransitionLayout {
//            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
//                AnimatedContent(targetState = editMode, modifier = modifier) { editModeEnabled ->
//                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        if (!isEditMode) {
                            SelectLocationButtonsNew(
                                onEnterEditMode = { onChangeIsEditMode(true) },
                                allLocations = allLocations,
                                selectedLocationId = selectedLocationId,
                                onSelectLocationId = onSelectLocationId,
                                showSelectedLocationId = showSelectedLocationId
                            )
                        } else {
                            EditLocationButtonsNew(
                                onExitEditMode = { onChangeIsEditMode(false) },
                                onLocationNameChanged = onLocationNameChanged,
                                onAddLocation = onAddLocation,
                                onDeleteLocationId = onDeleteLocationId,
                                allLocations = allLocations,
                                onReorderLocations = onReorderLocations,
                                lastLocationFocusRequester = lastLocationFocusRequester,
                                doneEnabled = doneEnabled
                            )
                        }
//                    }
//
//                }
//            }
//        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectLocationButtonsNew(
    modifier: Modifier = Modifier,
    onEnterEditMode: () -> Unit,
    allLocations: List<GroceryLocationUI>,
    showSelectedLocationId: Boolean,
    selectedLocationId: Int?,
    onSelectLocationId: (Int?) -> Unit
) {
    Column(modifier = Modifier.padding(bottom=10.dp, start = 10.dp, end = 10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.padding(start = 10.dp).fillMaxWidth()
        ) {
            Spacer(Modifier.width(48.dp))
            Text(
                text = stringResource(R.string.select_location),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onEnterEditMode) {
                Icon(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = stringResource(R.string.edit),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val noneSelected = showSelectedLocationId && selectedLocationId == null
            val color = if (noneSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)
            Surface(
                shape = RoundedCornerShape(50),
                color = color,
                onClick = { if (!noneSelected) onSelectLocationId(null) },
            ) {
                Text(stringResource(R.string.none), modifier = Modifier.padding(10.dp))
            }
            allLocations.forEach { location ->
                val selected = showSelectedLocationId && location.id == selectedLocationId
                val color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = color,
                    onClick = { if (!selected) onSelectLocationId(location.id) },
                ) {
                    Text(location.currentName, modifier = Modifier.padding(10.dp))
                }
            }
        }
//        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditLocationButtonsNew(
    modifier: Modifier = Modifier,
    onExitEditMode: () -> Unit,
    allLocations: List<GroceryLocationUI>,
    onLocationNameChanged: (Int, String) -> Unit,
    onAddLocation: () -> Unit,
    onDeleteLocationId: (Int) -> Unit,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit,
    doneEnabled: Boolean,
    lastLocationFocusRequester: FocusRequester
) {
    val hapticFeedback = LocalHapticFeedback.current
    val resources = LocalResources.current

    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onReorderLocations(from.index, to.index)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    val topBar = @Composable {
        EditScreenTopBarNew(
            title = stringResource(R.string.edit_locations),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            doneEnabled = doneEnabled,
            onDone = onExitEditMode
        )
    }
    if (allLocations.isEmpty()) {
        Column {
            topBar()
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        stringResource(R.string.no_locations),
                        style = MaterialTheme.typography.displaySmallEmphasized
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "With locations you can group your groceries by their store location",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        Scaffold(
            topBar = topBar
        ) { innerPadding ->
            EditScreenNew<GroceryLocationUI>(
                lazyListState = lazyListState,
                reorderableListState = reorderableListState,
                items = allLocations,
                key = { it.id },
                itemComposable = { index, loc ->
                    val textState = rememberTextFieldState(loc.currentName)
                    LaunchedEffect(textState.text) {
                        onLocationNameChanged(loc.id, textState.text.toString())
                    }
                    var showItems by remember { mutableStateOf(false) }
                    val bottomCornerRadius by animateIntAsState(if (showItems) 0 else 20)
                    EditScreenItemNew(
                        item = loc,
                        textState = textState,
                        itemBackgroundColor = MaterialTheme.colorScheme.secondary,
                        sharedElementModifier = null,
                        isError = { loc, _ -> loc.isError },
                        errorText = { loc, _ ->
                            if (loc.errorType == null) return@EditScreenItemNew null
                            return@EditScreenItemNew getLabelFromErrorType(loc.errorType, resources)
                        },
                        onClickDelete = { onDeleteLocationId(loc.id) },
                        actionButtons = {
                            IconButtonWithTooltip(
                                onClick = { showItems = !showItems },
                                tooltipText = stringResource(R.string.show_associated_items),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.zoom),
                                    contentDescription = stringResource(R.string.show_associated_items)
                                )
                            }
                        },
                        underElement = {
                            AnimatedVisibility(showItems, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                                    shape = RoundedCornerShape(0, 0, 20, 20),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        verticalArrangement = Arrangement.spacedBy(5.dp),
                                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp)
                                    ) {
                                        loc.assignedGroceries.forEach {
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                                                shape = CircleShape
                                            ) {
                                                Text(it, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        textPrefix = {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
                                modifier = Modifier.width(23.dp).height(20.dp).padding(end = 5.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        loc.assignedGroceries.size.toString(),
                                        color = MaterialTheme.colorScheme.background.copy(alpha=0.8f),
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(20, 20, bottomCornerRadius, bottomCornerRadius),
                        modifier = if (index == allLocations.lastIndex) Modifier.focusRequester(
                            lastLocationFocusRequester
                        ) else Modifier
                    )
                },
                newButtonText = stringResource(R.string.new_location),
                onPressNewButton = onAddLocation,
                newButtonBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SelectLocationButtonsNewPreview() {
    FoodTheme {
        Scaffold {
            SelectEditLocationButtonsNew(
                modifier = Modifier.padding(it),
                allLocations = listOf(GroceryLocationUI(
                    currentName = "My Location",
                    id = 1,
                    assignedGroceries = listOf("Tomaten", "Gurke", "Gurken", "Pfirsich", "Ananas", "Rote Bete")
                )),
                onAddLocation = { },
                onReorderLocations = { _, _ -> },
                isEditMode = true,
                onChangeIsEditMode = { },
                onLocationNameChanged = { _, _ -> },
                onDeleteLocationId = { },
                onSelectLocationId = { },
                showSelectedLocationId = false,
                lastLocationFocusRequester = remember { FocusRequester() },
                doneEnabled = true
            )
        }
    }
}