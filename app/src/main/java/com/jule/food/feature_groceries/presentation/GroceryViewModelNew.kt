package com.jule.food.feature_groceries.presentation

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.R
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListNew
import com.jule.food.feature_groceries.domain.MAX_LENGTH_LIST_NAME
import com.jule.food.feature_groceries.domain.use_case.GroceriesUseCases
import com.jule.food.feature_locations.domain.GroceryLocationNew
import com.jule.food.feature_locations.domain.use_case.LocationUseCases
import com.jule.food.others.ErrorType
import com.jule.food.others.SettingsRepository
import com.jule.food.others.SettingsRepository.Companion.SELECTED_LIST_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.listOf
import kotlin.time.Duration.Companion.milliseconds


@OptIn(FlowPreview::class)
@HiltViewModel
class GroceryViewModelNew @Inject constructor(
    private val groceriesUseCases: GroceriesUseCases,
    private val locationsUseCases: LocationUseCases,
    private val settingsRepository: SettingsRepository
): ViewModel() {
    private val _currentState = MutableStateFlow(GroceryScreenState())
    val currentState get() = _currentState.asStateFlow()
    
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var dataLists: List<GroceryListNew> = listOf()

    var getSettingsJob: Job? = null
    var getGroceriesJob: Job? = null
    var observeSelectedListIdJob: Job? = null
    var getListsJob: Job? = null
    var getLocationsJob: Job? = null

    var lastDeletedFinishedItems: List<GroceryItemNew>? = null

    var listUpdateJobs: MutableMap<Int, Job> = mutableMapOf()

    init {
        getSettings()
        getLocations()
        getGroceries()

        viewModelScope.launch {
            snapshotFlow { currentState.value.addSheetNameState.text.toString() }.debounce(500.milliseconds).collectLatest {
                handleAddSheetNameChange(it)
            }
        }
    }

    fun onEvent(event: GroceryScreenEvent) {
        when (event) {
            is GroceryScreenEvent.ChangeIsSelectionModeActive -> {
                _currentState.update { it.copy(
                    isSelectionModeActive = event.value
                )}
                // If selection mode is deactivated, remove focus for the case that the edit bottom sheet was focused
                if (!event.value) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ClearFocus)
                    }
                } else {
                    _currentState.update { it.copy(
                        selectedItemIds = listOf()
                    )}
                }
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ChangeShowEditSheet(event.value))
                }
            }
            is GroceryScreenEvent.AddGrocery -> {
                // Check for error
                if (currentState.value.addSheetNameState.text.isBlank() || currentState.value.selectedListId == null) return

                Log.d("AddGrocery", "Adding new Grocery Item ${currentState.value.addSheetNameState.text}")
                val item = GroceryItemNew(
                    currentState.value.addSheetNameState.text.trim().toString(),
                    currentState.value.addSheetDetailState.text.trim().toString(),
                    listId = currentState.value.selectedListId!!
                )
                viewModelScope.launch {
                    groceriesUseCases.addGroceryItem(item)
                }
                val itemText = currentState.value.addSheetNameState.text.trim().toString()
                if (currentState.value.addSheetSelectedLocationId != null) {
                    val location = currentState.value.locations.fastFirstOrNull { it.id == currentState.value.addSheetSelectedLocationId }?.toGroceryLocation()
                    if (location != null && !location.assignedGroceries.contains(itemText)) {
                        viewModelScope.launch {
                            locationsUseCases.addLocation(
                                location.copy(
                                    assignedGroceries = location.assignedGroceries + itemText
                                )
                            )
                        }
                        Log.d("AddGrocery", "Added to Location ${location.name}")
                    }
                    // Get locations that have this text assigned but aren't the correct location
                    val removeLocations = currentState.value.locations.filter { it.id != currentState.value.addSheetSelectedLocationId && it.assignedGroceries.contains(itemText) }.map { it.toGroceryLocation() }
                    Log.d("AddGrocery", "Removing from locations: $removeLocations")
                    viewModelScope.launch {
                        removeLocations.forEach {
                            // Update database where itemText is removed
                            locationsUseCases.addLocation(it.copy(assignedGroceries = it.assignedGroceries - itemText))
                        }
                    }
                }

                // Empty the text fields
                currentState.value.addSheetNameState.clearText()
                currentState.value.addSheetDetailState.clearText()
            }
            is GroceryScreenEvent.ChangeShowEditListScreen -> {
                _currentState.update { it.copy(
                    showEditListScreen = event.show
                )}
                // If edit list screen is deactivated, remove errors from list text fields
                if (!event.show) {
                    currentState.value.lists.forEach { presList ->
                        if (presList.isNameError) {
                            val dataName = dataLists.fastFirstOrNull { it.id == presList.id }?.name ?: "NULL"
                            presList.nameState.setTextAndPlaceCursorAtEnd(dataName)
                        }
                    }
                }
            }
            is GroceryScreenEvent.ChangeShowSelectLocationDialog -> {
                _currentState.update { it.copy(
                    showSelectLocationDialog = event.show
                )}
            }
            is GroceryScreenEvent.ChangeShowGroupingOptionDialog -> {
                _currentState.update { it.copy(
                    showGroupingOptionDialog = event.show
                )}
            }
            is GroceryScreenEvent.ChangeGroupingOption -> {
                _currentState.update { it.copy(
                    groupingOption = event.value,
                    showGroupingOptionDialog = false
                )}
            }
            is GroceryScreenEvent.ChangeSelectedListId -> {
                viewModelScope.launch {
                    settingsRepository.setSelectedListId(event.value)
                }
            }
            is GroceryScreenEvent.FinishItem -> {
                // Finds item with its id
                val item = currentState.value.activeItemsInCurrentList.fastFirstOrNull { it.id == event.id }
                if (item == null) return
                // Updates isFinised value in Database
                viewModelScope.launch {
                    groceriesUseCases.addGroceryItem(item.toGroceryItem().copy(isFinished = true))
                }
            }
            is GroceryScreenEvent.RestoreFinishedItem -> {
                // Finds item with its id
                val item = currentState.value.finishedItemsInCurrentList.fastFirstOrNull { it.id == event.id }
                if (item == null) return
                // Updates isFinised value in Database
                viewModelScope.launch {
                    groceriesUseCases.addGroceryItem(item.toGroceryItem().copy(isFinished = false))
                }
            }
            is GroceryScreenEvent.ChangeShowFinishedItems -> {
                // Update list with new value of showFinishedItems
                val list = currentState.value.selectedList ?: return
                val newList = list.copy(showFinishedItems = !list.showFinishedItems)
                _currentState.update { it.copy(
                    lists = currentState.value.lists.map {
                        if (it.id == currentState.value.selectedListId ) newList else it
                    },
                    selectedList = newList
                )}
            }
            is GroceryScreenEvent.DeleteFinishedItems -> {
                viewModelScope.launch {
                    lastDeletedFinishedItems = currentState.value.finishedItemsInCurrentList.map { it.toGroceryItem() }
                    groceriesUseCases.deleteGroceryItems(lastDeletedFinishedItems!!)
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        message = R.string.delete_finished_items,
                        action = R.string.undo,
                        onAction = {
                            onEvent(GroceryScreenEvent.RestoreDeletedItems)
                        }
                    ))
                }
            }
            is GroceryScreenEvent.RestoreDeletedItems -> {
                if (lastDeletedFinishedItems == null) return
                viewModelScope.launch {
                    lastDeletedFinishedItems?.forEach {
                        groceriesUseCases.addGroceryItem(it)
                    }
                }
            }
            is GroceryScreenEvent.AddLocation -> {
                viewModelScope.launch {
                    locationsUseCases.addLocation(GroceryLocationNew(
                        name = event.name,
                        assignedGroceries = listOf()
                    ))
                }
            }
            is GroceryScreenEvent.DeleteLocation -> {
                val location = currentState.value.locations.fastFirstOrNull { it.id == event.id }?.toGroceryLocation()
                if (location != null) {
                    viewModelScope.launch {
                        locationsUseCases.deleteLocation(location)
                    }
                }
            }
            is GroceryScreenEvent.ChangeShowAddGrocerySheet -> {
                _currentState.update { it.copy(
                    showAddGrocerySheet = event.show
                )}
            }
            is GroceryScreenEvent.SelectLocationId -> {
                val location = currentState.value.locations.fastFirstOrNull { it.id == event.id } ?: return
                // If the add sheet is active, change add sheet location ID
                if (currentState.value.showAddGrocerySheet) {
                    _currentState.update { it.copy(
                        addSheetSelectedLocationId = event.id,
                        addSheetSelectedLocationName = location.name.text.toString()
                    )}
                    return
                }
                // If the selection mode is active, change the location ID of selected items
                if (currentState.value.isSelectionModeActive) {
                    _currentState.update { it.copy(
                        activeItemsInCurrentList = currentState.value.activeItemsInCurrentList.map {
                            if (currentState.value.selectedItemIds.contains(it.id)) it.copy(locationId = event.id, locationName = location.name.text.toString()) else it
                        }
                    )}
                    val itemNames = currentState.value.selectedItemIds.mapNotNull { selectedItemId ->
                        currentState.value.activeItemsInCurrentList.fastFirstOrNull { it.id == selectedItemId }?.text?.text?.trim()?.toString() ?: null
                    }
                    val newAssignedGroceries = (location.assignedGroceries + itemNames).distinct()
                    // Add all item names to the selected location
                    viewModelScope.launch {
                        locationsUseCases.addLocation(location.toGroceryLocation().copy(assignedGroceries = newAssignedGroceries))
                    }
                    Log.d("SelectLocationId", "Selected Location: $location for items ${itemNames}")
                    // Get all locations that have any of these texts assigned
                    val removeLocations = currentState.value.locations.filter {
                        it.assignedGroceries.any { itemNames.contains(it) } && it.id != event.id
                    }.map { it.toGroceryLocation() }
                    viewModelScope.launch {
                        removeLocations.forEach { loc ->
                            val newAssignedGroceries = loc.assignedGroceries - itemNames.toSet()
                            locationsUseCases.addLocation(loc.copy(assignedGroceries = newAssignedGroceries))
                        }
                    }
                    Log.d("SelectLocationId", "Removed Item names from locations: ${removeLocations.map { it.name }}")
                }
            }
            is GroceryScreenEvent.AddList -> {
                viewModelScope.launch {
                    groceriesUseCases.addGroceryList(GroceryListNew(event.name))
                }
            }
            is GroceryScreenEvent.DeleteList -> {
                val list = currentState.value.lists.fastFirstOrNull { it.id == event.id }?.toGroceryList()
                if (list == null) return
                viewModelScope.launch {
                    groceriesUseCases.deleteGroceryList(list)
                }
            }
            is GroceryScreenEvent.AddItemIdsToSelection -> {
                val newSelectedItems = (currentState.value.selectedItemIds + event.ids).distinct()
                // If there is just one item selected, update the editing item
                if (newSelectedItems.size == 1) {
                    _currentState.update { it.copy(
                        editingItem = currentState.value.activeItemsInCurrentList.fastFirstOrNull { it.id == newSelectedItems[0] }
                    )}
                }
                // Update item ids and set selection mode active to true
                _currentState.update { it.copy(
                    selectedItemIds = newSelectedItems,
                    isSelectionModeActive = true
                )}
            }
            is GroceryScreenEvent.RemoveItemIdsFromSelection -> {
                // If all remaining items should be removed, deactivate selection mode instead of removing the items
                if (currentState.value.selectedItemIds.size == event.ids.size) {
                    _currentState.update { it.copy(
                        isSelectionModeActive = false
                    )}
                    return
                }
                val newSelectedItems = currentState.value.selectedItemIds - event.ids.toSet()
                // If there is just one item selected, update the editing item
                if (newSelectedItems.size == 1) {
                    _currentState.update { it.copy(
                        editingItem = currentState.value.activeItemsInCurrentList.fastFirstOrNull { it.id == newSelectedItems[0] }
                    )}
                }
                // Update selected item ids
                _currentState.update { it.copy(
                    selectedItemIds = newSelectedItems
                )}
            }
            is GroceryScreenEvent.ToggleItemIdSelection -> {
                if (currentState.value.selectedItemIds.contains(event.id)) {
                    onEvent(GroceryScreenEvent.RemoveItemIdsFromSelection(listOf(event.id)))
                } else {
                    onEvent(GroceryScreenEvent.AddItemIdsToSelection(listOf(event.id)))
                }
            }
        }
    }

    fun getSettings() {
        getSettingsJob?.cancel()
        getSettingsJob = settingsRepository.settingsFlow.onEach { settings ->
            Log.d("getSettings", "Settings: $settings")
            _currentState.update { it.copy(
                selectedListId = settings[SELECTED_LIST_ID],
                selectedList = currentState.value.lists.fastFirstOrNull { it.id == settings[SELECTED_LIST_ID] }
            )}
        }.launchIn(viewModelScope)
    }

    fun getLocations() {
        getLocationsJob?.cancel()
        getLocationsJob = locationsUseCases.getAllLocations().onEach { locs ->
            Log.d("getLocations", "Locations: $locs")
            val newGroceryItems = currentState.value.activeItemsInCurrentList.map { item ->
                val location = locs.fastFirstOrNull { it.assignedGroceries.contains(item.text.text.trim().toString()) }
                if (location != null) item.copy(locationId = location.id, locationName = location.name) else item
            }
            val newFinishedGroceryItems = currentState.value.finishedItemsInCurrentList.map { item ->
                val location = locs.fastFirstOrNull { it.assignedGroceries.contains(item.text.text.trim().toString()) }
                if (location != null) item.copy(locationId = location.id, locationName = location.name) else item
            }
            _currentState.update { it.copy(
                locations = locs.map { it.toPresentationLocation() },
                activeItemsInCurrentList = newGroceryItems,
                finishedItemsInCurrentList = newFinishedGroceryItems
            )}
        }.launchIn(viewModelScope)
    }


    @OptIn(FlowPreview::class)
    fun getGroceries() {
        getListsJob?.cancel()
        getListsJob = groceriesUseCases.getAllLists().onEach { lists ->
            Log.d("getLists", "Lists: $lists")
            
            // Reconcile lists to keep TextFieldState
            val currentPresLists = _currentState.value.lists
            val presLists = lists.map { list ->
                currentPresLists.fastFirstOrNull { it.id == list.id } ?: list.toPresentationList()
            }
            dataLists = lists
            
            _currentState.update { it.copy(
                lists = presLists
            )}

            // Add update jobs for each list that doesn't have one yet
            currentState.value.lists.forEach { list ->
                if (listUpdateJobs[list.id] == null) {
                    Log.d("getLists", "Adding update job for ID ${list.id}")
                    listUpdateJobs[list.id] = viewModelScope.launch {
                        snapshotFlow { list.nameState.text }
                        .debounce(500.milliseconds)
                        .distinctUntilChanged()
                        .collect { newName ->
                            handleListNameChange(list.id, newName.toString())
                        }
                    }
                }
            }

            val removeOldIds = listUpdateJobs.keys.filter { id -> !currentState.value.lists.any { id == it.id } }
            removeOldIds.forEach {
                Log.d("getLists", "Removing update job for ID ${it}")
                listUpdateJobs[it]?.cancel()
                listUpdateJobs.remove(it)
            }

            // If there is a selected list ID, select that list
            if (currentState.value.selectedListId != null) {
                _currentState.update { it.copy(
                    selectedList = currentState.value.lists.fastFirstOrNull { it.id == currentState.value.selectedListId }
                )}
                Log.d("getLists", "Selected List ${currentState.value.selectedListId}")
            }
            // If no list corresponds to the selected list ID and there is a first list, select that first list
            if (!lists.any { it.id == currentState.value.selectedListId } && lists.isNotEmpty()) {
                Log.d("getLists", "Setting selected list to first list: ${lists[0].id}")
                settingsRepository.setSelectedListId(lists[0].id!!)
            }
            // If there aren't any lists saved, add one with the name "Default"
            if (lists.isEmpty()) {
                groceriesUseCases.addGroceryList(GroceryListNew("Default"))
            }
        }.launchIn(viewModelScope)

        observeSelectedListIdJob?.cancel()
        observeSelectedListIdJob = currentState.map { it.selectedListId }
            .distinctUntilChanged()
            .onEach { listId ->
                Log.d("getGroceries", "listId has changed to $listId")
                getGroceriesJob?.cancel()
                getGroceriesJob = groceriesUseCases.getGroceriesInList(listId ?: 0).onEach { groceries ->
                    Log.d("getGroceries", "Groceries: $groceries (ListId: $listId)")
                    val presentationItems = groceries.map { groceryItem ->
                        val location = currentState.value.locations.fastFirstOrNull { it.assignedGroceries.contains(groceryItem.text) }
                        val presItem = groceryItem.toPresentationItem().copy(
                            locationId = location?.id,
                            locationName = location?.name?.text?.toString() ?: "No Location"
                        )
                        return@map presItem
                    }
                    _currentState.update { it.copy(
                        activeItemsInCurrentList = presentationItems.filter { !it.isFinished },
                        finishedItemsInCurrentList = presentationItems.filter { it.isFinished },
                        isDataLoaded = true
                    )}
                }.launchIn(viewModelScope)
            }.launchIn(viewModelScope)
    }
    fun handleListNameChange(id: Int, newName: String) {
        val list = currentState.value.lists.fastFirstOrNull { it.id == id } ?: run {
            Log.d("handleListNameChange", "List $id not found")
            return
        }

        Log.d("handleListNameChange", "List $id has new Name $newName")

        // Check for Errors
        val isBlank = newName.isBlank()
        val isTooLong = newName.length > MAX_LENGTH_LIST_NAME
        val isNameSame = (currentState.value.lists - list).any { it.nameState.text.trim().toString() == newName.trim() }
        val isError = isBlank || isTooLong || isNameSame
        // If there is an error, update list with error type
        if (isError) {
            val errorType = if (isBlank) ErrorType.IsEmpty else
                ( if (isTooLong) ErrorType.TooLong(MAX_LENGTH_LIST_NAME) else ErrorType.NameSame )
            val newList = list.copy(
                isNameError = true,
                nameErrorType = errorType
            )
            _currentState.update { it.copy(
                lists = currentState.value.lists.map { if (it.id == id) newList else it }
            )}
            Log.d("handleListNameChange", "List \"${list.nameState.text.toString()}\" ($id) could not be changed to \"$newName\" ($errorType)")
            return
        }
        // If there is no error
        // Update state to reflect that there is no error
        if (list.isNameError) {
            _currentState.update { it.copy(
                lists = currentState.value.lists.map { if (it.id == id) list.copy(isNameError = false) else it}
            )}
        }
        // Convert UI list to Data list and update it in the database
        val dataList = list.toGroceryList()
        viewModelScope.launch {
            groceriesUseCases.addGroceryList(dataList.copy(name = newName))
        }
    }
    fun handleAddSheetNameChange(newName: String) {
        // Check for Locations
        val location = currentState.value.locations.fastFirstOrNull { it.assignedGroceries.contains(newName.trim()) }
        _currentState.update { it.copy(
            addSheetSelectedLocationId = location?.id,
            addSheetSelectedLocationName = location?.name?.text?.toString()
        )}
    }
    sealed class UiEvent {
        data class ShowSnackbar(@StringRes val message: Int, @StringRes val action: Int? = null, val onAction: (() -> Unit)? = null): UiEvent()
        object ClearFocus: UiEvent()
        data class ChangeShowEditSheet(val show: Boolean): UiEvent()
    }
}