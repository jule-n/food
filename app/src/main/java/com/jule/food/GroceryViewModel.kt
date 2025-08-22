package com.jule.food

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.random.Random


enum class GroceryGroupingOption { None, Recipe, Location }
val groceryGroupingOptionsDisplay = mapOf(
    GroceryGroupingOption.None to R.string.none,
    GroceryGroupingOption.Recipe to R.string.recipe,
    GroceryGroupingOption.Location to R.string.store_location
)

class GroceryItemCategory(
    name: String,
    var items: SnapshotStateList<GroceryItem> = mutableStateListOf(),
    val id: UUID = UUID.randomUUID()
) {
    var name by mutableStateOf(name)
//    val id: UUID = UUID.randomUUID()
}

@Serializable
class SaveableGroceryItemCategory(
    val name: String,
    val items: List<SaveableGroceryItem>,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
)

@Serializable
class SaveableGroceryItemCategories(
    val categories: List<SaveableGroceryItemCategory>,
    @Serializable(with = UUIDSerializer::class)
    val selectedId: UUID? = null
)

@Serializable
class SaveableGroceryItem(
    val name: String,
    val details: String,
    @Serializable(with = UUIDSerializer::class)
    val recipeId: UUID? = null
)

class GroceryItem(
    name: String,
    details: String,
    recipeId: UUID? = null,
    val id: UUID = UUID.randomUUID()
) {
    var name by mutableStateOf(name)
    var details by mutableStateOf(details)
    var recipeId by mutableStateOf(recipeId)

    fun copy(): GroceryItem {
        return GroceryItem(name, details, recipeId)
    }
}

fun isCategoryError(name: String): Boolean {
    return name.isEmpty() || name.length > 20
}

class GroceryViewModel: ViewModel() {
    private var _dataLoaded by mutableStateOf(false)
    val dataLoaded: Boolean
        get() = _dataLoaded

    private var _groceryItemCategories = mutableStateListOf<GroceryItemCategory>()
    val groceryItemCategories: List<GroceryItemCategory>
        get() = _groceryItemCategories

    private var _selectedCategoryId: UUID? by mutableStateOf(null)
    val selectedCategoryId get() = _selectedCategoryId

    fun changeSelectedCategoryId (newId: UUID) {
        _selectedCategoryId = newId
    }

    private var _selectedGroupingOption by mutableStateOf(GroceryGroupingOption.None)
    val selectedGroupingOption get() = _selectedGroupingOption
    fun changeSelectedGroupingOption (newOption: GroceryGroupingOption) {
        _selectedGroupingOption = newOption
    }

    private var _showDeletedItems by mutableStateOf(false)
    val showDeletedItems get() = _showDeletedItems
    fun changeShowDeletedItems (newValue: Boolean) {
        _showDeletedItems = newValue
    }

    fun addCategory(name: String): UUID {
        val newCategory = GroceryItemCategory(name = name)
        _groceryItemCategories.add(newCategory)
        return newCategory.id
    }
    fun addCategory(name: String, id: UUID) {
        val newCategory = GroceryItemCategory(name = name, id = id)
        _groceryItemCategories.add(newCategory)

    }
    fun addCategory(category: GroceryItemCategory) {
        _groceryItemCategories.add(category)
//        _groceryItemCategories.sortBy { it.name }
    }
    fun removeCategory(id: UUID) {
        _groceryItemCategories.removeIf( { it.id == id } )
        if (_selectedCategoryId == id) {
            _selectedCategoryId = _groceryItemCategories[0].id
        }
    }

