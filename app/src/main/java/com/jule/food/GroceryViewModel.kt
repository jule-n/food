package com.jule.food

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.util.fastFirst
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID


enum class GroceryGroupingOption { None, Recipe, Location }
val groceryGroupingOptionsDisplay = mapOf(
    GroceryGroupingOption.None to R.string.none,
    GroceryGroupingOption.Recipe to R.string.recipe,
    GroceryGroupingOption.Location to R.string.store_location
)
val groceryGroupingOptionsIcons = mapOf(
    GroceryGroupingOption.None to R.drawable.checkbox,
    GroceryGroupingOption.Recipe to R.drawable.book,
    GroceryGroupingOption.Location to R.drawable.location
)

class GroceryItemCategory(
    name: String,
    var items: SnapshotStateList<GroceryItem> = mutableStateListOf(),
//    var deletedItems: SnapshotStateList<GroceryItem> = mutableStateListOf(),
    val id: UUID = UUID.randomUUID()
) {
    var name by mutableStateOf(name)
}

@Serializable
class SaveableGroceryItemCategory(
    val name: String,
    val items: List<SaveableGroceryItem>,
//    val deletedItems: List<SaveableGroceryItem>,
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

fun isCategoryNameTooLong(name: String): Boolean {
    return name.length > 40
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

    private val deletedItemsPerCategory: SnapshotStateMap<UUID, MutableList<GroceryItem>> = mutableStateMapOf()
    val selectedCategoryDeletedItems: List<GroceryItem> get() = deletedItemsPerCategory[selectedCategoryId] ?: listOf()
    fun addToDeletedItems(item: GroceryItem, categoryId: UUID) {
        if (deletedItemsPerCategory.containsKey(categoryId)) {
            deletedItemsPerCategory[categoryId]!!.add(item)
            deletedItemsPerCategory[categoryId]!!
        } else {
            deletedItemsPerCategory.put(categoryId, mutableListOf(item))
        }
    }
    fun removeFromDeletedItems(itemId: UUID, categoryId: UUID) {
        deletedItemsPerCategory[categoryId]?.removeIf { it.id == itemId }
    }

    private var _selectedGroupingOption by mutableStateOf(GroceryGroupingOption.None)
    val selectedGroupingOption get() = _selectedGroupingOption
    fun changeSelectedGroupingOption (newOption: GroceryGroupingOption) {
        _selectedGroupingOption = newOption
    }

    private var showDeletedItemsPerCategory: MutableMap<UUID, Boolean> = mutableStateMapOf()
    val selectedCategoryShowDeletedItems get() = showDeletedItemsPerCategory[selectedCategoryId] ?: false
    fun changeShowDeletedItems (categoryId: UUID, newValue: Boolean) {
        if (showDeletedItemsPerCategory.containsKey(categoryId)) {
            showDeletedItemsPerCategory[categoryId] = newValue
        } else {
            showDeletedItemsPerCategory.put(categoryId, newValue)
        }
    }

    fun addCategory(name: String): UUID {
        if (_groceryItemCategories.any { it.name == name }) {
            Log.e("addCategory", "Tried to add category \"$name\", but name already exists.")
            return UUID.randomUUID()
        }
        val newCategory = GroceryItemCategory(name = name)
        _groceryItemCategories.add(newCategory)
        return newCategory.id
    }

    fun addCategory(category: GroceryItemCategory) {
        if (_groceryItemCategories.any { it.id == category.id || it.name == category.name }) {
            Log.e("addCategory", "Tried to add category \"${category.name}\", but name or ID already exists.")
            return
        }
        _groceryItemCategories.add(category)
//        _groceryItemCategories.sortBy { it.name }
    }
    fun removeCategory(id: UUID) {
        _groceryItemCategories.removeIf { it.id == id }
        if (_selectedCategoryId == id) {
            _selectedCategoryId = _groceryItemCategories[0].id
        }
    }
    fun changeCategoryName(newName: String, id: UUID) {
        _groceryItemCategories.fastFirst { it.id == id }.name = newName
    }
    fun reorderCategories(fromIndex: Int, toIndex: Int) {
        _groceryItemCategories.apply {
            add(toIndex, removeAt(fromIndex))
        }
        Log.d("GroceryViewModel", "Attempting to move category from $fromIndex to $toIndex.")
    }
    fun addToGroceries(item: GroceryItem, categoryId: UUID) {
        val category = _groceryItemCategories.fastFirst { it.id == categoryId }
        category.items.add(item)
        category.items.sortBy { it.name }
    }
    fun addToGroceries(items: List<GroceryItem>, categoryId: UUID, recipeId: UUID, context: Context) {
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

        Toast.makeText(context, context.getString(R.string.added_n_groceries, items.size, category.name), Toast.LENGTH_SHORT).show()
    }
    fun removeFromGroceries(index: Int, categoryId: UUID) {
        _groceryItemCategories.fastFirst { it.id == categoryId }.items.removeAt(index)
    }
    fun changeGroceryItem(id: UUID, newName: String, newDetails: String, categoryId: UUID) {
        val category = _groceryItemCategories.fastFirst { it.id == categoryId }
        val item = category.items.fastFirst { it.id == id }
        item.name = newName
        item.details = newDetails
    }
    fun moveItemsToCategory(itemIds: List<UUID>, fromCategoryId: UUID, toCategoryId: UUID) {
        val fromCategory = _groceryItemCategories.fastFirst { it.id == fromCategoryId }
        val toCategory = _groceryItemCategories.fastFirst { it.id == toCategoryId }

        val items = fromCategory.items.filter { item -> itemIds.contains(item.id) }
        fromCategory.items.removeAll(items)
        toCategory.items.addAll(items)

        toCategory.items.sortBy { it.name }
    }

    private fun getSaveable(): SaveableGroceryItemCategories {
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
            val id = addCategory("Default")
            _selectedCategoryId = id
        }
        _dataLoaded = true
    }


    fun import(saveableCategories: SaveableGroceryItemCategories) {
        _groceryItemCategories.clear()


        val categories = getCategoriesFromSaveable(saveableCategories)

        categories.forEach { category ->
            addCategory(category)
        }

        _selectedCategoryId = if (_groceryItemCategories.firstOrNull { it.id == saveableCategories.selectedId } != null) {
            saveableCategories.selectedId
        } else {
            _groceryItemCategories.first().id
        }
    }
}

fun getCategoriesFromSaveable(saveableCategories: SaveableGroceryItemCategories): List<GroceryItemCategory> {
    val categories = mutableListOf<GroceryItemCategory>()

    saveableCategories.categories.forEach { saveableCategory ->
        val category = GroceryItemCategory(saveableCategory.name, mutableStateListOf(), saveableCategory.id)
        saveableCategory.items.forEach { item ->
            category.items.add(GroceryItem(item.name, item.details, item.recipeId))
        }
        categories.add(category)
    }

    return categories
}