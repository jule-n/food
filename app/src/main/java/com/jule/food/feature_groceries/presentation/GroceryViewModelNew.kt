package com.jule.food.feature_groceries.presentation

import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jule.food.R
import com.jule.food.feature_groceries.domain.GroceryItemNew
import com.jule.food.feature_groceries.domain.use_case.GroceriesUseCases
import com.jule.food.feature_locations.domain.GroceryLocationNew
import com.jule.food.feature_locations.domain.use_case.LocationUseCases
import com.jule.food.others.SettingsRepository
import com.jule.food.others.SettingsRepository.Companion.SELECTED_LIST_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    var getListsJob: Job? = null
    var getLocationsJob: Job? = null

    var lastDeletedFinishedItems: List<GroceryItemNew>? = null

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
            is GroceryScreenEvent.ChangeShowEditLocationScreen -> {
                _currentState.value = currentState.copy(
                    showEditLocationDialog = event.show
                )
            }
            is GroceryScreenEvent.ChangeSelectLocationScreen -> {

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
                currentState.itemsInCurrentList.fastFirstOrNull { it.id == event.id }?.isDeleted?.value = true
            }
            is GroceryScreenEvent.RestoreFinishedItem -> {
                currentState.itemsInCurrentList.fastFirstOrNull { it.id == event.id }?.isDeleted?.value = false
            }
            is GroceryScreenEvent.ChangeShowFinishedItems -> {
                currentState.selectedList?.showDeletedItems?.value = event.show
            }
            is GroceryScreenEvent.DeleteFinishedItems -> {
                viewModelScope.launch {
                    lastDeletedFinishedItems = currentState.itemsInCurrentList.filter { it.isDeleted.value }.map { it.toGroceryItem() }
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
            _currentState.value = currentState.copy(
                lists = lists.map { it.toPresentationList() }
            )
            if (!lists.any { it.id == currentState.selectedListId } && lists.isNotEmpty()) {
                Log.d("getLists", "Setting selected list to first list: ${lists[0].id}")
                settingsRepository.setSelectedListId(lists[0].id)
            }
        }.launchIn(viewModelScope)

        getGroceriesJob?.cancel()
        getGroceriesJob = groceriesUseCases.getGroceriesInList(currentState.selectedListId ?: 0).onEach { groceries ->
            Log.d("getGroceries", "Groceries: $groceries")
            val presentationItems = groceries.map { groceryItem ->
                val presItem = groceryItem.toPresentationItem()
                val location = currentState.locations.fastFirstOrNull { it.assignedGroceries.contains(groceryItem.text) }
                presItem.locationId.value = location?.id
                presItem.locationName.value = location?.name.toString()
                return@map presItem
            }
            _currentState.value = currentState.copy(
                itemsInCurrentList = presentationItems,
                isDataLoaded = true
            )
        }.launchIn(viewModelScope)
    }
    sealed class UiEvent {
        data class ShowSnackbar(@StringRes val message: Int, @StringRes val action: Int? = null, val onAction: (() -> Unit)? = null): UiEvent()
        object ClearFocus: UiEvent()
    }
}