    fun changeCategoryIndex(oldIndex: Int, newIndex: Int) {
        _groceryItemCategories.apply {
            add(newIndex, removeAt(oldIndex))
        }
    }
    fun changeCategoryName(newName: String, id: UUID) {
        _groceryItemCategories.fastFirst { it.id == id }.name = newName
    }
    fun addToGroceries(item: GroceryItem, categoryId: UUID) {
        val category = _groceryItemCategories.fastFirst { it.id == categoryId }
        category.items.add(item)
        category.items.sortBy { it.name }
    }
    fun addToGroceries(items: List<GroceryItem>, categoryId: UUID, recipeId: UUID) {
        val category = _groceryItemCategories.fastFirst { it.id == categoryId }
        items.forEach { item ->
//            Log.d("AddToGroceries", "Adding ${newItem.name}")
//            val indexSameItem = category.items.indexOfFirst { item -> item.name.lowercase() == newItem.name.lowercase() }
//            if (indexSameItem != -1) {
//                Log.d("AddToGroceries", "Match found")
//                // There is an item of the same name
//                val oldItem = category.items[indexSameItem]
//                val regex = Regex("""\d+\D+""")
//                if (oldItem.details.matches(regex) && newItem.details.matches(regex)) {
//                    Log.d("AddToGroceries", "Match satisfies Regex")
//                    val regexUnit = Regex("""\D+""")
//                    val oldUnit = regexUnit.find(oldItem.details)?.value
//                    val newUnit = regexUnit.find(newItem.details)?.value
//                    Log.d("AddToGroceries", "Old Unit: $oldUnit, New Unit: $newUnit")
//                    if (oldUnit == newUnit) {
//                        Log.d("AddToGroceries", "Units match")
//                        // Same unit, meaning we can add numbers together
//                        val regexNumber = Regex("""\d+""")
//                        val oldNumber = regexNumber.find(oldItem.details)?.value?.toInt()
//                        val newNumber = regexNumber.find(newItem.details)?.value?.toInt()
//                        val number = if (oldNumber != null && newNumber != null) oldNumber + newNumber else 39
//
//                        Log.d("AddToGroceries", "Old Number: $oldNumber, New Number: $newNumber, the sum is $number")
//                        category.items.removeAt(indexSameItem)
//                        category.items.add(GroceryItem(oldItem.name, "$number$oldUnit"))
//                        Log.d("AddToGroceries", "Item Updated: $number$oldUnit")
//                        return@forEach
//                    } else {
//                        Log.d("AddToGroceries", "Units don't match")
//                    }
//                } else {
//                    Log.d("AddToGroceries", "Match doesn't satisfy Regex")
//                }
//            }
            val newItem = item.copy()
            newItem.recipeId = recipeId
            category.items.add(newItem)
        }

        category.items.sortBy { it.name }
    }
    fun removeFromGroceries(index: Int, categoryId: UUID) {
        _groceryItemCategories.fastFirst { it.id == categoryId }.items.removeAt(index)
    }
    fun moveItemsToCategory(itemIds: List<UUID>, fromCategoryId: UUID, toCategoryId: UUID) {
        val fromCategory = _groceryItemCategories.fastFirst { it.id == fromCategoryId }
        val toCategory = _groceryItemCategories.fastFirst { it.id == toCategoryId }

        val items = fromCategory.items.filter { item -> itemIds.contains(item.id) }
        fromCategory.items.removeAll(items)
        toCategory.items.addAll(items)

        toCategory.items.sortBy { it.name }
    }

    fun getSaveable(): SaveableGroceryItemCategories {
        val output = mutableListOf<SaveableGroceryItemCategory>()
        groceryItemCategories.forEach { category ->
            val outputItems = mutableListOf<SaveableGroceryItem>()
            category.items.forEach { item ->
                outputItems.add(SaveableGroceryItem(item.name, item.details, item.recipeId))
            }
            output.add(SaveableGroceryItemCategory(category.name, outputItems, category.id))
        }
        return SaveableGroceryItemCategories(output, selectedCategoryId)
    }

    fun getJson(): String {
        return Json.encodeToString(getSaveable())
    }

    fun saveToFile(context: Context) {
        if (groceryItemCategories.isEmpty())
            return

//        val prefs: SharedPreferences = context.getSharedPreferences("com.jule.food", MODE_PRIVATE)
//        prefs.edit().putInt("selected_category", selectedCategoryIndex).apply()

        writeJsonToFile(context, "groceries.json", getSaveable())
    }

    fun getFromFile(context: Context) {
//        val prefs: SharedPreferences = context.getSharedPreferences("com.jule.food", MODE_PRIVATE)
//        _selectedCategoryIndex = prefs.getInt("selected_category", 0)

        val categories: SaveableGroceryItemCategories? = getJsonFromFile(context, "groceries.json", ignoreKeys = true)

        if (categories == null) {
            addCategory("Default")
            _dataLoaded = true
            Log.e("getFromFile","Grocery Data Not Found")
            Toast.makeText(context, "Grocery Data Not Found", Toast.LENGTH_SHORT).show()
            return
        }

        import(categories)

//            delay(1000)

        _dataLoaded = true
        Log.d("getFromFile","Grocery Data Loaded!")

    }

    fun initializeEmpty() {
        if (groceryItemCategories.isEmpty()) {
            addCategory("Default")
        }
        _dataLoaded = true
    }

    fun import(categories: SaveableGroceryItemCategories) {
        _groceryItemCategories.clear()

        categories.categories.forEach { category ->
            addCategory(category.name, category.id)
            category.items.forEach { item ->
                addToGroceries(GroceryItem(item.name, item.details, item.recipeId), category.id)
            }
        }

        if (_groceryItemCategories.firstOrNull { it.id == categories.selectedId } != null) {
            _selectedCategoryId = categories.selectedId
        } else {
            _selectedCategoryId = _groceryItemCategories.first().id
        }
    }
}