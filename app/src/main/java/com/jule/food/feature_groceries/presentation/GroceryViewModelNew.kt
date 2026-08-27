package com.jule.food.feature_groceries.presentation

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jule.food.R
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListNew
import com.jule.food.feature_groceries.domain.use_case.GroceriesUseCases
import com.jule.food.feature_locations.domain.GroceryLocationNew
import com.jule.food.feature_locations.domain.use_case.LocationUseCases
import com.jule.food.others.SettingsRepository
import com.jule.food.others.SettingsRepository.Companion.SELECTED_LIST_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.listOf


@HiltViewModel
class GroceryViewModelNew @Inject constructor(
    private val groceriesUseCases: GroceriesUseCases,
    private val locationsUseCases: LocationUseCases,
    private val settingsRepository: SettingsRepository
): ViewModel() {
    private val _currentState = mutableStateOf(GroceryScreenState())
    val currentState get() = _currentState.value
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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
    }

    fun onEvent(event: GroceryScreenEvent) {
        when (event) {
            is GroceryScreenEvent.ClearSelection -> {
                _currentState.value = currentState.copy(
                    isSelectionModeActive = false
                )
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ClearFocus)
                }
            }
            is GroceryScreenEvent.AddGrocery -> {
                Log.d("AddGrocery", "Adding new Grocery Item ${event.item.text}")
                viewModelScope.launch {
                    groceriesUseCases.addGroceryItem(event.item)
                }
                if (event.locationId != null) {
                    val location = currentState.locations.fastFirstOrNull { it.id == event.locationId }?.toGroceryLocation()
                    if (location != null) {
                        viewModelScope.launch {
                            locationsUseCases.addLocation(
                                location.copy(
                                    assignedGroceries = location.assignedGroceries + event.item.text
                                )
                            )
                        }
                    }
                }
            }
            is GroceryScreenEvent.ChangeShowEditListScreen -> {
                _currentState.value = currentState.copy(
                    showEditListScreen = event.show
                )
            }
            is GroceryScreenEvent.ChangeShowSelectLocationDialog -> {
                _currentState.value = currentState.copy(
                    showSelectLocationDialog = event.show
                )
            }
            is GroceryScreenEvent.ChangeShowGroupingOptionDialog -> {
                _currentState.value = currentState.copy(
                    showGroupingOptionDialog = event.show
                )
            }
            is GroceryScreenEvent.ChangeGroupingOption -> {
                _currentState.value = currentState.copy(
                    groupingOption = event.value,
                    showGroupingOptionDialog = false
                )
            }
            is GroceryScreenEvent.ChangeSelectedListId -> {
                viewModelScope.launch {
                    settingsRepository.setSelectedListId(event.value)
                }
            }
            is GroceryScreenEvent.FinishItem -> {
                // Finds item with its id
                val item = currentState.activeItemsInCurrentList.fastFirstOrNull { it.id == event.id }
                if (item == null) return
                // Removes the item from the active items and adds it to the finished items
                _currentState.value = currentState.copy(
                    activeItemsInCurrentList = currentState.activeItemsInCurrentList - item,
                    finishedItemsInCurrentList = currentState.finishedItemsInCurrentList + item.copy(
                        isFinished = true
                    )
                )
            }
            is GroceryScreenEvent.RestoreFinishedItem -> {
                // Finds item with its id
                val item = currentState.finishedItemsInCurrentList.fastFirstOrNull { it.id == event.id }
                if (item == null) return
                // Removes the item from the finished items and adds it to the active items
                _currentState.value = currentState.copy(
                    activeItemsInCurrentList = currentState.activeItemsInCurrentList + item.copy(
                        isFinished = false
                    ),
                    finishedItemsInCurrentList = currentState.finishedItemsInCurrentList - item
                )
            }
            is GroceryScreenEvent.ChangeShowFinishedItems -> {
                currentState.selectedList?.showDeletedItems?.value = event.show
            }
            is GroceryScreenEvent.DeleteFinishedItems -> {
                viewModelScope.launch {
                    lastDeletedFinishedItems = currentState.finishedItemsInCurrentList.map { it.toGroceryItem() }
                    lastDeletedFinishedItems?.forEach {
                        groceriesUseCases.deleteGroceryItem(it)
                    }
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
                val location = currentState.locations.fastFirstOrNull { it.id == event.id }?.toGroceryLocation()
                if (location != null) {
                    viewModelScope.launch {
                        locationsUseCases.deleteLocation(location)
                    }
                }
            }
            is GroceryScreenEvent.ChangeShowAddGrocerySheet -> {
                _currentState.value = currentState.copy(
                    showAddGrocerySheet = event.show
                )
            }
            is GroceryScreenEvent.ChangeAddSheetSelectedLocationId -> {
                _currentState.value = currentState.copy(
                    addSheetSelectedLocationId = event.id,
                    addSheetSelectedLocationName = currentState.locations.fastFirstOrNull { it.id == event.id }?.name?.text.toString()
                )
            }
            is GroceryScreenEvent.AddList -> {
                viewModelScope.launch {
                    groceriesUseCases.addGroceryList(GroceryListNew(event.name))
                }
            }
            is GroceryScreenEvent.DeleteList -> {
                val list = currentState.lists.fastFirstOrNull { it.id == event.id }?.toGroceryList()
                if (list == null) return
                viewModelScope.launch {
                    groceriesUseCases.deleteGroceryList(list)
                }
            }
        }
    }

    fun getSettings() {
        getSettingsJob?.cancel()
        getSettingsJob = settingsRepository.settingsFlow.onEach { settings ->
            Log.d("getSettings", "Settings: $settings")
            _currentState.value = currentState.copy(
                selectedListId = settings[SELECTED_LIST_ID],
                selectedList = currentState.lists.fastFirstOrNull { it.id == settings[SELECTED_LIST_ID] }
            )
        }.launchIn(viewModelScope)
    }

    fun getLocations() {
        getLocationsJob?.cancel()
        getLocationsJob = locationsUseCases.getAllLocations().onEach { locs ->
            Log.d("getLocations", "Locations: $locs")
            _currentState.value = currentState.copy(
                locations = locs.map { it.toPresentationLocation() }
            )
        }.launchIn(viewModelScope)
    }


    fun getGroceries() {
        getListsJob?.cancel()
        getListsJob = groceriesUseCases.getAllLists().onEach { lists ->
            Log.d("getLists", "Lists: $lists")
            // Create UI Lists from data lists and update current state
            val presLists = lists.map { it.toPresentationList() }
            _currentState.value = currentState.copy(
                lists = presLists
            )

            // Add update jobs for each list and cancel old ones
            presLists.forEach { list ->
                listUpdateJobs[list.id]?.cancel()
                listUpdateJobs[list.id] = viewModelScope.launch {
                    snapshotFlow { list.text.text.toString() }.distinctUntilChanged { newName ->
                        handleListNameChange(list.id, newName)
                    }
                }
            }
            // If there is a selected list ID, select that list
            if (currentState.selectedListId != null) {
                _currentState.value = currentState.copy(
                    selectedList = currentState.lists.fastFirstOrNull { it.id == currentState.selectedListId }
                )
            }
            // If no list corresponds to the selected list ID and there is a first list, select that first list
            if (!lists.any { it.id == currentState.selectedListId } && lists.isNotEmpty()) {
                Log.d("getLists", "Setting selected list to first list: ${lists[0].id}")
                settingsRepository.setSelectedListId(lists[0].id!!)
            }
            // If there aren't any lists saved, add one with the name "Default"
            if (lists.isEmpty()) {
                groceriesUseCases.addGroceryList(GroceryListNew("Default"))
            }
        }.launchIn(viewModelScope)

        observeSelectedListIdJob?.cancel()
        observeSelectedListIdJob = snapshotFlow { currentState.selectedListId }
            .distinctUntilChanged()
            .onEach { listId ->
                getGroceriesJob?.cancel()
                getGroceriesJob = groceriesUseCases.getGroceriesInList(listId ?: 0).onEach { groceries ->
                    Log.d("getGroceries", "Groceries: $groceries")
                    val presentationItems = groceries.map { groceryItem ->
                        val location = currentState.locations.fastFirstOrNull { it.assignedGroceries.contains(groceryItem.text) }
                        val presItem = groceryItem.toPresentationItem().copy(
                            locationId = location?.id,
                            locationName = location?.name?.text.toString()
                        )
                        return@map presItem
                    }
                    _currentState.value = currentState.copy(
                        activeItemsInCurrentList = presentationItems.filter { !it.isFinished },
                        finishedItemsInCurrentList = presentationItems.filter { it.isFinished },
                        isDataLoaded = true
                    )
                }.launchIn(viewModelScope)
            }.launchIn(viewModelScope)
    }
    fun handleListNameChange(id: Int, newName: String) {
        // TODO: Check for errors
        val list = currentState.lists.fastFirstOrNull { it.id == id }
        if (list == null) return

        Log.d("handleListNameChange", "Changed name of List \"${list.text.text.toString()}\" ($id) to $newName")
        val dataList = list.toGroceryList()
        viewModelScope.launch {
            groceriesUseCases.addGroceryList(dataList.copy(name = newName))
        }
    }
    sealed class UiEvent {
        data class ShowSnackbar(@StringRes val message: Int, @StringRes val action: Int? = null, val onAction: (() -> Unit)? = null): UiEvent()
        object ClearFocus: UiEvent()
    }
}