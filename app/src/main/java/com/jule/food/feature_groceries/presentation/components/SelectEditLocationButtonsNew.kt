package com.jule.food.feature_groceries.presentation.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.jule.food.feature_locations.domain.GroceryLocationPresentation
import com.jule.food.ui.theme.FoodTheme
import com.jule.food.utils.BasicTextFieldWithBox
import com.jule.food.utils.DeleteDialog
import com.jule.food.utils.FilledIconButtonWithTooltip
import com.jule.food.utils.IconButtonWithTooltip
import com.jule.food.utils.SheetErrorMessage
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun SelectEditLocationButtonsNew(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    allLocations: List<GroceryLocationPresentation>,
    onAddLocation: (String) -> Unit,
    onRemoveLocationId: (Int) -> Unit,
    onSelectLocationId: (Int) -> Unit,
    selectedLocationId: Int? = null,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit
) {

    var editMode by remember { mutableStateOf(allLocations.isEmpty()) }

    BackHandler(editMode && allLocations.isNotEmpty()) {
        editMode = false
    }

    Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp), modifier = modifier) {
//        SharedTransitionLayout {
//            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
//                AnimatedContent(targetState = editMode, modifier = modifier) { editModeEnabled ->
//                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        if (!editMode) {
                            SelectLocationButtonsNew(
                                onCancel = onCancel,
                                onEnterEditMode = { editMode = true },
                                allLocations = allLocations,
                                selectedLocationId = selectedLocationId,
                                onSelectLocationId = onSelectLocationId
                            )
                        } else {
                            EditLocationButtonsNew(
                                onExitEditMode = {
                                    if (allLocations.isNotEmpty())
                                        editMode = false
                                    else
                                        onCancel()
                                },
                                onAddLocation = onAddLocation,
                                onRemoveLocationId = onRemoveLocationId,
                                allLocations = allLocations,
                                onReorderLocations = onReorderLocations
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
    onCancel: () -> Unit,
    onEnterEditMode: () -> Unit,
    allLocations: List<GroceryLocationPresentation>,
    selectedLocationId: Int?,
    onSelectLocationId: (Int) -> Unit
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
            allLocations.forEach { location ->
                val selected = location.id == selectedLocationId
                val color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = color,
                    onClick = { if (!selected) onSelectLocationId(location.id!!) },
                ) {
                    Text(location.name.text.toString(), modifier = Modifier.padding(10.dp))
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
    allLocations: List<GroceryLocationPresentation>,
    onAddLocation: (String) -> Unit,
    onRemoveLocationId: (Int) -> Unit,
    onReorderLocations: (fromIndex: Int, toIndex: Int) -> Unit
) {
    var showAddLocationSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val resources = LocalResources.current
    val hapticFeedback = LocalHapticFeedback.current

    val focusManager = LocalFocusManager.current

    var locationIdToDelete: Int? by remember { mutableStateOf(null) }
    var showConfirmDeleteLocationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusManager.clearFocus(true)
    }

//    with (LocalSharedTransitionScope.current!!) {
//        Dialog(
//            onDismissRequest = onExitEditMode,
//            properties = DialogProperties(usePlatformDefaultWidth = false)
//        ) {
        Column(modifier = modifier.fillMaxSize()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                focusManager.clearFocus(true)
            }
        ) {
            Row(
                modifier = Modifier.height(64.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButtonWithTooltip(
                    onClick = onExitEditMode,
                    tooltipText = stringResource(R.string.back)
                ) {
                    Icon(
                        painterResource(R.drawable.arrow_left),
                        contentDescription = stringResource(R.string.back)
                    )
                }
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                    Text(
                        stringResource(R.string.edit_locations),
                        modifier = Modifier.padding(horizontal = 40.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Box(modifier = Modifier.width(IconButtonDefaults.mediumIconSize))
            }
            Spacer(Modifier.height(10.dp))

            val lazyListState = rememberLazyListState()
            val reorderableListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                onReorderLocations(from.index, to.index)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }


            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(15.dp),
                userScrollEnabled = true,
                contentPadding = PaddingValues(bottom = 200.dp)
            ) {
                if (allLocations.isEmpty()) {
                    item {
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
                }
                items(allLocations, key = { it.id!! }) { location ->
                    val textState = location.name
//                    val isNameEmpty = textState.text.isEmpty()
//                    val isNameTooLong = textState.text.trim().toString().length > 20
//                    val isNameSame = allLocations.filter { it.id != location.id }
//                        .any { it.name == textState.text.trim().toString() }
//
                    var showItems by remember { mutableStateOf(false) }
//
//                    DisposableEffect(Unit) {
//                        onDispose {
//                            if (!isNameEmpty && !isNameTooLong && !isNameSame && location.name != textState.text.trim()
//                                    .toString()
//                            )
//                                onChangeLocationName(textState.text.trim().toString(), location.id)
//
//                        }
//                    }
                    ReorderableItem(
                        state = reorderableListState,
                        key = location.id!!
                    ) {
                        Column(modifier = Modifier.animateItem()) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(20),
                                modifier = Modifier
                                    .height(48.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxSize().padding(start = 10.dp)
                                ) {
                                    BasicTextFieldWithBox(
                                        state = textState,
                                        lineLimits = TextFieldLineLimits.SingleLine,
                                        textStyle = ButtonDefaults.textStyleFor(40.dp),
                                        textColor = MaterialTheme.colorScheme.onTertiary,
                                        modifier = Modifier.weight(1f)
//                                            .onFocusChanged { focusState ->
//                                                if (!focusState.isFocused && (isNameEmpty || isNameTooLong)) {
//                                                    textState.setTextAndPlaceCursorAtEnd(location.name)
//                                                }
//                                            }
                                    )
                                    IconButtonWithTooltip(
                                        onClick = { showItems = !showItems },
                                        tooltipText = stringResource(R.string.show_associated_items),
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.zoom),
                                            contentDescription = stringResource(R.string.show_associated_items)
                                        )
                                    }
                                    IconButtonWithTooltip(
                                        onClick = {},
                                        tooltipText = stringResource(R.string.reorder_locations),
                                        modifier = Modifier.draggableHandle(
                                            onDragStarted = {
                                                hapticFeedback.performHapticFeedback(
                                                    HapticFeedbackType.GestureThresholdActivate
                                                )
                                            },
                                            onDragStopped = {
                                                hapticFeedback.performHapticFeedback(
                                                    HapticFeedbackType.GestureEnd
                                                )
                                            }
                                        )
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.drag_handle),
                                            contentDescription = stringResource(R.string.reorder_locations)
                                        )
                                    }
                                    FilledIconButtonWithTooltip(
                                        onClick = {
                                            locationIdToDelete = location.id
                                            showConfirmDeleteLocationDialog = true
                                        },
                                        tooltipText = stringResource(R.string.delete),
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.onError,
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.delete),
                                            contentDescription = stringResource(R.string.delete)
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(showItems) {
                                Text(location.assignedGroceries.joinToString(separator = ", "))
                            }

//                            SheetErrorMessage(
//                                isError = isNameEmpty || isNameTooLong || isNameSame,
//                                message = if (isNameTooLong) stringResource(
//                                    R.string.name_too_long,
//                                    20
//                                ) else
//                                    if (isNameEmpty) stringResource(R.string.name_empty) else
//                                        if (isNameSame) stringResource(R.string.name_already_exists) else ""
//                            )
                        }
                    }
                }
                item(key="AddLocationButton") {
                    Button(
                        onClick = { showAddLocationSheet = true },
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                        modifier = Modifier.padding(start = 10.dp, top = 20.dp).animateItem()
                    ) {
                        Icon(painterResource(R.drawable.add), contentDescription = null)
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.add_location))
                    }
                }
