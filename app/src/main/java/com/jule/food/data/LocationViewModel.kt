package com.jule.food.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import com.jule.food.R
import com.jule.food.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.collections.get


class GroceryLocation(
    name: String,
    val groceryNames: SnapshotStateList<String> = mutableStateListOf(),
    val id: UUID = UUID.randomUUID()
) {
    var name by mutableStateOf(name)
}

@Serializable
class SaveableGroceryLocation(
    val name: String,
    val groceryNames: List<String>,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID
)

@Serializable
class SaveableGroceryLocations(
    val locations: List<SaveableGroceryLocation>
)

class LocationViewModel: ViewModel() {
    private var _dataLoaded by mutableStateOf(false)
    val dataLoaded: Boolean
        get() = _dataLoaded

    private var _groceryLocations = mutableStateListOf<GroceryLocation>()
    val groceryLocations: List<GroceryLocation> get () = _groceryLocations

    var groceryViewModel: GroceryViewModel? = null
    var recipeViewModel: RecipeViewModel? = null


    fun addGroceryLocation(groceryLocationName: String, context: Context) {
        if (_groceryLocations.any { it.name == groceryLocationName}) {
            Log.e("addGroceryLocation", "Tried to add grocery location \"$groceryLocationName\", but name already exists.")
            return
        }
        _groceryLocations.add(GroceryLocation(groceryLocationName))
        Log.d("addGroceryLocation", "Added new location: $groceryLocationName")

        saveToFile(context)
    }

    fun addGroceryLocation(location: GroceryLocation) {
        if (_groceryLocations.any { it.name == location.name}) {
            Log.e("addGroceryLocation", "Tried to add grocery location \"${location.name}\", but name already exists.")
            return
        }
        if (_groceryLocations.any { it.id == location.id}) {
            Log.e("addGroceryLocation", "Tried to add grocery location \"${location.name}\", but ID already exists.")
            return
        }
        _groceryLocations.add(location)
        Log.d("addGroceryLocation", "Added new location: ${location.name}")
    }
    fun removeGroceryLocation(locationId: UUID, context: Context) {
        _groceryLocations.removeIf { it.id == locationId }
        groceryViewModel?.onRemoveGroceryLocation(locationId)
        recipeViewModel?.onRemoveGroceryLocation(locationId)

        saveToFile(context)
    }
    fun reorderGroceryLocations(fromIndex: Int, toIndex: Int, context: Context) {
        _groceryLocations.apply {
            add(toIndex, removeAt(fromIndex))
        }
        saveToFile(context)
    }

    fun changeGroceryLocationName(newName: String, locationId: UUID, context: Context) {
        if (_groceryLocations.any { it.name == newName}) {
            Log.e("changeGroceryLocationName", "Tried to change name to \"$newName\", but name already exists.")
            return
        }
        _groceryLocations.firstOrNull { it.id == locationId }?.name = newName
        Log.d("changeGroceryLocationName", "Changed location name to: $newName")

        saveToFile(context)
    }
    fun getLocationNameFromId(locationId: UUID): String {
        return _groceryLocations.firstOrNull { it.id == locationId }?.name ?: "NOT FOUND"
    }

    fun addGroceryNameToLocation(groceryName: String, locationId: UUID, context: Context) {
        _groceryLocations.forEach {
            val containsName = it.groceryNames.contains(groceryName)
            if (it.id == locationId && !containsName) {
                it.groceryNames.add(groceryName)
            } else if (it.id != locationId && containsName) {
                it.groceryNames.remove(groceryName)
            }
        }
        groceryViewModel?.onAddGroceryNameToLocation(groceryName, locationId)
        recipeViewModel?.onAddGroceryNameToLocation(groceryName, locationId)

        saveToFile(context)
    }
    fun removeGroceryFromAllLocations(groceryName: String, context: Context) {
        _groceryLocations.forEach { it.groceryNames.remove(groceryName) }

        Log.d("removeGroceryFromAllLocations", "Removing ${groceryName} from all locations.")
        groceryViewModel?.onRemoveGroceryNameFromAllLocations(groceryName)
        recipeViewModel?.onRemoveGroceryNameFromAllLocations(groceryName)

        saveToFile(context)
    }

    fun changeLocationsWithNewGroceries(newGroceries: List<GroceryItem>, context: Context) {
        newGroceries.forEach { item ->
            if (item.locationId == null) {
                removeGroceryFromAllLocations(item.name, context)
            } else {
                addGroceryNameToLocation(item.name, item.locationId!!, context)
            }
        }
    }


    private fun getSaveable(locations: List<GroceryLocation> = _groceryLocations): SaveableGroceryLocations {
        val locations = mutableListOf<SaveableGroceryLocation>()
        groceryLocations.forEach { location ->
            locations.add(SaveableGroceryLocation(location.name, location.groceryNames, location.id))
        }
        return SaveableGroceryLocations(locations)
    }

    fun getJson(): String {
        return Json.encodeToString(getSaveable())
    }

    fun getJson(locations: List<GroceryLocation>): String {
        return Json.encodeToString(getSaveable(locations))
    }

    fun saveToFile(context: Context) {
        writeJsonToFile(context, "locations.json", getSaveable())
    }

    fun getFromFile(context: Context) {
        val locations: SaveableGroceryLocations? =
            getJsonFromFile(context, "locations.json", ignoreKeys = true)

        if (locations == null) {
            initializeEmpty()
            Log.e("getFromFile","Locations Not Found")
            Toast.makeText(context, "Locations Not Found", Toast.LENGTH_SHORT).show()
            return
        }

        import(locations)

//            delay(1000)

        _dataLoaded = true
        Log.d("getFromFile","Grocery Data Loaded!")

    }

    fun initializeEmpty() {
        _dataLoaded = true
    }


    fun import(saveableLocations: SaveableGroceryLocations) {
        _groceryLocations.clear()

        val locations = getLocationsFromSaveable(saveableLocations)

        locations.forEach { location ->
            addGroceryLocation(location)
        }

        Log.d("import", "Imported ${_groceryLocations.count()} locations: ${_groceryLocations.joinToString(separator = ", ") { it.name }}")
    }
}


fun getLocationsFromSaveable(saveableLocations: SaveableGroceryLocations): List<GroceryLocation> {
    val locations = mutableListOf<GroceryLocation>()

    saveableLocations.locations.forEach { saveableLocation ->
        val location = GroceryLocation(saveableLocation.name, saveableLocation.groceryNames.toMutableStateList(), saveableLocation.id)
        locations.add(location)
    }

    return locations
}