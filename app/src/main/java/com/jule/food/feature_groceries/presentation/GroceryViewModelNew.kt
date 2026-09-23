package com.jule.food.feature_groceries.presentation

import android.util.Log
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jule.food.data.GroceryGroupingOption
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.GroceryListNew
import com.jule.food.feature_groceries.domain.GroceryListUI
import com.jule.food.feature_groceries.domain.use_case.GroceriesUseCases
import com.jule.food.feature_locations.domain.GroceryLocationNew
import com.jule.food.feature_locations.domain.use_case.LocationUseCases
import com.jule.food.others.SettingsRepository
import com.jule.food.others.SettingsRepository.Companion.SELECTED_LIST_ID
import com.jule.food.others.SettingsRepository.Companion.GROCERY_GROUPING_OPTION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
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

    var getSettingsJob: Job? = null
    var getGroceriesJob: Job? = null
    var observeSelectedListIdJob: Job? = null
    var getListsJob: Job? = null
    var getLocationsJob: Job? = null

    var lastDeletedFinishedItems: List<GroceryItemNew>? = null

    val itemReducer = GroceryItemReducer()
    val listReducer = GroceryListReducer()
    val locationReducer = GroceryLocationReducer()

    var deletedListIds: MutableList<Int> = mutableListOf()
    var deletedLocationIds: MutableList<Int> = mutableListOf()

    init {
        getSettings()
        getLocations()
        getGroceries()

        viewModelScope.launch {
            snapshotFlow { currentState.value.addSheetNameState.text.toString() }.debounce(200.milliseconds).collectLatest {
                handleAddSheetNameChange(it)
            }
        }
    }

    fun onEvent(event: GroceryScreenEvent) {
        handleSideEffects(event)

        val newState = when (event) {
                is GroceryScreenEvent.ItemEvent -> {
                    itemReducer.reduce(currentState.value, event)
                }

                is GroceryScreenEvent.ListEvent -> {
                    listReducer.reduce(currentState.value, event)

                }
                is GroceryScreenEvent.LocationEvent -> {
                    locationReducer.reduce(currentState.value, event)
                }
            }
        if (currentState.value !== newState) {
            _currentState.update { newState }
        }
    }

    fun handleSideEffects(event: GroceryScreenEvent) {
        when (event) {
            is GroceryScreenEvent.ItemEvent.ChangeIsSelectionModeActive -> {
                // Clear focus if selection mode is deactivated if bottom sheet was focused
                if (event.value == false) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ClearFocus)
                    }
                }
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ChangeShowEditSheet(event.value))
                }
            }
            is GroceryScreenEvent.ItemEvent.AddGrocery -> {
                val name = currentState.value.addSheetNameState.text.trim().toString()
                if (name.isBlank()) return

                Log.d("AddGrocery", "Adding new Grocery Item \"${name}\"")
                val item = GroceryItemNew(
                    name,
                    currentState.value.addSheetDetailState.text.trim().toString(),
                    listId = currentState.value.selectedListId!!
                )
                viewModelScope.launch {
                    groceriesUseCases.addGroceryItem(item)
                    locationsUseCases.addItemNameToLocation(
                        name,
                        currentState.value.addSheetSelectedLocationId,
                        currentState.value.locations.mapIndexed { index, loc -> loc.toGroceryLocation(index) }
                    )
                }
                currentState.value.addSheetNameState.clearText()
                currentState.value.addSheetDetailState.clearText()
            }
            is GroceryScreenEvent.ItemEvent.FinishItem -> {
                // Finds item with its id
                val item = currentState.value.activeItemsInCurrentList.fastFirstOrNull { it.id == event.id } ?: return
                // Updates isFinished value in Database
                viewModelScope.launch {
                    groceriesUseCases.addGroceryItem(item.toGroceryItem().copy(isFinished = true))
                }
            }
            is GroceryScreenEvent.ItemEvent.RestoreFinishedItem -> {
                // Finds item with its id
                val item = currentState.value.finishedItemsInCurrentList.fastFirstOrNull { it.id == event.id } ?: return
                // Updates isFinished value in Database
                viewModelScope.launch {
                    groceriesUseCases.addGroceryItem(item.toGroceryItem().copy(isFinished = false))
                }
            }
            is GroceryScreenEvent.ItemEvent.DeleteFinishedItems -> {
                viewModelScope.launch {
                    lastDeletedFinishedItems = currentState.value.finishedItemsInCurrentList.map { it.toGroceryItem() }
                    groceriesUseCases.deleteGroceryItems(lastDeletedFinishedItems!!)
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        messageType = UiEvent.SnackbarMessageType.DeletedNFinishedItems,
                        extraArgs = listOf(lastDeletedFinishedItems?.size?.toString() ?: "0"),
                        onAction = {
                            onEvent(GroceryScreenEvent.ItemEvent.RestoreDeletedItems)
                        }
                    ))
                }
            }
            is GroceryScreenEvent.ItemEvent.RestoreDeletedItems -> {
                if (lastDeletedFinishedItems == null) return
                viewModelScope.launch {
                    lastDeletedFinishedItems?.forEach {
                        groceriesUseCases.addGroceryItem(it)
                    }
                }
            }

            is GroceryScreenEvent.ItemEvent.ChangeGroupingOption -> {
                if (currentState.value.groupingOption == event.value || !currentState.value.showGroupingOptionDialog) return
                viewModelScope.launch {
                    settingsRepository.setGroceryGroupingOption(event.value)
                }
            }

            is GroceryScreenEvent.ListEvent.ChangeSelectedListId -> {
                if (currentState.value.selectedListId == event.value) return
                viewModelScope.launch {
                    settingsRepository.setSelectedListId(event.value)
                }
            }

            is GroceryScreenEvent.ListEvent.AddList -> {
                viewModelScope.launch {
                    delay(100.milliseconds)
                    _eventFlow.emit(UiEvent.FocusLastList)
                }
            }
            is GroceryScreenEvent.ListEvent.DeleteList -> {
                deletedListIds.add(event.id)
            }
            is GroceryScreenEvent.ListEvent.OpenEditListScreen -> {
                if (currentState.value.isSelectionModeActive) {
                    viewModelScope.launch {
                        _eventFlow.emit(UiEvent.ChangeShowEditSheet(false))
                    }
                }
            }
            is GroceryScreenEvent.ListEvent.DoneEditListScreen -> {
                // If there is an error in any list, don't go away from edit screen
                if (currentState.value.lists.any { it.isNameError }) return
                // Save data from lists to database
                viewModelScope.launch {
                    groceriesUseCases.addGroceryLists(currentState.value.lists)
                    groceriesUseCases.deleteGroceryLists(deletedListIds)
                    deletedListIds.clear()
                }
            }
            is GroceryScreenEvent.ListEvent.SelectListInDialog -> {
                // Move selected items to other list
                if (!currentState.value.isSelectionModeActive ||
                    currentState.value.selectedItemIds.isEmpty() ||
                    !currentState.value.showSelectListDialog) return

                val newList = currentState.value.lists.fastFirstOrNull { it.id == event.id } ?: return
                val selectedItems = currentState.value.activeItemsInCurrentList.filter { currentState.value.selectedItemIds.contains(it.id) }
                viewModelScope.launch {
                    groceriesUseCases.addGroceryItems(selectedItems.map { it.toGroceryItem().copy(listId = event.id) })
                    _eventFlow.emit(UiEvent.ShowToast(
                        UiEvent.ToastMessageType.MovedNItemsToList,
                        extraArgs = listOf(currentState.value.selectedItemIds.size.toString(), newList.currentName)
                    ))
                    _eventFlow.emit(UiEvent.ChangeShowEditSheet(false))
                }
            }

            is GroceryScreenEvent.LocationEvent.DoneEditLocationDialog -> {
                // If there is an error in any location, don't go away from edit screen
                if (currentState.value.locations.any { it.isError }) return
                // Save data from locations to database
                viewModelScope.launch {
                    locationsUseCases.addLocations(currentState.value.locations)
                    locationsUseCases.deleteLocations(deletedLocationIds)
                    deletedLocationIds.clear()
                }
            }
            is GroceryScreenEvent.LocationEvent.AddLocation -> {
                viewModelScope.launch {
                    delay(100.milliseconds)
                    _eventFlow.emit(UiEvent.FocusLastLocation)
                }
            }
            is GroceryScreenEvent.LocationEvent.DeleteLocation -> {
                deletedLocationIds.add(event.id)
            }
            is GroceryScreenEvent.LocationEvent.SelectLocationId -> {
                // If the add sheet is active, change add sheet location ID (state change, so it is in reducer)
                // If the selection mode is active, change the location ID of selected items
                if (!currentState.value.isSelectionModeActive || currentState.value.showAddGrocerySheet) return

                val itemNames = currentState.value.selectedItemIds.mapNotNull { selectedItemId ->
                    currentState.value.activeItemsInCurrentList.fastFirstOrNull { it.id == selectedItemId }?.text?.text?.trim()?.toString()
                }
                viewModelScope.launch {
                    locationsUseCases.addItemNamesToLocation(itemNames, event.id, currentState.value.locations.mapIndexed { index, loc -> loc.toGroceryLocation(index) })
                }
            }
            else -> { }
        }
    }

    fun getSettings() {
        getSettingsJob?.cancel()
        getSettingsJob = settingsRepository.settingsFlow.onEach { settings ->
            Log.d("getSettings", "Settings: $settings")
            _currentState.update { it.copy(
                selectedListId = settings[SELECTED_LIST_ID],
                selectedList = currentState.value.lists.fastFirstOrNull { it.id == settings[SELECTED_LIST_ID] },
                groupingOption = GroceryGroupingOption.entries[settings[GROCERY_GROUPING_OPTION] ?: 0]
            )}
        }.launchIn(viewModelScope)
    }

    fun getLocations() {
        getLocationsJob?.cancel()
        getLocationsJob = locationsUseCases.getAllLocations().onEach { locs ->
            Log.d("getLocations", "Locations: $locs")
            val newGroceryItems = currentState.value.activeItemsInCurrentList.map { item ->
                val location = locs.fastFirstOrNull { it.assignedGroceries.contains(item.text.text.trim().toString()) }
                if (location != null) item.copy(locationId = location.id, locationName = location.name) else item.copy(locationId = null)
            }
            val newFinishedGroceryItems = currentState.value.finishedItemsInCurrentList.map { item ->
                val location = locs.fastFirstOrNull { it.assignedGroceries.contains(item.text.text.trim().toString()) }
                if (location != null) item.copy(locationId = location.id, locationName = location.name) else item.copy(locationId = null)
            }
            _currentState.update { it.copy(
                locations = locs.map { it.toUILocation() },
                activeItemsInCurrentList = newGroceryItems,
                finishedItemsInCurrentList = newFinishedGroceryItems
            )}
        }.launchIn(viewModelScope)
    }


    @OptIn(FlowPreview::class)
    fun getGroceries() {
        getListsJob?.cancel()
        getListsJob = groceriesUseCases.getAllLists().onEach { lists ->
            Log.d("getLists", "There are ${lists.size} lists: ${lists.map { "${it.name} (${it.id})" }.fastJoinToString(",")}")
            
            // Reconcile lists to keep gridstate etc
            val currentPresLists = _currentState.value.lists
            val presLists = lists.map { list ->
                // If the list already exists, take the existing one
                currentPresLists.fastFirstOrNull { it.id == list.id } ?: list.toPresentationList()
            }
            
            _currentState.update { it.copy(
                lists = presLists
            )}

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
                groceriesUseCases.addGroceryList(GroceryListNew("Default", sortOrder = 0))
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
                            locationName = location?.currentName ?: ""
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
    fun handleAddSheetNameChange(newName: String) {
        // Check for Locations
        val location = currentState.value.locations.fastFirstOrNull { it.assignedGroceries.contains(newName.trim()) }
        _currentState.update { it.copy(
            addSheetSelectedLocationId = location?.id,
            addSheetSelectedLocationName = location?.currentName
        )}
    }


    sealed class UiEvent {
        enum class SnackbarMessageType { DeletedNFinishedItems }
        data class ShowSnackbar(
            val messageType: SnackbarMessageType,
            val extraArgs: List<String>? = null,
            val onAction: (() -> Unit)? = null
        ): UiEvent()
        object ClearFocus: UiEvent()
        data class ChangeShowEditSheet(val show: Boolean): UiEvent()
        object FocusLastList: UiEvent()
        object FocusLastLocation: UiEvent()
        enum class ToastMessageType { MovedNItemsToList }
        data class ShowToast(val messageType: ToastMessageType, val extraArgs: List<String>? = null): UiEvent()
    }
}