//            }
            }
//            Spacer(Modifier.height(20.dp))
        }

//    }
    if (showAddLocationSheet) {
        SimpleAddEditBottomSheet(
            onDismissRequest = { showAddLocationSheet = false },
            onConfirm = {
                onAddLocation(it)
                showAddLocationSheet = false
            },
            placeholderText = stringResource(R.string.new_location),
            nameTooLongLimit = 20
        )
    }
    if (showConfirmDeleteLocationDialog && locationIdToDelete != null) {
        val deletedCategoryName = allLocations.fastFirstOrNull { it.id == locationIdToDelete }?.name ?: ""
        DeleteDialog(
            title = stringResource(R.string.delete_location),
            onDismissRequest = {
                showConfirmDeleteLocationDialog = false
                locationIdToDelete = null
            },
            onConfirm = {
                Toast.makeText(
                    context,
                    resources.getString(R.string.deleted_category_name, deletedCategoryName),
                    Toast.LENGTH_SHORT
                ).show()

                onRemoveLocationId(locationIdToDelete!!)
                showConfirmDeleteLocationDialog = false
                locationIdToDelete = null
            }
        ) {
            Text(
                stringResource(
                    R.string.are_you_sure_you_want_to_delete_category_name,
                    deletedCategoryName
                )
            )
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun SelectLocationButtonsPreview() {
//    FoodTheme {
//        Scaffold {
//            SelectEditLocationButtons(
//                onCancel = {},
//                modifier = Modifier.padding(it),
//                allLocations = listOf(GroceryLocation("Location 1"), GroceryLocation("Location 2")),
//                onAddLocation = { },
//                onSelectLocation = { },
//                onRemoveLocation = { },
//                onChangeLocationName = { _, _ -> },
//                onReorderLocations = { _, _ -> }
//            )
//        }
//    }
//}