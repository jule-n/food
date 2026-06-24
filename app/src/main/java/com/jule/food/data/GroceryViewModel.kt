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
import com.jule.food.ui.groceries_recipes.GroceryListAddingOption
import com.jule.food.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.collections.get


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
    var finishedItems: SnapshotStateList<GroceryItem> = mutableStateListOf(),
    val id: UUID = UUID.randomUUID()
) {
    var name by mutableStateOf(name)
}

@Serializable
class SaveableGroceryItemCategory(
    val name: String,
    val items: List<SaveableGroceryItem>,
    val finishedItems: List<SaveableGroceryItem>? = null,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
)

@Serializable
class SaveableGroceryItemCategories(
    val categories: List<SaveableGroceryItemCategory>,
    @Serializable(with = UUIDSerializer::class)
    val selectedId: UUID? = null,
    val groupingOption: GroceryGroupingOption? = null
)

@Serializable
class SaveableGroceryItem(
    val name: String,
    val details: String,
    @Serializable(with = UUIDSerializer::class)
    val recipeId: UUID? = null,
    @Serializable(with = UUIDSerializer::class)
    val locationId: UUID? = null,
    @Serializable(with = UUIDSerializer::class)
    val categoryId: UUID? = null
)

@Serializable
class ListOfSaveableGroceryItems(
    val items: List<SaveableGroceryItem>
)

