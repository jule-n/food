package com.jule.food.feature_groceries.presentation

import android.preference.PreferenceManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jule.food.feature_groceries.domain.use_case.GroceriesUseCases
import com.jule.food.feature_locations.domain.GroceryLocationNew
import com.jule.food.feature_locations.domain.use_case.LocationUseCases
import com.jule.food.others.SettingsRepository
import com.jule.food.others.SettingsRepository.Companion.SELECTED_LIST_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
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

    var locations: List<GroceryLocationNew> by mutableStateOf(listOf())

    var getSettingsJob: Job? = null
    var getGroceriesJob: Job? = null
    var getListsJob: Job? = null
    var getLocationsJob: Job? = null

    init {
        getSettings()
        getLocations()
        getGroceries()
    }

    fun getSettings() {
        getSettingsJob?.cancel()
        getSettingsJob = settingsRepository.settingsFlow.onEach { settings ->
            _currentState.value = currentState.copy(
                selectedListId = settings[SELECTED_LIST_ID]
            )
        }.launchIn(viewModelScope)
    }

    fun getLocations() {
        getLocationsJob?.cancel()
        getLocationsJob = locationsUseCases.getAllLocations().onEach { locs ->
            locations = locs
        }.launchIn(viewModelScope)
    }


    fun getGroceries() {
        getListsJob?.cancel()
        getListsJob = groceriesUseCases.getAllLists().onEach { lists ->
            _currentState.value = currentState.copy(
                lists = lists
            )
            if (!lists.any { it.id == currentState.selectedListId } && lists.isNotEmpty()) {
                settingsRepository.setSelectedListId(lists[0].id)
            }
        }.launchIn(viewModelScope)

        getGroceriesJob?.cancel()
        getGroceriesJob = groceriesUseCases.getGroceriesInList(currentState.selectedListId ?: 0).onEach { groceries ->
            val presentationItems = groceries.map { groceryItem ->
                val presItem = groceryItem.toPresentationItem()
                presItem.locationId.value = locations.fastFirstOrNull { it.assignedGroceries.contains(groceryItem.text) }?.id
                return@map presItem
            }
            _currentState.value = currentState.copy(
                itemsInCurrentList = presentationItems
            )
        }.launchIn(viewModelScope)
    }
}