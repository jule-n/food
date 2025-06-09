package com.jule.food

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.random.Random

class GroceryItemCategory(
    name: String,
    var items: SnapshotStateList<GroceryItem> = mutableStateListOf(),
) {
    var name by mutableStateOf(name)
    val id: UUID = UUID.randomUUID()
}

@Serializable
class SaveableGroceryItemCategory(
    val name: String,
    val items: List<SaveableGroceryItem>
)

@Serializable
class SaveableGroceryItemCategories(
    val categories: List<SaveableGroceryItemCategory>
)

@Serializable
class SaveableGroceryItem(
    val name: String,
    val details: String
)

class GroceryItem(
    var name: String,
    var details: String,
//    id: UUID
) {
    private var _id: UUID = UUID.randomUUID()
    val id get() = _id
    fun generateNewId() {
        _id = UUID.randomUUID()
    }
}

class GroceryViewModel: ViewModel() {
    private var _groceryItemCategories = mutableStateListOf<GroceryItemCategory>()
    val groceryItemCategories: List<GroceryItemCategory>
        get() = _groceryItemCategories

    private var _selectedCategoryIndex by mutableIntStateOf(0)
    val selectedCategoryIndex get() = _selectedCategoryIndex

    fun changeSelectedCategoryIndex (newIndex: Int) {
        _selectedCategoryIndex = newIndex
    }

    fun addCategory(name: String) {
        val newCategory = GroceryItemCategory(name = name)

        _groceryItemCategories.add(newCategory)
//        _groceryItemCategories.sortBy { it.name }
    }
    fun addCategory(category: GroceryItemCategory) {
        _groceryItemCategories.add(category)
//        _groceryItemCategories.sortBy { it.name }
    }
    fun removeCategory(index: Int) {
        _groceryItemCategories.removeAt(index)
        if (selectedCategoryIndex >= index && selectedCategoryIndex != 0)
            _selectedCategoryIndex -= 1
    }

    fun changeCategoryIndex(oldIndex: Int, newIndex: Int) {
        _groceryItemCategories.apply {
            add(newIndex, removeAt(oldIndex))
        }
    }
    fun changeCategoryName(index: Int, newName: String) {
        _groceryItemCategories[index].name = newName
//        _groceryItemCategories.sortBy { it.name }
//        Log.d("ChangeCategoryName", "Change category Name $index to $newName")
    }
    fun addToGroceries(item: GroceryItem, categoryIndex: Int) {
        _groceryItemCategories[categoryIndex].items.add(item)
        _groceryItemCategories[categoryIndex].items.sortBy { it.name }
    }
    fun addToGroceries(items: List<GroceryItem>, categoryIndex: Int) {
//        items.forEach {
//            it.generateNewId()
//        }
        // IDs cannot match!
        items.forEach { newItem ->
            Log.d("AddToGroceries", "Adding ${newItem.name}")
            val indexSameItem = _groceryItemCategories[categoryIndex].items.indexOfFirst { item -> item.name.lowercase() == newItem.name.lowercase() }
            if (indexSameItem != -1) {
                Log.d("AddToGroceries", "Match found")
                // There is an item of the same name
                val oldItem = _groceryItemCategories[categoryIndex].items[indexSameItem]
                val regex = Regex("""\d+\D+""")
                if (oldItem.details.matches(regex) && newItem.details.matches(regex)) {
                    Log.d("AddToGroceries", "Match satisfies Regex")
                    val regexUnit = Regex("""\D+""")
                    val oldUnit = regexUnit.find(oldItem.details)?.value
                    val newUnit = regexUnit.find(newItem.details)?.value
                    Log.d("AddToGroceries", "Old Unit: $oldUnit, New Unit: $newUnit")
                    if (oldUnit == newUnit) {
                        Log.d("AddToGroceries", "Units match")
                        // Same unit, meaning we can add numbers together
                        val regexNumber = Regex("""\d+""")
                        val oldNumber = regexNumber.find(oldItem.details)?.value?.toInt()
                        val newNumber = regexNumber.find(newItem.details)?.value?.toInt()
                        val number = if (oldNumber != null && newNumber != null) oldNumber + newNumber else 39

                        Log.d("AddToGroceries", "Old Number: $oldNumber, New Number: $newNumber, the sum is $number")
                        _groceryItemCategories[categoryIndex].items.removeAt(indexSameItem)
                        _groceryItemCategories[categoryIndex].items.add(GroceryItem(oldItem.name, "$number$oldUnit"))
                        Log.d("AddToGroceries", "Item Updated: $number$oldUnit")
                        return@forEach
                    } else {
                        Log.d("AddToGroceries", "Units don't match")
                    }
                } else {
                    Log.d("AddToGroceries", "Match doesn't satisfy Regex")
                }
            }
            if (_groceryItemCategories[categoryIndex].items.indexOfFirst { item -> item.id == newItem.id} != -1)
                newItem.generateNewId()
            _groceryItemCategories[categoryIndex].items.add(newItem)
        }

        _groceryItemCategories[categoryIndex].items.sortBy { it.name }
    }
    fun removeFromGroceries(index: Int, categoryIndex: Int) {
        _groceryItemCategories[categoryIndex].items.removeAt(index)
    }

    fun getSaveable(): SaveableGroceryItemCategories {
        val output = mutableListOf<SaveableGroceryItemCategory>()
        groceryItemCategories.forEach { category ->
            val outputItems = mutableListOf<SaveableGroceryItem>()
            category.items.forEach { item ->
                outputItems.add(SaveableGroceryItem(item.name, item.details))
            }
            output.add(SaveableGroceryItemCategory(category.name, outputItems))
        }
        return SaveableGroceryItemCategories(output)
    }

    fun getJson(): String {
        return Json.encodeToString(getSaveable())
    }

    fun saveToFile(context: Context) {
        if (groceryItemCategories.isEmpty())
            return

        val prefs: SharedPreferences = context.getSharedPreferences("com.jule.food", MODE_PRIVATE)
        prefs.edit().putInt("selected_category", selectedCategoryIndex).apply()

        writeJsonToFile(context, "groceries.json", getSaveable())
    }

    fun getFromFile(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences("com.jule.food", MODE_PRIVATE)
        _selectedCategoryIndex = prefs.getInt("selected_category", 0)

        val categories: SaveableGroceryItemCategories? = getJsonFromFile(context, "groceries.json")

        if (categories == null) {
            addCategory("Default")
            return
        }
        import(categories)
    }

    fun import(categories: SaveableGroceryItemCategories) {
        _groceryItemCategories.clear()

        categories.categories.forEachIndexed { index, category ->
            addCategory(category.name)
            category.items.forEach { item ->
                addToGroceries(GroceryItem(item.name, item.details), index)
            }
        }
        _selectedCategoryIndex = 0
    }
}