class GroceryItem(
    name: String,
    details: String,
    recipeId: UUID? = null,
    locationId: UUID? = null,
    categoryId: UUID? = null,
    val id: UUID = UUID.randomUUID()
) {
    var name by mutableStateOf(name)
    var details by mutableStateOf(details)
    var recipeId by mutableStateOf(recipeId)
    var locationId by mutableStateOf(locationId)
    var categoryId by mutableStateOf(categoryId)

    fun copy(): GroceryItem {
        return GroceryItem(name, details, recipeId, locationId, categoryId)
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

    private val gridStatesPerCategory: SnapshotStateMap<UUID, LazyGridState> = mutableStateMapOf()
    @Composable fun getGridStateForSelectedCategory(): LazyGridState? {
        if (selectedCategoryId == null)
            return null

        if (gridStatesPerCategory.containsKey(selectedCategoryId)) {
            return gridStatesPerCategory[selectedCategoryId]!!
        }

        val newGridState = rememberLazyGridState()
        gridStatesPerCategory[selectedCategoryId!!] = newGridState
        return newGridState
    }

    private fun getCategoryFromId(categoryId: UUID, functionName: String): GroceryItemCategory {
        val category = _groceryItemCategories.fastFirstOrNull { it.id == categoryId }
        if (category == null) {
            Log.e(functionName, "Category with ID $categoryId not found")
            return GroceryItemCategory("NULL")
        }
        return category
    }

    fun addToDeletedItems(item: GroceryItem, categoryId: UUID) {
        val category = getCategoryFromId(categoryId, "addToDeletedItems")
        category.finishedItems.add(item)
    }
    fun restoreDeletedItems(items: List<GroceryItem>, categoryId: UUID) {
        val category = getCategoryFromId(categoryId, "restoreDeletedItems")
        category.finishedItems.addAll(items)
    }
    fun removeFromDeletedItems(itemId: UUID, categoryId: UUID) {
        val category = getCategoryFromId(categoryId, "removeFromDeletedItems")
        category.finishedItems.removeIf { it.id == itemId }
    }
    fun clearDeletedItems(categoryId: UUID) {
        val category = getCategoryFromId(categoryId, "clearDeletedItems")
        category.finishedItems.clear()
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
            showDeletedItemsPerCategory[categoryId] = newValue
        }
    }


    fun onRemoveGroceryLocation(locationId: UUID) {
        var index = 0
        _groceryItemCategories.forEach {
            it.items.forEach {
                if (it.locationId == locationId) {
                    it.locationId = null
                    index++
                }
            }
        }

        Log.d("GroceryVM:onRemoveGroceryLocation", "Removed location from $index items")
    }
    fun onAddGroceryNameToLocation(groceryName: String, locationId: UUID) {
        var index = 0
        _groceryItemCategories.forEach {
            it.items.forEach {
                if (it.name.trim() == groceryName.trim()) {
                    it.locationId = locationId
                    index++
                }
            }
        }
        Log.d("GroceryVM:onAddGroceryNameToLocation", "Added location to $index items ($groceryName)")
    }
    fun onRemoveGroceryNameFromAllLocations(groceryName: String) {
        var index = 0
        _groceryItemCategories.forEach {
            it.items.forEach {
                if (it.name.trim() == groceryName.trim()) {
                    it.locationId = null
                    index++
                }
            }
        }

        Log.d("GroceryVM:onRemoveGroceryNameFromAllLocations", "Removed ${groceryName} from all locations ($index)")
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
    // Adds new grocery category
    fun addCategory(category: GroceryItemCategory) {
        var categoryName = category.name
        var index = 2
        if (_groceryItemCategories.any { it.id == category.id}) {
            Log.e("addCategory", "Tried to add category with ID ${category.id}, but ID already exists.")
            return
        }
        while (_groceryItemCategories.any { it.name == categoryName } && index < 100) {
            categoryName = category.name + index.toString()
            index++
        }
        if (_groceryItemCategories.any { it.name == categoryName }) {
            Log.e("addCategory", "Tried to add category \"$categoryName\", but name already exists.")
            return
        }
        category.name = categoryName
        _groceryItemCategories.add(category)
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
    }
    fun getCategoryNameFromId(id: UUID): String {
        return getCategoryFromId(id, "getCategoryNameFromId").name
    }
    fun addToGroceries(item: GroceryItem, categoryId: UUID) {
        val category = getCategoryFromId(categoryId, "addToGroceries")
        // If the ID already exists, create a new item with a different ID
        if (category.items.any { it.id == item.id }) {
            category.items.add(item.copy())
        } else {
//            Log.d("addToGroceries", "Adding new Item: ${item.name} with recipe ID ${item.recipeId} and location ID ${item.locationId}")
            category.items.add(item)
        }

        category.items.sortBy { it.name }
    }
    fun addToGroceries(items: List<GroceryItem>, categoryId: UUID) {
        val category = getCategoryFromId(categoryId, "addToGroceries")
        items.forEach { item ->
            val newItem = item.copy()
            category.items.add(newItem)
        }

        category.items.sortBy { it.name }
    }
    fun addToGroceriesFromRecipe(items: List<GroceryItem>, addingOption: GroceryListAddingOption, categoryId: UUID, recipeId: UUID, context: Context) {
        val category = getCategoryFromId(categoryId, "addToGroceriesFromRecipe")
        items.forEach { item ->
            val newItem = item.copy()
            newItem.recipeId = recipeId
            if (addingOption == GroceryListAddingOption.AllGroceries || (addingOption == GroceryListAddingOption.OnlyNoList && newItem.categoryId == null)) {
                // If all groceries are added to this category or this grocery does not have a category assigned
                // Add this item to default category
                category.items.add(newItem)
            } else {
                // If only groceries without category are added to default category, but this item has a category assigned
                // Get category
                val thisCategory = getCategoryFromId(newItem.categoryId!!, "addToGroceriesFromRecipe")
                // Add this item to category
                thisCategory.items.add(newItem)

            }
        }

        category.items.sortBy { it.name }

        Toast.makeText(context, context.getString(R.string.added_n_groceries, items.size, category.name), Toast.LENGTH_SHORT).show()
    }
    fun removeFromGroceries(index: Int, categoryId: UUID) {
        getCategoryFromId(categoryId, "removeFromGroceries").items.removeAt(index)
    }
    fun changeGroceryItem(id: UUID, newName: String, newDetails: String, categoryId: UUID) {
        val category = getCategoryFromId(categoryId, "changeGroceryItem")
        val item = category.items.fastFirst { it.id == id }
        item.name = newName
        item.details = newDetails
    }
    fun moveItemsToCategory(itemIds: List<UUID>, fromCategoryId: UUID, toCategoryId: UUID) {
        val fromCategory = getCategoryFromId(fromCategoryId, "moveItemsToCategory")
        val toCategory = getCategoryFromId(toCategoryId, "moveItemsToCategory")

        val items = fromCategory.items.filter { item -> itemIds.contains(item.id) }
        fromCategory.items.removeAll(items)
        toCategory.items.addAll(items)

        toCategory.items.sortBy { it.name }
    }


    private fun getSaveable(categories: List<GroceryItemCategory> = groceryItemCategories): SaveableGroceryItemCategories {
        val output = mutableListOf<SaveableGroceryItemCategory>()
        categories.forEach { category ->
            val outputItems = mutableListOf<SaveableGroceryItem>()
            val outputFinishedItems = mutableListOf<SaveableGroceryItem>()
            category.items.forEach { item ->
                outputItems.add(SaveableGroceryItem(item.name, item.details, item.recipeId, item.locationId))
            }
            category.finishedItems.forEach { item ->
                outputFinishedItems.add(SaveableGroceryItem(item.name, item.details, item.recipeId, item.locationId))
            }
            output.add(SaveableGroceryItemCategory(category.name, outputItems, outputFinishedItems, category.id))
        }
        return SaveableGroceryItemCategories(output, selectedCategoryId, selectedGroupingOption)
    }

    fun getJson(): String {
        return Json.encodeToString(getSaveable())
    }

//    fun getJson(categories: List<GroceryItemCategory>): String {
//        return Json.encodeToString(getSaveable(categories))
//    }

    fun getJson(items: List<GroceryItem>): String {
        val saveableList = items.map {
            SaveableGroceryItem(it.name, it.details, it.recipeId, it.locationId)
        }
        return Json.encodeToString(ListOfSaveableGroceryItems(saveableList))
    }

    fun saveToFile(context: Context) {
        Log.d("saveToFile", "Called. dataLoaded=$_dataLoaded, categoriesEmpty=${groceryItemCategories.isEmpty()}")
        if (groceryItemCategories.isEmpty())
            return

//        val prefs: SharedPreferences = context.getSharedPreferences("com.jule.food", MODE_PRIVATE)
//        prefs.edit().putInt("selected_category", selectedCategoryIndex).apply()

        writeJsonToFile(context, "groceries.json", getSaveable())
    }

    fun getFromFile(context: Context) {
//        val prefs: SharedPreferences = context.getSharedPreferences("com.jule.food", MODE_PRIVATE)
//        _selectedCategoryIndex = prefs.getInt("selected_category", 0)

        val categories: SaveableGroceryItemCategories? =
            getJsonFromFile(context, "groceries.json", ignoreKeys = true)

        if (categories == null) {
            initializeEmpty()
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

        _selectedGroupingOption = saveableCategories.groupingOption ?: GroceryGroupingOption.None
    }
}

fun getCategoriesFromSaveable(saveableCategories: SaveableGroceryItemCategories): List<GroceryItemCategory> {
    val categories = mutableListOf<GroceryItemCategory>()

    saveableCategories.categories.forEach { saveableCategory ->
        val category = GroceryItemCategory(saveableCategory.name, mutableStateListOf(), mutableStateListOf(), saveableCategory.id)
        saveableCategory.items.forEach { item ->
            category.items.add(GroceryItem(item.name, item.details, item.recipeId, item.locationId))
        }
        saveableCategory.finishedItems?.forEach { item ->
            category.finishedItems.add(GroceryItem(item.name, item.details, item.recipeId, item.locationId))
        }
        categories.add(category)
    }

    return categories
}

fun getGroceriesFromSaveable(saveableGroceryList: ListOfSaveableGroceryItems): List<GroceryItem> {
    val items = saveableGroceryList.items.map {
        GroceryItem(it.name, it.details, it.recipeId, it.locationId)
    }
    return items
}